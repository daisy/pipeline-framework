package org.daisy.pipeline.script;

import java.net.URL;
import java.util.Map;

import org.daisy.common.file.URLs;
import org.daisy.pipeline.script.impl.StaxXProcScriptParser;

// TODO: Auto-generated Javadoc
/**
 * The Class XProcScriptService defines the script basic attributes loaded from the OSGI bundle.
 */
public class XProcScriptService {

	/** The Constant SCRIPT_URL. */
	public static final String SCRIPT_URL = "script.url";

	/** The Constant SCRIPT_DESCRIPTION. */
	public static final String SCRIPT_DESCRIPTION = "script.description";

	/** The Constant SCRIPT_ID. */
	public static final String SCRIPT_ID = "script.id";
	/** The Constant SCRIPT_ID. */
	public static final String SCRIPT_VERSION = "script.version";

	/** The url. */
	private URL url;

	/** The id. */
	private String id;

	/** The description. */
	private String description;

	/** The version. */
	private String version;

	/** The script. */
	private XProcScript script;

	private StaxXProcScriptParser parser;

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
	public void activate(Map<?, ?> properties, Class<?> context) {
		if (properties.get(SCRIPT_ID) == null
				|| properties.get(SCRIPT_ID).toString().isEmpty()) {
			throw new IllegalArgumentException(SCRIPT_ID
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
		if (properties.get(SCRIPT_VERSION) == null
				|| properties.get(SCRIPT_VERSION).toString().isEmpty()) {
			throw new IllegalArgumentException(SCRIPT_VERSION
					+ " property must not be empty");
		}
		String path = properties.get(SCRIPT_URL).toString();
		url = URLs.getResourceFromJAR(path, context);
		if (url == null)
			throw new IllegalArgumentException("Resource at location " + path + " could not be found");
		id = properties.get(SCRIPT_ID).toString();
		description = properties.get(SCRIPT_DESCRIPTION).toString();
                version= properties.get(SCRIPT_VERSION).toString();
	}

	/**
	 * Gets the script URL.
	 *
	 * @return the url
	 */
	public URL getURL() {
		return url;
	}

	/**
	 * Gets the script ID.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
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
	 * Gets the script version.
	 *
	 * @return the description
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Loads the script into a XProcScript object.
	 *
	 * @return the x proc script
	 */
	public XProcScript load() {
		if (parser == null)
			throw new IllegalStateException("Object was not property initialized");
		if (script == null)
			script = parser.parse(this);
		return script;
	}

	/**
	 * {@link StaxXProcScriptParser} set by {@link ScriptRegistry}
	 */
	void setParser(StaxXProcScriptParser parser) {
		this.parser = parser;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("Id: " + id);
		buf.append(", desc: " + description);
		buf.append(", url: " + url.toString());
		buf.append(", version: " + version.toString());
		return buf.toString();
	}
}
