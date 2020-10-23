package FTCEngine.Core.Auto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import FTCEngine.Core.Input;
import FTCEngine.Core.OpModeBase;
import FTCEngine.Core.Time;

public abstract class AutoOpModeBase extends OpModeBase
{
	@Override
	public final void init()
	{
		super.init();

		//Register all config options
		ArrayList<ConfigOption> options = new ArrayList<ConfigOption>();
		Set<Input.Button> buttons = new HashSet<Input.Button>();

		appendConfigOptions(options);

		//Check to make sure that no two options use the same button
		for (int i = 0; i < options.size(); i++)
		{
			ConfigOption option = options.get(i);

			for (int j = 0; j < option.getButtonCount(); j++)
			{
				Input.Button button = option.getButton(j);

				if (buttons.add(button)) getHelper(Input.class).registerButton(getConfigOptionInputSource(), button);
				else throw new IllegalArgumentException("Two config options are using the same button " + button.name());
			}
		}

		configOptions = (ConfigOption[]) options.toArray();
	}

	protected void appendConfigOptions(List<ConfigOption> options)
	{
		options.add(new ConfigOption()
		{
			@Override
			public String getLabel()
			{
				return "Alliance";
			}

			@Override
			public String getOption()
			{
				return alliance == Alliance.blue ? "Blue" : "Red";
			}

			@Override
			public int getButtonCount()
			{
				return 1;
			}

			@Override
			public Input.Button getButton(int index)
			{
				return Input.Button.RIGHT_BUMPER;
			}

			@Override
			public void onButtonDown(Input.Button button)
			{
				alliance = alliance.toggle();
			}
		});
	}


	private final ArrayList<BehaviorJob<?>> jobs = new ArrayList<BehaviorJob<?>>();
	private ConfigOption[] configOptions;

	boolean isQueueingJobs;
	private int currentJobIndex;

	/**
	 * This is an object that acts as a reference tag to indicate we should execute all of the actions/jobs
	 * before this tag in the execution stack.
	 */
	private static final BehaviorJob<?> executeJobAction = new BehaviorJob<>(null, null);

	private Alliance alliance = Alliance.blue;

	public Alliance getAlliance()
	{
		return alliance;
	}

	protected Input.Source getConfigOptionInputSource()
	{
		return Input.Source.CONTROLLER_1;
	}

	@Override
	public void init_loop()
	{
		super.init_loop();

		//Update config options
		Input input = getHelper(Input.class);
		Input.Source source = getConfigOptionInputSource();
		StringBuilder builder = new StringBuilder();

		for (ConfigOption option : configOptions)
		{
			builder.append(option.getLabel());
			builder.append(" (");

			for (int i = 0; i < option.getButtonCount(); i++)
			{
				Input.Button button = option.getButton(i);
				builder.append(button.name());

				builder.append(i == option.getButtonCount() - 1 ? ')' : ',');
				if (input.getButtonDown(source, button)) option.onButtonDown(button);
			}

			telemetry.addData(builder.toString(), option.getOption());
			builder.setLength(0); //Clears builder
		}
	}

	@Override
	public void start()
	{
		super.start();

		isQueueingJobs = true;
		queueJobs();
		isQueueingJobs = false;

		if (jobs.get(jobs.size() - 1) != executeJobAction) throw new InternalError("Internal engine error! No execute action was appended to the end of the job list!");

		System.out.println(Arrays.toString(jobs.toArray()));
	}

	/**
	 * This is where you should queue all of the jobs that you want to execute.
	 * This method will be invoked right when you press the start button.
	 */
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
		telemetry.update();
	}

	protected <TBehavior extends AutoBehavior<TJob>, TJob extends Job> void execute(TBehavior behavior, TJob job)
	{
		execute(behavior, job, false);
	}

	protected <TBehavior extends AutoBehavior<TJob>, TJob extends Job> void execute(TBehavior behavior, TJob job, boolean overrideReverse)
	{
		buffer(behavior, job, overrideReverse);
		addExecuteAction();
	}


	protected <TBehavior extends AutoBehavior<TJob>, TJob extends Job> void buffer(TBehavior behavior, TJob job)
	{
		buffer(behavior, job, false);
	}

	protected <TBehavior extends AutoBehavior<TJob>, TJob extends Job> void buffer(TBehavior behavior, TJob job, boolean overrideReverse)
	{
		checkQueueState();

		if (getAlliance() == Alliance.red && !overrideReverse) job.reverse();
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
		if (isQueueingJobs) return;
		throw new IllegalStateException("Invalid time for queueing jobs");
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

		private boolean jobStarted = true;

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

			if (job.getIsDone()) behavior.setCurrentJob(null);
			return job.getIsDone();
		}

		@Override
		public String toString()
		{
			return job == null ? "_NULL_" : job.toString();
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
