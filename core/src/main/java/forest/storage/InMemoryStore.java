package forest.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import forest.event.Event;

public class InMemoryStore implements Store {
	
	private List<Event> events = new ArrayList<Event>();
	
	@Override
	public void put(Event event) {
		events.add(event);
	}

	@Override
	public Iterator<Event> events() {
		return events.iterator();
	}

	@Override
	public Iterator<Event> events(Query query) {
		return new EventIterator(query);
	}
	
	private class EventIterator implements Iterator<Event> {
		
		private final Query query;
		private int i = 0;
		private Event next;
		private boolean finished;
		
		public EventIterator(Query query) {
			this.query = query;
		}
		
		@Override
		public boolean hasNext() {
			if (isFinished()) return false; // Optimisation?
			prepareNext();
			if (isFinished()) return false;
			return getNext() != null;
		}

		@Override
		public Event next() {
			prepareNext();
			return getNextforReturn();
		}

		private synchronized Event getNextforReturn() {
			Event nextForReturn = getNext();
			next = null;
			return nextForReturn;
		}

		private synchronized void prepareNext() {
			if (getNext() != null) return; // Next is already prepared.
			while (i < events.size()) {
				Event nextCandidate = events.get(i);
				i++;
				if (query.matches(nextCandidate)) {
					next = nextCandidate;
					return;
				}
			}
			finished = true;
		}

		private synchronized boolean isFinished() {
			return finished;
		}

		private synchronized Event getNext() {
			return next;
		}

		@Override
		public void remove() {
			throw new RuntimeException("Cannot remove events through this iterator.");
		}
		
	}

}
