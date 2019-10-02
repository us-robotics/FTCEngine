package FTCEngine.Core;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;
import java.util.List;

import FTCEngine.Debug;

public abstract class Main extends OpMode
{
	public Main()
	{
		timeControl = new Time.Control();
		time = new Time(timeControl);
	}

	private Behavior[] allBehaviors;

	public final Time time;
	private final Time.Control timeControl;

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

		timeControl.onInit();

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

		timeControl.afterLoop();
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
