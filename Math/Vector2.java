package FTCEngine.Math;

public final class Vector2
{
	public Vector2(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

	public final float x;
	public final float y;

	public static final Vector2 zero = new Vector2(0f, 0f);
	public static final Vector2 one = new Vector2(1f, 1f);

	public static final Vector2 right = new Vector2(1f, 0f);
	public static final Vector2 left = new Vector2(-1f, 0f);
	public static final Vector2 up = new Vector2(0f, 1f);
	public static final Vector2 down = new Vector2(0f, -1f);

	/**
	 * Adds the two vector2s together
	 */
	public Vector2 add(Vector2 other) {return new Vector2(x + other.x, y + other.y);}

	/**
	 * Subtracts the two vector2s
	 */
	public Vector2 sub(Vector2 other) {return new Vector2(x - other.x, y - other.y);}

	/**
	 * Multiplies all of the elements with other
	 */
	public Vector2 mul(float other) {return new Vector2(x * other, y * other);}

	/**
	 * Divides all of the elements with other
	 */
	public Vector2 div(float other) {return new Vector2(x / other, y / other);}

	/**
	 * Scales this vector by other
	 */
	public Vector2 scale(Vector2 other) {return new Vector2(x * other.x, y * other.y);}

	public Vector2 normalize()
	{
		float magnitude = getMagnitude();
		return Mathf.almostEquals(magnitude, 0f) ? zero : div(magnitude);
	}

	/**
	 * Rotates this vector by angle in degrees
	 */
	public Vector2 rotate(float angle)
	{
		angle *= Mathf.Degree2Radian;

		float sin = (float)Math.sin(angle);
		float cos = (float)Math.cos(angle);

		return new Vector2(cos * x - sin * y, sin * x + cos * y);
	}

	/**
	 * Returns an element based on index, x = 0, y = 1, z = 2
	 */
	public float get(int index)
	{
		switch (index)
		{
			case 0: return x;
			case 1: return y;
		}

		throw new IndexOutOfBoundsException();
	}

	/**
	 * Gets the magnitude of this vector
	 */
	public float getMagnitude()
	{
		return (float)Math.sqrt((double)x * x + (double)y * y);
	}

	/**
	 * Gets the squared magnitude of this vector,
	 * reduces a call to sqrt which can be expensive
	 */
	public float getMagnitudeSquared()
	{
		return x * x + y * y;
	}

	/**
	 * Gets angle with respect to origin in degrees
	 * Returned value between [0f,360f)
	 */
	private float getAngle()
	{
		return Mathf.toUnsignedAngle((float)Math.atan2(y, x) * Mathf.Radian2Degree);
	}

	/**
	 * Returns the distance between the two input vectors
	 */
	public static float distance(Vector2 vector1, Vector2 vector2)
	{
		return vector1.sub(vector2).getMagnitude();
	}

	/**
	 * Returns the squared distance between the two input vectors,
	 * reduces a call to sqrt which can be expensive
	 */
	public static float distanceSquared(Vector2 vector1, Vector2 vector2)
	{
		return vector1.sub(vector2).getMagnitudeSquared();
	}

	/**
	 * Returns the dot product of the two vectors
	 */
	public static float dot(Vector2 vector1, Vector2 vector2)
	{
		return vector1.x * vector2.x + vector1.y * vector2.y;
	}

	/**
	 * Returns the angle in degrees between the two vectors, with respect to the origin
	 * Value returned guaranteed to be the smallest possible angle between the two vectors,
	 * so the value is never smaller than 0 and never larger than 180
	 */
	public static float angle(Vector2 vector1, Vector2 vector2)
	{
		float angle = Math.abs(vector1.getAngle() - vector2.getAngle());
		return angle > 180f ? 360f - angle : angle;
	}

	/**
	 * Returns the angle in degrees from vector1 to vector2, with respect to the origin
	 * Value returned guaranteed to be never smaller than -180 and never larger than 180
	 */
	public static float signedAngle(Vector2 vector1, Vector2 vector2)
	{
		float angle = vector2.getAngle() - vector1.getAngle();
		return angle > 180f ? angle - 360f : angle;
	}

	public static Vector2 lerp(Vector2 start, Vector2 end, float time)
	{
		time = Mathf.clamp01(time);
		return new Vector2(Mathf.lerpUnclamped(start.x, end.x, time), Mathf.lerpUnclamped(start.y, end.y, time));
	}

	public static Vector2 lerpUnclamped(Vector2 start, Vector2 end, float time)
	{
		return new Vector2(Mathf.lerpUnclamped(start.x, end.x, time), Mathf.lerpUnclamped(start.y, end.y, time));
	}

	@Override
	public int hashCode()
	{
		final int Prime = 104827;

		int result = 105691;

		result = result * Prime + Float.floatToIntBits(x);
		result = result * Prime + Float.floatToIntBits(y);

		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Vector2)) return false;
		return Mathf.almostEquals(distanceSquared(this, (Vector2)obj), 0f);
	}

	@Override
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}
}
