package FTCEngine.Experimental;

/**
 * An implementation of Java's Function interface or C#'s Func delegate
 */
public interface Func<T, U>
{
	U apply(T input);
}