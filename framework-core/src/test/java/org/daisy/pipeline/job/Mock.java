package org.daisy.pipeline.job;

import java.net.URI;

import java.util.Set;

class Mock   {
	public static JobContext mockContext(JobId id){
		return new AbstractJobContext(id,null,null){
			@Override
			public void writeXProcResult() {
			}

			@Override
			public Set<URI> getFiles() {
				return null;
			}

			@Override
			public URI getZip() {
				return null;
			}

			@Override
			public URI toZip(URI... files) {
				return null;
			}

		};
	}
}
