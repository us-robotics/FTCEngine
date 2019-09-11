package FTCEngine.Core.Auto;

import FTCEngine.Core.Behavior;
import FTCEngine.Core.Main;

public abstract class AutoBehavior extends Behavior
{
	public AutoBehavior(Main opMode)
	{
		super(opMode);
	}

	Procedure currentProcedure;

	protected final Procedure getCurrentProcedure()
	{
		return currentProcedure;
	}

	@Override
	public final void update()
	{
		super.update();


	}

	/**
	 * Updates the robot with its procedure
	 * @return Did the robot finish its procedure on this behavior?
	 */
	protected abstract boolean updateProcedure();
}
