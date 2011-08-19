package org.daisy.pipeline.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.daisy.common.base.Provider;

import com.google.common.collect.ImmutableMap;

public final class ZipResourceContext implements ResourceCollection {
	private final Map<String, Provider<InputStream>> resources;

	public ZipResourceContext(final ZipFile zip) {
		ImmutableMap.Builder<String, Provider<InputStream>> mapBuilder = ImmutableMap
				.builder();
		Enumeration<? extends ZipEntry> entries = zip.entries();
		while (entries.hasMoreElements()) {
			final ZipEntry entry = entries.nextElement();
			mapBuilder.put(entry.getName(), new Provider<InputStream>() {

				@Override
				public InputStream provide() {
					try {
						return zip.getInputStream(entry);
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}

			});
		}
		this.resources = mapBuilder.build();
	}

	@Override
	public Iterable<Provider<InputStream>> getResources() {
		return resources.values();
	}

	@Override
	public Provider<InputStream> getResource(String name) {
		return resources.get(name);
	}

	@Override
	public Iterable<String> getNames() {
		return resources.keySet();
	}

}
