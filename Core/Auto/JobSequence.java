package FTCEngine.Core.Auto;

import java.util.ArrayList;

import FTCEngine.Core.OpModeBase;
import FTCEngine.Core.Time;

public abstract class JobSequence
{
	public JobSequence(OpModeBase opMode)
	{
		this.opMode = opMode;
	}

	public final OpModeBase opMode;
	private ArrayList<BehaviorJob<?>> jobs;

	private boolean queuedJobs;
	private int currentJobIndex;

	/**
	 * This is an object that acts as a reference tag to indicate we should execute all of the actions/jobs
	 * before this tag in the execution stack.
	 */
	private static final BehaviorJob<?> executeJobAction = new BehaviorJob<>(null, null);

	/**
	 * This is where you should queue all of the jobs that you want to execute.
	 * This method will be invoked in the constructor of this JobSequence
	 */
	protected abstract void queueJobs();

	public void tryQueueJobs()
	{
		if (queuedJobs) return;

		jobs = new ArrayList<>();
		queueJobs();
		queuedJobs = true;

		if (jobs.size() == 0) return;
		if (jobs.get(jobs.size() - 1) != executeJobAction) throw new IllegalStateException("No execute action was appended after a job was queued!");
	}

	public void reset()
	{
		currentJobIndex = 0;
	}

	protected <TBehavior extends AutoBehavior<TJob>, TJob extends Job, TJobIn extends TJob> void execute(TBehavior behavior, TJobIn job)
	{
		buffer(behavior, job);
		addExecuteAction();
	}

	protected <TBehavior extends AutoBehavior<TJob>, TJob extends Job, TJobIn extends TJob> void buffer(TBehavior behavior, TJobIn job)
	{
		checkQueueState();

//		for (int i = jobs.size() - 1; ; i--)
//		{
//
//		}

		jobs.add(new BehaviorJob<>(behavior, job));
	}

	protected void execute()
	{
		checkQueueState();
		addExecuteAction();
	}

	protected void wait(float second)
	{
		checkQueueState();

		jobs.add(new WaitJob(second, opMode));
		addExecuteAction();
	}

	private void addExecuteAction()
	{
		if (jobs.size() == 0 || jobs.get(jobs.size() - 1) != executeJobAction) jobs.add(executeJobAction);
		else throw new IllegalStateException("Cannot execute without adding any job!");
	}

	private void checkQueueState()
	{
		if (jobs != null && !queuedJobs) return;
		throw new IllegalStateException("Invalid time for queueing jobs! You should only queue in the queueJobs method.");
	}

	/**
	 * Updates and runs this JobSequence, advances forward internal state if necessary.
	 *
	 * @return whether the entire sequence has finished or not.
	 */
	public boolean run()
	{
		if (!queuedJobs) throw new IllegalStateException("Sequence not queued!");
		if (currentJobIndex >= jobs.size()) return true; //All jobs finished

		int current = currentJobIndex;
		boolean allJobsFinished = true;

		while (jobs.get(current) != executeJobAction)
		{
			allJobsFinished &= jobs.get(current).updateJob();
			current++;
		}

		if (allJobsFinished) currentJobIndex = current + 1;
		return currentJobIndex >= jobs.size();
	}

	private static class BehaviorJob<TJob extends Job>
	{
		public BehaviorJob(AutoBehavior<TJob> behavior, TJob job)
		{
			this.behavior = behavior;
			this.job = job;
		}

		public final AutoBehavior<TJob> behavior;
		public final TJob job;

		private boolean jobStarted;

		/**
		 * Invoked once when the job starts.
		 */
		protected void startJob()
		{
			behavior.setCurrentJob(job);
			behavior.onJobAdded();
		}

		protected void checkStartJob()
		{
			if (jobStarted) return;

			jobStarted = true;
			startJob();
		}

		/**
		 * This method will be invoked after the main update method in Behavior if this BehaviorJob is currently executing
		 * (or if other behavior jobs executing parallel to this job is executing)
		 * NOTE: Returns false if the job is still executing, or true if the job is already done
		 */
		public boolean updateJob()
		{
			checkStartJob();

			if (behavior.getCurrentJob() == null) return true;
			if (behavior.getCurrentJob() != job) throw new InternalError("Internal engine error! This update method should not be invoked when behvaior has a different job!");

			if (job.getIsDone())
			{
				behavior.setCurrentJob(null);
				return true;
			}

			return false;
		}

		@Override
		public String toString()
		{
			return job == null ? "NO_JOB" : job.toString();
		}
	}

	private static class WaitJob extends BehaviorJob<Job>
	{
		public WaitJob(float second, OpModeBase opMode)
		{
			super(null, null);

			this.second = second;
			time = opMode.time;
		}

		private final float second;
		private final Time time;

		float startTime;

		@Override
		protected void startJob()
		{
			startTime = time.getTime();
		}

		@Override
		public boolean updateJob()
		{
			checkStartJob();
			return time.getTime() - startTime >= second;
		}

		@Override
		public String toString()
		{
			return "Waiting for " + second + " seconds";
		}
	}
}
