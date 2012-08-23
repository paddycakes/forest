package forest.storage;

import static forest.query.Queries.between;
import static forest.query.Queries.propertyEquals;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import forest.event.LogEvent;


public class SerializingEventsListStoreTests {
	
	private static final DateTime start = new DateTime();
	
	private SerializingEventsListStore store;
	
	@Before
	public void setUp() {
		store = new SerializingEventsListStore();
	}
	
	@Test
	public void allEventsIteratesOverAllEventsInTheOrderTheyWereAdded() {
		LogEvent first = new LogEvent("One");
		LogEvent second = new LogEvent("Two");
		store.put(first);
		store.put(second);
		
		Iterator<LogEvent> events = allEvents();
		assertEquals(first, events.next());
		assertEquals(second, events.next());
		assertFalse(events.hasNext());
	}
	
	@Test
	public void queringEventStoreByParameterValueIteratesOverMatchingEventsInOrderAdded() {
		store.put(new LogEvent("Biffed $thingyId", 503));
		store.put(new LogEvent("Stonked $thingyId", 301));
		store.put(new LogEvent("Bazooed $thingyId", 503));
		store.put(new LogEvent("Squinked $thingyId", 4));
		
		Iterator<LogEvent> events = store.events(propertyEquals("thingyId", 503)).iterator();
		assertEquals("Biffed 503", events.next().getMessage());
		assertEquals("Bazooed 503", events.next().getMessage());
		assertFalse(events.hasNext());
	}
	


	private Iterator<LogEvent> allEvents() {
		return store.events(between(start.minusMinutes(10), start.plusMinutes(10))).iterator();
	}

}
