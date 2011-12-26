package forest.storage;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.or;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import forest.event.Event;
import forest.query.CompositeQuery;
import forest.query.OrQuery;
import forest.query.PropertyEqualsQuery;
import forest.query.Query;
import forest.query.QueryVisitor;
import forest.query.TimeBetweenQuery;

public class IteratingQueryEvaluator implements QueryVisitor, Iterable<Event> {
	
	private final Iterator<Event> allEvents;
	private final Stack<Predicate<Event>> filters = new Stack<Predicate<Event>>();

	public IteratingQueryEvaluator(Iterator<Event> allEvents) {
		this.allEvents = allEvents;
	}
	
	@Override
	public Iterator<Event> iterator() {
		checkForSingleFilter();
		return Iterators.filter(allEvents, filters.pop());
	}
	
	@Override
	public void visit(final PropertyEqualsQuery query) {
		filters.push(new Predicate<Event>() {
			@Override
			public boolean apply(Event event) {
				Object value = event.getParameter(query.getProperty());
				return value != null && value.equals(query.getValue());
			}
		});
	}
	
	@Override
	public void visit(final TimeBetweenQuery query) {
		filters.push(new Predicate<Event>() {
			@Override
			public boolean apply(Event event) {
				return event.getTime().isAfter(query.getFrom())
						&& event.getTime().isBefore(query.getTo());
			}
		});
	}
	
	@Override
	public void visit(CompositeQuery query) {
		Predicate<Event> andPredicate = and(underlyingFilters(query));
		filters.push(andPredicate);
	}
	
	@Override
	public void visit(OrQuery query) {
		Predicate<Event> orPredicate = or(underlyingFilters(query));
		filters.push(orPredicate);
	}

	private List<Predicate<Event>> underlyingFilters(CompositeQuery query) {
		List<Predicate<Event>> underlyingFilters = new ArrayList<Predicate<Event>>();
		for (Query subQuery : query.getUnderlying()) {
			subQuery.accept(this);
			underlyingFilters.add(filters.pop());
		}
		return underlyingFilters;
	}

	private void checkForSingleFilter() {
		if (filters.size() != 1) {
			throw new RuntimeException("There should only be 1 filter, but found " 
					+ filters.size() + ". Ensure you called a visit method exactly once.");
		}
	}

}
