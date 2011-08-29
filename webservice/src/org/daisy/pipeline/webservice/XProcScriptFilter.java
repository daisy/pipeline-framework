package org.daisy.pipeline.webservice;

import org.daisy.common.base.Filter;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcOptionMetadata.Direction;
import org.daisy.pipeline.script.XProcScript;

public final class XProcScriptFilter implements Filter<XProcScript> {

	public static final XProcScriptFilter INSTANCE = new XProcScriptFilter();

	private static final String ANY_URI_TYPE = "anyURI";
	private static final String ANY_FILE_URI_TYPE = "anyFileURI";
	private static final String ANY_DIR_URI_TYPE = "anyDirURI";

	private XProcScriptFilter() {
		// singleton
	}

	@Override
	public XProcScript filter(XProcScript script) {
		XProcPipelineInfo xproc = script.getXProcPipelineInfo();
		// create the script builder
		XProcScript.Builder scriptBuilder = new XProcScript.Builder()
				.withNiceName(script.getName()).withDescription(
						script.getDescription());
		// create the filtered pipeline info
		XProcPipelineInfo.Builder xprocBuilder = new XProcPipelineInfo.Builder();
		xprocBuilder.withURI(xproc.getURI());
		// copy input ports
		for (XProcPortInfo port : xproc.getInputPorts()) {
			xprocBuilder.withPort(port);
			scriptBuilder.withPortMetadata(port.getName(),
					script.getPortMetadata(port.getName()));
		}
		// copy parameter ports
		for (String port : xproc.getParameterPorts()) {
			// FIXME parameter ports should return XProcPortInfo
			xprocBuilder.withPort(XProcPortInfo.newParameterPort(port, false));
			scriptBuilder.withPortMetadata(port, script.getPortMetadata(port));
		}
		// output ports are not copied
		// copy options
		for (XProcOptionInfo option : xproc.getOptions()) {
			XProcOptionMetadata metadata = script.getOptionMetadata(option
					.getName());
			// filter-out options that are both OUTPUT options with type
			// inheriting from anyURI
			if (!(metadata.getDirection() == Direction.OUTPUT && 
					(ANY_URI_TYPE.equals(metadata.getMediaType())
					 || 
					 ANY_FILE_URI_TYPE.equals(metadata.getType()) 
					 || 
					 ANY_DIR_URI_TYPE.equals(metadata.getType())))) {
				xprocBuilder.withOption(option);
				scriptBuilder.withOptionMetadata(option.getName(),
						script.getOptionMetadata(option.getName()));
			}

		}
		scriptBuilder.withPipelineInfo(xprocBuilder.build());
		return scriptBuilder.build();
	}
}
