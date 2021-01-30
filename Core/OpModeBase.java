package FTCEngine.Core;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import FTCEngine.Core.Auto.ConfigOption;
import FTCEngine.Core.Auto.JobSequence;
import FTCEngine.Helpers.CollectionHelper;

public abstract class OpModeBase extends OpMode
{
	public OpModeBase()
	{
		//Create all helpers
		time = new Time(this);
		input = new Input(this);
		debug = new Debug(this);

		allHelpers = new Helper[]{time, input, debug};
	}

	private final Helper[] allHelpers;
	private List<Behavior> allBehaviors; //Semi-readonly, sorted by class object hashcode for binary search
	private List<ConfigOption> allConfigOptions;

	public final Time time;
	public final Input input;
	public final Debug debug;

	private OpModePhase currentPhase = OpModePhase.INVALID;

	/**
	 * This is the input source that will be used to change our config options.
	 * Can be overridden
	 */
	protected Input.Source getConfigOptionInputSource()
	{
		return Input.Source.CONTROLLER_1;
	}

	private JobSequence runningSequence;

	/**
	 * Returns the current phase of the opMode.
	 */
	public OpModePhase getPhase()
	{
		return currentPhase;
	}

	/**
	 * Returns whether the opMode is running a sequence or not.
	 * Usually if a sequence is running, input should be paused.
	 */
	public boolean hasSequence()
	{
		return runningSequence != null;
	}

	/**
	 * This method will be invoked once before init to get all the behaviors we want to run.
	 * You should create the new behaviors using the "new" keyword and add them into the list
	 */
	protected abstract void addBehaviors(List<Behavior> behaviorList);

	protected abstract void appendConfigOptions(List<ConfigOption> options);

	public void assignSequence(JobSequence jobSequence)
	{
		if (jobSequence == null) runningSequence = null;
		else
		{
			if (jobSequence.opMode != this) throw new IllegalArgumentException("Invalid jobSequence opMode");

			runningSequence = jobSequence;
			runningSequence.reset();
		}
	}

	@Override
	public void init()
	{
		//Fetch/create all behaviors
		ArrayList<Behavior> behaviors = new ArrayList<>();
		addBehaviors(behaviors);

		allBehaviors = new ArrayList<Behavior>(behaviors.size());
		CollectionHelper.sort(behaviors, PriorityExtractor.behaviorExtractor); //Sorts by class object hashcode

		for (int i = 0; i < behaviors.size(); i++)
		{
			Behavior current = behaviors.get(i);

			if (i == 0 || current.getClass() != allBehaviors.get(i - 1).getClass()) allBehaviors.add(current);
			else throw new IllegalArgumentException("Cannot add two behaviors with the same type! Duplicate type: " + current.getClass());
		}

		//Initialize internal op mode
		for (Helper helper : allHelpers) helper.beforeInit();
		currentPhase = OpModePhase.INITIALIZE;

		//Awake all behaviors
		for (int i = 0; i < allBehaviors.size(); i++) allBehaviors.get(i).awake(hardwareMap);
		for (Helper helper : allHelpers) helper.afterInit();

		//Register all config options
		allConfigOptions = new ArrayList<>();
		Set<Input.Button> buttons = new HashSet<>();

		appendConfigOptions(allConfigOptions);

		//Check to make sure that no two options use the same button
		for (int i = 0; i < allConfigOptions.size(); i++)
		{
			ConfigOption option = allConfigOptions.get(i);

			for (int j = 0; j < option.getButtonCount(); j++)
			{
				Input.Button button = option.getButton(j);

				if (buttons.add(button)) input.registerButton(getConfigOptionInputSource(), button);
				else throw new IllegalArgumentException("Two config options are using the same button " + button.name());
			}
		}

		//Telemetry
		telemetry.update();
	}

	@Override
	public void init_loop()
	{
		super.init_loop();

		//Update
		currentPhase = OpModePhase.INIT_LOOP;

		for (int i = 0; i < allBehaviors.size(); i++) allBehaviors.get(i).awakeUpdate();
		for (Helper helper : allHelpers) helper.initLoop();

		//Update config options
		Input.Source source = getConfigOptionInputSource();
		StringBuilder builder = new StringBuilder();

		for (ConfigOption option : allConfigOptions)
		{
			builder.append(option.getLabel());
			builder.append(" (");

			for (int i = 0; i < option.getButtonCount(); i++)
			{
				Input.Button button = option.getButton(i);
				builder.append(button.name());

				builder.append(i == option.getButtonCount() - 1 ? ')' : ',');
				if (input.getButtonDown(source, button)) option.onButtonDown(button);
			}

			debug.addData(builder.toString(), option.getOption());
			builder.setLength(0); //Clears builder
		}

		//Telemetry
		telemetry.update();
	}

	@Override
	public void start()
	{
		super.start();

		currentPhase = OpModePhase.START;

		for (Helper helper : allHelpers) helper.beforeStart();
		for (Behavior behavior : allBehaviors) behavior.start();
		for (Helper helper : allHelpers) helper.afterStart();

		telemetry.update(); //Telemetry
	}

	@Override
	public void loop()
	{
		currentPhase = OpModePhase.LOOP;

		for (Helper helper : allHelpers) helper.beforeUpdate();

		//Update sequence
		if (hasSequence())
		{
			boolean completed = runningSequence.run();
			if (completed) runningSequence = null;
		}

		for (Behavior behavior : allBehaviors) behavior.update();
		for (Helper helper : allHelpers) helper.afterUpdate();

		telemetry.update(); //Telemetry
	}

	@Override
	public void stop()
	{
		super.stop();

		currentPhase = OpModePhase.STOP;

		for (Helper helper : allHelpers) helper.beforeStop();
		for (Behavior behavior : allBehaviors) behavior.stop();
		for (Helper helper : allHelpers) helper.afterStop();

		telemetry.update(); //Telemetry
		currentPhase = OpModePhase.INVALID;
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

		public void beforeUpdate()
		{
		}

		public void afterUpdate()
		{
		}

		public void beforeStop()
		{
		}

		public void afterStop()
		{
		}
	}

	/**
	 * A helper class used for sorting the behaviors.
	 */
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