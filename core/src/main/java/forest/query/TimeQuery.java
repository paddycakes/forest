package forest.query;

import org.joda.time.DateTime;

public class TimeQuery implements Query {

	private final DateTime time;
	private final Operator operator;

	public TimeQuery(DateTime time, Operator operator) {
		this.time = time;
		this.operator = operator;
	}
	
	public DateTime getTime() {
		return time;
	}
	
	public Operator getOperator() {
		return operator;
	}

	@Override
	public void accept(QueryVisitor evaluator) {
		evaluator.visit(this);
	}

}
