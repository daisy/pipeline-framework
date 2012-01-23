/*
 *
 */
package org.daisy.pipeline.script;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.daisy.common.xproc.XProcPipeline;
import org.daisy.common.xproc.XProcPipelineInfo;

// TODO: Auto-generated Javadoc
/**
 * XProcScript is an enhanced {@link XProcPipeline} with some extra information, such as production, port and options metadata.
 */
public final class XProcScript {

	/**
	 * Builder for {@link XProcScript} objects.
	 */
	public static class Builder {

		/** The pipeline info. */
		private XProcPipelineInfo pipelineInfo;

		/** The name. */
		private String name;

		/** The description. */
		private String description;

		/** The homepage. */
		private String homepage;

		/** The ports metadata. */
		private final Map<String, XProcPortMetadata> portsMetadata=new HashMap<String, XProcPortMetadata>();

		/** The options metadata. */
		private final Map<QName, XProcOptionMetadata> optionsMetadata=new HashMap<QName, XProcOptionMetadata>();

		/**
		 * With pipeline info.
		 *
		 * @param pipelineInfo the pipeline info
		 * @return the builder
		 */
		public Builder withPipelineInfo(XProcPipelineInfo pipelineInfo){
			this.pipelineInfo=pipelineInfo;
			return this;
		}

		/**
		 * With nice name.
		 *
		 * @param name the name
		 * @return the builder
		 */
		public Builder withNiceName(String name){
			if(name!=null) {
				this.name=name;
			}
			return this;
		}

		/**
		 * With description.
		 *
		 * @param description the description
		 * @return the builder
		 */
		public Builder withDescription(String description){
			if (description!=null) {
				this.description=description;
			}
			return this;
		}

		/**
		 * With homepage.
		 *
		 * @param homepage the homepage
		 * @return the builder
		 */
		public Builder withHomepage(String homepage) {
			if (homepage != null) {
				this.homepage = homepage;
			}
			return this;
		}

		/**
		 * With port metadata.
		 *
		 * @param name the name
		 * @param metadata the metadata
		 * @return the builder
		 */
		public Builder withPortMetadata(String name,XProcPortMetadata metadata){
			portsMetadata.put(name,metadata);
			return this;
		}

		/**
		 * With option metadata.
		 *
		 * @param name the name
		 * @param metadata the metadata
		 * @return the builder
		 */
		public Builder withOptionMetadata(QName name,XProcOptionMetadata metadata){
			optionsMetadata.put(name,metadata);
			return this;
		}

		/**
		 * Builds the {@link XProcScript} instance.
		 *
		 * @return the {@link XProcScript}
		 */
		public XProcScript build(){
			return new XProcScript(pipelineInfo,name,description,homepage,portsMetadata,optionsMetadata);
		}
	}

	/** The pipeline info. */
	private final XProcPipelineInfo pipelineInfo;

	/** The name. */
	private final String name;

	/** The description. */
	private final String description;

	/** The homepage. */
	private final String homepage;

	/** The ports metadata. */
	private final Map<String, XProcPortMetadata> portsMetadata;

	/** The options metadata. */
	private final Map<QName, XProcOptionMetadata> optionsMetadata;


	/**
	 * Instantiates a new x proc script.
	 *
	 * @param pipelineInfo the pipeline info
	 * @param name the name
	 * @param description the description
	 * @param homepage the homepage
	 * @param portsMetadata the ports metadata
	 * @param optionsMetadata the options metadata
	 */
	public XProcScript(XProcPipelineInfo pipelineInfo, String name,
			String description, String homepage, Map<String, XProcPortMetadata> portsMetadata,
			Map<QName, XProcOptionMetadata> optionsMetadata) {
		this.pipelineInfo = pipelineInfo;
		this.name = name;
		this.description = description;
		this.homepage = homepage;
		this.portsMetadata = portsMetadata;
		this.optionsMetadata = optionsMetadata;
	}

	/**
	 * Gets the {@link XProcPipelineInfo}
	 *
	 * @return the x proc pipeline info
	 */
	public final XProcPipelineInfo getXProcPipelineInfo() {
		return pipelineInfo;
	}

	/**
	 * Gets the uRI.
	 *
	 * @return the uRI
	 */
	public final URI getURI(){
		return pipelineInfo.getURI();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public final String getDescription() {
		return description;
	}

	/**
	 * Gets the homepage.
	 *
	 * @return the homepage
	 */
	public final String getHomepage() {
		return homepage;
	}

	/**
	 * Gets the port metadata.
	 *
	 * @param name the name
	 * @return the port metadata
	 */
	public final XProcPortMetadata getPortMetadata(String name) {
		return portsMetadata.get(name);
	}

	/**
	 * Gets the option metadata.
	 *
	 * @param name the name
	 * @return the option metadata
	 */
	public final XProcOptionMetadata getOptionMetadata(QName name) {
		return optionsMetadata.get(name);
	}
}
