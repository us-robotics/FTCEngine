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
		if (getIsDone()) throw new IllegalStateException("Job is already finished!");
		isDone = true;
	}
}
