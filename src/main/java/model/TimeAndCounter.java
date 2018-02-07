package model;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeAndCounter {
	private volatile Instant currentModification;
	private volatile Instant previousModocifation;
	private volatile AtomicInteger atomicInteger;

	public TimeAndCounter(){
		this.currentModification = Instant.now();
		this.atomicInteger = new AtomicInteger(1);
	}

	public AtomicInteger getAtomicInteger() {
		return atomicInteger;
	}

	public Instant getPreviousModification() {
		return previousModocifation;
	}

	public synchronized TimeAndCounter updateTimeAndCounter(){
		this.previousModocifation = this.currentModification;
		this.currentModification = Instant.now();
		this.atomicInteger.incrementAndGet();
		return this;
	}

}
