package FTCEngine.Delegates;

/**
 * An implementation of Java's Consumer interface or C#'s Action delegate
 */
public interface Action<T>
{
	void accept(T input);
}