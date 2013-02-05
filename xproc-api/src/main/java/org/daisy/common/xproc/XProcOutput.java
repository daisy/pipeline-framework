package org.daisy.common.xproc;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Result;

import org.daisy.common.base.Provider;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;


/**
 * The Class XProcOutput gives access to the result documents generated after the pipeline exection, this class is immutable.
 */
public class XProcOutput {

	/**
	 * The Class Builder.
	 */
	public static final class Builder {

		

		/** The outputs. */
		private final HashMap<String, Provider<Result>> outputs = Maps
				.newHashMap();

	
	

		/**
		 * With output.
		 *
		 * @param port the port
		 * @param result the result
		 * @return the builder
		 */
		public Builder withOutput(String port, Provider<Result> result) {
			// TODO check if compatible with info
			outputs.put(port, result);
			return this;
		}

		/**
		 * Builds the xproc output object
		 *
		 * @return the xproc output
		 */
		public XProcOutput build() {
			return new XProcOutput(outputs);
		}
	}

	/** The outputs. */
	private final Map<String, Provider<Result>> outputs;

	/**
	 * Instantiates a new x proc output.
	 *
	 * @param outputs the outputs
	 */
	private XProcOutput(Map<String, Provider<Result>> outputs) {
		this.outputs = ImmutableMap.copyOf(outputs);
	}

	/**
	 * Gets the result provider for the given port name
	 *
	 * @param port the port
	 * @return the result provider
	 */
	public Provider<Result> getResultProvider(String port) {
		return outputs.get(port);
	}
}
