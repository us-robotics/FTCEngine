package FTCEngine.Test;

import java.text.DecimalFormat;

import FTCEngine.Math.Vector2;

public class TestMain
{
	public static void main(String[] args)
	{
		Vector2 a = new Vector2(-3f, 0f);
		Vector2 b = new Vector2(3f, 0f);

		System.out.println(Vector2.signedAngle(a, b));
	}
}
