package forest.query;



public class AndQuery extends CompositeQuery {
	
	public AndQuery(Query... underlying) {
		super(underlying);
	}
	
	@Override
	public void accept(QueryVisitor evaluator) {
		evaluator.visit(this);
	}

}
