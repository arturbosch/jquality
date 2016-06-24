package com.gitlab.artismarti.smartsmells.metrics

import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * @author artur
 */
final class MetricThreadPool {

	final static Executor service = Executors.newFixedThreadPool(Runtime.runtime.availableProcessors())

	private MetricThreadPool() {}

}
