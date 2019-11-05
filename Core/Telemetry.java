package FTCEngine.Core;

public class Telemetry extends OpModeBase.Helper
{
	public Telemetry(OpModeBase opMode)
	{
		super(opMode);
	}

	public void addData(String caption, Object value)
	{
		opMode.telemetry.addData(caption, value);
	}
}
