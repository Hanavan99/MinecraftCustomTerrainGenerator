package com.mctg.thread;

public interface DelegatedTask {

	public void run();

	/**
	 * Gets the progress of the task, between 0.0 and 1.0;
	 * 
	 * @return the progress
	 */
	public float getProgress();

}
