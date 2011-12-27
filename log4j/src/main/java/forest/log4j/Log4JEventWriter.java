package forest.log4j;

import static forest.log.EventLogger.LoggerParams.LOG_LEVEL;
import static forest.log.EventLogger.LoggerParams.LOG_NAME;
import static forest.log.EventLogger.LoggerParams.THREAD_NAME;
import static forest.log.EventLogger.LoggerParams.THROWABLE;

import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import forest.event.Event;
import forest.event.EventWriter;
import forest.log.LogLevel;

public class Log4JEventWriter implements EventWriter {
	
	private static final String FQCN = Category.class.getName();

	@Override
	public void write(Event event) {
		Logger logger = Logger.getLogger(event.getParameter(LOG_NAME).toString());
		// ? logger.setLevel(log4JLevel); // Ensure log level matches? Reset after?
		Level log4JLevel = getLevel(event);
		// Inlined this call, so that timestamp can be set: logger.log(log4JLevel, event.getMessage()); 
	    if(logger.getLoggerRepository().isDisabled(log4JLevel.toInt())) {
	      return;
	    } else if(log4JLevel.isGreaterOrEqual(logger.getEffectiveLevel())) {
	    	// ? Log event.getMessage() or event with an EventRenderer (implements ObjectRender) ?
			logger.callAppenders(buildLog4jEvent(event));
	    }
	}

	public static LoggingEvent buildLog4jEvent(Event event) {
		Logger logger = Logger.getLogger(event.getParameter(LOG_NAME).toString());
		Level log4JLevel = getLevel(event);
		Throwable throwable = (Throwable) event.getParameter(THROWABLE);
		return new LoggingEvent(FQCN, logger, event.getTime().getMillis(),
				log4JLevel, event.getMessage(), (String) event.getParameter(THREAD_NAME),
				new ThrowableInformation(throwable, logger), null, null, null);
	}

	private static Level getLevel(Event event) {
		LogLevel level = (LogLevel) event.getParameter(LOG_LEVEL);
		switch (level) {
		case DEBUG: return Level.DEBUG;
		case INFO: return Level.INFO;
		case WARN: return Level.WARN;
		case ERROR: return Level.ERROR;
		case FATAL: return Level.FATAL;
		default: throw new RuntimeException("No logger found for forest log level: " + level);
		}
	}

}
