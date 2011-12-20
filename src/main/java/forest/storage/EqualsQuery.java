package forest.storage;

import forest.event.Event;

public class EqualsQuery implements Query {

	private final String variable;
	private final Object value;

	public EqualsQuery(String variable, Object value) {
		this.variable = variable;
		this.value = value;
	}

	@Override
	public boolean matches(Event event) {
		Object eventValue = event.getParameter(variable);
		return eventValue != null && eventValue.equals(value);
	}

}
