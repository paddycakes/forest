package forest.webconsole;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ForestSession {
	
	private static final String DEFAULT_PATTERN = "%d [%t] %-5p %c %x - %m%n";
	
	private static final Logger log = LogManager.getLogger(ForestSession.class);
	
	private long id;
	private String pattern;
	
	public ForestSession(long id) {
		log.info(String.format("Creating new Forest session with id %s.", id));
		this.id = id;
		pattern = DEFAULT_PATTERN;
	}
	
	public long getId() {
		return id;
	}
	
	public String getPattern() {
		return pattern;
	}

}
