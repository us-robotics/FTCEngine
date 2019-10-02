package FTCEngine.Core;

import java.util.concurrent.TimeUnit;

public class Time
{
	public Time(Control control)
	{
		this.control = control;
		control.time = this;
	}

	private final Control control;

	private long initialTime;
	private long previousTime;

	public float getDeltaTime()
	{
		return nanoToSec(System.nanoTime() - previousTime);
	}

	public float getTime()
	{
		return nanoToSec(System.nanoTime() - initialTime);
	}

	private static float nanoToSec(long nano)
	{
		return (float)((double)nano / 1E9);
	}

	/**
	 * Use this class to update the time class
	 */
	public static class Control
	{
		private Time time;

		public void setTime(Time time)
		{
			if (time == null) this.time = time;
			else throw new IllegalArgumentException("Target time is already set! It is semi-readonly");
		}

		public void onInit()
		{
			checkTime();
			time.initialTime = System.nanoTime();
			time.previousTime = System.nanoTime();
		}

		public void afterLoop()
		{
			checkTime();
			time.previousTime = System.nanoTime();
		}

		private void checkTime()
		{
			if (time == null) throw new IllegalStateException("You did not set the target time to control yet!");
		}
	}
}