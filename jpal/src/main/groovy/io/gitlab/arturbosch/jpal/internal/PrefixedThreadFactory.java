package io.gitlab.arturbosch.jpal.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Artur Bosch
 */
public class PrefixedThreadFactory implements ThreadFactory {
	private static final AtomicInteger poolNumber = new AtomicInteger(1);
	private final ThreadGroup group;
	private final AtomicInteger threadNumber = new AtomicInteger(1);
	private final String namePrefix;

	public PrefixedThreadFactory(String name) {
		SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() :
				Thread.currentThread().getThreadGroup();
		namePrefix = name + "-" +
				poolNumber.getAndIncrement() +
				"-thread-";
	}

	public Thread newThread(Runnable r) {
		Thread t = new Thread(group, r,
				namePrefix + threadNumber.getAndIncrement(),
				0);
		t.setDaemon(true);
		if (t.getPriority() != Thread.NORM_PRIORITY)
			t.setPriority(Thread.NORM_PRIORITY);
		return t;
	}
}
