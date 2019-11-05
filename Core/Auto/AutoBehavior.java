package FTCEngine.Core.Auto;

import FTCEngine.Core.Behavior;
import FTCEngine.Core.OpModeBase;

public abstract class AutoBehavior<E> extends Behavior
{
	public AutoBehavior(OpModeBase opMode)
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

	public abstract void receiveParameter(E parameter);
}
