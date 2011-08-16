package org.daisy.pipeline.script;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

public final class XProcScriptService {

	/** The Constant CONVERTER_URL. */
	public static final String CONVERTER_URL = "converter.url";

	/** The Constant CONVERTER_DESCRIPTION. */
	public static final String CONVERTER_DESCRIPTION = "converter.description";

	/** The Constant CONVERTER_NAME. */
	public static final String CONVERTER_NAME = "converter.name";

	private URI uri;
	private String name;
	private String description;
	private Supplier<XProcScript> script;

	public XProcScriptService() {
	}

	public void activate(Map<?, ?> properties) {
		if (properties.get(CONVERTER_NAME) == null
				|| properties.get(CONVERTER_NAME).toString().isEmpty()) {
			throw new IllegalArgumentException(CONVERTER_NAME
					+ " property must not be empty");
		}

		if (properties.get(CONVERTER_DESCRIPTION) == null
				|| properties.get(CONVERTER_DESCRIPTION).toString().isEmpty()) {
			throw new IllegalArgumentException(CONVERTER_DESCRIPTION
					+ " property must not be empty");
		}
		if (properties.get(CONVERTER_URL) == null
				|| properties.get(CONVERTER_URL).toString().isEmpty()) {
			throw new IllegalArgumentException(CONVERTER_URL
					+ " property must not be empty");
		}
		try {
			this.uri = new URI(properties.get(CONVERTER_URL).toString());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(CONVERTER_URL
					+ " property must not be a legal URI");
		}
		this.name = properties.get(CONVERTER_NAME).toString();
		this.description = properties.get(CONVERTER_DESCRIPTION).toString();
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
