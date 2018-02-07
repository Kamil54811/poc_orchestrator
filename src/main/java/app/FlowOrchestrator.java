package app;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import model.Message;
import model.TimeAndCounter;

public class FlowOrchestrator {

	private final int OCCURRENCE_LIMIT;
	private final int TIME_LIMIT;
	private final ExecutorService WORKING_POOL;
	private volatile ConcurrentMap<String, AtomicReference<TimeAndCounter>> map;

	public FlowOrchestrator(int timeLimit, int maxLimit) {
		this.OCCURRENCE_LIMIT = maxLimit;
		this.TIME_LIMIT = timeLimit;
		this.WORKING_POOL = Executors.newCachedThreadPool();
		this.map = new ConcurrentHashMap<>();
	}

	public void processMessage(Message message) {
		WORKING_POOL.execute(() -> processOrSkip(message));
	}

	private void processOrSkip(Message message){
		Optional<AtomicReference<TimeAndCounter>> occurrences = Optional
				.ofNullable(map.computeIfPresent(
						message.getMapKey(),
						(s, timeAndCounterAtomicReference) ->
								new AtomicReference<>(timeAndCounterAtomicReference.updateAndGet(TimeAndCounter::updateTimeAndCounter))));

		if (occurrences.isPresent() && occurrences.get().get().getAtomicInteger().get() > OCCURRENCE_LIMIT) {
			Instant lastModification = occurrences.get().get().getPreviousModification();
			Instant now = Instant.now();
			if (Duration.between(lastModification,now).getSeconds() > TIME_LIMIT){
				replaceAndProcess(message);
			} else {
				reject(message);
			}
		} else {
			map.putIfAbsent(message.getMapKey(), new AtomicReference<>(new TimeAndCounter()) );
			process(message);
		}
	}

	private void reject(Message message) { }

	private void replaceAndProcess(Message message) {
		System.out.println("Reset for " + message.getMapKey());
		map.replace(message.getMapKey(), new AtomicReference<>(new TimeAndCounter()));
		process(message);
	}

	private void process(Message message) {}

	public ConcurrentMap<String, AtomicReference<TimeAndCounter>> getMap() {
		return map;
	}
}
