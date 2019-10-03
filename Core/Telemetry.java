package FTCEngine.Core;

public class Telemetry extends Main.Helper
{
	public Telemetry(Main opMode)
	{
		super(opMode);
	}

	public void addData(String caption, Object value)
	{
		opMode.telemetry.addData(caption, value);
	}
}
