package FTCEngine.Test;

import org.firstinspires.ftc.teamcode.TestOp;

import java.text.DecimalFormat;

import FTCEngine.Math.Vector2;

public class TestMain
{
	public static void main(String[] args)
	{
		TestOp op = new TestOp();

		op.init();
		op.start();

		while (true)
		{
			op.loop();
		}
	}
}
