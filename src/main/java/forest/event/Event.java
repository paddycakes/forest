package forest.event;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Event {
	
	private static final Pattern variablePattern = Pattern.compile("\\$\\w+");
	
	private final String message;
	private Map<String, Object> parameters;

	public Event(String message, Object... parameters) {
		this.parameters = new HashMap<String, Object>();
		String substituedMessage = message;
		if (parameters.length > 0) {
			Matcher matcher = variablePattern.matcher(message);
			for (int i = 0; matcher.find(); i++) {
				String variable = matcher.group();
				Object value = parameters[i];
				this.parameters.put(variable.substring(1), value); // variable.substring(1) removes the $
				substituedMessage = substituedMessage.replace(variable, value.toString());
			}
		}
		this.message = substituedMessage;
		
	}

	public String getMessage() {
		return message;
	}
	
	public Object getParameter(String variable) {
		return parameters.get(variable);
	}
	
}