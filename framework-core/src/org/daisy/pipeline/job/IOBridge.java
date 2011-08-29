package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcInput.Builder;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.XProcOptionMetadata.Direction;
import org.daisy.pipeline.script.XProcScript;

public class IOBridge {

	public static final String ANY_FILE_URI = "anyFileURI";
	public static final String ANY_DIR_URI = "anyDirURI";
	static HashSet<String> OPTIONS_TO_TRANSLATE = new HashSet<String>();
	HashSet<String> mGeneratedOutputs = new HashSet<String>();
	static {
		OPTIONS_TO_TRANSLATE.add(ANY_DIR_URI);
		OPTIONS_TO_TRANSLATE.add(ANY_FILE_URI);
	}
	private File mContextDir;
	private File mOutputDir;

	public IOBridge(File baseDir) throws IOException {
		mContextDir = new File(baseDir + File.separator
				+ IOConstants.IO_DATA_SUBDIR);
		if (!mContextDir.exists() && !mContextDir.mkdirs()) {
			throw new IOException("Could not create context dir:"
					+ mContextDir.getAbsolutePath());
		}
		;
		mOutputDir = new File(baseDir + File.separator
				+ IOConstants.IO_OUTPUT_SUBDIR);
		if (!mOutputDir.exists() && !mOutputDir.mkdirs()) {
			throw new IOException("Could not create context dir:"
					+ mOutputDir.getAbsolutePath());
		}
		;
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

		Iterable<String> paramInfos = script.getXProcPipelineInfo()
				.getParameterPorts();
		for (String paramInfo : paramInfos) {
			for (QName name : input.getParameters(paramInfo).keySet()) {
				resolvedInput.withParameter(paramInfo, name, input
						.getParameters(paramInfo).get(name));
			}
		}
	}

	protected void resolveOptions(XProcScript script, XProcInput input,
			Builder resolvedInput) {
		Iterable<XProcOptionInfo> optionInfos = script.getXProcPipelineInfo()
				.getOptions();
		for (XProcOptionInfo optionInfo : optionInfos) {
			if (OPTIONS_TO_TRANSLATE.contains(script.getOptionMetadata(
					optionInfo.getName()).getType())) {
				String subDir = script.getOptionMetadata(optionInfo.getName())
						.getDirection() == Direction.INPUT ? mContextDir
						.toURI().toString() : mOutputDir.toURI().toString();

				String strUri = input.getOptions().get(optionInfo.getName());
				;
				if (script.getOptionMetadata(optionInfo.getName())
						.getDirection() == Direction.OUTPUT) {
					if (strUri == null || strUri.isEmpty()) {

						strUri = (optionInfo.getSelect() != null && !optionInfo
								.getSelect().isEmpty()) ? optionInfo
								.getSelect() : IOHelper.generateOutput(
								optionInfo.getName().toString(),
								script.getOptionMetadata(optionInfo.getName())
										.getType(),
								script.getOptionMetadata(optionInfo.getName())
										.getMediaType());
					}
					if (mGeneratedOutputs.contains(strUri)) {
						throw new IllegalArgumentException(
								"Conflict when generating uri's a default value and option name have the same name:"
										+ strUri);
					}
					mGeneratedOutputs.add(strUri);
				}
				URI relUri = null;
				try {
					relUri = URI.create(strUri);

				} catch (Exception e) {
					throw new RuntimeException(
							"Error parsing uri for input option:"
									+ optionInfo.getName(), e);
				}
				URI uri = IOHelper.map(subDir, relUri.toString());
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
