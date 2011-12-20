package forest.log;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import forest.event.Event;
import forest.storage.InMemoryStore;
import forest.storage.Query;
import forest.storage.Store;

public class EventLoggerTests {
	
	private EventLogger log;
	private Store store;
	
	@Before
	public void setUp() {
		store = new InMemoryStore();
		log = new EventLogger(store);
	}
	
	@Test
	public void infoLogsAreStoredInEventStoreInTheOrderAdded() {
		log.info("One");
		log.info("Two");
		
		Iterator<Event> events = store.events();
		assertEquals("One", events.next().getMessage());
		assertEquals("Two", events.next().getMessage());
		assertFalse(events.hasNext());
	}
	
	@Test
	public void infoLogParameterIsSubstitutedIntoEventMessage() {
		log.info("Saving thingy $thingyId and happy about it.", 301);
		
		assertEquals("Saving thingy 301 and happy about it.", store.events().next().getMessage());
	}
	
	@Test
	public void infoLogEventsContainParameters() {
		log.info("Biffed $thingyId", 503);
		log.info("Bazooed $widget", "one");
		
		Iterator<Event> events = store.events();
		assertEquals(503, events.next().getParameter("thingyId"));
		assertEquals("one", events.next().getParameter("widget"));
	}
	
	@Test
	public void infoLogEventReturnsNullForUnknownParameter() {
		log.info("Biffed $thingyId", 503);
		
		Iterator<Event> events = store.events();
		assertNull(events.next().getParameter("unknown"));
	}

}
