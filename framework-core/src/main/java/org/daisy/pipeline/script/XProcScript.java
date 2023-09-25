package org.daisy.pipeline.script;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.datatypes.DatatypeRegistry;
import org.daisy.pipeline.datatypes.DatatypeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * XProc based implementation of {@link Script}
 */
public final class XProcScript extends Script {

	private static final Logger logger = LoggerFactory.getLogger(XProcScript.class);

	private final URI uri;
	private final Map<String,XProcScriptOption> inputOptions;
	private final List<XProcScriptOption> tempOptions;
	private final Map<String,XProcScriptOption> resultOptions;
	private final Optional<ScriptPort> statusPort;

	/**
	 * The URI of the XProc pipeline.
	 */
	public URI getURI() {
		return uri;
	}

	public XProcScriptOption getInputOption(String name) {
		return inputOptions.get(name);
	}

	public Iterable<XProcScriptOption> getTempOptions() {
		return tempOptions;
	}

	public XProcScriptOption getResultOption(String name) {
		return resultOptions.get(name);
	}

	public Optional<ScriptPort> getStatusPort() {
		return statusPort;
	}

	@Override
	public String toString() {
		return String.format("XProcScript[name=%s]", this.getName());
	}

	/**
	 * Builder for {@link XProcScript} objects.
	 */
	public static class Builder extends Script.Builder {

		private final URI uri;
		private final Map<String,XProcScriptOption> inputOptions = new HashMap<>();
		private final List<XProcScriptOption> tempOptions = new ArrayList<>();
		private final Map<String,XProcScriptOption> resultOptions = new HashMap<>();
		private ScriptPort statusPort;
		private final DatatypeRegistry datatypes;

		public Builder(XProcScriptService descriptor, URI uri, DatatypeRegistry datatypes) {
			super(descriptor);
			this.uri = uri;
			this.datatypes = datatypes;
		}

		public Builder(String id, String version, URI uri, DatatypeRegistry datatypes) {
			super(id, version);
			this.uri = uri;
			this.datatypes = datatypes;
		}

		public Builder withOption(XProcOptionInfo info, XProcOptionMetadata metadata) {
			switch (metadata.getOutput()) {
			case TEMP:
				if (XProcOptionMetadata.ANY_DIR_URI.equals(metadata.getType())) {
					// need (unique) name to derive default value from
					tempOptions.add(new XProcScriptOption(uniqueOptionName(info.getName()), info, metadata, datatypes));
					return this;
				}
				break;
			case RESULT:
				String type = metadata.getType();
				if (XProcOptionMetadata.ANY_FILE_URI.equals(type) || XProcOptionMetadata.ANY_DIR_URI.equals(type)) {
					String portName = uniqueOutputPortName(info.getName());
					ScriptPort port = new ScriptPort() {
							@Override
							public String getName() {
								return portName;
							}
							@Override
							public boolean isPrimary() {
								return metadata.isPrimary();
							}
							@Override
							public boolean isSequence() {
								return XProcOptionMetadata.ANY_DIR_URI.equals(type);
							}
							@Override
							public boolean isRequired() {
								return false;
							}
							@Override
							public String getNiceName() {
								return metadata.getNiceName();
							}
							@Override
							public String getDescription() {
								return metadata.getDescription();
							}
							@Override
							public String getMediaType() {
								return metadata.getMediaType();
							}
						};
					withOutputPort(port.getName(), port);
					// need (unique) name to derive default file/dir from
					resultOptions.put(port.getName(),
					                  new XProcScriptOption(uniqueOptionName(info.getName()), info, metadata, datatypes));
					return this;
				}
				break;
			default:
				if (XProcOptionMetadata.ANY_FILE_URI.equals(metadata.getType())) {
					String portName = uniqueInputPortName(info.getName());
					ScriptPort port = new ScriptPort() {
							@Override
							public String getName() {
								return portName;
							}
							@Override
							public boolean isPrimary() {
								return metadata.isPrimary();
							}
							@Override
							public boolean isSequence() {
								return metadata.isSequence();
							}
							@Override
							public boolean isRequired() {
								return info.isRequired();
							}
							@Override
							public String getNiceName() {
								return metadata.getNiceName();
							}
							@Override
							public String getDescription() {
								return metadata.getDescription();
							}
							@Override
							public String getMediaType() {
								return metadata.getMediaType();
							}
						};
					withInputPort(port.getName(), port);
					inputOptions.put(port.getName(), new XProcScriptOption(null, info, metadata, datatypes));
					return this;
				}
			}
			XProcScriptOption option = new XProcScriptOption(uniqueOptionName(info.getName()), info, metadata, datatypes);
			super.withOption(option.getName(), option);
			return this;
		}

		public Builder withInputPort(XProcPortInfo info, XProcPortMetadata metadata) {
			ScriptPort port = new XProcScriptPort(info, metadata);
			super.withInputPort(port.getName(), port);
			return this;
		}

		public Builder withOutputPort(XProcPortInfo info, XProcPortMetadata metadata) {
			String mediaType = metadata.getMediaType();
			ScriptPort port = new XProcScriptPort(info, metadata);
			if (XProcPortMetadata.MEDIA_TYPE_STATUS_XML.equals(mediaType))
				statusPort = port;
			else
				super.withOutputPort(port.getName(), port);
			return this;
		}

		/**
		 * Builds the {@link XProcScript} instance.
		 */
		@Override
		public XProcScript build() {
			return new XProcScript(uri, id, version, shortName, description, homepage,
			                       inputPorts, outputPorts, options, inputFilesets, outputFilesets,
			                       inputOptions, tempOptions, resultOptions, statusPort);
		}

		private Map<String,Integer> inputPortCollisions = new HashMap<>();
		private Map<String,Integer> optionCollisions = new HashMap<>();
		private Map<String,Integer> outputPortCollisions = new HashMap<>();

		/**
		 * Remove namespace and handle collisions.
		 */
		private String uniqueInputPortName(QName qName) {
			String name = qName.getLocalPart();
			if (inputPorts.containsKey(name)) {
				inputPorts.put(name + "-1", inputPorts.remove(name));
				inputPortCollisions.put(name, 1);
			}
			if (inputPortCollisions.containsKey(name)) {
				int i = inputPortCollisions.get(name) + 1;
				inputPortCollisions.put(name, i);
				return name + "-" + i;
			} else
				return name;
		}

		/**
		 * Remove namespace and handle collisions.
		 */
		private String uniqueOptionName(QName qName) {
			String name = qName.getLocalPart();
			if (options.containsKey(name)) {
				options.put(name + "-1", options.remove(name));
				optionCollisions.put(name, 1);
			}
			if (optionCollisions.containsKey(name)) {
				int i = optionCollisions.get(name) + 1;
				optionCollisions.put(name, i);
				return name + "-" + i;
			} else
				return name;
		}

		/**
		 * Remove namespace and handle collisions.
		 */
		private String uniqueOutputPortName(QName qName) {
			String name = qName.getLocalPart();
			if (outputPorts.containsKey(name)) {
				outputPorts.put(name + "-1", outputPorts.remove(name));
				outputPortCollisions.put(name, 1);
			}
			if (outputPortCollisions.containsKey(name)) {
				int i = outputPortCollisions.get(name) + 1;
				outputPortCollisions.put(name, i);
				return name + "-" + i;
			} else
				return name;
		}
	}

	private XProcScript(URI uri, String id, String version, String name, String description, String homepage,
	                    Map<String,ScriptPort> inputPorts, Map<String,ScriptPort> outputPorts, Map<String,ScriptOption> options,
	                    List<String> inputFilesets, List<String> outputFilesets,
	                    Map<String,XProcScriptOption> inputOptions, List<XProcScriptOption> tempOptions,
	                    Map<String,XProcScriptOption> resultOptions, ScriptPort statusPort) {
		super(id, version, name, description, homepage, inputPorts, outputPorts, options, inputFilesets, outputFilesets);
		this.uri = uri;
		this.inputOptions = ImmutableMap.copyOf(inputOptions);
		this.tempOptions = ImmutableList.copyOf(tempOptions);
		this.resultOptions = ImmutableMap.copyOf(resultOptions);
		this.statusPort = Optional.ofNullable(statusPort);
	}

	@Override
	public XProcScriptOption getOption(String name) {
		return (XProcScriptOption)super.getOption(name);
	}

	public static class XProcScriptOption implements ScriptOption {

		private final String name;
		// XProcDecorator requires access to info
		private final XProcOptionInfo info;
		private final XProcOptionMetadata metadata;
		private final String defaultValue;
		private final DatatypeService datatype;

		public XProcScriptOption(String name,
		                         XProcOptionInfo info,
		                         XProcOptionMetadata metadata,
		                         DatatypeRegistry datatypes) {
			this.name = name;
			this.info = info;
			this.metadata = metadata;
			if (info.isRequired()) {
				defaultValue = null;
			} else {
				String select = info.getSelect();
				if (select == null || "".equals(select)) {
					// script options must have a default value even if the XProc option does not have one
					defaultValue = "";
				} else {
					// the default value of script options must be a string literal
					char quote = select.charAt(0);
					if ((quote == '"' || quote == '\'') && select.charAt(select.length() - 1) == quote)
						defaultValue = select.substring(1, select.length() - 1);
					else {
						logger.debug("Select statement is not a string literal: " + select);
						defaultValue = "";
					}
				}
			}
			String type = metadata.getType();
			if (type == null || "".equals(type) || "xs:string".equals(type) || "string".equals(type))
				datatype = DatatypeService.XS_STRING;
			else if ("xs:integer".equals(type) || "integer".equals(type))
				datatype = DatatypeService.XS_INTEGER;
			else if ("xs:nonNegativeInteger".equals(type) || "nonNegativeInteger".equals(type))
				datatype = DatatypeService.XS_NON_NEGATIVE_INTEGER;
			else if ("xs:boolean".equals(type) || "boolean".equals(type))
				datatype = DatatypeService.XS_BOOLEAN;
			else if ("xs:anyURI".equals(type) || "anyURI".equals(type))
				datatype = DatatypeService.XS_ANY_URI;
			else if ("anyFileURI".equals(type))
				datatype = DatatypeService.ANY_FILE_URI;
			else if ("anyDirURI".equals(type))
				datatype = DatatypeService.ANY_DIR_URI;
			else {
				datatype = datatypes.getDatatype(type).orNull();
				if (datatype == null)
					throw new IllegalArgumentException(
						"Invalid px:type '" + type + "': does not match a known data type");
			}
		}

		@Override
		public String getName() {
			return name;
		}

		public QName getXProcOptionName() {
			return info.getName();
		}

		@Override
		public boolean isRequired() {
			return info.isRequired();
		}

		@Override
		public String getDefault() {
			return defaultValue;
		}

		@Override
		public String getNiceName() {
			return metadata.getNiceName();
		}

		@Override
		public String getDescription() {
			return metadata.getDescription();
		}

		@Override
		public DatatypeService getType() {
			return datatype;
		}

		@Override
		public String getMediaType() {
			return metadata.getMediaType();
		}

		@Override
		public boolean isPrimary() {
			return metadata.isPrimary();
		}

		@Override
		public boolean isSequence() {
			return metadata.isSequence();
		}

		@Override
		public boolean isOrdered() {
			return metadata.isOrdered();
		}

		/**
		 * Separator for serializing a sequence of values.
		 */
		public String getSeparator() {
			return metadata.getSeparator();
		}
	}

	private static class XProcScriptPort implements ScriptPort {

		private final XProcPortInfo info;
		private final XProcPortMetadata metadata;

		public XProcScriptPort(XProcPortInfo info,
		                       XProcPortMetadata metadata) {
			this.info = info;
			this.metadata = metadata;
		}

		@Override
		public String getName() {
			return info.getName();
		}

		@Override
		public boolean isPrimary() {
			return info.isPrimary();
		}

		@Override
		public boolean isSequence() {
			return info.isSequence();
		}

		@Override
		public boolean isRequired() {
			return info.isRequired();
		}

		@Override
		public String getNiceName() {
			return metadata.getNiceName();
		}

		@Override
		public String getDescription() {
			return metadata.getDescription();
		}

		@Override
		public String getMediaType() {
			return metadata.getMediaType();
		}
	}
}
