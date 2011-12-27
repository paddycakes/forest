package forest.query;


public interface QueryVisitor {

	void visit(ParameterQuery query);

	void visit(TimeQuery query);

	void visit(AndQuery query);

	void visit(OrQuery qery);
	
}
