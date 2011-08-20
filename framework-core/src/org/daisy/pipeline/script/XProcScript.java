package org.daisy.pipeline.script;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.daisy.common.xproc.XProcPipelineInfo;

public final class XProcScript {
	public static class Builder implements MetadataBuilder<XProcScript>{
		XProcPipelineInfo pipelineInfo;
		private String name;
		private String description;
		private Map<String, XProcPortMetadata> portsMetadata=new HashMap<String, XProcPortMetadata>();
		private Map<QName, XProcOptionMetadata> optionsMetadata=new HashMap<QName, XProcOptionMetadata>();
		public Builder withPipelineInfo(XProcPipelineInfo pipelineInfo){
			this.pipelineInfo=pipelineInfo;
			return this;
		}
		public Builder withNiceName(String name){
			if(name!=null)
				this.name=name;
			return this;
		}
		public Builder withDescription(String description){
			if (description!=null)
				this.description=description;
			return this;
		}
		public Builder withPortMetadata(String name,XProcPortMetadata metadata){
			portsMetadata.put(name,metadata);
			return this;
		}
		public Builder withOptionMetadata(QName name,XProcOptionMetadata metadata){
			optionsMetadata.put(name,metadata);
			return this;
		}
		
		public XProcScript build(){
			return new XProcScript(pipelineInfo,name,description,portsMetadata,optionsMetadata);
		}
	}
	private final XProcPipelineInfo pipelineInfo;
	private final String name;
	private final String description;
	private final Map<String, XProcPortMetadata> portsMetadata;
	private final Map<QName, XProcOptionMetadata> optionsMetadata;

	
	public XProcScript(XProcPipelineInfo pipelineInfo, String name,
			String description, Map<String, XProcPortMetadata> portsMetadata,
			Map<QName, XProcOptionMetadata> optionsMetadata) {
		this.pipelineInfo = pipelineInfo;
		this.name = name;
		this.description = description;
		this.portsMetadata = portsMetadata;
		this.optionsMetadata = optionsMetadata;
	}

	public final XProcPipelineInfo getXProcPipelineInfo() {
		return pipelineInfo;
	}

	public final URI getURI(){
		return pipelineInfo.getURI();
	}
	public final String getName() {
		return name;
	}
	public final String getDescription() {
		return description;
	}

	public final XProcPortMetadata getPortMetadata(String name) {
		return portsMetadata.get(name);
	}
	public final XProcOptionMetadata getOptionMetadata(QName name) {
		return optionsMetadata.get(name);
	}
}
