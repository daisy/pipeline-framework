package org.daisy.pipeline.job;

import java.net.URI;

// TODO: Auto-generated Javadoc
/**
 * The Class JobResult.
 */
public class JobResult {

	static class Builder{
		private String idx;
		private URI uri;
		private String mediaType;

		/**
		 * Constructs a new instance.
		 */
		public Builder() {
		}

		/**
		 * Sets the idx for this instance.
		 *
		 * @param idx The idx.
		 */
		public Builder withIdx(String idx) {
			this.idx = idx;
			return this;
		}

		/**
		 * Sets the uri for this instance.
		 *
		 * @param uri The uri.
		 */
		public Builder withUri(URI uri) {
			this.uri = uri;
			return this;
		}

		/**
		 * Sets the mediaType for this instance.
		 *
		 * @param mediaType The mediaType.
		 */
		public Builder withMediaType(String mediaType) {
			this.mediaType = mediaType;
			return this;
		}
		
		public JobResult build(){
			return new  JobResult(this.idx,this.uri,this.mediaType);
		}
	}

	//short index for the result 
	private String idx;

	// uri to the actual file
	private URI uri;

	//media type
	private String mediaType;

	/**
	 * Constructs a new instance.
	 *
	 * @param idx The idx for this instance.
	 * @param uri The uri for this instance.
	 * @param mediaType The mediaType for this instance.
	 */
	public JobResult(String idx, URI uri, String mediaType) {
		this.idx = idx;
		this.uri = uri;
		this.mediaType = mediaType;
	}

	/**
	 * Gets the idx for this instance.
	 *
	 * @return The idx.
	 */
	public String getIdx() {
		return this.idx;
	}

	/**
	 * Gets the uri for this instance.
	 *
	 * @return The uri.
	 */
	public URI getUri() {
		return this.uri;
	}

	/**
	 * Gets the mediaType for this instance.
	 *
	 * @return The mediaType.
	 */
	public String getMediaType() {
		return this.mediaType;
	}

}
