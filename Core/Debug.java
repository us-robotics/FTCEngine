package FTCEngine.Core;

public class Debug extends OpModeBase.Helper
{
	public Debug(OpModeBase opMode)
	{
		super(opMode);
	}

	public void addData(String caption, Object value)
	{
		opMode.telemetry.addData(caption, value);
	}

	public void addData(String caption)
	{
		opMode.telemetry.addData(caption, "");
	}
}
