package FTCEngine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Debug
{
	private static final Object[] paramsArrayCache = new Object[4];
	private static final LinkedBlockingQueue<String> logList = new LinkedBlockingQueue<String>();

	private static int logListCapacity = 30;

	public static int getLogListCapacity()
	{
		return logListCapacity;
	}

	public static void setLogListCapacity(int logListCapacity)
	{
		if (logListCapacity <= 0) throw new IllegalArgumentException("logListCapacity must be a positive number!");

		Debug.logListCapacity = logListCapacity;
		tryTrimLogList();
	}

	public static void log(Object... objects)
	{
		logInternal(objects, objects.length);
	}

	public static void log(Object object0)
	{
		paramsArrayCache[0] = object0;
		logInternal(paramsArrayCache, 1);
	}

	public static void log(Object object0, Object object1)
	{
		paramsArrayCache[0] = object0;
		paramsArrayCache[1] = object1;
		logInternal(paramsArrayCache, 2);
	}

	public static void log(Object object0, Object object1, Object object2)
	{
		paramsArrayCache[0] = object0;
		paramsArrayCache[1] = object1;
		paramsArrayCache[2] = object2;
		logInternal(paramsArrayCache, 3);
	}

	public static void log(Object object0, Object object1, Object object2, Object object3)
	{
		paramsArrayCache[0] = object0;
		paramsArrayCache[1] = object1;
		paramsArrayCache[2] = object2;
		paramsArrayCache[3] = object3;
		logInternal(paramsArrayCache, 4);
	}

	/**
	 * Returns a multi-line string combining the first several logged strings
	 *
	 * @param maxCount The line count of the returning string (or how many logs are we going to return)
	 */
	public static String getLogged(int maxCount)
	{
		if (maxCount <= 0) throw new IllegalArgumentException("maxCount must be a positive number");

		StringBuilder builder = new StringBuilder();
		int length = Math.min(maxCount, logList.size());

		int index = 0;

		for (String string : logList)
		{
			builder.append(string);

			if (++index == length) break;
			builder.append(System.getProperty("line.separator"));
		}

		return builder.toString();
	}

	/**
	 * @param objects Logs objects in this array
	 * @param length  will only process objects from index 0 to this value [inclusive, exclusive)
	 */
	private static void logInternal(Object[] objects, int length)
	{
		for (int i = 0; i < length; i++)
		{
			logList.add(toString(objects[i]));
			tryTrimLogList();
		}
	}

	private static String toString(Object target)
	{
		//NOTE: This implementation might change
		return target.toString();
	}

	/**
	 * Trim log list to correct size if needed
	 */
	private static void tryTrimLogList()
	{
		while (logList.size() > getLogListCapacity()) logList.remove();
	}
}
