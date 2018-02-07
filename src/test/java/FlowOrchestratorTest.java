
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import app.FlowOrchestrator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import model.Message;
import model.TimeAndCounter;
import org.junit.BeforeClass;
import org.junit.Test;

public class FlowOrchestratorTest {

	private static FlowOrchestrator sharedOrchestrator;
	private static ExecutorService commonPool;
	private static int TIME_LIMIT_MILIS;

	@BeforeClass
	public static void initPoolAndOrchestrator(){
		TIME_LIMIT_MILIS = 1000;
		sharedOrchestrator = new FlowOrchestrator(TIME_LIMIT_MILIS / 1000, 2);
		commonPool = Executors.newFixedThreadPool(3);
	}

	@Test
	public void simpleInsertionTest() throws InterruptedException {
		//given timeLimit = 1 , maxLimit = 2
		Message m = new Message("a", "b", "c");
		Message d = new Message("a", "b", "c");
		//when
		for (int i = 0; i < 3; i++) {
			commonPool.execute(() -> sharedOrchestrator.processMessage(m));
			commonPool.execute(() -> sharedOrchestrator.processMessage(m));
		}
		Thread.sleep(3*TIME_LIMIT_MILIS);
		for (int i = 0; i <2; i++) {
			commonPool.execute(() -> sharedOrchestrator.processMessage(d));
			commonPool.execute(() -> sharedOrchestrator.processMessage(d));

		}
		Thread.sleep(TIME_LIMIT_MILIS /10);

		//then
		int count = sharedOrchestrator.getMap().getOrDefault(new Message("a", "b", "c").getMapKey(), new AtomicReference<>(new TimeAndCounter())).get().getAtomicInteger().get();
		assertTrue(count > 0 && count < 7);
	}


	@Test
	public void shouldContainTwoElements() throws InterruptedException {
		//given
		Message m = new Message("a", "b", "c");
		Message d = new Message("a", "z", "c");
		//when
		Thread.sleep(TIME_LIMIT_MILIS);
		for (int i = 0; i < 2; i++) {
			commonPool.execute(() -> sharedOrchestrator.processMessage(m));
			commonPool.execute(() -> sharedOrchestrator.processMessage(d));
		}
		//then
		Thread.sleep(TIME_LIMIT_MILIS /5);
		assertEquals(sharedOrchestrator.getMap().size() , 2);
	}

}