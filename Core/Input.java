package FTCEngine.Core;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import FTCEngine.Experimental.Func;
import FTCEngine.Helpers.CollectionHelper;
import FTCEngine.Math.Vector2;

public class Input extends Main.Helper
{
	public Input(Main opMode)
	{
		super(opMode);
	}

	private ArrayList<ButtonState> registeredButtons = new ArrayList<ButtonState>();

	private HashMap<Button, Func<Gamepad, Boolean>> buttonToAccessor = new HashMap<Button, Func<Gamepad, Boolean>>()
	{{
		put(Button.IDK, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.a;}
		});

		put(Button.SOMEOTHERKEY, new Func<Gamepad, Boolean>()
		{
			@Override
			public Boolean apply(Gamepad input) {return input.b;}
		});
	}};

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

	public Vector2 getVector(Source source, Joystick joystick)
	{
		checkPhase();
		Gamepad gamepad = getGamepad(source);

		switch (joystick)
		{
			case LEFT: return new Vector2(gamepad.left_stick_x, gamepad.left_stick_y);
			case RIGHT: return new Vector2(gamepad.right_stick_x, gamepad.right_stick_y);
		}

		throw new IllegalArgumentException("Joystick (" + joystick + ") is illegal");
	}

	public Vector2 getDirection(Source source, Joystick joystick)
	{
		return getVector(source, joystick).normalize();
	}

	public float getMagnitude(Source source, Joystick joystick)
	{
		return getVector(source, joystick).getMagnitude();
	}

	@Override
	public void beforeLoop()
	{
		super.beforeLoop();
	}

	private void checkPhase()
	{
		if (opMode.getPhase() != OpModePhase.LOOP) throw new IllegalStateException("Cannot access input outside of the core loop!");
	}

	public enum Button
	{
		IDK,
		RANDOMTHINGS,
		SOMEOTHERKEY;

		public static final int length = Button.values().length;
	}

	public enum Joystick
	{
		LEFT,
		RIGHT;

		public static final int length = Joystick.values().length;
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

		public void updateButton()
		{
//TODO


		}
	}

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