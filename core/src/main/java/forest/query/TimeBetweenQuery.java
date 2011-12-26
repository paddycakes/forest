package forest.query;

import org.joda.time.DateTime;

public class TimeBetweenQuery implements Query {

	private final DateTime from;
	private final DateTime to;

	public TimeBetweenQuery(DateTime from, DateTime to) {
		this.from = from;
		this.to = to;
	}
	
	public DateTime getFrom() {
		return from;
	}
	
	public DateTime getTo() {
		return to;
	}

	@Override
	public void accept(QueryVisitor evaluator) {
		evaluator.visit(this);
	}

}
