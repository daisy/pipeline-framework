package org.daisy.pipeline.webservice;

import java.net.URI;
import java.net.URISyntaxException;

import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class ScriptResource.
 */
public class ScriptResource extends AuthenticatedResource {
	/** The script. */
	private XProcScript script = null;
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(ScriptResource.class.getName());

	/* (non-Javadoc)
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override
	public void doInit() {
		super.doInit();
		if (!isAuthenticated()) {
			return;
		}

		// TODO refer to scripts by their IDs instead of URIs
		// however, scripts don't have IDs yet so we have to wait
		// introduced temporary parameter "scriptid" to avoid conflict with auth param, recently renamed to "id"
		URI scriptUri = null;

		try {
			scriptUri = new URI(getQuery().getFirstValue("scriptid"));
		} catch (URISyntaxException e) {
			logger.error(e.getMessage());
			return;
		}
		ScriptRegistry scriptRegistry = ((PipelineWebService) getApplication()).getScriptRegistry();
		XProcScriptService unfilteredScript = scriptRegistry
				.getScript(scriptUri);
		if (unfilteredScript != null) {
			script = (((PipelineWebService) getApplication()).isLocal()) ? unfilteredScript
					.load() : XProcScriptFilter.INSTANCE
					.filter(unfilteredScript.load());
		}
	}

	/**
	 * Gets the resource.
	 *
	 * @return the resource
	 */
	@Get("xml")
	public Representation getResource() {
		if (!isAuthenticated()) {
			setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
			return null;
		}

		if (script == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		}
		setStatus(Status.SUCCESS_OK);
		DomRepresentation dom = new DomRepresentation(
				MediaType.APPLICATION_XML,
				XmlFormatter.xprocScriptToXml(script));
		return dom;
	}
}
