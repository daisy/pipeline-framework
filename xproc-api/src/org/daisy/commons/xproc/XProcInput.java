package org.daisy.commons.xproc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.daisy.common.base.Provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


public final class XProcInput {
	public static final class Builder {
		private final XProcPipelineInfo info;
		private final HashMap<String, List<Provider<Source>>> inputs = Maps
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

		public Builder withInput(String port, Provider<Source> source) {
			// TODO check if compatible with info
			if (inputs.containsKey(port)) {
				inputs.get(port).add(source);
			} else {
				List<Provider<Source>> resources = Lists.newLinkedList();
				resources.add(source);
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

	private final Map<String, List<Provider<Source>>> inputs;
	private final Map<String, Map<QName, String>> parameters;
	private final Map<QName, String> options;

	private XProcInput(Map<String, List<Provider<Source>>> inputs,
			Map<String, Map<QName, String>> parameters,
			Map<QName, String> options) {
		ImmutableMap.Builder<String, List<Provider<Source>>> inputsBuilder = ImmutableMap
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

	public Iterable<Provider<Source>> getInputs(String port) {
		return ImmutableList.copyOf(inputs.get(port));
	}

	public Map<QName, String> getParameters(String port) {
		return ImmutableMap.copyOf(parameters.get(port));
	}

	public Map<QName, String> getOptions() {
		return options;
	}

}
