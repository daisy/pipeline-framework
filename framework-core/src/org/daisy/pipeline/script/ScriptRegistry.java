package org.daisy.pipeline.script;

import java.net.URI;

public interface ScriptRegistry {
	public XProcScript getScript(URI uri);
	public XProcScriptService getDescriptor(URI uri);
	
	public Iterable<XProcScriptService> getDescriptors();
	public Iterable<XProcScript> getScripts();
}
