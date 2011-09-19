/*
 * 
 */
package org.daisy.pipeline.script;

import java.net.URI;

// TODO: Auto-generated Javadoc
/**
 * The Interface XProcScriptParser.
 */
public interface XProcScriptParser  {

	/**
	 * Parses the script file
	 *
	 * @param uri the uri
	 * @return the x proc script
	 */
	public XProcScript parse(URI uri);

}
