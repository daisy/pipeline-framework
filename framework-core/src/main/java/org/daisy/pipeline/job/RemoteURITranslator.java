package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.util.HashSet;

import javax.xml.transform.Source;

import org.daisy.common.base.Provider;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;

import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcScript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

class RemoteURITranslator  implements URITranslator {
	static String IO_DATA_SUBDIR = "context";

	/** The I o_ outpu t_ subdir. */
	static String IO_OUTPUT_SUBDIR = "output";
	/** The Constant ANY_FILE_URI. */
	public static final String ANY_FILE_URI = "anyFileURI";

	/** The Constant ANY_DIR_URI. */
	public static final String ANY_DIR_URI = "anyDirURI";

	/** The Constant ORG_DAISY_PIPELINE_IOBASE. */
	public static final String ORG_DAISY_PIPELINE_IOBASE = "org.daisy.pipeline.iobase";

	private static final Logger logger = LoggerFactory.getLogger(RemoteURITranslator.class);

	/** The m context dir. */
	private File contextDir;

	/** The m output dir. */
	private File outputDir;

	/** The m base dir. */
	private File baseDir;

	static HashSet<String> OPTIONS_TO_TRANSLATE = Sets.newHashSet();

	/** The m generated outputs. */
	HashSet<String> generatedOutputs = Sets.newHashSet();
	static {
		OPTIONS_TO_TRANSLATE.add(ANY_DIR_URI);
		OPTIONS_TO_TRANSLATE.add(ANY_FILE_URI);
	}

	/**
	 * Constructs a new instance.
	 *
	 * @param contextDir The contextDir for this instance.
	 */
	private RemoteURITranslator(File baseDir,File contextDir,File outputDir) {
		this.contextDir = contextDir;
		this.baseDir= baseDir;
		this.outputDir= outputDir;
	}

	public static RemoteURITranslator from(JobId id) throws IOException {
		return RemoteURITranslator.from(id,null);
	}

	public static RemoteURITranslator from(JobId id,ResourceCollection resources) throws IOException {
		if (System.getProperty(ORG_DAISY_PIPELINE_IOBASE) == null) {
			throw new IllegalStateException("The property "
					+ ORG_DAISY_PIPELINE_IOBASE + " is not set");
		}
		//Base based on the the id
		File ioBase = new File(System.getProperty(ORG_DAISY_PIPELINE_IOBASE));
		ioBase.mkdir();
		File baseDir = new File(ioBase, id.toString());
		baseDir.mkdirs();

		File contextDir = new File(baseDir + File.separator + IO_DATA_SUBDIR);
		if (!contextDir.exists() && !contextDir.mkdirs()) {
			throw new IOException("Could not create context dir:"
					+ contextDir.getAbsolutePath());
		}
		;
		File outputDir = new File(baseDir + File.separator + IO_OUTPUT_SUBDIR);
		if (!outputDir.exists() && !outputDir.mkdirs()) {
			throw new IOException("Could not create context dir:"
					+ outputDir.getAbsolutePath());
		}
		;

		if (resources != null) {
			IOHelper.dump(resources,contextDir);
		}
		return new RemoteURITranslator(baseDir,contextDir,outputDir);
	}


	@Override
	public XProcInput translateInputs(XProcScript script,XProcInput input) {
		XProcInput.Builder translated = new XProcInput.Builder();
		try{
			translateInputPorts(script.getXProcPipelineInfo(), input, translated);
		}catch(IOException ex){
			throw new RuntimeException("Error translating inputs",ex);
		}
		return translated.build();
	}

	/**
	 * Resolve input ports.
	 *
	 * @param script the script
	 * @param input the input
	 * @param builder the builder
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void translateInputPorts(final XProcPipelineInfo info,final  XProcInput input,
			XProcInput.Builder builder) throws IOException {

		Iterable<XProcPortInfo> inputInfos =info.getInputPorts();
		//filter those ports which are null
		Predicate<XProcPortInfo> isNotNull = new Predicate<XProcPortInfo>(){
			public boolean apply(XProcPortInfo portInfo){
				return input.getInputs(portInfo.getName()) != null;
			}
		};

		for (XProcPortInfo portInfo : Collections2.filter(Lists.newLinkedList(inputInfos),isNotNull)) {
			//number of inputs for this port
			int inputCnt = 0;
			for (Provider<Source> prov : input.getInputs(portInfo.getName())) {
				URI relUri = null;
				if (prov.provide().getSystemId() != null) {
					try {
						relUri = URI.create(prov.provide().getSystemId());
					} catch (Exception e) {
						throw new RuntimeException(
								"Error parsing uri when building the input port"
								+ portInfo.getName(), e);
					}
				} else {
					//this is the case when no zip context was provided (all comes from the xml)
					//is this case still applicable?
					relUri = URI.create(portInfo.getName() + '-' + inputCnt
							+ ".xml");
				}
				URI uri = IOHelper.map(contextDir.toURI().toString(),relUri.toString());
				prov.provide().setSystemId(uri.toString());
				builder.withInput(portInfo.getName(), prov);
				inputCnt++;
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
	private void tranlateOptions(final XProcScript script , final XProcInput input,
			XProcInput.Builder resolvedInput) {
		Iterable<XProcOptionInfo> optionInfos = script.getXProcPipelineInfo().getOptions();

		//Filter those otions which we have to translate 
		final Predicate<XProcOptionInfo> haveTo= new Predicate<XProcOptionInfo>(){
			public boolean apply(XProcOptionInfo optionInfo){
				return OPTIONS_TO_TRANSLATE.contains(script.getOptionMetadata(
					optionInfo.getName()).getType());
			}
		};
		//those which I dont have to 
		final Predicate<XProcOptionInfo> donthaveTo= new Predicate<XProcOptionInfo>(){
			public boolean apply(XProcOptionInfo optionInfo){
				return !haveTo.apply(optionInfo);
			}
		};
		//verbatim copy of those that I dont have to process	
		for (XProcOptionInfo optionInfo : Collections2.filter(Lists.newLinkedList(optionInfos),donthaveTo)) {
				resolvedInput.withOption(optionInfo.getName(), input
						.getOptions().get(optionInfo.getName()));
		}

		//translate input/output uris
		for (XProcOptionInfo optionInfo : Collections2.filter(Lists.newLinkedList(optionInfos),haveTo)) {
			//output dir for outputs 
			//context dir for intputs 
			String subDir = !URITranslatorHelper.isOutput(script,optionInfo)? contextDir
				.toURI().toString() : outputDir.toURI().toString();

			String strUri = input.getOptions().get(optionInfo.getName());
			//output's names can be generated and create conflicts take care of that
			if (URITranslatorHelper.isOutput(script,optionInfo)) {
				if (!URITranslatorHelper.notEmpty(strUri)) {
					//get default value or generate a new one
					strUri = (URITranslatorHelper.notEmpty(optionInfo.getSelect()))? 
						optionInfo.getSelect() : 
						IOHelper.generateOutput(
								optionInfo.getName().toString(),
								script.getOptionMetadata(optionInfo.getName())
								.getType(),
								script.getOptionMetadata(optionInfo.getName())
								.getMediaType());
				}
				if (generatedOutputs.contains(strUri)) {
					throw new IllegalArgumentException(
							"Conflict when generating uri's a default value and option name have the same name:"
							+ strUri);
				}
				generatedOutputs.add(strUri);
			}
			URI relUri = null;
			try {
				relUri = URI.create(strUri);

			} catch (Exception e) {
				throw new RuntimeException(
						"Error parsing uri for option:"
						+ optionInfo.getName(), e);
			}
			URI uri=IOHelper.map(subDir, relUri.toString());
			resolvedInput.withOption(optionInfo.getName(), uri.toString());
		}

	}
}
