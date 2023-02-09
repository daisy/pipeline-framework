package org.daisy.pipeline.script;

public class BoundScript {

	private final Script script;
	private final ScriptInput input;

	private BoundScript(Script script, ScriptInput input) {
		this.script = script;
		this.input = input;
	}

	public static BoundScript from(Script script, ScriptInput input) {
		return new BoundScript(script, input);
	}

	public Script getScript() {
		return script;
	}

	public ScriptInput getInput() {
		return input;
	}
}
