package forest.query;

import static forest.query.Operator.EQUAL;
import static forest.query.Operator.GREATER_THAN_EQUAL;
import static forest.query.Operator.LESS_THAN_EQUAL;

import org.joda.time.DateTime;

public class Queries {

	public static Query propertyEquals(String property, Object value) {
		return new ParameterQuery(property, value, EQUAL);
	}

	public static Query between(DateTime from, DateTime to) {
		return and(new TimeQuery(from, GREATER_THAN_EQUAL), new TimeQuery(to, LESS_THAN_EQUAL));
	}
	
	public static Query and(Query... underlying) {
		return new AndQuery(underlying);
	}
	
	public static Query or(Query... underlying) {
		return new OrQuery(underlying);
	}

}
