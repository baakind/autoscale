package no.uio.master.autoscale.token;

/**
 * Each token-generator should implement this interface.
 * @author andreas
 */
public interface TokenGenerator<T> {

	/**
	 * Generate new token based on <tt>a</tt> and <tt>b</tt>.
	 * @param a
	 * @param b
	 * @return
	 */
	public T generateToken(T a, T b);
}
