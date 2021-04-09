package FTCEngine.Core;

public class Time extends OpModeBase.Helper
{
	public Time(OpModeBase main)
	{
		super(main);
	}

	private long initialTime;
	private long previousTime;
	private float deltaTime;

	/**
	 * @return the delta time from the previous frame to this frame in seconds
	 */
	public float getDeltaTime()
	{
		return deltaTime;
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
	}

	@Override
	public void beforeStart()
	{
		super.beforeStart();
		previousTime = System.nanoTime();
	}

	@Override
	public void beforeUpdate()
	{
		super.beforeUpdate();
		long time = System.nanoTime();

		deltaTime = nanoToSec(time - previousTime);
		deltaTime = Math.max(deltaTime, 0f);

		previousTime = time;
	}
}