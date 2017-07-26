package io.gitlab.arturbosch.smartsmells;

import io.gitlab.arturbosch.jpal.core.CompilationStorage;
import io.gitlab.arturbosch.jpal.core.JPAL;
import io.gitlab.arturbosch.jpal.resolution.Resolver;
import io.gitlab.arturbosch.smartsmells.api.Detector;
import io.gitlab.arturbosch.smartsmells.api.DetectorFacade;
import io.gitlab.arturbosch.smartsmells.smells.shotgunsurgery.ShotgunSurgeryDetector;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ShotgunSurgeryPerformanceTest {

	private static CompilationStorage storage;

	@Benchmark
	public void withoutParallel() {
		run(new ShotgunSurgeryDetector());
	}

	private CompilationStorage init(Path path) {
		if (storage == null) {
			storage = JPAL.newInstance(path);
		}
		return storage;
	}

	private void run(Detector detector) {
		Path path = Paths.get("/home/artur/Repos/elasticsearch/core/src/main/");

		CompilationStorage storage = init(path);
		DetectorFacade facade = DetectorFacade.builder().with(detector).build();

		int result = facade.justRun(storage.getAllCompilationInfo(), new Resolver(storage)).of(detector.getType())
				.size();
		System.out.println("#smells: " + result);
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(".*" + ShotgunSurgeryPerformanceTest.class.getSimpleName() + ".*")
				.forks(1)
				.build();

		new Runner(opt).run();
	}
}