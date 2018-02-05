import app.FlowOrchestrator;
import java.util.concurrent.TimeUnit;
import model.Message;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkTest {

	@Benchmark
	@BenchmarkMode(Mode.All)
	@OutputTimeUnit(TimeUnit.MICROSECONDS)
	public void test() {
		FlowOrchestrator flowOrchestrator = new FlowOrchestrator(5, 100);
		for (int i = 0; i < 1000; i++) {
			flowOrchestrator.processMessage(new Message("a", "b", "c"));
			flowOrchestrator.processMessage(new Message("b", "c", "c"));
			flowOrchestrator.processMessage(new Message("c", "b", "c"));
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(BenchmarkTest.class.getSimpleName())
				.forks(1)
				.build();

		new Runner(opt).run();
	}

}
