package forest.storage;

import forest.event.LogEvent;

// TODO: Deprecate for LogEventHandler
public interface LogEventListener {
	
	void newLogEvent(LogEvent e);

}
