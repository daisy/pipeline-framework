/*
 *
 */
package org.daisy.pipeline.script;

// TODO: Auto-generated Javadoc
/**
 * Metadata associated to a port.
 */
public class XProcPortMetadata {

	/**
	 *  Builds the {@link XProcPortMetadata} object
	 */
	public static final class Builder {

		/** The nice name. */
		private String niceName;

		/** The description. */
		private String description;

		/** The media type. */
		private String mediaType;

		/**
		 * With nice name.
		 *
		 * @param niceName the nice name
		 * @return the builder
		 */
		public Builder withNiceName(String niceName){
			this.niceName=niceName;
			return this;
		}

		/**
		 * With description.
		 *
		 * @param description the description
		 * @return the builder
		 */
		public Builder withDescription(String description){
			this.description=description;
			return this;
		}

		/**
		 * With media type.
		 *
		 * @param mediaType the media type
		 * @return the builder
		 */
		public Builder withMediaType(String mediaType){
			this.mediaType=mediaType;
			return this;
		}

		/**
		 * Builds the instance.
		 *
		 * @return the x proc port metadata
		 */
		public XProcPortMetadata build(){
			return new XProcPortMetadata(niceName, description, mediaType);
		}

	}

	/** The nice name. */
	final private String niceName;

	/** The description. */
	final private String description;

	/** The media type. */
	final private String mediaType;


	/**
	 * Instantiates a new x proc port metadata.
	 *
	 * @param niceName the nice name
	 * @param description the description
	 * @param mediaType the media type
	 */
	public XProcPortMetadata(String niceName, String description,
			String mediaType) {
		super();
		this.niceName = niceName;
		this.description = description;
		this.mediaType = mediaType;
	}

	/**
	 * Gets the nice name.
	 *
	 * @return the nice name
	 */
	public String getNiceName() {
		return niceName;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the media type.
	 *
	 * @return the media type
	 */
	public String getMediaType() {
		return mediaType;
	}

}
