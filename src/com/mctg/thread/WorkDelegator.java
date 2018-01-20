package com.mctg.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JProgressBar;

public class WorkDelegator {

	private List<DelegatedTask> tasks;
	private int maxThreads;
	private List<Thread> threads = new ArrayList<Thread>();

	public WorkDelegator(List<DelegatedTask> tasks, int maxThreads) {
		this.tasks = tasks;
		this.maxThreads = maxThreads;
	}

	public void start(JProgressBar progress, int updateDelay) {
		Thread dispatcher = new Thread() {
			@Override
			public void run() {
				// create all of the threads
				AtomicInteger threadCount = new AtomicInteger(maxThreads);
				for (final DelegatedTask task : tasks) {
					threads.add(new Thread(() -> {
						task.run();
						this.interrupt();
						threads.remove(this);
						System.out.println("Thread finished");
					}));
				}

				// begin running them all
				for (int i = 0; i < maxThreads && i < threads.size(); i++) {
					threads.get(i).start();
					System.out.println("Started a thread");
				}
				while (true) {
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException e) {

					}
					if (threadCount.get() == tasks.size()) {
						break;
					}
					threads.get(threadCount.getAndIncrement()).start();
					System.out.println("Started a thread (" + threadCount.get() + ")");
				}
			}
		};

		if (progress != null && updateDelay > 0) {
			Thread updater = new Thread(() -> {
				do {
					try {
						Thread.sleep(updateDelay);
						progress.setValue((int) (getProgress() * 100));
					} catch (InterruptedException e) {
						break;
					}
				} while (isRunning());
			});
			updater.start();
		}
		dispatcher.start();
	}

	public float getProgress() {
		float total = 0;
		DelegatedTask[] tasks;
		synchronized (this.tasks) {
			tasks = this.tasks.toArray(new DelegatedTask[0]);
		}
		for (DelegatedTask task : tasks) {
			total += task.getProgress();
		}
		return total / tasks.length;
	}

	public boolean isRunning() {
		return threads.size() != 0;
	}

}
