package FTCEngine.Core.Auto;

import java.util.ArrayList;

import FTCEngine.Core.Input;
import FTCEngine.Core.OpModeBase;
import FTCEngine.Core.Time;

public abstract class AutoOpModeBase extends OpModeBase
{
	@Override
	public final void init()
	{
		super.init();
		awake();
	}

	protected void awake()
	{
		getInput().registerButton(Input.Source.CONTROLLER_1, Input.Button.RIGHT_BUMPER);
	}

	boolean isQueueingJobs;

	private ArrayList<BehaviorJob<?>> jobs = new ArrayList<BehaviorJob<?>>();
	private int currentJobIndex;

	private static final BehaviorJob<?> executeJobAction = new BehaviorJob<>(null, null);

	private boolean isBlue = true;
	private boolean overrideReverse;

	@Override
	public final boolean getIsAuto()
	{
		return true;
	}
	public boolean getIsBlue() {return isBlue;}

	protected Input getInput()
	{
		return getHelper(Input.class);
	}

//	protected Time getTime() {
//		return getHelper(Time.class);
//	}

	@Override
	public final void init_loop()
	{
		super.init_loop();
		configLoop();
	}

	protected void configLoop()
	{
		if (getInput().getButtonDown(Input.Source.CONTROLLER_1, Input.Button.RIGHT_BUMPER)) isBlue = !isBlue;
		telemetry.addData("Side (RBumper)", isBlue ? "blue" : "red");
	}

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
			allJobsFinished &= jobs.get(current).updateJob();
			telemetry.addData(jobs.get(current).job == null ? "Ran:" : "Running: ", jobs.get(current).toString());

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

		if (!getIsBlue() && !overrideReverse) job.reverse();
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

	private void startOverrideReverse() {overrideReverse = true;}
	private void endOverrideReverse() {overrideReverse = false;}

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
			behavior.setCurrentJob(job);
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
		 * NOTE: Returns false if the job is still executing, or true if the job is already done
		 */
		public boolean updateJob()
		{
			checkStartJob();

			if (behavior.getCurrentJob() == null) return true;
			if (behavior.getCurrentJob() != job) throw new InternalError("Internal engine error! This update method should not be invoked when behvaior has a different job!");

			if (job.getIsDone()) behavior.setCurrentJob(null);
			return job.getIsDone();
		}

		@Override
		public String toString()
		{
			return job.toString();
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

		@Override
		public String toString()
		{
			return "Waiting for " + second + " seconds";
		}
	}
}
