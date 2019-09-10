package FTCEngine.Core;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import FTCEngine.Debug;

public abstract class Main extends OpMode
{
	/**
	 * @param allBehaviors All the behaviors we want to run. You are suppose to instantiate the new
	 *                     behaviors using the "new" keyword and put them into an array
	 */
	public Main(Behavior[] allBehaviors)
	{
		this.allBehaviors = allBehaviors;
	}

	private final Behavior[] allBehaviors;


	@Override
	public void init()
	{
		for (int i = 0; i < allBehaviors.length; i++)
		{
			allBehaviors[i].awake(hardwareMap);
		}
	}

	@Override
	public void start()
	{
		super.start();

		for (int i = 0; i < allBehaviors.length; i++)
		{
			allBehaviors[i].start();
		}
	}

	@Override
	public void loop()
	{
		for (int i = 0; i < allBehaviors.length; i++)
		{
			allBehaviors[i].update();
		}

		telemetry.addData("", Debug.getLogged(5));
		telemetry.update();
	}


	@Override
	public void stop()
	{
		super.stop();

		for (int i = 0; i < allBehaviors.length; i++)
		{
			allBehaviors[i].stop();
		}
	}
}
