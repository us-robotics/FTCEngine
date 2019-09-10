package FTCEngine.Math;

public final class Vector3
{
	public Vector3(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public final float x;
	public final float y;
	public final float z;

	public float get(int index)
	{
		switch (index)
		{
			case 0: return x;
			case 1: return y;
			case 2: return z;
		}
	}

	public float getMagnitude()
	{
		return Math.sqrt(x * x + y * y + z * z);
	}

	public float distance(Vector3 vector1, Vector3 vector2)
	{
		return (vector1 - vector2).getMagniude();
	}

	public float getDot()
	{
		return
	}
}
