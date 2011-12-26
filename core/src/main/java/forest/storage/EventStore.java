package forest.storage;

import forest.event.Event;
import forest.query.Query;

public interface EventStore {
	
	void put(Event event);
	
	Iterable<Event> events(Query query);
	
	void addEventListener(LogEventListener listener);
	
}
