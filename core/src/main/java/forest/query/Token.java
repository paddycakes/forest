package forest.query;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;


public class Token {
	
	private final TokenType type;
	private final Object value;

	public Token(TokenType type) {
		this(type, null);
	}
	
	public Token(TokenType type, Object value) {
		this.type = type;
		this.value = value;
	}
	
	public TokenType getType() {
		return type;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getStringValue() {
		return (String) value;
	}
	
	public double getDoubleValue() {
		return (Double) value;
	}
	
	public int getIntValue() {
		return (Integer) value;
	}

	public LocalDate getLocalDateValue() {
		return (LocalDate) value;
	}

	public LocalTime getLocalTimeValue() {
		return (LocalTime) value;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder("<");
		s.append(getClass().getSimpleName())
			.append(":")
			.append(type.name());
		if (value != null) {
			s.append(" '").append(value).append("'");
		}
		s.append(">");
		return s.toString();
	}

}
