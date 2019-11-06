package FTCEngine.Core.Auto;

import java.util.ArrayList;

import FTCEngine.Core.OpModeBase;
import FTCEngine.Core.Time;

public abstract class AutoOpModeBase extends OpModeBase
{
	boolean isQueueingJobs;

	ArrayList<BehaviorJob<?>> jobs = new ArrayList<BehaviorJob<?>>();
	int currentJobIndex;

	static final BehaviorJob<?> executeJobAction = new BehaviorJob<>(null, null);

	@Override
	public void start()
	{
		super.start();

		isQueueingJobs = true;
		queueJobs();
		isQueueingJobs = false;

		if (jobs.get(jobs.size() - 1) != executeJobAction) throw new InternalError("Internal engine error! No execute action was appended to the end of the job list!");
	}

	protected abstract void queueJobs();

	@Override
	public void loop()
	{
		super.loop();
		if (currentJobIndex >= jobs.size()) return; //All jobs finished

		int current = currentJobIndex;
		boolean allJobsFinished = true;

		while (jobs.get(current) != executeJobAction)
		{
			allJobsFinished &= !jobs.get(current).updateJob();
			current++;
		}

		if (allJobsFinished) currentJobIndex = current + 1;
	}

	protected <TBehavior extends AutoBehavior<TJob>, TJob extends Job> void execute(TBehavior behavior, TJob job)
	{
		buffer(behavior, job);
		addExecuteAction();
	}

	protected <TBehavior extends AutoBehavior<TJob>, TJob extends Job> void buffer(TBehavior behavior, TJob job)
	{
		checkQueueState();
		jobs.add(new BehaviorJob<TJob>(behavior, job));
	}

	protected void execute()
	{
		checkQueueState();
		addExecuteAction();
	}

	protected void wait(float second)
	{
		checkQueueState();

		jobs.add(new WaitJob(second, this));
		addExecuteAction();
	}

	private void addExecuteAction()
	{
		if (jobs.size() == 0 || jobs.get(jobs.size() - 1) != executeJobAction) jobs.add(executeJobAction);
		else throw new IllegalStateException("Cannot execute without adding any job!");
	}

	private void checkQueueState()
	{
		if (!isQueueingJobs) throw new IllegalStateException("Invalid time for queueing jobs");
	}

	static class BehaviorJob<TJob extends Job>
	{
		public BehaviorJob(AutoBehavior<TJob> behavior, TJob job)
		{
			this.behavior = behavior;
			this.job = job;
		}

		public final AutoBehavior<TJob> behavior;
		public final TJob job;

		private boolean firstTimeExecutingJob = true;

		protected void startJob()
		{
			behavior.onJobAdded();
		}

		protected void checkStartJob()
		{
			if (!firstTimeExecutingJob) return;

			firstTimeExecutingJob = false;
			startJob();
		}

		/**
		 * This method will be invoked after the main update method if this BehaviorJob is currently executing
		 * (or if other behavior jobs executing parallel to this job is executing)
		 * NOTE: Returns true if the job is still executing, or false if the job is already done
		 */
		public boolean updateJob()
		{
			checkStartJob();

			if (behavior.getCurrentJob() == null) return false;
			if (behavior.getCurrentJob() != job) throw new InternalError("Internal engine error! This update method should not be invoked when behvaior has a different job!");

			if (job.getIsDone()) behavior.setCurrentJob(null);
			return job.getIsDone();
		}
	}

	static class WaitJob extends BehaviorJob<Job>
	{
		public WaitJob(float second, AutoOpModeBase opMode)
		{
			super(null, null);

			this.second = second;
			time = opMode.getHelper(Time.class);
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
	}
}
