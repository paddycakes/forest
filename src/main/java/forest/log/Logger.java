package forest.log;

public interface Logger {
	
	void error(String message, Object... parameters);
	
	/**
	 * log.info("Editing widget $widgetName (id $widgetId", "Thingimibob", 332);
	 * @param message
	 * @param parameters
	 */
	void info(String message, Object... parameters);

}
