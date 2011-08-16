package org.daisy.pipeline.script;

import java.net.URI;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

//TODO check thread safety 
public class DefaultScriptRegistry implements ScriptRegistry {

	private final Map<URI, Supplier<XProcScript>> scripts = Maps.newHashMap();
	private final Map<URI, XProcScriptService> descriptors = Maps.newHashMap();
	private XProcScriptParser parser;

	public void register(final XProcScriptService script) {
		// TODO check
		descriptors.put(script.getURI(), script);
		scripts.put(script.getURI(),
				Suppliers.memoize(new Supplier<XProcScript>() {

					@Override
					public XProcScript get() {
						return parser.parse(script.getURI());
					}
				}));
	}

	public void unregister(XProcScriptService script) {
		// TODO check
		scripts.remove(script);
	}

	@Override
	public XProcScriptService getDescriptor(URI uri) {
		// TODO check
		return descriptors.get(uri);
	}

	@Override
	public Iterable<XProcScriptService> getDescriptors() {
		// TODO check
		return ImmutableList.copyOf(descriptors.values());
	}

	@Override
	public XProcScript getScript(URI uri) {
		// TODO check
		return scripts.get(uri).get();
	}

	@Override
	public Iterable<XProcScript> getScripts() {
		// TODO check
		// TODO replace with Suppliers#supplierFunction() from guava 09
		return Iterables.transform(ImmutableList.copyOf(scripts.values()),
				new Function<Supplier<XProcScript>, XProcScript>() {
					@Override
					public XProcScript apply(Supplier<XProcScript> supplier) {
						return supplier.get();
					}
				});
	}

	public void setParser(XProcScriptParser parser) {
		// TODO check
		this.parser = parser;
	}

	public void unsetParser(XProcScriptParser parser) {
		if (this.parser == parser)
			this.parser = null;
	}
}
