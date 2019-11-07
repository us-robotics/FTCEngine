package FTCEngine.Core;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;
import java.util.HashMap;

import FTCEngine.Experimental.Func;
import FTCEngine.Helpers.CollectionHelper;
import FTCEngine.Math.Vector2;

public class Input extends OpModeBase.Helper
{
	public Input(OpModeBase opMode)
	{
		super(opMode);
	}

	private ArrayList<ButtonState> registeredButtons = new ArrayList<ButtonState>();

	/**
	 * Tell the input that the program is going to use this button
	 * The methods would not work if you do not register the button!
	 * You cannot register any buttons anymore after entering the main LOOP
	 */
	public void registerButton(Source source, Button button)
	{
		if (opMode.getPhase() != OpModePhase.INITIALIZE && opMode.getPhase() != OpModePhase.START) throw new IllegalStateException("Invalid OpMode state to register button: " + opMode.getPhase());

		int index = CollectionHelper.binarySearch(registeredButtons, PriorityExtractor.instance, PriorityExtractor.getPriority(source, button));
		if (index >= 0) return; //Button already registered

		registeredButtons.add(~index, new ButtonState(source, button));
	}

	private ButtonState getButtonState(Source source, Button button)
	{
		int index = CollectionHelper.binarySearch(registeredButtons, PriorityExtractor.instance, PriorityExtractor.getPriority(source, button));

		if (index >= 0) return registeredButtons.get(index);
		throw new IllegalArgumentException("No registered button with " + source + " and " + button);
	}

	public Gamepad getGamepad(Source source)
	{
		switch (source)
		{
			case CONTROLLER_1: return opMode.gamepad1;
			case CONTROLLER_2: return opMode.gamepad2;
		}

		throw new IllegalArgumentException("Source (" + source + ") is an illegal source for gamepad");
	}

	public boolean getButton(Source source, Button button)
	{
		checkPhase();
		return getButtonState(source, button).currentPressed;
	}

	public boolean getButtonDown(Source source, Button button)
	{
		checkPhase();

		ButtonState state = getButtonState(source, button);
		return state.isCurrentPressed() && !state.isPreviousPressed();
	}

	public boolean getButtonUp(Source source, Button button)
	{
		checkPhase();

		ButtonState state = getButtonState(source, button);
		return !state.isCurrentPressed() && state.isPreviousPressed();
	}

	public float getTrigger(Source source, Button trigger)
	{
		checkPhase();
		Gamepad gamepad = getGamepad(source);

		switch (trigger)
		{
			case LEFT_TRIGGER: return gamepad.left_trigger;
			case RIGHT_TRIGGER: return gamepad.right_trigger;
		}

		throw new IllegalArgumentException("Trigger (" + trigger + ") is illegal");
	}

	public Vector2 getVector(Source source, Button joystick)
	{
		checkPhase();
		Gamepad gamepad = getGamepad(source);

		switch (joystick)
		{
			case LEFT_JOYSTICK: return new Vector2(gamepad.left_stick_x, -gamepad.left_stick_y);
			case RIGHT_JOYSTICK: return new Vector2(gamepad.right_stick_x, -gamepad.right_stick_y);
		}

		throw new IllegalArgumentException("Joystick (" + joystick + ") is illegal");
	}

	public Vector2 getDirection(Source source, Button joystick)
	{
		return getVector(source, joystick).normalize();
	}

	public float getMagnitude(Source source, Button joystick)
	{
		return getVector(source, joystick).getMagnitude();
	}

	@Override
	public void beforeLoop()
	{
		super.beforeLoop();

		for (int i = 0; i < registeredButtons.size(); i++)
		{
			registeredButtons.get(i).updateButton(this);
		}
	}

	private void checkPhase()
	{
		if (opMode.getPhase() != OpModePhase.LOOP) throw new IllegalStateException("Cannot access input outside of the core loop!");
	}

	public enum Button
	{
		A,
		B,
		X,
		Y,
		START,
		BACK,
		GUIDE,
		DPAD_RIGHT,
		DPAD_LEFT,
		DPAD_UP,
		DPAD_DOWN,

		LEFT_BUMPER,
		RIGHT_BUMPER,

		LEFT_TRIGGER,
		RIGHT_TRIGGER,

		LEFT_JOYSTICK,
		RIGHT_JOYSTICK;

		public static final int length = Button.values().length;
	}

	public enum Source
	{
		CONTROLLER_1,
		CONTROLLER_2;

		public static final int length = Source.values().length;
	}

	private static class ButtonState
	{
		public ButtonState(Source source, Button button)
		{
			if (!allButtonToAccessor.containsKey(button)) throw new IllegalArgumentException("The button (" + button + ") cannot be registered.");

			this.source = source;
			this.button = button;
		}

		public final Source source;
		public final Button button;

		private boolean currentPressed; //WHY CAN THE OUTER CLASS ACCESS THESE TWO FIELDS??????
		private boolean previousPressed; //JAVA IS SO BAD

		public boolean isCurrentPressed()
		{
			return currentPressed;
		}
		public boolean isPreviousPressed()
		{
			return previousPressed;
		}

		public void updateButton(Input input)
		{
			boolean isPressed = allButtonToAccessor.get(button).apply(input.getGamepad(source));

			previousPressed = isCurrentPressed();
			currentPressed = isPressed;
		}
	}

	private static HashMap<Button, Func<Gamepad, Boolean>> allButtonToAccessor = new HashMap<Button, Func<Gamepad, Boolean>>()
	{{
		put(Button.A, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.a;}
		});

		put(Button.B, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.b;}
		});

		put(Button.X, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.x;}
		});

		put(Button.Y, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.y;}
		});

		put(Button.START, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.start;}
		});

		put(Button.BACK, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.back;}
		});

		put(Button.GUIDE, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.guide;}
		});

		put(Button.DPAD_RIGHT, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.dpad_right;}
		});

		put(Button.DPAD_LEFT, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.dpad_left;}
		});

		put(Button.DPAD_UP, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.dpad_up;}
		});

		put(Button.DPAD_DOWN, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.dpad_down;}
		});

		put(Button.LEFT_BUMPER, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.left_bumper;}
		});

		put(Button.RIGHT_BUMPER, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.right_bumper;}
		});
	}};

	private static class PriorityExtractor implements CollectionHelper.PriorityExtractor<ButtonState>
	{
		public static final PriorityExtractor instance = new PriorityExtractor();

		@Override
		public int getPriority(ButtonState item)
		{
			return getPriority(item.source, item.button);
		}

		public static int getPriority(Source source, Button button)
		{
			return source.ordinal() * Button.length + button.ordinal();
		}
	}
}