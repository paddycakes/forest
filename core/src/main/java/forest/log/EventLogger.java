package forest.log;

import static forest.log.LogLevel.DEBUG;
import static forest.log.LogLevel.ERROR;
import static forest.log.LogLevel.FATAL;
import static forest.log.LogLevel.INFO;
import static forest.log.LogLevel.WARN;

import java.util.HashMap;
import java.util.Map;

import forest.event.Event;
import forest.storage.Store;

public class EventLogger implements Logger {

	public static final String LOG_NAME = "__logName";
	public static final String LOG_LEVEL = "__logLevel";
	
	private final Store store;
	private final String name;

	public EventLogger(String name, Store store) {
		this.name = name;
		this.store = store;
	}

	@Override
	public void debug(String message, Object... parameters) {
		log(DEBUG, message, parameters);
	}
	
	@Override
	public void info(String message, Object... parameters) {
		log(INFO, message, parameters);
	}

	@Override
	public void warn(String message, Object... parameters) {
		log(WARN, message, parameters);
	}

	@Override
	public void error(String message, Object... parameters) {
		log(ERROR, message, parameters);
	}

	@Override
	public void fatal(String message, Object... parameters) {
		log(FATAL, message, parameters);
	}


	private void log(LogLevel level, String message, Object... parameters) {
		store.put(new Event(message, parameters, loggerParams(level)));
	}
	
	private Map<String, Object> loggerParams(LogLevel value) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(LOG_LEVEL, value);
		map.put(LOG_NAME, name);
		return map;
	}

}
