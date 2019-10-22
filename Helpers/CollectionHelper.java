package FTCEngine.Helpers;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CollectionHelper
{
	/**
	 * Sort the list using Collections.sort using the priorities extracted by the PriorityExtractor to determine order
	 */
	public static <T> void sort(List<T> list, PriorityExtractor<T> extractor)
	{
		Collections.sort(list, getComparatorFromExtractor(extractor));
	}

	/**
	 * Binary search the list, but compares the items using the priorities extracted by the PriorityExtractor
	 * Returns an negative number if not fount. You can bit flip the not found result to get where the key
	 * would be if it in the list
	 */
	public static <T> int binarySearch(List<T> list, T key, PriorityExtractor<T> extractor)
	{
		return binarySearch(list, extractor, extractor.getPriority(key));
	}

	/**
	 * Binary search the list, but compares the items using the priorities extracted by the PriorityExtractor
	 * NOTE: This method enables you to search the list with a different type of item, thanks to the extractors
	 * Returns an negative number if not fount. You can bit flip the not found result to get where the key
	 * would be if it in the list
	 */
	public static <T, U> int binarySearch(List<T> list, PriorityExtractor<T> elementExtractor, U key, PriorityExtractor<U> keyExtractor)
	{
		return binarySearch(list, elementExtractor, keyExtractor.getPriority(key));
	}

	/**
	 * Binary search the list, but compares the items using the priorities extracted by the PriorityExtractor
	 * NOTE: This method enables you to just pass in an integer as the priority of the key
	 * Returns an negative number if not fount. You can bit flip the not found result to get where the key
	 * would be if it in the list
	 */
	public static <T> int binarySearch(List<T> list, PriorityExtractor<T> elementExtractor, int keyPriority)
	{
		if (list.size() == 0) return ~0;

		int minIndex = 0;
		int maxIndex = list.size() - 1;

		int index = (minIndex + maxIndex) / 2;
		T current = list.get(index);

		while (true)
		{
			int compared = Integer.compare(elementExtractor.getPriority(current), keyPriority);

			if (compared == 0) return index;

			if (compared > 0) maxIndex = index - 1;
			else minIndex = index + 1;

			index = (minIndex + maxIndex) / 2;
			if (minIndex > maxIndex) return ~minIndex;

			current = list.get(index);
		}
	}

	private static <T> Comparator<T> getComparatorFromExtractor(final PriorityExtractor<T> extractor)
	{
		return new Comparator<T>()
		{
			@Override
			public int compare(T t1, T t2)
			{
				return Integer.compare(extractor.getPriority(t1), extractor.getPriority(t2));
			}
		};
	}

	public static interface PriorityExtractor<T>
	{
		int getPriority(T item);
	}
}
