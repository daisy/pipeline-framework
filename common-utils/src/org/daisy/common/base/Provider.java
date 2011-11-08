package org.daisy.common.base;


/**
 * Provider interface.
 *
 * @param <T> the generic type
 */
public interface Provider<T> {
	
	/**
	 * Provides an instance of T.
	 *
	 * @return the t
	 */
	T provide();
}
