package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;

import java.net.URI;

import java.util.Collection;
import java.util.HashSet;

import javax.xml.transform.Source;

import org.daisy.common.base.Provider;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;

import org.daisy.pipeline.script.XProcScript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicates;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

class MappingURITranslator  implements URITranslator {
	
	final static String IO_DATA_SUBDIR = "context";
	/** The I o_ outpu t_ subdir. */
	final static String IO_OUTPUT_SUBDIR = "output";

	/** The Constant ORG_DAISY_PIPELINE_IOBASE. */
	public static final String ORG_DAISY_PIPELINE_IOBASE = "org.daisy.pipeline.iobase";

	private static final Logger logger = LoggerFactory.getLogger(MappingURITranslator.class);

	/** The m context dir. */
	private final File contextDir;

	/** The m output dir. */
	private final File outputDir;

	/** The m base dir. */

	private final XProcScript script;

	/** The m generated outputs. */
	private final HashSet<String> generatedOutputs = Sets.newHashSet();
	
	enum TranslatableOption{
		ANY_DIR_URI("anyDirURI"),
		ANY_FILE_URI("anyFileURI");
		private final String name;
		TranslatableOption(String name){
			this.name=name;
		}

		public String getName() {
			return this.name;
		}
		@Override
		public String toString(){
			return this.name;
		}
		public static boolean contains(String optionType){
			//creating a map for just too elements is not going to make that much difference.
			for(TranslatableOption opt:TranslatableOption.values()){
				if(opt.getName().equals(optionType))
					return true;	
			}
			return false;
		}
	}

	/**
	 * Constructs a new instance.
	 *
	 * @param contextDir The contextDir for this instance.
	 */
	private MappingURITranslator(File contextDir,File outputDir,XProcScript script) {
		this.contextDir = contextDir;
		this.outputDir= outputDir;
		this.script=script;
	}

	public static MappingURITranslator from(JobId id,XProcScript script) throws IOException {
		return MappingURITranslator.from(id,script,null);
	}

	public static MappingURITranslator from(JobId id,XProcScript script,ResourceCollection resources) throws IOException {
		if (System.getProperty(ORG_DAISY_PIPELINE_IOBASE) == null) {
			throw new IllegalStateException("The property "
					+ ORG_DAISY_PIPELINE_IOBASE + " is not set");
		}
		//Base based on the the id
		File ioBase=IOHelper.makeDirs(System.getProperty(ORG_DAISY_PIPELINE_IOBASE));
		File baseDir = IOHelper.makeDirs(new File(ioBase, id.toString()));

		File contextDir = IOHelper.makeDirs(new File(baseDir, IO_DATA_SUBDIR));
		File outputDir = IOHelper.makeDirs(new File(baseDir, IO_OUTPUT_SUBDIR));

		if (resources != null) {
			logger.debug("Storing the resource collection");
			IOHelper.dump(resources,contextDir);
		}
		return new MappingURITranslator(contextDir,outputDir,script);
	}


	@Override
	public XProcInput translateInputs(XProcInput input) {
		logger.debug(String.format("Translating inputs for script :%s",script));
		XProcInput.Builder translated = new XProcInput.Builder();
		try{
			translateInputPorts(script, input, translated);
			translateOptions(script, input, translated);
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
	void translateInputPorts(final XProcScript script,final  XProcInput input,
			XProcInput.Builder builder) throws IOException {

		Iterable<XProcPortInfo> inputInfos =script.getXProcPipelineInfo().getInputPorts();
		//filter those ports which are null

		//for (XProcPortInfo portInfo : Collections2.filter(Lists.newLinkedList(inputInfos),URITranslatorHelper.getNullPortFilter(input))) {
		//There shouldnt be any null input port because of how the XProcPipelineInfo works
		for (XProcPortInfo portInfo : inputInfos){
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
				URI uri = contextDir.toURI().resolve(relUri);
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
	void translateOptions(final XProcScript script , final XProcInput input,
			XProcInput.Builder resolvedInput) {

		Collection<XProcOptionInfo> optionInfos = Lists.newLinkedList(script.getXProcPipelineInfo().getOptions());

		//options which are translatable and outputs	
		Collection<XProcOptionInfo> outputs= Collections2.filter(optionInfos,URITranslatorHelper.getTranslatableOutputOptionsFilter(script));

		this.translateOutputOptions(outputs,input,resolvedInput);
		//options which are translatable and inputs 
		Collection<XProcOptionInfo> inputs= Collections2.filter(optionInfos,URITranslatorHelper.getTranslatableInputOptionsFilter(script));
		this.translateInputOptions(inputs,input,resolvedInput);

		//options that are to be verbatim copied 
		Collection<XProcOptionInfo> verbatims= Collections2.filter(optionInfos,Predicates.not(URITranslatorHelper.getTranslatableOptionFilter(script)));
		this.copyOptions(verbatims,input,resolvedInput);


	}

	void copyOptions(Collection<XProcOptionInfo> options,XProcInput input,XProcInput.Builder builder){
			for(XProcOptionInfo option: options){
				builder.withOption(option.getName(),input.getOptions().get(option.getName()));
			}
	}

	void translateInputOptions(Collection<XProcOptionInfo> options,XProcInput input,XProcInput.Builder builder){
			for(XProcOptionInfo option: options){
				String relative = input.getOptions().get(option.getName());
				if(URITranslatorHelper.notEmpty(relative)){
					try{
						URI uri=contextDir.toURI().resolve(URI.create(relative));
						builder.withOption(option.getName(), uri.toString());
					}catch(IllegalArgumentException e){
						throw new RuntimeException(String.format("Error parsing uri (%s) for option %s",relative,option.getName()));
					}
				}

			}
	}

	void translateOutputOptions(Collection<XProcOptionInfo> options,XProcInput input,XProcInput.Builder builder){
			for(XProcOptionInfo option: options){
				String relative = input.getOptions().get(option.getName());
				if(!URITranslatorHelper.notEmpty(relative)){
					relative=URITranslatorHelper.notEmpty(option.getSelect()) ? option.getSelect() : 
						URITranslatorHelper.generateOptionOutput(option,script); 
					//maybe it should be better to check all the outputs at the end?
					if (generatedOutputs.contains(relative)) {
						throw new IllegalArgumentException(
								String.format("Conflict when generating uri's a default value and option name have are equal: %s",relative));
					}
					generatedOutputs.add(relative);
				}

				try{
					URI uri=outputDir.toURI().resolve(URI.create(relative));
					builder.withOption(option.getName(), uri.toString());
				}catch(IllegalArgumentException e){
					throw new RuntimeException(String.format("Error parsing uri (%s) for option %s",relative,option.getName()),e);
				}

			}
	}
}
