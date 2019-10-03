package FTCEngine.Core;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FTCEngine.Experimental.Action;

public abstract class Main extends OpMode
{
	public Main()
	{
		//Create all allHelpers
		allHelpers = new HashMap<Class<?>, Helper>();
		Action<Helper> assignHelper = new Action<Helper>()
		{
			@Override
			public void accept(Helper helper)
			{
				allHelpers.put(helper.getClass(), helper);
			}
		};

		assignHelper.accept(new Time(this));
		assignHelper.accept(new Input(this));
	}

	private Behavior[] allBehaviors; //Semi-readonly
	private final HashMap<Class<?>, Helper> allHelpers;

	private OpModePhase currentPhase = OpModePhase.invalid;

	/**
	 * This method will be invoked once before init to get all the behaviors we want to run.
	 * You should create the new behaviors using the "new" keyword and add them into the list
	 */
	public abstract void addBehaviors(List<Behavior> behaviorList);

	public OpModePhase getPhase()
	{
		return currentPhase;
	}

	/**
	 * Gets the helper object with its reflection class type
	 */
	public <T extends Helper> T getHelper(Class<T> tClass)
	{
		Helper result = allHelpers.get(tClass);
		return tClass.isInstance(result) ? (T)result : null;
	}

	@Override
	public final void init()
	{
		//Fetch/create all behaviors
		ArrayList<Behavior> behaviors = new ArrayList<>();
		addBehaviors(behaviors);

		allBehaviors = new Behavior[behaviors.size()];
		behaviors.toArray(allBehaviors);

		//Initialize internal op mode
		currentPhase = OpModePhase.initialize;
		for (Map.Entry<Class<?>, Helper> entry : allHelpers.entrySet()) entry.getValue().beforeInit();

		//Awake all behaviors
		for (int i = 0; i < allBehaviors.length; i++)
		{
			allBehaviors[i].awake(hardwareMap);
		}

		for (Map.Entry<Class<?>, Helper> entry : allHelpers.entrySet()) entry.getValue().afterInit();
	}

	@Override
	public final void start()
	{
		super.start();

		currentPhase = OpModePhase.start;
		for (Map.Entry<Class<?>, Helper> entry : allHelpers.entrySet()) entry.getValue().beforeStart();

		for (int i = 0; i < allBehaviors.length; i++)
		{
			allBehaviors[i].start();
		}

		for (Map.Entry<Class<?>, Helper> entry : allHelpers.entrySet()) entry.getValue().afterStart();
	}

	@Override
	public final void loop()
	{
		currentPhase = OpModePhase.loop;
		for (Map.Entry<Class<?>, Helper> entry : allHelpers.entrySet()) entry.getValue().beforeLoop();

		for (int i = 0; i < allBehaviors.length; i++)
		{
			allBehaviors[i].update();
		}

//		telemetry.addData("", Debug.getLogged(5));
		telemetry.update();

		for (Map.Entry<Class<?>, Helper> entry : allHelpers.entrySet()) entry.getValue().afterLoop();
	}


	@Override
	public final void stop()
	{
		super.stop();

		currentPhase = OpModePhase.stop;
		for (Map.Entry<Class<?>, Helper> entry : allHelpers.entrySet()) entry.getValue().beforeStop();

		for (int i = 0; i < allBehaviors.length; i++)
		{
			allBehaviors[i].stop();
		}

		for (Map.Entry<Class<?>, Helper> entry : allHelpers.entrySet()) entry.getValue().afterStop();
		currentPhase = OpModePhase.invalid;
	}

	/**
	 * A class which should only be extended by the engine to create other helper classes
	 * NOTE: The event methods are invoked by the Main class, during the time of the event
	 */
	static class Helper
	{
		public Helper(Main main)
		{
			this.main = main;
		}

		protected final Main main;

		public void beforeInit() {}
		public void afterInit() {}

		public void beforeStart() {}
		public void afterStart() {}

		public void beforeLoop() {}
		public void afterLoop() {}

		public void beforeStop() {}
		public void afterStop() {}
	}
}