package forest.storage;

import static java.util.Collections.synchronizedList;
import static java.util.Collections.unmodifiableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import forest.event.LogEvent;
import forest.query.Query;

public class SerializingEventsListStore implements EventStore {
	
	private static final Logger log = LogManager.getLogger(SerializingEventsListStore.class);
	
	private List<LogEvent> events = synchronizedList(new ArrayList<LogEvent>());
	private final File file;
	private Collection<LogEventListener> eventListeners = synchronizedList(new ArrayList<LogEventListener>());
	
	public SerializingEventsListStore() {
		this(null);
	}

	public SerializingEventsListStore(File file) {
		this.file = file;
	}

	@Override
	public void put(LogEvent event) {
		events.add(event);
		notifyEventListeners(event);
//		save();
	}

	private Iterator<LogEvent> allEvents() {
		synchronized (events) {
			return unmodifiableList(new ArrayList<LogEvent>(events)).iterator();
		}
	}

	@Override
	public Iterable<LogEvent> events(Query query) {
		if (query == null) {
			// TODO: Replace with a required 'all' query...?
			return new Iterable<LogEvent>() {
				@Override
				public Iterator<LogEvent> iterator() {
					return allEvents();
				}
			};
		}
		IteratingQueryEvaluator iterable = new IteratingQueryEvaluator(allEvents());
		query.accept(iterable);
		return iterable;
	}
	
	@Override
	public void addEventListener(LogEventListener listener) {
		eventListeners.add(listener);
	}
	
	
	/* --- Private --- */

	private void notifyEventListeners(LogEvent event) {
		for (LogEventListener listener : eventListeners) {
			listener.newLogEvent(event);
		}
	}

//	private void save() {
//		if (file == null) return;
//		
//		FileOutputStream fileOutputStream = null;
//		ObjectOutputStream objectOutputStream = null;
//		try {
//			fileOutputStream = new FileOutputStream(file);
//			objectOutputStream = new ObjectOutputStream(fileOutputStream);
//			objectOutputStream.writeObject(events);
//		} catch (IOException e) {
//			log.error("Error saving events:", e);
//		} finally {
//			try {
//				if (fileOutputStream != null) fileOutputStream.close();
//				if (objectOutputStream != null) objectOutputStream.close();
//			} catch (IOException e) {
//				log.error("Error closing output streams while saving events:", e);
//			}
//		}
//	}

}
