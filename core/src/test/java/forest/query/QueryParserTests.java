package forest.query;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class QueryParserTests {

	@Test
	public void propertyEqualsQuery() {
		Query query = new QueryParser("prop:widgetId = 12").query();
		
		assertTrue(query instanceof PropertyEqualsQuery);
        PropertyEqualsQuery propQuery = (PropertyEqualsQuery) query;
        assertEquals("widgetId", propQuery.getProperty());
        assertTrue(propQuery.getValue() instanceof Integer);
        assertEquals(12, propQuery.getValue());
	}

}
