package FTCEngine.Core.Auto;

public enum Alliance
{
	blue,
	red;

	public Alliance toggle()
	{
		if (this == blue) return red;
		if (this == red) return blue;
		return null;
	}
}
