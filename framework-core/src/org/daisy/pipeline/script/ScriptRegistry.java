/*
 * 
 */
package org.daisy.pipeline.script;

import java.net.URI;


/**
 * The Interface ScriptRegistry keeps track of the scripts defined by the loaded modules.
 */
public interface ScriptRegistry {
	
	/**
	 * Gets the script located at the uri.
	 *
	 * @param uri the uri
	 * @return the script
	 */
	public XProcScriptService getScript(URI uri);
	
	/**
	 * Gets all the scripts.
	 *
	 * @return the scripts
	 */
	public Iterable<XProcScriptService> getScripts();
}
 