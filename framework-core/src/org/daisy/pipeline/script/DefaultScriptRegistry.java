/*
 * 
 */
package org.daisy.pipeline.script;

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

// TODO: Auto-generated Javadoc
//TODO check thread safety 
/**
 * The Class DefaultScriptRegistry.
 */
public class DefaultScriptRegistry implements ScriptRegistry {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DefaultScriptRegistry.class); 

	/** The descriptors. */
	private final Map<URI, XProcScriptService> descriptors = Maps.newHashMap();
	
	/** The parser. */
	private XProcScriptParser parser;

	/**
	 * Activate.
	 */
	public void activate(){
		logger.trace("Activating script registry");
	}
	
	/**
	 * Register.
	 *
	 * @param script the script
	 */
	public void register(final XProcScriptService script) {
		logger.trace("registering script {}",script.getURI());
		if (!script.hasParser()){
			script.setParser(parser);
		}
		// TODO check
		descriptors.put(script.getURI(), script);
	}

	/**
	 * Unregister.
	 *
	 * @param script the script
	 */
	public void unregister(XProcScriptService script) {
		// TODO check
		descriptors.remove(script);
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.script.ScriptRegistry#getScript(java.net.URI)
	 */
	@Override
	public XProcScriptService getScript(URI uri) {
		return descriptors.get(uri);
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.script.ScriptRegistry#getScripts()
	 */
	@Override
	public Iterable<XProcScriptService> getScripts() {
		return ImmutableList.copyOf(descriptors.values());
	}

	/**
	 * Sets the parser.
	 *
	 * @param parser the new parser
	 */
	public void setParser(XProcScriptParser parser) {
		// TODO check
		this.parser = parser;
	}

	/**
	 * Unset parser.
	 *
	 * @param parser the parser
	 */
	public void unsetParser(XProcScriptParser parser) {
	}
}
