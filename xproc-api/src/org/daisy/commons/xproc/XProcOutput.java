package org.daisy.commons.xproc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;

import org.daisy.common.base.Provider;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class XProcOutput {
	public static final class Builder {
		private final XProcPipelineInfo info;
		private final HashMap<String, List<Provider<Result>>> outputs = Maps
				.newHashMap();

		public Builder() {
			this.info = null;
		}

		public Builder(XProcPipelineInfo info) {
			this.info = info;
		}

		public Builder withOutput(String port, Provider<Result> result) {
			// TODO check if compatible with info
			if (outputs.containsKey(port)) {
				outputs.get(port).add(result);
			} else {
				List<Provider<Result>> resources = Lists.newLinkedList();
				resources.add(result);
				outputs.put(port, resources);
			}
			return this;
		}

		public XProcOutput build() {
			return new XProcOutput(outputs);
		}
	}

	private final Map<String, List<Provider<Result>>> outputs;

	private XProcOutput(Map<String, List<Provider<Result>>> outputs) {
		ImmutableMap.Builder<String, List<Provider<Result>>> outputsBuilder = ImmutableMap
				.builder();
		for (String key : outputs.keySet()) {
			outputsBuilder.put(key, ImmutableList.copyOf(outputs.get(key)));
		}
		this.outputs = outputsBuilder.build();
	}

	public Iterable<Provider<Result>> getOutputs(String port) {
		return ImmutableList.copyOf(outputs.get(port));
	}
}
