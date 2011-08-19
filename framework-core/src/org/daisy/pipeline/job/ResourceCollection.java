package org.daisy.pipeline.job;

import java.io.InputStream;

import org.daisy.common.base.Provider;

public interface ResourceCollection {
	Iterable<Provider<InputStream>> getResources();

	Iterable<String> getNames();

	Provider<InputStream> getResource(String name);
}
