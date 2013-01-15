package org.daisy.pipeline.persistence.jobs;

import java.net.URI;

import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;

public class ScriptRegistryHolder{
	private static ScriptRegistry registry=null;	

	public static void setScriptRegistry(ScriptRegistry registry){
		ScriptRegistryHolder.registry=registry;
	}
	public static ScriptRegistry getScriptRegistry(){
		if(registry == null){
			throw new IllegalStateException("No script registry configured");
		}
		return registry;
	}

	public static XProcScript load(URI scriptUri){
		return getScriptRegistry().getScript(scriptUri).load();			
	}
}
