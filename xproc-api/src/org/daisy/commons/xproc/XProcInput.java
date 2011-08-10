package org.daisy.commons.xproc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.daisy.commons.xproc.io.Resource;

public class XProcInput {
	public static final class Builder {
		private final XProcPipelineInfo info;
		private final HashMap<String, List<Resource>> inputs = new HashMap<String, List<Resource>>();
		private final Map<String, Map<QName, String>> parameters = new HashMap<String, Map<QName, String>>();
		private final Map<QName, String> options = new HashMap<QName, String>();

		public Builder() {
			this.info = null;
		}

		public Builder(XProcPipelineInfo info) {
			this.info = info;
		}

		public Builder withInput(String port, Resource resource) {
			// TODO check if compatible with info
			if (inputs.containsKey(port)) {
				inputs.get(port).add(resource);
			} else {
				LinkedList<Resource> resources = new LinkedList<Resource>();
				resources.add(resource);
				inputs.put(port, resources);
			}
			return this;
		}

		public Builder withOption(QName name, String value) {
			// TODO check if compatible with info
			options.put(name, value);
			return this;
		}

		public Builder withParameter(String port, QName name, String value) {
			// TODO check if compatible with info
			if (parameters.containsKey(port)) {
				parameters.get(port).put(name, value);
			} else {
				Map<QName, String> params = new HashMap<QName, String>();
				params.put(name, value);
				parameters.put(port, params);
			}
			return this;
		}

		public XProcInput build() {
			return new XProcInput(inputs, parameters, options);
		}
	}

	private Map<String, List<Resource>> inputs;
	private Map<String, Map<QName, String>> parameters;
	private Map<QName, String> options;

	private XProcInput(Map<String, List<Resource>> inputs,
			Map<String, Map<QName, String>> parameters,
			Map<QName, String> options) {
		this.inputs = inputs;
		this.parameters = parameters;
		this.options = options;
	}

	public Iterable<Resource> getInputs(String port) {
		// TODO return immutable copy
		return inputs.get(port);
	}

	public Map<QName, String> getParameters(String port) {
		// TODO return immutable copy
		return parameters.get(port);
	}

	public Map<QName, String> getOptions() {
		// TODO return immutable copy
		return options;
	}

}
