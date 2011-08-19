package org.daisy.common.xproc;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;

import org.daisy.common.base.Provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class XProcOutput {
	public static final class Builder {
		private final XProcPipelineInfo info;
		private final HashMap<String, Provider<Result>> outputs = Maps
				.newHashMap();

		public Builder() {
			this.info = null;
		}

		public Builder(XProcPipelineInfo info) {
			this.info = info;
		}

		public Builder withOutput(String port, Provider<Result> result) {
			// TODO check if compatible with info
			outputs.put(port, result);
			return this;
		}

		public XProcOutput build() {
			return new XProcOutput(outputs);
		}
	}

	private final Map<String, Provider<Result>> outputs;

	private XProcOutput(Map<String, Provider<Result>> outputs) {
		this.outputs = ImmutableMap.copyOf(outputs);
	}

	public Provider<Result> getResultProvider(String port) {
		return outputs.get(port);
	}
}
