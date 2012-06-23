package org.daisy.pipeline.webservice;

import java.util.ArrayList;
import java.util.Iterator;

import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
import org.daisy.pipeline.webserviceutils.XmlFormatter;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

// TODO: Auto-generated Javadoc
/**
 * The Class ScriptsResource.
 */
public class ScriptsResource extends AuthenticatedResource {
	/** The scripts. */
	ArrayList<XProcScript> scripts = null;

	/* (non-Javadoc)
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override
	public void doInit() {
		super.doInit();
		if (!isAuthenticated()) {
			return;
		}
		ScriptRegistry scriptRegistry = webservice().getScriptRegistry();
		Iterable<XProcScriptService> unfilteredScripts = scriptRegistry.getScripts();
		Iterator<XProcScriptService> it = unfilteredScripts.iterator();
		scripts = new ArrayList<XProcScript>();

		while (it.hasNext()) {
			XProcScriptService unfilteredScript = it.next();
			XProcScript script = (webservice().isLocal()) ? unfilteredScript
					.load() : XProcScriptFilter.INSTANCE
					.filter(unfilteredScript.load());
					scripts.add(script);
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

		this.setStatus(Status.SUCCESS_OK);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML,
				XmlFormatter.xprocScriptsToXml(scripts));
		return dom;

	}
}
