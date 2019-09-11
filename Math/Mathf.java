package FTCEngine.Math;

public class Mathf
{
	public static final float Degree2Radian = (float)(Math.PI / 180d);
	public static final float Radian2Degree = (float)(180d / Math.PI);

	/**
	 * Returns true if value1 and value2 are approximately the same
	 * Use this instead of the double equals sign
	 */
	public static boolean almostEquals(float value1, float value2)
	{
		return almostEquals(value1, value2, 0.00001f);
	}

	/**
	 * Returns true if value1 and value2 are approximately the same
	 * Use this instead of the double equals sign
	 *
	 * @param epsilon The amount of tolerance for the comparision, default is 0.00001f
	 */
	public static boolean almostEquals(float value1, float value2, float epsilon)
	{
		if (value1 == value2) return true;

		final float Min = (1L << 23) * Float.MIN_VALUE;
		float difference = Math.abs(value1 - value2);

		if (value1 == 0d || value2 == 0d || difference < Min) return difference < epsilon * Min;
		return difference / (Math.abs(value1) + Math.abs(value2)) < epsilon;
	}

	/**
	 * Clamps value to be not lower than min and not higher than max
	 */
	public static float clamp(float value, float min, float max)
	{
		return Math.min(max, Math.max(min, value));
	}

	/**
	 * Clamps value to be not lower than 0 and not higher than 1
	 */
	public static float Clamp01(float value)
	{
		return clamp(value, 0f, 1f);
	}
}
