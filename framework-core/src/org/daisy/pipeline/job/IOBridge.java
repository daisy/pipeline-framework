package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcInput.Builder;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.XProcOptionMetadata.Direction;
import org.daisy.pipeline.script.XProcScript;



/**
 * The Class IOBridge handles some io operations relevant to the execution of pipelines.
 */
public class IOBridge {

	/** The Constant ORG_DAISY_PIPELINE_IOBASE. */
	public static final String ORG_DAISY_PIPELINE_IOBASE = "org.daisy.pipeline.iobase";


	public static final String ORG_DAISY_PIPELINE_LOCAL = "org.daisy.pipeline.ws.local";
	/** The Constant ANY_FILE_URI. */
	public static final String ANY_FILE_URI = "anyFileURI";

	/** The Constant ANY_DIR_URI. */
	public static final String ANY_DIR_URI = "anyDirURI";

	/** The OPTION s_ t o_ translate. */
	static HashSet<String> OPTIONS_TO_TRANSLATE = new HashSet<String>();

	/** The m generated outputs. */
	HashSet<String> mGeneratedOutputs = new HashSet<String>();
	static {
		OPTIONS_TO_TRANSLATE.add(ANY_DIR_URI);
		OPTIONS_TO_TRANSLATE.add(ANY_FILE_URI);
	}

	/** The m context dir. */
	private final File mContextDir;

	/** The m output dir. */
	private final File mOutputDir;

	/** The m base dir. */
	private final File mBaseDir;

	/** The m id. */
	private final JobId mId;

	/**
	 * Instantiates a new iO bridge to be used by the job having the id provided.
	 *
	 * @param id the id
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public IOBridge(JobId id) throws IOException {
		mId=id;

		if (System.getProperty(ORG_DAISY_PIPELINE_IOBASE) == null) {
			throw new IllegalStateException("The property "
					+ ORG_DAISY_PIPELINE_IOBASE + " is not set");
		}

		File ioBase = new File(
				System.getProperty(ORG_DAISY_PIPELINE_IOBASE));
		ioBase.mkdir();
		mBaseDir = new File(ioBase, id.toString());
		mBaseDir.mkdirs();

		mContextDir = new File(mBaseDir + File.separator
				+ IOConstants.IO_DATA_SUBDIR);
		if (!mContextDir.exists() && !mContextDir.mkdirs()) {
			throw new IOException("Could not create context dir:"
					+ mContextDir.getAbsolutePath());
		}
		;
		mOutputDir = new File(mBaseDir + File.separator
				+ IOConstants.IO_OUTPUT_SUBDIR);
		if (!mOutputDir.exists() && !mOutputDir.mkdirs()) {
			throw new IOException("Could not create context dir:"
					+ mOutputDir.getAbsolutePath());
		}
		;

	}

	/**
	 * Resolves the uris defined in the XProcInput input object to the actual files in the job context.
	 *
	 * @param script the script
	 * @param input the input
	 * @param context the context
	 * @return the x proc input
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public XProcInput resolve(XProcScript script, XProcInput input,
			ResourceCollection context) throws IOException {
		XProcInput.Builder resolvedInput = new XProcInput.Builder();
		if (context != null) {
			storeResources(context);
		}
		resolveInputPorts(script, input, resolvedInput);
		resolveOptions(script, input, resolvedInput);
		resolveParams(script, input, resolvedInput);
		return resolvedInput.build();
	}

	/**
	 * Resolve params.
	 *
	 * @param script the script
	 * @param input the input
	 * @param resolvedInput the resolved input
	 */
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

	/**
	 * Resolve options, input/output options without value will be automaticaly assigned.
	 *
	 * @param script the script
	 * @param input the input
	 * @param resolvedInput the resolved input
	 */
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
				URI uri = null;
				//absolute means  mapping
				if(relUri.getScheme()==null) {
					uri=IOHelper.map(subDir, relUri.toString());
				} else {
					uri= relUri;
				}
				resolvedInput.withOption(optionInfo.getName(), uri.toString());
			} else {
				resolvedInput.withOption(optionInfo.getName(), input
						.getOptions().get(optionInfo.getName()));
			}
		}

	}

	/**
	 * Store resources.
	 *
	 * @param context the context
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void storeResources(ResourceCollection context)
			throws IOException {
		for (String path : context.getNames()) {
			IOHelper.dump(context.getResource(path).provide(), mContextDir
					.toURI().toString(), path.replace("\\", "/"));
		}
	}

	/**
	 * Resolve input ports.
	 *
	 * @param script the script
	 * @param input the input
	 * @param builder the builder
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Zips outputs of the execution.
	 *
	 * @return the uRI
	 */
	public URI zipOutput(){
		if (isLocal()) {
			return mOutputDir.toURI();
		}
		List<File> files = IOHelper.treeFileList(mOutputDir);
		try {
			URI zipFile = IOHelper.zipFromEntries(files,new File(mBaseDir,"results.zip"),mOutputDir.getAbsolutePath()+File.separator);
			return zipFile;
		} catch (Exception e) {
			throw new RuntimeException("Error while building zip file with the results:"+e.getMessage(),e);
		}
	}

	/**
	 * Gets the log file.
	 *
	 * @return the log file
	 */
	public URI getLogFile() {
		return new File(mBaseDir,mId.toString()+".log").toURI();
	}

	private boolean isLocal(){
		return System.getProperty(ORG_DAISY_PIPELINE_LOCAL)!=null&&System.getProperty(ORG_DAISY_PIPELINE_LOCAL).equalsIgnoreCase("true");
	}
}
