package FTCEngine.Core;

public enum OpModePhase
{
	/**
	 * Any non-engine code should not be run when the op mode is in this phase
	 */
	INVALID,
	INITIALIZE,
	START,
	LOOP,
	STOP
}
