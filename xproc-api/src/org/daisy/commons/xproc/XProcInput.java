package org.daisy.commons.xproc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.xml.namespace.QName;

import org.daisy.commons.xproc.io.Resource;

public class XProcInput {
	public static final class Builder {
		private final XProcPipelineInfo info;
		private final HashMap<String, List<Resource>> inputs = Maps
				.newHashMap();
		private final Map<String, Map<QName, String>> parameters = Maps
				.newHashMap();
		private final Map<QName, String> options = Maps.newHashMap();

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

	private final Map<String, List<Resource>> inputs;
	private final Map<String, Map<QName, String>> parameters;
	private final Map<QName, String> options;

	private XProcInput(Map<String, List<Resource>> inputs,
			Map<String, Map<QName, String>> parameters,
			Map<QName, String> options) {
		ImmutableMap.Builder<String, List<Resource>> inputsBuilder = ImmutableMap
				.builder();
		for (String key : inputs.keySet()) {
			inputsBuilder.put(key, ImmutableList.copyOf(inputs.get(key)));
		}
		this.inputs = inputsBuilder.build();
		ImmutableMap.Builder<String, Map<QName, String>> parametersBuilder = ImmutableMap
				.builder();
		for (String key : parameters.keySet()) {
			parametersBuilder
					.put(key, ImmutableMap.copyOf(parameters.get(key)));
		}
		this.parameters = parametersBuilder.build();
		this.options = ImmutableMap.copyOf(options);
	}

	public Iterable<Resource> getInputs(String port) {
		return ImmutableList.copyOf(inputs.get(port));
	}

	public Map<QName, String> getParameters(String port) {
		return ImmutableMap.copyOf(parameters.get(port));
	}

	public Map<QName, String> getOptions() {
		return options;
	}

}
