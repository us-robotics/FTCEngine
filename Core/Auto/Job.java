package FTCEngine.Core.Auto;

public abstract class Job
{
	private boolean isDone;

	public boolean getIsDone()
	{
		return isDone;
	}

	public void finishJob()
	{
		if (!getIsDone()) isDone = true;
		else throw new IllegalStateException("AutoJob is already finished!");
	}

	public void reverse() {}
}
