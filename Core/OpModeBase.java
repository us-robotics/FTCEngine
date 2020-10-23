package FTCEngine.Core;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FTCEngine.Core.Auto.AutoOpModeBase;
import FTCEngine.Delegates.Action;
import FTCEngine.Helpers.CollectionHelper;

public abstract class OpModeBase extends OpMode
{
	public OpModeBase()
	{
		//Create all helpers
		allHelpers = new HashMap<Class<?>, Helper>();

		allHelpers.put(Time.class, new Time(this));
		allHelpers.put(Input.class, new Input(this));
		allHelpers.put(Telemetry.class, new Telemetry(this));
	}

	private List<Behavior> allBehaviors; //Semi-readonly, sorted by class object hashcode for binary search
	private final HashMap<Class<?>, Helper> allHelpers;

	private OpModePhase currentPhase = OpModePhase.INVALID;

	public boolean getIsAuto()
	{
		return this instanceof AutoOpModeBase;
	}

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
	public <T extends Helper> T getHelper(Class<T> helperClass)
	{
		Helper result = allHelpers.get(helperClass);
		return helperClass.isInstance(result) ? (T) result : null;
	}

	/**
	 * Gets the behavior from its class object
	 * Returns null if found none
	 */
	public <T extends Behavior> T getBehavior(Class<T> behaviorClass)
	{
		int index = CollectionHelper.binarySearch(allBehaviors, PriorityExtractor.behaviorExtractor, behaviorClass, PriorityExtractor.classExtractor);
		return index < 0 ? null : (T) allBehaviors.get(index);
	}

	@Override
	public void init()
	{
		//Fetch/create all behaviors
		ArrayList<Behavior> behaviors = new ArrayList<>();
		addBehaviors(behaviors);

		allBehaviors = initializeBehaviors(behaviors);

		//Initialize internal op mode
		currentPhase = OpModePhase.INITIALIZE;
		for (Helper helper : allHelpers.values()) helper.beforeInit();

		//Awake all behaviors
		for (int i = 0; i < allBehaviors.size(); i++)
		{
			allBehaviors.get(i).awake(hardwareMap);
		}

		telemetry.update();

		for (Helper helper : allHelpers.values()) helper.afterInit();
	}

	private static List<Behavior> initializeBehaviors(ArrayList<Behavior> source)
	{
		List<Behavior> result = new ArrayList<Behavior>(source.size());
		CollectionHelper.sort(source, PriorityExtractor.behaviorExtractor); //Sorts by class object hashcode

		for (int i = 0; i < source.size(); i++)
		{
			Behavior current = source.get(i);

			if (i == 0 || current.getClass() != result.get(i - 1).getClass()) result.add(current);
			else throw new IllegalArgumentException("Cannot add two behaviors with the same type! Duplicate type: " + current.getClass());
		}

		return result;
	}

	@Override
	public void init_loop()
	{
		super.init_loop();

		currentPhase = OpModePhase.INIT_LOOP;
		for (Helper helper : allHelpers.values()) helper.initLoop();

		telemetry.update();
	}

	@Override
	public void start()
	{
		super.start();

		currentPhase = OpModePhase.START;
		for (Helper helper : allHelpers.values()) helper.beforeStart();

		for (int i = 0; i < allBehaviors.size(); i++)
		{
			allBehaviors.get(i).start();
		}

		telemetry.update();

		for (Helper helper : allHelpers.values()) helper.afterStart();
	}

	@Override
	public void loop()
	{
		currentPhase = OpModePhase.LOOP;
		for (Helper helper : allHelpers.values()) helper.beforeLoop();

		//Main loop
		for (int i = 0; i < allBehaviors.size(); i++)
		{
			allBehaviors.get(i).update();
		}

		telemetry.update();

		for (Helper helper : allHelpers.values()) helper.afterLoop();
	}


	@Override
	public void stop()
	{
		super.stop();

		currentPhase = OpModePhase.STOP;
		for (Helper helper : allHelpers.values()) helper.beforeStop();

		for (int i = 0; i < allBehaviors.size(); i++)
		{
			allBehaviors.get(i).stop();
		}

		telemetry.update();

		for (Helper helper : allHelpers.values()) helper.afterStop();
		currentPhase = OpModePhase.INVALID;
	}

	/**
	 * A class which should only be extended by the engine to create other helper classes
	 * NOTE: The event methods are invoked by the OpModeBase class, during the time of the event
	 */
	static class Helper
	{
		public Helper(OpModeBase opMode)
		{
			this.opMode = opMode;
		}

		protected final OpModeBase opMode;

		public void beforeInit()
		{
		}

		public void afterInit()
		{
		}

		public void initLoop()
		{
		}

		public void afterInitLoop()
		{
		}

		public void beforeStart()
		{
		}

		public void afterStart()
		{
		}

		public void beforeLoop()
		{
		}

		public void afterLoop()
		{
		}

		public void beforeStop()
		{
		}

		public void afterStop()
		{
		}
	}

	private static class PriorityExtractor
	{
		public static final BehaviorExtractor behaviorExtractor = new BehaviorExtractor();
		public static final ClassExtractor classExtractor = new ClassExtractor();

		public static class BehaviorExtractor implements CollectionHelper.PriorityExtractor<FTCEngine.Core.Behavior>
		{
			@Override
			public int getPriority(FTCEngine.Core.Behavior item)
			{
				return item.getClass().hashCode();
			}
		}

		public static class ClassExtractor implements CollectionHelper.PriorityExtractor<Class<?>>
		{
			@Override
			public int getPriority(Class<?> item)
			{
				return item.hashCode();
			}
		}
	}
}