package org.daisy.commons.xproc;

import java.net.URI;
import java.util.Map;

import javax.xml.namespace.QName;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public final class XProcPipelineInfo {

	public static final class Builder {

		private URI uri;
		private final Map<String, XProcPortInfo> inputPorts = Maps.newHashMap();
		private final Map<String, XProcPortInfo> parameterPorts = Maps.newHashMap();
		private final Map<String, XProcPortInfo> outputPorts = Maps.newHashMap();
		private final Map<QName, XProcOptionInfo> options = Maps.newHashMap();

		public Builder() {
		}

		public Builder withURI(URI uri) {
			this.uri = uri;
			return this;
		}

		public Builder withPort(XProcPortInfo port) {
			switch (port.getKind()) {
			case INPUT:
				inputPorts.put(port.getName(), port);
				break;
			case OUTPUT:
				outputPorts.put(port.getName(), port);
				break;
			case PARAMETER:
				parameterPorts.put(port.getName(), port);
				break;
			}
			return this;
		}

		public Builder withOption(XProcOptionInfo option) {
			this.options.put(option.getName(), option);
			return this;
		}

		public final XProcPipelineInfo build() {
			return new XProcPipelineInfo(uri, inputPorts, parameterPorts,
					outputPorts, options);
		}
	}

	private final URI uri;
	private final Map<String, XProcPortInfo> inputPorts;
	private final Map<String, XProcPortInfo> parameterPorts;
	private final Map<String, XProcPortInfo> outputPorts;
	private final Map<QName, XProcOptionInfo> options;

	private XProcPipelineInfo(URI uri, Map<String, XProcPortInfo> inputPorts,
			Map<String, XProcPortInfo> parameterPorts,
			Map<String, XProcPortInfo> outputPorts,
			Map<QName, XProcOptionInfo> options) {
		this.uri = uri;
		this.inputPorts = ImmutableMap.copyOf(inputPorts);
		this.parameterPorts = ImmutableMap.copyOf(parameterPorts);
		this.outputPorts = ImmutableMap.copyOf(outputPorts);
		this.options = ImmutableMap.copyOf(options);
	}

	public URI getURI() {
		return uri;
	}

	public Iterable<XProcPortInfo> getInputPorts() {
		return inputPorts.values();
	}

	public XProcPortInfo getInputPort(String name) {
		return inputPorts.get(name);
	}

	public Iterable<XProcOptionInfo> getOptions() {
		return options.values();
	}

	public XProcOptionInfo getOption(QName name) {
		return options.get(name);
	}

	public Iterable<XProcPortInfo> getOutputPorts() {
		return outputPorts.values();
	}

	public XProcPortInfo getOutputPort(String name) {
		return outputPorts.get(name);
	}

	public Iterable<String> getParameterPorts() {
		return parameterPorts.keySet();
	}
}
