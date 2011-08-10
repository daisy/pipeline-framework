package org.daisy.pipeline.job;

public interface Filter<T> {
	T filter(T in);
}
