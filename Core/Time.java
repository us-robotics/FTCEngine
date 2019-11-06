package FTCEngine.Core;

public class Time extends OpModeBase.Helper
{
	public Time(OpModeBase main)
	{
		super(main);
	}

	private long initialTime;
	private long previousTime;

	/**
	 * @return the delta time from the previous frame to this frame in seconds
	 */
	public float getDeltaTime()
	{
		return nanoToSec(System.nanoTime() - previousTime);
	}

	/**
	 * @return time in seconds
	 */
	public float getTime()
	{
		return nanoToSec(System.nanoTime() - initialTime);
	}

	private static float nanoToSec(long nano)
	{
		return (float)((double)nano / 1E9);
	}

	@Override
	public void beforeInit()
	{
		super.beforeInit();

		initialTime = System.nanoTime();
		previousTime = System.nanoTime();
	}

	@Override
	public void afterLoop()
	{
		super.afterLoop();
		previousTime = System.nanoTime();
	}
}