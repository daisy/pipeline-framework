package org.daisy.common.base;


/**
 * Common behaviour for filters.
 *
 * @param <T> the generic type
 */
public interface Filter<T> {
	
	/**
	 * Filters the input parameter
	 *
	 * @param in the in
	 * @return the t
	 */
	T filter(T in);
}
