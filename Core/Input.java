package FTCEngine.Core;

import com.qualcomm.robotcore.hardware.Gamepad;

import FTCEngine.Math.Vector2;

public class Input extends Main.Helper
{
	public Input(Main main)
	{
		super(main);
	}

	public Gamepad getGamepad(Source source)
	{
		switch (source)
		{
			case controller1: return main.gamepad1;
			case controller2: return main.gamepad2;
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
			case left: return new Vector2(gamepad.left_stick_x, gamepad.left_stick_y);
			case right: return new Vector2(gamepad.right_stick_x, gamepad.right_stick_y);
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
		if (main.getPhase() != OpModePhase.loop) throw new IllegalStateException("Cannot access input outside of the core loop!");
	}

	public enum Button
	{
		idk,
		randomthings,
		someotherkey
	}

	public enum Joystick
	{
		left,
		right
	}

	public enum Source
	{
		controller1,
		controller2
	}
}