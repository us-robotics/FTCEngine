package FTCEngine.Core;

import com.qualcomm.robotcore.hardware.Gamepad;

import java.util.ArrayList;

import FTCEngine.Math.Vector2;

public class Input extends Main.Helper
{
	public Input(Main opMode)
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

	}

	private ButtonState getButtonState(Source source, Button button)
	{
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

	public boolean getButtonDown(Source source, Button button)
	{
		throw new UnsupportedOperationException();
	}

	public boolean getButtonUp(Source source, Button button)
	{
		throw new UnsupportedOperationException();
	}

	public Vector2 getVector(Source source, Joystick joystick)
	{
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
		if (opMode.getPhase() != OpModePhase.LOOP) throw new IllegalStateException("Cannot access input outside of the core LOOP!");
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

	}
}