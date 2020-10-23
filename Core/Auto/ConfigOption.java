package FTCEngine.Core.Auto;

import FTCEngine.Core.Input;

public interface ConfigOption
{
	String getLabel();
	String getOption();

	/**
	 * Should return the number of buttons that you want to use to configure this option.
	 */
	int getButtonCount();

	/**
	 * Should return the button that you want to use at index.
	 * Index will not be smaller than 0 nor larger than or equals to getButtonCount
	 */
	Input.Button getButton(int index);

	/**
	 * This method is invoked when a button you indicated above is pressed down.
	 * You should use this method to modify the config option.
	 */
	void onButtonDown(Input.Button button);
}
