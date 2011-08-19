package org.daisy.pipeline.script;

import java.net.URI;
import java.util.Map;

import javax.xml.namespace.QName;

import org.daisy.common.xproc.XProcPipelineInfo;

public final class XProcScript {

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
