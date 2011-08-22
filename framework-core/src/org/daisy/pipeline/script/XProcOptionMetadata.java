package org.daisy.pipeline.script;

public class XProcOptionMetadata {
	public enum Direction{
		INPUT,OUTPUT,NA
	}
	final private String niceName;
	final private String description;
	final private String type;
	final private String mediaType;
	final private Direction direction;
	private XProcOptionMetadata(String niceName, String description,
			String type, String mediaType,Direction direction) {
		super();
		this.niceName = niceName;
		this.description = description;
		this.type = type;
		this.mediaType = mediaType;
		this.direction=direction;
	}

	public String getNiceName() {
		return niceName;
	}

	public String getDescription() {
		return description;
	}

	public String getType() {
		return type;
	}

	public String getMediaType() {
		return mediaType;
	}
	public Direction getDirection() {
		return direction;
	}
	public static final class Builder {
		private String niceName;
		private String description;
		private String type;
		private String mediaType;
		private Direction direction;

		public Builder withDescription(String description) {
			this.description = description;
			return this;
		}

		public Builder withNiceName(String niceName) {
			this.niceName = niceName;
			return this;
		}

		public Builder withType(String type) {
			this.type = type;
			return this;
		}

		public Builder withMediaType(String mediaType) {
			this.mediaType = mediaType;
			return this;
		}
		public Builder withDirection(String direction) {
			if(direction.equalsIgnoreCase(Direction.OUTPUT.toString())){
				this.direction=Direction.OUTPUT;
			}else{
				this.direction=Direction.INPUT;
			}
			return this;
		}
		public XProcOptionMetadata build(){
			return new XProcOptionMetadata(niceName,description,type,mediaType,direction);
		}
	}



}
