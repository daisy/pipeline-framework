package org.daisy.pipeline.job;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.daisy.common.base.Provider;

import com.google.common.collect.ImmutableMap;

// TODO: Auto-generated Javadoc
/**
 * The Class ZipResourceContext wrapps a zip file into a resource collection, it is used as context to execute pipelines.
 */
public final class ZipResourceContext implements ResourceCollection {

	/** The resources. */
	private final Map<String, Provider<InputStream>> resources;

	/**
	 * Instantiates a new zip resource context.
	 *
	 * @param zip the zip
	 */
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
		resources = mapBuilder.build();
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.ResourceCollection#getResources()
	 */
	@Override
	public Iterable<Provider<InputStream>> getResources() {
		return resources.values();
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.ResourceCollection#getResource(java.lang.String)
	 */
	@Override
	public Provider<InputStream> getResource(String name) {
		return resources.get(name);
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.job.ResourceCollection#getNames()
	 */
	@Override
	public Iterable<String> getNames() {
		return resources.keySet();
	}

}
