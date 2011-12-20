package forest.storage;

import static forest.storage.Queries.eq;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import forest.event.Event;


public class InMemoryStoreTests {
	
	private InMemoryStore store;
	
	@Before
	public void setUp() {
		store = new InMemoryStore();
	}
	
	@Test
	public void eventsIteratesOverAllEventsInTheOrderTheyWereAdded() {
		Event first = new Event("One");
		Event second = new Event("Two");
		store.put(first);
		store.put(second);
		
		Iterator<Event> events = store.events();
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
		
		Iterator<Event> events = store.events(eq("thingyId", 503));
		assertEquals("Biffed 503", events.next().getMessage());
		assertEquals("Bazooed 503", events.next().getMessage());
		assertFalse(events.hasNext());
	}

}
