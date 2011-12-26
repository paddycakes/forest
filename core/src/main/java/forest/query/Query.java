package forest.query;



public interface Query {

	void accept(QueryVisitor visitor);

}
