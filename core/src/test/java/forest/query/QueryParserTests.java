package forest.query;

import static forest.query.Operator.GREATER_THAN_EQUAL;
import static forest.query.Operator.LESS_THAN_EQUAL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

/*
 * from=25/12/2011-12:13:56.135 and to=12:34:23.532 and prop:widgetId=23 
 */
public class QueryParserTests {

	@Test
	public void propertyEqualsIntegerQuery() {
		Query query = new QueryParser("prop:widgetId = 12").query();
		assertPropertyEqualsQuery("widgetId", 12, query);
	}
	
	@Test
	public void propertyEqualsStringQuery() {
		Query query = new QueryParser("prop:name = 'yup'").query();
		assertPropertyEqualsQuery("name", "yup", query);
	}
	
	@Test
	public void propertyEqualsDoubleQuery() {
		Query query = new QueryParser("prop:price = 12.3").query();
		assertPropertyEqualsQuery("price", 12.3, query);
	}
	
	@Test
	public void fromDateTime() {
		Query query = new QueryParser("from=25/12/2011-14:34:51.567").query();
		assertTimeQuery(new DateTime(2011, 12, 25, 14, 34, 51, 567), GREATER_THAN_EQUAL, query);
	}
	
	@Test
	public void toDateTime() {
		Query query = new QueryParser("to=2/1/2012-7:03").query();
		assertTimeQuery(new DateTime(2012, 1, 2, 7, 3, 0, 0), LESS_THAN_EQUAL, query);
	}
	
	@Test
	public void and() {
		Query query = new QueryParser("prop:widgetId = 12 and prop:name = 'dude'").query();
		
		assertEquals(AndQuery.class, query.getClass());
		AndQuery andQuery = (AndQuery) query;
		List<Query> underlying = andQuery.getUnderlying();
		assertEquals(2, underlying.size());
		assertPropertyEqualsQuery("widgetId", 12, underlying.get(0));
		assertPropertyEqualsQuery("name", "dude", underlying.get(1));
	}
	
	
	/* --- Private helpers --- */
	
	private void assertPropertyEqualsQuery(String expectedName, Object expectedValue, Query actual) {
		assertEquals(ParameterQuery.class, actual.getClass());
        ParameterQuery propQuery = (ParameterQuery) actual;
        assertEquals(expectedName, propQuery.getParamName());
        assertTrue(expectedValue.getClass().isAssignableFrom(propQuery.getValue().getClass()));
        assertEquals(expectedValue, propQuery.getValue());
	}

	private void assertTimeQuery(DateTime expectedDateTime, Operator expectedOperator, Query actual) {
		assertEquals(TimeQuery.class, actual.getClass());
		TimeQuery timeQuery = (TimeQuery) actual;
		assertEquals(expectedDateTime, timeQuery.getTime());
		assertEquals(expectedOperator, timeQuery.getOperator());
	}

}
