package forest.webconsole;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.commons.cli.ParseException;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import forest.log.EventLogger;
import forest.storage.EventStore;
import forest.storage.SerializingEventsListStore;

public class WebConsole {

	public static void main(String[] args) throws ParseException {
		WebConsoleOptions options = new WebConsoleOptions(args);
		if (options.printHelp())
			return;

		startServer(options);
	}

	private static void startServer(WebConsoleOptions options) {
		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		ForestRequestHandler requestHandler = new ForestRequestHandler(getEventStore(options));
		bootstrap.setPipelineFactory(new WebConsolePipelineFactory(requestHandler));
		int port = options.getPort();
		bootstrap.bind(new InetSocketAddress(port));
		System.out.println("Started Forest web console on port " + port);
	}

	private static EventStore getEventStore(WebConsoleOptions options) {
		SerializingEventsListStore eventStore = new SerializingEventsListStore();
		final EventLogger logger = new EventLogger(EventLogger.class.getName(), eventStore);
		Executors.newFixedThreadPool(3).submit(new Runnable() {
			@Override
			public void run() {
				int i = 1;
				while (true) {
					logger.info("This is my message $id for widget $widgetId", i++, i % 5);
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		return eventStore;
	}

}
