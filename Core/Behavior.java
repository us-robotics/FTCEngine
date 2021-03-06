package FTCEngine.Core;

import com.qualcomm.robotcore.hardware.HardwareMap;

/**
 * A basic class classifying and controlling different mechanism/part of the robot
 */
public abstract class Behavior
{
	/**
	 * NOTE: Do not configure the electronics in the constructor, do them in the awake method!
	 */
	public Behavior(OpModeBase opMode)
	{
		this.opMode = opMode;
	}

	protected final OpModeBase opMode;

	/**
	 * This method will get invoked one time between the init button and the play button
	 * Should mostly be used to INITIALIZE the electronics
	 */
	public void awake(HardwareMap hardwareMap)
	{
	}

	/**
	 * Invoked continuously after initialization before start.
	 * First invocation happens right after awake.
	 */
	public void awakeUpdate()
	{

	}

	/**
	 * This method will get invoked once when the driver pressed the play button
	 */
	public void start()
	{
	}

	/**
	 * This method get continuously invoked when in play mode
	 */
	public void update()
	{
	}

	/**
	 * This method will get invoked when the driver pressed the STOP button
	 */
	public void stop()
	{
	}
}
