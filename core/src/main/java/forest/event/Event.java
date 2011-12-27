package forest.event;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public class Event implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Pattern variablePattern = Pattern.compile("\\$\\w+");
	
	private final DateTime time = new DateTime();
	private String message;
	private Map<String, Object> parameters;
	
	public Event() {
	}
	
	public Event(String message, Object[] parameters, Map<String, Object> additionalParameters) {
		this.parameters = additionalParameters != null 
				? new HashMap<String, Object>(additionalParameters) 
				: new HashMap<String, Object>();
		String substitutedMessage = message;
		if (parameters.length > 0) {
			Matcher matcher = variablePattern.matcher(message);
			for (int i = 0; matcher.find(); i++) {
				String variable = matcher.group();
				Object value = parameters[i];
				this.parameters.put(variable.substring(1), value); // variable.substring(1) removes the $
				substitutedMessage = substitutedMessage.replace(variable, value.toString());
			}
		}
		this.message = substitutedMessage;
	}

	public Event(String message, Object... parameters) {
		this(message, parameters, null);
	}

	public String getMessage() {
		return message;
	}
	
	public Object getParameter(String variable) {
		return parameters.get(variable);
	}

	public DateTime getTime() {
		return time;
	}
	
}