package forest.log;


public interface Logger {
	
	void debug(String message, Object... parameters);
	
	void info(String message, Object... parameters);

	void warn(String message, Object... parameters);
	
	void error(String message, Object... parameters);
	
	void fatal(String message, Object... parameters);

}
