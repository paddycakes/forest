package forest.storage;

import forest.event.LogEvent;
import forest.query.Query;

public interface EventStore {
	
	void put(LogEvent event);
	
	Iterable<LogEvent> events(Query query);
	
	void addEventListener(LogEventListener listener);
	
}
