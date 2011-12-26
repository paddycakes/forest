package forest.query;



public class OrQuery extends CompositeQuery {
	
	public OrQuery(Query... underlying) {
		super(underlying);
	}

	@Override
	public void accept(QueryVisitor evaluator) {
		evaluator.visit(this);
	}

}
