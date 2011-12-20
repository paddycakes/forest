package forest.log;

import forest.event.Event;
import forest.storage.Store;

public class EventLogger implements Logger {

	private final Store store;

	public EventLogger(Store store) {
		this.store = store;
	}
	
	@Override
	public void info(String message, Object... parameters) {
		store.put(new Event(message, parameters));
	}

	@Override
	public void error(String message, Object... parameters) {
		// TODO Auto-generated method stub

	}

}
