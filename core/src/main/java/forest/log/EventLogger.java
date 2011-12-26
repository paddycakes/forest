package forest.log;

import static forest.log.EventLogger.LoggerParams.LOG_LEVEL;
import static forest.log.EventLogger.LoggerParams.LOG_NAME;
import static forest.log.EventLogger.LoggerParams.THREAD_NAME;
import static forest.log.EventLogger.LoggerParams.THROWABLE;
import static forest.log.LogLevel.DEBUG;
import static forest.log.LogLevel.ERROR;
import static forest.log.LogLevel.FATAL;
import static forest.log.LogLevel.INFO;
import static forest.log.LogLevel.WARN;

import java.util.HashMap;
import java.util.Map;

import forest.event.Event;
import forest.storage.EventStore;

public class EventLogger implements Logger {
	
	public static final class LoggerParams {
		public static final String LOG_LEVEL = "__logLevel";
		public static final String LOG_NAME = "__logName";
		public static final String THREAD_NAME = "__threadName";
		public static final String THROWABLE = "__throwable";
	}

	private final EventStore store;
	private final String name;

	public EventLogger(String name, EventStore store) {
		this.name = name;
		this.store = store;
	}

	@Override
	public void debug(String message, Object... parameters) {
		debug(message, null, parameters);
	}
	
	@Override
	public void debug(String message, Throwable t, Object... parameters) {
		log(DEBUG, message, t, parameters);
	}
	
	@Override
	public void info(String message, Object... parameters) {
		info(message, null, parameters);
	}
	
	@Override
	public void info(String message, Throwable t, Object... parameters) {
		log(INFO, message, t, parameters);
	}

	@Override
	public void warn(String message, Object... parameters) {
		warn(message, null, parameters);
	}
	
	@Override
	public void warn(String message, Throwable t, Object... parameters) {
		log(WARN, message, t, parameters);
	}

	@Override
	public void error(String message, Object... parameters) {
		error(message, null, parameters);
	}
	
	@Override
	public void error(String message, Throwable t, Object... parameters) {
		log(ERROR, message, t, parameters);
	}

	@Override
	public void fatal(String message, Object... parameters) {
		fatal(message, null, parameters);
	}
	
	@Override
	public void fatal(String message, Throwable t, Object... parameters) {
		log(FATAL, message, t, parameters);		
	}


	private void log(LogLevel level, String message, Throwable t, Object... parameters) {
		store.put(new Event(message, parameters, loggerParams(level, t)));
	}
	
	private Map<String, Object> loggerParams(LogLevel value, Throwable t) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(LOG_NAME, name);
		map.put(LOG_LEVEL, value);
		map.put(THREAD_NAME, Thread.currentThread().getName());
		map.put(THROWABLE, t);
		return map;
	}

}
