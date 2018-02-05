package app;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import model.Message;

public class FlowOrchestrator {

	private final int OCCURRENCE_LIMIT;
	private final ExecutorService WORKING_POOL;
	private volatile Map<Message, AtomicInteger> workingMap;
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public FlowOrchestrator(int timeLimit, int maxLimit) {
		this.OCCURRENCE_LIMIT = maxLimit;
		this.WORKING_POOL = Executors.newCachedThreadPool();
		this.workingMap = new ConcurrentHashMap<>();
		scheduler.scheduleAtFixedRate(this::cleanMap, timeLimit, timeLimit, TimeUnit.SECONDS);
	}

	private void cleanMap() {
		Map<Message, AtomicInteger> messageAtomicIntegerMap = new ConcurrentHashMap<>();
		this.workingMap = messageAtomicIntegerMap;
	}

	public void processMessage(Message message) {
		WORKING_POOL.execute(() -> processOrSkip(message));
	}

	private void processOrSkip(Message message) {
		if (!(workingMap.putIfAbsent(message, new AtomicInteger(1)) == null)) {
			if (!( workingMap.get(message).incrementAndGet() > OCCURRENCE_LIMIT)) {
				process();
			}
		} else {
			process();
		}
	}

	public Map<Message, AtomicInteger> getWorkingMap() {
		return workingMap;
	}

	private void process() {}

}
