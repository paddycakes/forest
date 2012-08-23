package forest.log;

import java.util.concurrent.atomic.AtomicInteger;

import forest.event.LogEvent;
import forest.event.LogEventHandler;
import forest.storage.EventStore;

public class LogEventStorageHandler implements LogEventHandler {
	
	private final AtomicInteger counter = new AtomicInteger();
	
	final EventStore store;

	public LogEventStorageHandler(EventStore store) {
		this.store = store;
	}

	@Override
	public void handle(LogEvent event) {
		int count = counter.incrementAndGet();
		if ((count % 5) == 0) throw new RuntimeException("Random test failure!");
		store.put(event);
	}

}
