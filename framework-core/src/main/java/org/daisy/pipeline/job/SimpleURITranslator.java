package org.daisy.pipeline.job;

import org.daisy.common.base.Provider;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.common.xproc.XProcPortInfo;

import org.daisy.pipeline.script.XProcScript;
import javax.xml.transform.Result;

class SimpleURITranslator  implements URITranslator {

	XProcScript script;

	

	/**
	 * Constructs a new instance.
	 *
	 * @param script The script for this instance.
	 */
	private SimpleURITranslator(XProcScript script) {
		this.script = script;
	}


	public static SimpleURITranslator from(XProcScript script){
		return new SimpleURITranslator(script);
	}
	@Override
	public XProcInput translateInputs(XProcInput input) {
		//no mappings for inputs
		return input;
	}

	@Override
	public XProcOutput translateOutput(XProcOutput output) {
		// create the outputs in case they are needed
		XProcOutput.Builder builder = new XProcOutput.Builder();
		Iterable<XProcPortInfo> outputInfos = script.getXProcPipelineInfo()
				.getOutputPorts();
		for (XProcPortInfo info : outputInfos) {
			if (output.getResultProvider(info.getName()) != null
					&& !output.getResultProvider(info.getName()).provide()
							.getSystemId().isEmpty()) {
				String parts[] = URITranslatorHelper
						.getDynamicResultProviderParts(info.getName(), output
								.getResultProvider(info.getName()), script
								.getPortMetadata(info.getName()).getMediaType());
				builder.withOutput(info.getName(), new DynamicResultProvider(
						parts[0], parts[1]));
			}else{
				builder.withOutput(info.getName(), new EmptyResult());
			}

		}
		return builder.build();
	}
	
	private static class EmptyResult implements Result,Provider<Result>{

		@Override
		public void setSystemId(String arg0) {
			//ignore
		}

		@Override
		public String getSystemId() {
			return "";
		}

		@Override
		public Result provide() {
			return this;
		}	

	}
	
}
