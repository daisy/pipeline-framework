package org.daisy.pipeline.script;

public class XProcPortMetadata {
	public static final class Builder implements MetadataBuilder<XProcPortMetadata>{
		private String niceName;
		private String description;
		private String mediaType;
		
		public Builder withNiceName(String niceName){
			this.niceName=niceName;
			return this;			
		}
		
		public Builder withDescription(String description){
			this.description=description;
			return this;			
		}
		public Builder withMediaType(String mediaType){
			this.mediaType=mediaType;
			return this;			
		}
		
		public XProcPortMetadata build(){
			return new XProcPortMetadata(niceName, description, mediaType);
		}
		
	}
	
	final private String niceName;
	final private String description;
	final private String mediaType;

	
	public XProcPortMetadata(String niceName, String description,
			String mediaType) {
		super();
		this.niceName = niceName;
		this.description = description;
		this.mediaType = mediaType;
	}

	public String getNiceName() {
		return niceName;
	}

	public String getDescription() {
		return description;
	}

	public String getMediaType() {
		return mediaType;
	}

}
