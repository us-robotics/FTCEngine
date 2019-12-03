package FTCEngine.Core;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FTCEngine.Experimental.Action;
import FTCEngine.Helpers.CollectionHelper;

public abstract class OpModeBase extends OpMode
{
	public OpModeBase()
	{
		//Create all allHelpers
		allHelpers = new HashMap<Class, Helper>();
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
		assignHelper.accept(new Telemetry(this));
	}

	private List<Behavior> allBehaviors; //Semi-readonly, sorted by class object hashcode for binary search
	private final HashMap<Class, Helper> allHelpers;

	private OpModePhase currentPhase = OpModePhase.INVALID;
	public abstract boolean getIsAuto();

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
		return helperClass.isInstance(result) ? (T)result : null;
	}

	/**
	 * Gets the behavior from its class object
	 * Returns null if found none
	 */
	public <T extends Behavior> T getBehavior(Class<T> behaviorClass)
	{
		int index = CollectionHelper.binarySearch(allBehaviors, PriorityExtractor.behaviorExtractor, behaviorClass, PriorityExtractor.classExtractor);
		return index < 0 ? null : (T)allBehaviors.get(index);
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
		for (Map.Entry<Class, Helper> entry : allHelpers.entrySet()) entry.getValue().beforeInit();

		//Awake all behaviors
		for (int i = 0; i < allBehaviors.size(); i++)
		{
			allBehaviors.get(i).awake(hardwareMap);
		}

		telemetry.update();

		for (Map.Entry<Class, Helper> entry : allHelpers.entrySet()) entry.getValue().afterInit();
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
	public void start()
	{
		super.start();

		currentPhase = OpModePhase.START;
		for (Map.Entry<Class, Helper> entry : allHelpers.entrySet()) entry.getValue().beforeStart();

		for (int i = 0; i < allBehaviors.size(); i++)
		{
			allBehaviors.get(i).start();
		}

		telemetry.update();

		for (Map.Entry<Class, Helper> entry : allHelpers.entrySet()) entry.getValue().afterStart();
	}

	@Override
	public void loop()
	{
		currentPhase = OpModePhase.LOOP;
		for (Map.Entry<Class, Helper> entry : allHelpers.entrySet()) entry.getValue().beforeLoop();

		for (int i = 0; i < allBehaviors.size(); i++)
		{
			allBehaviors.get(i).update();
		}

//		telemetry.addData("", Debug.getLogged(5));
		telemetry.update();

		for (Map.Entry<Class, Helper> entry : allHelpers.entrySet()) entry.getValue().afterLoop();
	}


	@Override
	public void stop()
	{
		super.stop();

		currentPhase = OpModePhase.STOP;
		for (Map.Entry<Class, Helper> entry : allHelpers.entrySet()) entry.getValue().beforeStop();

		for (int i = 0; i < allBehaviors.size(); i++)
		{
			allBehaviors.get(i).stop();
		}

		telemetry.update();

		for (Map.Entry<Class, Helper> entry : allHelpers.entrySet()) entry.getValue().afterStop();
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

		public void beforeInit() {}

		public void afterInit() {}

		public void beforeStart() {}

		public void afterStart() {}

		public void beforeLoop() {}

		public void afterLoop() {}

		public void beforeStop() {}

		public void afterStop() {}
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