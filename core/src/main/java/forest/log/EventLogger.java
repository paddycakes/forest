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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SingleThreadedClaimStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;

import forest.event.LogEvent;
import forest.event.LogEventHandler;

public class EventLogger implements Logger {

	public static final class LoggerParams {
		public static final String LOG_LEVEL = "__logLevel";
		public static final String LOG_NAME = "__logName";
		public static final String THREAD_NAME = "__threadName";
		public static final String THROWABLE = "__throwable";
	}

	private final String logName;
	private final RingBuffer<DisruptorBucket> ringBuffer;

	public EventLogger(String logName, LogEventHandler... logEventHandlers) {
		this.logName = logName;
		Executor executor = Executors.newFixedThreadPool(10);
		int ringSize = 64;
		Disruptor<DisruptorBucket> disruptor = new Disruptor<DisruptorBucket>(
				new DisruptorBucketFactory(), executor,
				new SingleThreadedClaimStrategy(ringSize),
				new SleepingWaitStrategy());
		ExceptionHandler exceptionHandler = new ExceptionHandler() {
			
			@Override
			public void handleOnStartException(Throwable ex) {
			}
			
			@Override
			public void handleOnShutdownException(Throwable ex) {
			}
			
			@Override
			public void handleEventException(Throwable ex, long sequence, Object event) {
				DisruptorBucket bucket = (DisruptorBucket) event;
				lo
			}
		};
		disruptor.handleExceptionsWith(exceptionHandler);
		DisruptorEventHandler log4jEventHandler = new DisruptorEventHandler(logEventHandlers[1]);
		disruptor.handleExceptionsFor(log4jEventHandler).with(exceptionHandler)
		disruptor.handleEventsWith(new DisruptorEventHandler(logEventHandlers[0]))
			.then(log4jEventHandler);
		ringBuffer = disruptor.start();
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
		log(new LogEvent(message, parameters, loggerParams(level, t)));
	}

	private Map<String, Object> loggerParams(LogLevel value, Throwable t) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(LOG_NAME, logName);
		map.put(LOG_LEVEL, value);
		map.put(THREAD_NAME, Thread.currentThread().getName());
		map.put(THROWABLE, t);
		return map;
	}

	@Override
	public void log(LogEvent event) {
		// TODO: Add to Disruptor queue and send to underlyingLogger.
		// TODO: Refactor to register log event listener. Guava event bus?
		// http://code.google.com/p/guava-libraries/wiki/EventBusExplained
		long sequence = ringBuffer.next();
		DisruptorBucket disruptorBucket = ringBuffer.get(sequence);
		disruptorBucket.setEvent(event); 
		ringBuffer.publish(sequence);  
	}

	private static class DisruptorEventHandler implements EventHandler<DisruptorBucket> {
		
		private final LogEventHandler logEventHandler;

		public DisruptorEventHandler(LogEventHandler logEventHandler) {
			this.logEventHandler = logEventHandler;
		}
		
		@Override
		public void onEvent(DisruptorBucket bucket, long sequence, boolean endOfBatch) throws Exception {
			// TODO: Handle batching here?
			logEventHandler.handle(bucket.getEvent());
		}

	}

	private static class DisruptorBucket {

		private LogEvent event;

		public LogEvent getEvent() {
			return event;
		}

		public void setEvent(LogEvent event) {
			this.event = event;
		}

	}

	private static class DisruptorBucketFactory implements
			EventFactory<DisruptorBucket> {

		@Override
		public DisruptorBucket newInstance() {
			return new DisruptorBucket();
		}

	}
}
