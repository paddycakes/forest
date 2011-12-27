package forest.query;




public class ParameterQuery implements Query {

	private final String paramName;
	private final Object value;
	private final Operator operator;

	public ParameterQuery(String paramName, Object value, Operator operator) {
		this.paramName = paramName;
		this.value = value;
		this.operator = operator;
	}
	
	public String getParamName() {
		return paramName;
	}
	
	public Object getValue() {
		return value;
	}
	
	public Operator getOperator() {
		return operator;
	}
	
	@Override
	public void accept(QueryVisitor evaluator) {
		evaluator.visit(this);
	}

}
