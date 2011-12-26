package forest.storage;

import static forest.query.Queries.between;
import static forest.query.Queries.propertyEquals;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import java.util.Iterator;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import forest.event.Event;


public class SerializingEventsListStoreTests {
	
	private static final DateTime start = new DateTime();
	
	private SerializingEventsListStore store;
	
	@Before
	public void setUp() {
		store = new SerializingEventsListStore();
	}
	
	@Test
	public void allEventsIteratesOverAllEventsInTheOrderTheyWereAdded() {
		Event first = new Event("One");
		Event second = new Event("Two");
		store.put(first);
		store.put(second);
		
		Iterator<Event> events = allEvents();
		assertEquals(first, events.next());
		assertEquals(second, events.next());
		assertFalse(events.hasNext());
	}
	
	@Test
	public void queringEventStoreByParameterValueIteratesOverMatchingEventsInOrderAdded() {
		store.put(new Event("Biffed $thingyId", 503));
		store.put(new Event("Stonked $thingyId", 301));
		store.put(new Event("Bazooed $thingyId", 503));
		store.put(new Event("Squinked $thingyId", 4));
		
		Iterator<Event> events = store.events(propertyEquals("thingyId", 503)).iterator();
		assertEquals("Biffed 503", events.next().getMessage());
		assertEquals("Bazooed 503", events.next().getMessage());
		assertFalse(events.hasNext());
	}
	


	private Iterator<Event> allEvents() {
		return store.events(between(start.minusMinutes(10), start.plusMinutes(10))).iterator();
	}

}
