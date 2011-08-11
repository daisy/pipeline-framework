package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.common.xproc.XProcInput.Builder;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcOptionMetadata.Direction;

public class IOBridge {
	static String DATA_SUBDIR = "context";
	private File mContextDir;

	public IOBridge(File baseDir) throws IOException{
		mContextDir = new File(baseDir +File.separator+DATA_SUBDIR);
		if(!mContextDir.exists()&&!mContextDir.mkdirs()){
			throw new IOException("Could not create context dir:" + mContextDir.getAbsolutePath());
		};
	}

	public XProcInput resolve(XProcScript script, XProcInput input,
			ResourceCollection context) throws IOException {
		XProcInput.Builder resolvedInput = new XProcInput.Builder();
		this.storeResources(context);
		this.resolveInputPorts(script, input, resolvedInput);
		this.resolveOptions(script, input, resolvedInput);
		this.resolveParams(script, input, resolvedInput);
		return resolvedInput.build();
	}

	protected void resolveParams(XProcScript script, XProcInput input,
			Builder resolvedInput) {

		Iterable<String> paramInfos = script.getXProcPipelineInfo().getParameterPorts();
		for(String paramInfo:paramInfos){
			for (QName name:input.getParameters(paramInfo).keySet()){
				resolvedInput.withParameter(paramInfo, name,input.getParameters(paramInfo).get(name));
			}
		}
	}

	protected void resolveOptions(XProcScript script, XProcInput input,
			Builder resolvedInput) {
		Iterable<XProcOptionInfo> optionInfos = script.getXProcPipelineInfo()
				.getOptions();
		for (XProcOptionInfo optionInfo : optionInfos) {
			if (script.getOptionMetadata(optionInfo.getName()).getDirection() == Direction.INPUT) {
				URI relUri = null;
				try {
					relUri = URI.create(input.getOptions().get(
							optionInfo.getName()));

				} catch (Exception e) {
					throw new RuntimeException(
							"Error parsing uri for input option:"
									+ optionInfo.getName(), e);
				}
				URI uri = IOHelper.map(mContextDir.toURI().toString(), relUri
						.toString());
				resolvedInput.withOption(optionInfo.getName(), uri.toString());
			} else {
				resolvedInput.withOption(optionInfo.getName(), input
						.getOptions().get(optionInfo.getName()));
			}
		}

	}

	protected void storeResources(ResourceCollection context)
			throws IOException {
		for (String path : context.getNames()) {
			IOHelper.dump(context.getResource(path).provide(), mContextDir
					.toURI().toString(), path);
		}
	}

	protected void resolveInputPorts(XProcScript script, XProcInput input,
			XProcInput.Builder builder) throws IOException {
		Iterable<XProcPortInfo> inputInfos = script.getXProcPipelineInfo()
				.getInputPorts();
		for (XProcPortInfo info : inputInfos) {
			int inputCnt = 0;
			if (input.getInputs(info.getName()) != null) {
				for (Provider<Source> prov : input.getInputs(info.getName())) {
					URI relUri = null;
					if (prov.provide().getSystemId() != null) {
						try {
							relUri = URI.create(prov.provide().getSystemId());
						} catch (Exception e) {
							throw new RuntimeException(
									"Error parsing uri when building the input port"
											+ info.getName(), e);
						}
					} else {
						relUri = URI.create(info.getName() + '-' + inputCnt
								+ ".xml");
					}
					// if uri == null -> I presume there is something inside
					// if the uri is not relative I wont map it
					if (relUri.getScheme() == null) {
						URI uri = IOHelper.map(mContextDir.toURI().toString(),
								relUri.toString());
						prov.provide().setSystemId(uri.toString());
						builder.withInput(info.getName(), prov);
					} else {
						builder.withInput(info.getName(), prov);
					}
					inputCnt++;
				}
			}
		}
	}

}
