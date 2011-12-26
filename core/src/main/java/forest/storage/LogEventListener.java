package forest.storage;

import forest.event.Event;

public interface LogEventListener {
	
	void newLogEvent(Event e);

}
