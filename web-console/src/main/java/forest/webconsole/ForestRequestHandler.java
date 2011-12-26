package forest.webconsole;

import static forest.log4j.Log4JEventWriter.buildLog4jEvent;
import static java.lang.String.format;
import static org.jboss.netty.buffer.ChannelBuffers.copiedBuffer;
import static org.jboss.netty.channel.ChannelFutureListener.CLOSE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.COOKIE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SET_COOKIE;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static org.jboss.netty.util.CharsetUtil.UTF_8;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;

import forest.event.Event;
import forest.query.Query;
import forest.query.QueryParser;
import forest.storage.EventStore;

public class ForestRequestHandler extends SimpleChannelUpstreamHandler {

	private static final String FILE_ENCODING = UTF_8.name();
	private static final String TEXT_CONTENT_TYPE = "text/plain; charset=" + FILE_ENCODING;
	private static final String XHTML_CONTENT_TYPE = "application/xhtml+xml; charset=" + FILE_ENCODING;
	private static final String JAVASCRIPT_CONTENT_TYPE = "application/javascript; charset=" + FILE_ENCODING;
	private static final String CSS_CONTENT_TYPE = "text/css; charset=UTF-8";

	private static final Logger log = LogManager.getLogger(ForestRequestHandler.class);

	private static final String SESSION_ID_COOKIE = "forestWebConsoleSessionId";
	private static final AtomicLong nextSessionId = new AtomicLong(1233);

	private final EventStore eventStore;
	private final Map<Long, ForestSession> sessions = new ConcurrentHashMap<Long, ForestSession>();

	public ForestRequestHandler(EventStore eventStore) {
		this.eventStore = eventStore;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		HttpRequest request = (HttpRequest) e.getMessage();
		ForestSession session = getSession(request);
		QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
		String path = queryStringDecoder.getPath();
		Map<String, List<String>> queryParams = queryStringDecoder.getParameters();
		log.debug(format("Received request with path %s and query parameters %s", path, queryParams));

		if (path.startsWith("/data")) {
			PatternLayout layout = new PatternLayout(session.getPattern());
			StringBuilder content = new StringBuilder();
			for (Event event : eventStore.events(parseQuery(paramQ(queryParams)))) {
				content.append("<li>").append(layout.format(buildLog4jEvent(event))).append("</li>");
			}
			writeResponse(session, OK, e, content, TEXT_CONTENT_TYPE);
		} else {
			String fileName = path.equals("/") ? "/console.html" : path;
			InputStream resource = this.getClass().getResourceAsStream(fileName);
			if (resource != null) {
				CharSequence content = IOUtils.toString(resource, FILE_ENCODING);
				writeResponse(session, OK, e, content, getContentType(fileName));
			} else {
				writeResponse(session, NOT_FOUND, e);
			}
		}
	}

	private String paramQ(Map<String, List<String>> queryParams) {
		List<String> paramQ = queryParams.get("q");
		return paramQ != null && paramQ.size() > 0 ? paramQ.get(0) : null;
	}

	private Query parseQuery(String q) {
		return q != null && !q.trim().equals("") ? new QueryParser(q).query() : null;
	}

	private String getContentType(String fileName) {
		if (fileName.endsWith("js")) return JAVASCRIPT_CONTENT_TYPE;
		else if (fileName.endsWith("css")) return CSS_CONTENT_TYPE;
		else return XHTML_CONTENT_TYPE;
	}

	private void writeResponse(ForestSession session, HttpResponseStatus status, MessageEvent e) {
		writeResponse(session, status, e, null, null);
	}

	private void writeResponse(ForestSession session, HttpResponseStatus status, MessageEvent e, CharSequence content,
			String contentType) {
		HttpRequest request = (HttpRequest) e.getMessage();
		boolean keepAlive = isKeepAlive(request);

		HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);
		if (content != null) {
			response.setContent(copiedBuffer(content.toString(), UTF_8));
			response.setHeader(CONTENT_TYPE, contentType);
			if (keepAlive) response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
		}

		setSessionIdCookie(session, response);

		ChannelFuture future = e.getChannel().write(response);
		if (!keepAlive) future.addListener(CLOSE);
	}

	private ForestSession getSession(HttpRequest request) {
		Cookie sessionIdCookie = findSessionIdCookie(request);
		if (sessionIdCookie != null) {
			try {
				Long sessionId = Long.valueOf(sessionIdCookie.getValue());
				ForestSession session = sessions.get(sessionId);
				if (session != null) return session;
			} catch (NumberFormatException e) {
				log.error(String.format("Could not parse session id from cookie value: %s. Creating new session.",
						sessionIdCookie.getValue()));
			}
		}
		return newSession();
	}

	private ForestSession newSession() {
		ForestSession newSession = new ForestSession(nextSessionId.getAndIncrement());
		sessions.put(newSession.getId(), newSession);
		return newSession;
	}

	private Cookie findSessionIdCookie(HttpRequest request) {
		String cookieString = request.getHeader(COOKIE);
		if (cookieString != null) {
			CookieDecoder cookieDecoder = new CookieDecoder();
			Set<Cookie> cookies = cookieDecoder.decode(cookieString);
			if (!cookies.isEmpty()) {
				for (Cookie cookie : cookies) {
					if (cookie.getName().equals(SESSION_ID_COOKIE)) {
						return cookie;
					}
				}
			}
		}
		return null;
	}

	private void setSessionIdCookie(ForestSession session, HttpResponse response) {
		CookieEncoder cookieEncoder = new CookieEncoder(true);
		DefaultCookie sessionIdCookie = new DefaultCookie(SESSION_ID_COOKIE, Long.toString(session.getId()));
		cookieEncoder.addCookie(sessionIdCookie);
		response.addHeader(SET_COOKIE, cookieEncoder.encode());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

	// buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
	// buf.append("===================================\r\n");
	//
	// buf.append("VERSION: " + request.getProtocolVersion() + "\r\n");
	// buf.append("HOSTNAME: " + getHost(request, "unknown") + "\r\n");
	// buf.append("REQUEST_URI: " + request.getUri() + "\r\n\r\n");
	//
	// for (Map.Entry<String, String> h : request.getHeaders()) {
	// buf.append("HEADER: " + h.getKey() + " = " + h.getValue() + "\r\n");
	// }
	// buf.append("\r\n");
	//
	// QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.getUri());
	// Map<String, List<String>> params = queryStringDecoder.getParameters();
	// if (!params.isEmpty()) {
	// for (Entry<String, List<String>> p : params.entrySet()) {
	// String key = p.getKey();
	// List<String> vals = p.getValue();
	// for (String val : vals) {
	// buf.append("PARAM: " + key + " = " + val + "\r\n");
	// }
	// }
	// buf.append("\r\n");
	// }
	// ChannelBuffer content = request.getContent();
	// if (content.readable()) {
	// buf.append("CONTENT: " + content.toString(CharsetUtil.UTF_8) + "\r\n");
	// }

}
