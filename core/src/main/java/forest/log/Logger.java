package forest.log;

import forest.event.LogEvent;


public interface Logger {
	
	void log(LogEvent event);
	
	void debug(String message, Object... parameters);
	
	void debug(String message, Throwable t, Object... parameters);
	
	void info(String message, Object... parameters);
	
	void info(String message, Throwable t, Object... parameters);

	void warn(String message, Object... parameters);
	
	void warn(String message, Throwable t, Object... parameters);
	
	void error(String message, Object... parameters);
	
	void error(String message, Throwable t, Object... parameters);
	
	void fatal(String message, Object... parameters);
	
	void fatal(String message, Throwable t, Object... parameters);

}
