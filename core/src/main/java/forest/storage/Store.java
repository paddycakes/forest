package forest.storage;

import java.util.Iterator;

import forest.event.Event;

public interface Store {
	
	void put(Event event);
	
	Iterator<Event> events();
	
	Iterator<Event> events(Query query);
	
}
