package FTCEngine.Core;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;
import java.util.List;

import FTCEngine.Debug;

public abstract class Main extends OpMode
{
	private Behavior[] allBehaviors;

	/**
	 * This method will be invoked once before init to get all the behaviors we want to run.
	 * You should create the new behaviors using the "new" keyword and add them into the list
	 */
	public abstract void addBehaviors(List<Behavior> behaviorList);

	@Override
	public void init()
	{
		//Initialize all behaviors
		ArrayList<Behavior> behaviors = new ArrayList<>();
		addBehaviors(behaviors);

		allBehaviors = new Behavior[behaviors.size()];
		behaviors.toArray(allBehaviors);

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

	public class Time
	{
		public Time(Main opMode)
		{
			this.opMode = opMode;
		}

		private Main opMode;

//System.nanoTime?
	}
}
