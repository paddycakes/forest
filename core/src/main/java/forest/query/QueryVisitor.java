package forest.query;


public interface QueryVisitor {

	void visit(PropertyEqualsQuery query);

	void visit(TimeBetweenQuery query);

	void visit(CompositeQuery query);

	void visit(OrQuery qery);
	
}
