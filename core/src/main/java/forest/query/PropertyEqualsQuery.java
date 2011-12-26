package forest.query;



public class PropertyEqualsQuery implements Query {

	private final String property;
	private final Object value;

	public PropertyEqualsQuery(String property, Object value) {
		this.property = property;
		this.value = value;
	}
	
	public String getProperty() {
		return property;
	}
	
	public Object getValue() {
		return value;
	}
	
	@Override
	public void accept(QueryVisitor evaluator) {
		evaluator.visit(this);
	}

}
