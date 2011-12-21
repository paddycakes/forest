package forest.log;

import static forest.log.EventLogger.LOG_LEVEL;
import static forest.log.EventLogger.LOG_NAME;
import static forest.log.LogLevel.*;
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

import forest.event.Event;
import forest.storage.InMemoryStore;
import forest.storage.Store;

public class EventLoggerTests {
	
	private static final DateTime now = new DateTime();
	
	private EventLogger log;
	private Store store;
	private String name = "my.logger";
	
	@Before
	public void setUp() {
		store = new InMemoryStore();
		log = new EventLogger(name, store);
	}
	
	@After
	public void tearDown() {
		setCurrentMillisSystem();
	}
	
	@Test
	public void logsAreStoredInEventStoreInTheOrderAdded() {
		log.info("One");
		log.info("Two");
		
		Iterator<Event> events = store.events();
		assertEquals("One", events.next().getMessage());
		assertEquals("Two", events.next().getMessage());
		assertFalse(events.hasNext());
	}
	
	@Test
	public void logParameterIsSubstitutedIntoEventMessage() {
		log.info("Saving thingy $thingyId and happy about it.", 301);
		
		assertEquals("Saving thingy 301 and happy about it.", store.events().next().getMessage());
	}
	
	@Test
	public void logEventsContainParameters() {
		log.info("Biffed $thingyId", 503);
		log.info("Bazooed $widget", "one");
		
		Iterator<Event> events = store.events();
		assertEquals(503, events.next().getParameter("thingyId"));
		assertEquals("one", events.next().getParameter("widget"));
	}
	
	@Test
	public void logEventReturnsNullForUnknownParameter() {
		log.info("Biffed $thingyId", 503);
		
		Iterator<Event> events = store.events();
		assertNull(events.next().getParameter("unknown"));
	}
	
	@Test
	public void logSetsTimestampOnEvent() {
		fixTimeTo(now);
		
		log.info("Loggin'...");
		assertEquals(now, store.events().next().getTime());
	}
	
	@Test
	public void logSetsCategoryOnEvent() {
		log.info("Biffed $thingyId", 503);
		
		assertEquals(name, store.events().next().getParameter(LOG_NAME));
	}
	
	@Test
	public void logsSetLogLevelParameterOnEvent() {
		log.debug("Debug");
		log.info("Info");
		log.warn("Warn");
		log.error("Error");
		log.fatal("Fatal");
		
		Iterator<Event> events = store.events();
		assertNextEventIs(DEBUG, "Debug", events);
		assertNextEventIs(INFO, "Info", events);
		assertNextEventIs(WARN, "Warn", events);
		assertNextEventIs(ERROR, "Error", events);
		assertNextEventIs(FATAL, "Fatal", events);
	}

	private void assertNextEventIs(LogLevel level, String message, Iterator<Event> events) {
		Event event = events.next();
		assertEquals(message, event.getMessage());
		assertEquals(level, event.getParameter(LOG_LEVEL));
	}

	
	
	/* --- Private Helpers --- */

	private void fixTimeTo(DateTime dateTime) {
		setCurrentMillisFixed(dateTime.getMillis());
	}

}
