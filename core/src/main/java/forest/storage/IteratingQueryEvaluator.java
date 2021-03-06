package forest.storage;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;

import forest.event.Event;
import forest.query.AndQuery;
import forest.query.CompositeQuery;
import forest.query.Operator;
import forest.query.OrQuery;
import forest.query.ParameterQuery;
import forest.query.Query;
import forest.query.QueryVisitor;
import forest.query.TimeQuery;

public class IteratingQueryEvaluator implements QueryVisitor, Iterable<Event> {
	
	private static final Logger log = LogManager.getLogger(IteratingQueryEvaluator.class);
	
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
	public void visit(final ParameterQuery query) {
		filters.push(new Predicate<Event>() {
			@Override
			public boolean apply(Event event) {
				Object value = event.getParameter(query.getParamName());
				if (value == null) return false;
				if (query.getOperator() == Operator.EQUAL) {
					return value.equals(query.getValue());
				}
				@SuppressWarnings("rawtypes")
				Comparable valueAsComparable = (Comparable) value;
				@SuppressWarnings("unchecked")
				int comparison = valueAsComparable.compareTo(query.getValue());
				switch (query.getOperator()) {
				case LESS_THAN:
					return comparison < 0;
				case LESS_THAN_EQUAL:
					return comparison <= 0;					
				case GREATER_THAN_EQUAL:
					return comparison >= 0;					
				case GREATER_THAN:
					return comparison > 0;					
				default:
					throw new RuntimeException("Unexpected operator: " + query.getOperator());
				}
			}
		});
	}
	
	@Override
	public void visit(final TimeQuery query) {
		filters.push(new Predicate<Event>() {
			@Override
			public boolean apply(Event event) {
				switch (query.getOperator()) {
				case LESS_THAN:
					return event.getTime().isBefore(query.getTime());
				case LESS_THAN_EQUAL:
					log.debug(format("LESS_THAN_EQUAL - Event time: %s // Query time: %s // returning: %s.", 
							event.getTime(), query.getTime(), 
							event.getTime().isBefore(query.getTime())
								|| event.getTime().isEqual(query.getTime())));
					return event.getTime().isBefore(query.getTime())
							|| event.getTime().isEqual(query.getTime());
				case EQUAL:
					return event.getTime().equals(query.getTime());
				case GREATER_THAN:
					return event.getTime().isAfter(query.getTime());
				case GREATER_THAN_EQUAL:
					log.debug(format("GREATER_THAN_EQUAL - Event time: %s // Query time: %s // returning: %s.", 
							event.getTime(), query.getTime(), 
							event.getTime().isAfter(query.getTime())
								|| event.getTime().isEqual(query.getTime())));
					return event.getTime().isAfter(query.getTime())
							|| event.getTime().isEqual(query.getTime());
				default:
					throw new RuntimeException("Unexpected operator: " + query.getOperator());
				}
			}
		});
	}
	
	@Override
	public void visit(AndQuery query) {
		Predicate<Event> andPredicate = Predicates.and(underlyingFilters(query));
		filters.push(andPredicate);
	}
	
	@Override
	public void visit(OrQuery query) {
		Predicate<Event> orPredicate = Predicates.or(underlyingFilters(query));
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
