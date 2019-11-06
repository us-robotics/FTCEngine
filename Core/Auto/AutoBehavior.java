package FTCEngine.Core.Auto;

import FTCEngine.Core.Behavior;
import FTCEngine.Core.OpModeBase;

public abstract class AutoBehavior<TJob extends Job> extends Behavior
{
	public AutoBehavior(OpModeBase opMode)
	{
		super(opMode);
	}

	private TJob currentJob;

	protected final TJob getCurrentJob()
	{
		return currentJob;
	}

	/**
	 * This method should/will not be used outside of the engine,
	 * that is why it is not public
	 */
	void setCurrentJob(TJob currentJob)
	{
		this.currentJob = currentJob;
	}

	@Override
	public void update()
	{
		super.update();
		if (getCurrentJob() != null && !getCurrentJob().getIsDone()) updateJob();
	}

	/**
	 * This method will get invoked when a job is added to this behavior
	 * (so when getCurrentJob turns from null to not null)
	 */
	public void onJobAdded() {}

	/**
	 * Updates the robot with the requested jobs
	 */
	protected abstract void updateJob();
}
