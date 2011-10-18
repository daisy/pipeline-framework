/*
 * 
 */
package org.daisy.pipeline.script;

/**
 * Option related metadata.
 */
public class XProcOptionMetadata {

	/**
	 * The Enum Direction.
	 */
	public enum Direction {

		/** The INPUT. */
		INPUT,
		/** The OUTPUT. */
		OUTPUT,
		/** The NA. */
		NA
	}

	/** The nice name. */
	final private String niceName;

	/** The description. */
	final private String description;

	/** The type. */
	final private String type;

	/** The media type. */
	final private String mediaType;

	/** The direction. */
	final private Direction direction;

	/**
	 * Instantiates a new {@link XProcOptionMetadata} object.
	 * 
	 * @param niceName
	 *            the nice name
	 * @param description
	 *            the description
	 * @param type
	 *            the type
	 * @param mediaType
	 *            the media type
	 * @param direction
	 *            the direction
	 */
	private XProcOptionMetadata(String niceName, String description,
			String type, String mediaType, Direction direction) {
		super();
		this.niceName = niceName;
		this.description = description;
		this.type = type;
		this.mediaType = mediaType;
		this.direction = direction;
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
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the media type.
	 * 
	 * @return the media type
	 */
	public String getMediaType() {
		return mediaType;
	}

	/**
	 * Gets the direction, if {@link Direction} is output it should be a uri with type AnyFileURI or AnyDirURI, input may be other any c:type.
	 * 
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Builds the {@link XProcOptionMetadata} object.
	 */
	public static final class Builder {

		/** The nice name. */
		private String niceName;

		/** The description. */
		private String description;

		/** The type. */
		private String type;

		/** The media type. */
		private String mediaType;

		/** The direction. */
		private Direction direction;

		/**
		 * With description.
		 * 
		 * @param description
		 *            the description
		 * @return the builder
		 */
		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		/**
		 * With nice name.
		 * 
		 * @param niceName
		 *            the nice name
		 * @return the builder
		 */
		public Builder withNiceName(String niceName) {
			this.niceName = niceName;
			return this;
		}

		/**
		 * With type.
		 * 
		 * @param type
		 *            the type
		 * @return the builder
		 */
		public Builder withType(String type) {
			this.type = type;
			return this;
		}

		/**
		 * With media type.
		 * 
		 * @param mediaType
		 *            the media type
		 * @return the builder
		 */
		public Builder withMediaType(String mediaType) {
			this.mediaType = mediaType;
			return this;
		}

		/**
		 * With direction.
		 * 
		 * @param direction
		 *            the direction
		 * @return the builder
		 */
		public Builder withDirection(String direction) {
			if (direction.equalsIgnoreCase(Direction.OUTPUT.toString())) {
				this.direction = Direction.OUTPUT;
			} else {
				this.direction = Direction.INPUT;
			}
			return this;
		}

		/**
		 * Builds instance
		 * 
		 * @return the {@link XProcOptionMetadata}
		 */
		public XProcOptionMetadata build() {
			return new XProcOptionMetadata(niceName, description, type,
					mediaType, direction);
		}
	}

}
