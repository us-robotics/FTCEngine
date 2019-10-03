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
	public static float clamp01(float value)
	{
		return clamp(value, 0f, 1f);
	}

	/**
	 * Linear-interpolation, returns a value between START and end based on the percentage of time
	 */
	public static float lerp(float start, float end, float time)
	{
		return lerpUnclamped(start, end, clamp01(time));
	}

	/**
	 * Linear-interpolation without clamping the time variable between 0 and 1
	 */
	public static float lerpUnclamped(float start, float end, float time)
	{
		return (end - start) * time + start;
	}

	/**
	 * Inverse linear-interpolation
	 */
	public static float inverseLerp(float start, float end, float value)
	{
		return (value - start) / (end - start);
	}

	/**
	 * Convert value to an angle between -180 (Exclusive) and 180 (Inclusive) with the same rotational value as input.
	 */
	public static float toSignedAngle(float value)
	{
		return repeat(-(value + 180f), 360f) + 180f;
	}

	/**
	 * Convert value to an angle between -179 and 180 with the same rotational value as input.
	 */
	public static int toSignedAngle(int value)
	{
		return repeat(-(value + 180), 360) + 180;
	}

	/**
	 * Convert value to an angle between 0f (Inclusive) and 360f (Exclusive) with the same rotational value as input.
	 */
	public static float toUnsignedAngle(float value)
	{
		return repeat(value, 360f);
	}

	/**
	 * Convert value to an angle between 0 and 359 with the same rotational value as input.
	 */
	public static int toUnsignedAngle(int value)
	{
		return repeat(value, 360);
	}

	/**
	 * Wraps value around length so it is always between 0f (Inclusive) and length (Exclusive)
	 */
	public static float repeat(float value, float length)
	{
		float result = value % length;
		return result < 0f ? result + length : result;
	}

	/**
	 * Wraps value around length so it is always between 0 (Inclusive) and length (Exclusive)
	 */
	public static int repeat(int value, int length)
	{
		int result = value % length;
		return result < 0 ? result + length : result;
	}
}
