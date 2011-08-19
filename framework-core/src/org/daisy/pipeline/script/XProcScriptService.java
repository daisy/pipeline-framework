package org.daisy.pipeline.script;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public final class XProcScriptService {

	public static final String SCRIPT_URL = "script.url";
	public static final String SCRIPT_DESCRIPTION = "script.description";
	public static final String SCRIPT_NAME = "script.name";

	private URI uri;
	private String name;
	private String description;
	private Supplier<XProcScript> script;

	public XProcScriptService() {
	}

	public void activate(Map<?, ?> properties) {
		if (properties.get(SCRIPT_NAME) == null
				|| properties.get(SCRIPT_NAME).toString().isEmpty()) {
			throw new IllegalArgumentException(SCRIPT_NAME
					+ " property must not be empty");
		}

		if (properties.get(SCRIPT_DESCRIPTION) == null
				|| properties.get(SCRIPT_DESCRIPTION).toString().isEmpty()) {
			throw new IllegalArgumentException(SCRIPT_DESCRIPTION
					+ " property must not be empty");
		}
		if (properties.get(SCRIPT_URL) == null
				|| properties.get(SCRIPT_URL).toString().isEmpty()) {
			throw new IllegalArgumentException(SCRIPT_URL
					+ " property must not be empty");
		}
		try {
			this.uri = new URI(properties.get(SCRIPT_URL).toString());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(SCRIPT_URL
					+ " property must not be a legal URI");
		}
		this.name = properties.get(SCRIPT_NAME).toString();
		this.description = properties.get(SCRIPT_DESCRIPTION).toString();
	}

	public URI getURI() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public XProcScript load() {
		return script.get();
	}

	public void setParser(final XProcScriptParser parser) {
		script = Suppliers.memoize(new Supplier<XProcScript>() {
			@Override
			public XProcScript get() {
				return parser.parse(uri);
			}
		});
	}

	public boolean hasParser() {
		return script != null;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Name: " + name);
		buf.append(", desc: " + description);
		buf.append(", uri: " + uri.toString());
		return buf.toString();
	}
}
