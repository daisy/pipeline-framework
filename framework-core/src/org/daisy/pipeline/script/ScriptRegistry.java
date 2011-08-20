package org.daisy.pipeline.script;

import java.net.URI;

public interface ScriptRegistry {
	
	public XProcScriptService getScript(URI uri);
	public Iterable<XProcScriptService> getScripts();
}
 