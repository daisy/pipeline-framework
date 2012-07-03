/*
 *
 */
package org.daisy.pipeline.script;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

// TODO: Auto-generated Javadoc
/**
 * The Class XProcScriptService defines the script basic attributes loaded from the OSGI bundle.
 */
public final class XProcScriptService {

	/** The Constant SCRIPT_URL. */
	public static final String SCRIPT_URL = "script.url";

	/** The Constant SCRIPT_DESCRIPTION. */
	public static final String SCRIPT_DESCRIPTION = "script.description";

	/** The Constant SCRIPT_NAME. */
	public static final String SCRIPT_NAME = "script.name";

	/** The uri. */
	private URI uri;

	/** The name. */
	private String name;

	/** The description. */
	private String description;

	/** The script. */
	private Supplier<XProcScript> script;

	/**
	 * Instantiates a new x proc script service.
	 */
	public XProcScriptService() {
	}

	/**
	 * Activate method called by ds.
	 *
	 * @param properties the properties
	 */
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
			uri = new URI(properties.get(SCRIPT_URL).toString());
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(SCRIPT_URL
					+ " property must not be a legal URI");
		}
		name = properties.get(SCRIPT_NAME).toString();
		description = properties.get(SCRIPT_DESCRIPTION).toString();
	}

	/**
	 * Gets the script URI.
	 *
	 * @return the uRI
	 */
	public URI getURI() {
		return uri;
	}

	/**
	 * Gets the script name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the script description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Loads the script into a XProcScript object.
	 *
	 * @return the x proc script
	 */
	public XProcScript load() {
		return script.get();
	}

	/**
	 * Sets the parser.
	 *
	 * @param parser the new parser
	 */
	public void setParser(final XProcScriptParser parser) {
		script = Suppliers.memoize(new Supplier<XProcScript>() {
			@Override
			public XProcScript get() {
				return parser.parse(XProcScriptService.this);
			}
		});
	}

	/**
	 * Checks for parser.
	 *
	 * @return true, if successful
	 */
	public boolean hasParser() {
		return script != null;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Name: " + name);
		buf.append(", desc: " + description);
		buf.append(", uri: " + uri.toString());
		return buf.toString();
	}
}
