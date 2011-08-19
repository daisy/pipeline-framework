package org.daisy.pipeline.script;

import java.net.URI;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

//TODO check thread safety 
public class DefaultScriptRegistry implements ScriptRegistry {

	private final Map<URI, XProcScriptService> descriptors = Maps.newHashMap();
	private XProcScriptParser parser;

	public void register(final XProcScriptService script) {
		if (!script.hasParser()){
			script.setParser(parser);
		}
		// TODO check
		descriptors.put(script.getURI(), script);
	}

	public void unregister(XProcScriptService script) {
		// TODO check
		descriptors.remove(script);
	}

	@Override
	public XProcScriptService getScript(URI uri) {
		return descriptors.get(uri);
	}

	@Override
	public Iterable<XProcScriptService> getScripts() {
		return ImmutableList.copyOf(descriptors.values());
	}

	public void setParser(XProcScriptParser parser) {
		// TODO check
		this.parser = parser;
	}

	public void unsetParser(XProcScriptParser parser) {
	}
}
