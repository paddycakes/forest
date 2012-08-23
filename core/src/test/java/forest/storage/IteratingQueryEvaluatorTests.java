package forest.storage;

import static forest.query.Operator.EQUAL;
import static forest.query.Operator.GREATER_THAN;
import static forest.query.Operator.GREATER_THAN_EQUAL;
import static forest.query.Operator.LESS_THAN;
import static forest.query.Operator.LESS_THAN_EQUAL;
import static org.joda.time.DateTimeUtils.setCurrentMillisSystem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Test;

import forest.event.LogEvent;
import forest.query.AndQuery;
import forest.query.Operator;
import forest.query.OrQuery;
import forest.query.ParameterQuery;
import forest.query.TimeQuery;


public class IteratingQueryEvaluatorTests {
	
	private IteratingQueryEvaluator queryEvaluator;
	
	@Test
	public void paramQueryLessThan() {
		Iterator<LogEvent> events = paramQuery("widgetId", LESS_THAN, 3);
		
		assertEquals("Making widget 1", events.next().getMessage());
		assertEquals("Editing widget 1", events.next().getMessage());
		assertEquals("Making widget 2", events.next().getMessage());
		assertEquals("Editing widget 2", events.next().getMessage());
		assertFalse(events.hasNext());
		
	}
	
	@Test
	public void paramQueryLessThanEqual() {
		Iterator<LogEvent> events = paramQuery("widgetId", LESS_THAN_EQUAL, 2);
		
		assertEquals("Making widget 1", events.next().getMessage());
		assertEquals("Editing widget 1", events.next().getMessage());
		assertEquals("Making widget 2", events.next().getMessage());
		assertEquals("Editing widget 2", events.next().getMessage());
		assertFalse(events.hasNext());
		
	}
	
	@Test
	public void paramQueryEqual() {
		Iterator<LogEvent> events = paramQuery("widgetId", EQUAL, 3);
		
		assertEquals("Making widget 3", events.next().getMessage());
		assertEquals("Editing widget 3", events.next().getMessage());
		assertFalse(events.hasNext());
		
	}
	
	@Test
	public void paramQueryGreaterThanEqual() {
		Iterator<LogEvent> events = paramQuery("widgetId", GREATER_THAN_EQUAL, 9);
		
		assertEquals("Making widget 9", events.next().getMessage());
		assertEquals("Editing widget 9", events.next().getMessage());
		assertEquals("Making widget 10", events.next().getMessage());
		assertEquals("Editing widget 10", events.next().getMessage());
		assertFalse(events.hasNext());
		
	}
	
	@Test
	public void paramQueryGreaterThan() {
		Iterator<LogEvent> events = paramQuery("widgetId", GREATER_THAN, 8);
		
		assertEquals("Making widget 9", events.next().getMessage());
		assertEquals("Editing widget 9", events.next().getMessage());
		assertEquals("Making widget 10", events.next().getMessage());
		assertEquals("Editing widget 10", events.next().getMessage());
		assertFalse(events.hasNext());
		
	}
	
	@Test
	public void timeQueryLessThan() {
		Iterator<LogEvent> events = timeQuery(LESS_THAN, 12);
		
		assertEquals("It's 10:00", events.next().getMessage());
		assertEquals("It's 11:00", events.next().getMessage());
		assertFalse(events.hasNext());		
	}
	
	@Test
	public void timeQueryLessThanEqual() {
		Iterator<LogEvent> events = timeQuery(LESS_THAN_EQUAL, 11);
		
		assertEquals("It's 10:00", events.next().getMessage());
		assertEquals("It's 11:00", events.next().getMessage());
		assertFalse(events.hasNext());		
	}
	
	@Test
	public void timeQueryEqual() {
		Iterator<LogEvent> events = timeQuery(EQUAL, 14);
		
		assertEquals("It's 14:00", events.next().getMessage());
		assertFalse(events.hasNext());
	}
	
	@Test
	public void timeQueryGreaterThanEqual() {
		Iterator<LogEvent> events = timeQuery(GREATER_THAN_EQUAL, 19);
		
		assertEquals("It's 19:00", events.next().getMessage());
		assertEquals("It's 20:00", events.next().getMessage());
		assertFalse(events.hasNext());
	}
	
	@Test
	public void timeQueryGreaterThan() {
		Iterator<LogEvent> events = timeQuery(GREATER_THAN, 18);
		
		assertEquals("It's 19:00", events.next().getMessage());
		assertEquals("It's 20:00", events.next().getMessage());
		assertFalse(events.hasNext());		
	}
	
	@Test
	public void andQuery() {
		Iterator<LogEvent> events = orParamsEqualQuery("widgetId", 2, 7);
		
		assertEquals("Making widget 2", events.next().getMessage());
		assertEquals("Editing widget 2", events.next().getMessage());
		assertEquals("Making widget 7", events.next().getMessage());
		assertEquals("Editing widget 7", events.next().getMessage());
		assertFalse(events.hasNext());
		
	}
	
	@Test
	public void orQuery() {
		Iterator<LogEvent> events = timeBetweenAndQuery(13, 15);
		
		assertEquals("It's 13:00", events.next().getMessage());
		assertEquals("It's 14:00", events.next().getMessage());
		assertEquals("It's 15:00", events.next().getMessage());
		assertFalse(events.hasNext());
		
	}
	
	
	/* --- Private helpers --- */

	private Iterator<LogEvent> timeQuery(Operator operator, int queryHours) {
		ArrayList<LogEvent> events = timeEvents();
		queryEvaluator = new IteratingQueryEvaluator(events.iterator());
		queryEvaluator.visit(new TimeQuery(todayAt(queryHours), operator));
		return queryEvaluator.iterator();
	}

	private Iterator<LogEvent> timeBetweenAndQuery(int fromHours, int toHours) {
		ArrayList<LogEvent> events = timeEvents();
		queryEvaluator = new IteratingQueryEvaluator(events.iterator());
		TimeQuery fromQuery = new TimeQuery(todayAt(fromHours), GREATER_THAN_EQUAL);
		TimeQuery toQuery = new TimeQuery(todayAt(toHours), LESS_THAN_EQUAL);
		AndQuery andQuery = new AndQuery(fromQuery , toQuery);
		queryEvaluator.visit(andQuery);
		return queryEvaluator.iterator();
	}

	private ArrayList<LogEvent> timeEvents() {
		ArrayList<LogEvent> events = new ArrayList<LogEvent>();
		for (int eventHours = 10; eventHours <= 20; eventHours++) {
			DateTimeUtils.setCurrentMillisFixed(todayAt(eventHours).getMillis());
			events.add(new LogEvent("It's " + eventHours + ":00"));
		}
		setCurrentMillisSystem();
		return events;
	}

	private DateTime todayAt(int hoursOfDay) {
		return new DateTime().withTime(hoursOfDay, 0, 0, 0);
	}

	private Iterator<LogEvent> paramQuery(String paramName, Operator operator, int paramValue) {
		ArrayList<LogEvent> events = paramEvents();
		queryEvaluator = new IteratingQueryEvaluator(events.iterator());		
		queryEvaluator.visit(new ParameterQuery(paramName, paramValue, operator));
		return queryEvaluator.iterator();
	}

	private Iterator<LogEvent> orParamsEqualQuery(String paramName, int firstValue, int secondValue) {
		ArrayList<LogEvent> events = paramEvents();
		queryEvaluator = new IteratingQueryEvaluator(events.iterator());
		ParameterQuery firstUnderlying = new ParameterQuery(paramName, firstValue, EQUAL);
		ParameterQuery secondUnderlying = new ParameterQuery(paramName, secondValue, EQUAL);
		OrQuery orQuery = new OrQuery(firstUnderlying, secondUnderlying);
		queryEvaluator.visit(orQuery);
		return queryEvaluator.iterator();
	}	

	private ArrayList<LogEvent> paramEvents() {
		ArrayList<LogEvent> events = new ArrayList<LogEvent>();
		for (int i = 1; i <= 10; i++) {
			events.add(new LogEvent("Making widget $widgetId", i));
			events.add(new LogEvent("Editing widget $widgetId", i));
		}
		return events;
	}

}
