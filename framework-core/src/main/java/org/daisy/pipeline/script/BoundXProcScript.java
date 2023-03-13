package org.daisy.pipeline.script;

import org.daisy.common.xproc.XProcInput;

public class BoundXProcScript {

	private final XProcScript script;
	private final XProcInput input;

	private BoundXProcScript(XProcScript script, XProcInput input) {
		this.script = script;
		this.input = input;
	}

	public static BoundXProcScript from(XProcScript script, XProcInput input) {
		return new BoundXProcScript(script, input);
	}

	/**
	 * @return the script
	 */
	public XProcScript getScript() {
		return script;
	}

	/**
	 * @return the input
	 */
	public XProcInput getInput() {
		return input;
	}
}
