package forest.log;

import static forest.log.EventLogger.LoggerParams.LOG_LEVEL;
import static forest.log.EventLogger.LoggerParams.LOG_NAME;
import static forest.log.EventLogger.LoggerParams.THREAD_NAME;
import static forest.log.EventLogger.LoggerParams.THROWABLE;
import static forest.log.LogLevel.DEBUG;
import static forest.log.LogLevel.ERROR;
import static forest.log.LogLevel.FATAL;
import static forest.log.LogLevel.INFO;
import static forest.log.LogLevel.WARN;
import static forest.query.Queries.between;
import static org.joda.time.DateTimeUtils.setCurrentMillisFixed;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import forest.event.LogEvent;
import forest.storage.EventStore;
import forest.storage.SerializingEventsListStore;

public class EventLoggerTests {
	
	private static final DateTime start = new DateTime();
	
	private EventLogger log;
	private EventStore store;
	private String name = "my.logger";
	
	@Before
	public void setUp() {
		store = new SerializingEventsListStore();
		log = new LogEventStorageHandler(name, store);
	}
	
	@After
	public void tearDown() {
		setCurrentMillisSystem();
	}
	
	@Test
	public void logsAreStoredInEventStoreInTheOrderAdded() {
		log.info("One");
		log.info("Two");
		
		Iterator<LogEvent> events = allEvents();
		assertEquals("One", events.next().getMessage());
		assertEquals("Two", events.next().getMessage());
		assertFalse(events.hasNext());
	}

	private Iterator<LogEvent> allEvents() {
		return store.events(between(start.minusMinutes(10), start.plusMinutes(10))).iterator();
	}
	
	@Test
	public void logParameterIsSubstitutedIntoEventMessage() {
		log.info("Saving thingy $thingyId and happy about it.", 301);
		
		assertEquals("Saving thingy 301 and happy about it.", allEvents().next().getMessage());
	}
	
	@Test
	public void logEventsContainParameters() {
		log.info("Biffed $thingyId", 503);
		log.info("Bazooed $widget", "one");
		
		Iterator<LogEvent> events = allEvents();
		assertEquals(503, events.next().getParameter("thingyId"));
		assertEquals("one", events.next().getParameter("widget"));
	}
	
	@Test
	public void logEventReturnsNullForUnknownParameter() {
		log.info("Biffed $thingyId", 503);
		
		Iterator<LogEvent> events = allEvents();
		assertNull(events.next().getParameter("unknown"));
	}
	
	@Test
	public void logSetsTimestampOnEvent() {
		fixTimeTo(start);
		
		log.info("Loggin'...");
		assertEquals(start, allEvents().next().getTime());
	}
	
	@Test
	public void logSetsCategoryOnEvent() {
		log.info("Biffed $thingyId", 503);
		
		assertEquals(name, allEvents().next().getParameter(LOG_NAME));
	}
	
	@Test
	public void logSetsThrowableOnEvent() {
		RuntimeException exception = new RuntimeException("Doh!");
		log.info("Ooops", exception);
		
		assertEquals(exception, allEvents().next().getParameter(THROWABLE));
	}
	
	@Test
	public void logSetsThreadNameOnEvent() {
		log.info("Hiya!");
		
		assertEquals(Thread.currentThread().getName(), allEvents().next().getParameter(THREAD_NAME));
	}
	
	@Test
	public void logsSetLogLevelParameterOnEvent() {
		log.debug("Debug");
		log.info("Info");
		log.warn("Warn");
		log.error("Error");
		log.fatal("Fatal");
		
		Iterator<LogEvent> events = allEvents();
		assertNextEventIs(DEBUG, "Debug", events);
		assertNextEventIs(INFO, "Info", events);
		assertNextEventIs(WARN, "Warn", events);
		assertNextEventIs(ERROR, "Error", events);
		assertNextEventIs(FATAL, "Fatal", events);
	}

	private void assertNextEventIs(LogLevel level, String message, Iterator<LogEvent> events) {
		LogEvent event = events.next();
		assertEquals(message, event.getMessage());
		assertEquals(level, event.getParameter(LOG_LEVEL));
	}

	
	
	/* --- Private Helpers --- */

	private void fixTimeTo(DateTime dateTime) {
		setCurrentMillisFixed(dateTime.getMillis());
	}

}
