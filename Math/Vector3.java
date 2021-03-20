package FTCEngine.Math;

public final class Vector3
{
	public Vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(Vector2 xy)
	{
		this(xy, 0f);
	}

	public Vector3(Vector2 xy, float z)
	{
		this.x = xy.x;
		this.y = xy.y;
		this.z = z;
	}

	public final float x;
	public final float y;
	public final float z;

	public static final Vector3 zero = new Vector3(0f, 0f, 0f);
	public static final Vector3 one = new Vector3(1f, 1f, 1f);

	public static final Vector3 right = new Vector3(1f, 0f, 0f);
	public static final Vector3 left = new Vector3(-1f, 0f, 0f);
	public static final Vector3 up = new Vector3(0f, 1f, 0f);
	public static final Vector3 down = new Vector3(0f, -1f, 0f);
	public static final Vector3 forward = new Vector3(0f, 0f, 1f);
	public static final Vector3 backward = new Vector3(0f, 0f, -1f);


	/**
	 * Adds the two vector3s together
	 */
	public Vector3 add(Vector3 other) {return new Vector3(x + other.x, y + other.y, z + other.z);}

	/**
	 * Subtracts the two vector3s
	 */
	public Vector3 sub(Vector3 other) {return new Vector3(x - other.x, y - other.y, z - other.z);}

	/**
	 * Multiplies all of the elements with other
	 */
	public Vector3 mul(float other) {return new Vector3(x * other, y * other, z * other);}

	/**
	 * Divides all of the elements with other
	 */
	public Vector3 div(float other) {return new Vector3(x / other, y / other, z / other);}

	/**
	 * Scales this vector by other
	 */
	public Vector3 scale(Vector3 other) {return new Vector3(x * other.x, y * other.y, z * other.z);}

	/**
	 * Change the magnitude of the vector to one, without modifying its direction
	 */
	public Vector3 normalize()
	{
		float magnitude = getMagnitude();
		return Mathf.almostEquals(magnitude, 0f) ? zero : div(magnitude);
	}

	/**
	 * Normalizes each of the axis values separately
	 */
	public Vector3 individualNormalize()
	{
		return new Vector3(Mathf.normalize(x), Mathf.normalize(y), Mathf.normalize(z));
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
			case 2: return z;
		}

		throw new IndexOutOfBoundsException();
	}

	/**
	 * Returns a Vector2 with this vector3's x and y
	 */
	public Vector2 toXY()
	{
		return new Vector2(x, y);
	}

	/**
	 * Returns a Vector2 with this vector3's x and z
	 */
	public Vector2 toXZ()
	{
		return new Vector2(x, z);
	}

	/**
	 * Gets the magnitude of this vector
	 */
	public float getMagnitude()
	{
		return (float)Math.sqrt((double)x * x + (double)y * y + (double)z * z);
	}

	/**
	 * Gets the squared magnitude of this vector,
	 * reduces a call to sqrt which can be expensive
	 */
	public float getMagnitudeSquared()
	{
		return x * x + y * y + z * z;
	}

	/**
	 * Returns the distance between the two input vectors
	 */
	public static float distance(Vector3 vector1, Vector3 vector2)
	{
		return vector1.sub(vector2).getMagnitude();
	}

	/**
	 * Returns the squared distance between the two input vectors,
	 * reduces a call to sqrt which can be expensive
	 */
	public static float distanceSquared(Vector3 vector1, Vector3 vector2)
	{
		return vector1.sub(vector2).getMagnitudeSquared();
	}

	/**
	 * Returns the dot product of the two vectors
	 */
	public static float dot(Vector3 vector1, Vector3 vector2)
	{
		return vector1.x * vector2.x + vector1.y * vector2.y + vector1.z * vector2.z;
	}

	/**
	 * Returns the angle in degrees between the two vectors, with respect to the origin
	 */
	public static float angle(Vector3 vector1, Vector3 vector2)
	{
		float divider = (float)Math.sqrt((double)vector1.getMagnitudeSquared() * (double)vector2.getMagnitudeSquared());
		if (Mathf.almostEquals(divider, 0f)) return 0f;

		return (float)Math.acos((double)Mathf.clamp(dot(vector1, vector2) / divider, -1f, 1f)) * Mathf.Radian2Degree;
	}

	public static Vector3 lerp(Vector3 start, Vector3 end, float time)
	{
		time = Mathf.clamp01(time);
		return new Vector3(Mathf.lerpUnclamped(start.x, end.x, time), Mathf.lerpUnclamped(start.y, end.y, time), Mathf.lerpUnclamped(start.z, end.z, time));
	}

	public static Vector3 lerpUnclamped(Vector3 start, Vector3 end, float time)
	{
		return new Vector3(Mathf.lerpUnclamped(start.x, end.x, time), Mathf.lerpUnclamped(start.y, end.y, time), Mathf.lerpUnclamped(start.z, end.z, time));
	}

	@Override
	public int hashCode()
	{
		final int Prime = 104827;

		int result = 105691;

		result = result * Prime + Float.floatToIntBits(x);
		result = result * Prime + Float.floatToIntBits(y);
		result = result * Prime + Float.floatToIntBits(z);

		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Vector3)) return false;
		return Mathf.almostEquals(distanceSquared(this, (Vector3)obj), 0f);
	}

	@Override
	public String toString()
	{
		return String.format("(%.4f, %.4f, %.4f)", x, y, z);
	}
}
