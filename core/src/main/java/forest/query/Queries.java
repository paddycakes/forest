package forest.query;

import org.joda.time.DateTime;

public class Queries {

	public static Query propertyEquals(String property, Object value) {
		return new PropertyEqualsQuery(property, value);
	}

	public static Query between(DateTime from, DateTime to) {
		return new TimeBetweenQuery(from, to);
	}
	
	public static Query and(Query... underlying) {
		return new AndQuery(underlying);
	}
	
	public static Query or(Query... underlying) {
		return new OrQuery(underlying);
	}

}
