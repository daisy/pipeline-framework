package org.daisy.pipeline.webservice;

import java.util.ArrayList;
import java.util.Iterator;

import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

public class ScriptsResource extends AuthenticatedResource {
	ArrayList<XProcScript> scripts = null;
	
	@Override
	public void doInit() {
		super.doInit();
		if (!isAuthenticated()) return;
		ScriptRegistry scriptRegistry = ((PipelineWebService)this.getApplication()).getScriptRegistry();
		Iterable<XProcScriptService> unfilteredScripts = scriptRegistry.getScripts();
		Iterator<XProcScriptService> it = unfilteredScripts.iterator();
		scripts = new ArrayList<XProcScript>();
		
		while (it.hasNext()) {
			XProcScriptService unfilteredScript = it.next();
			XProcScript script = XProcScriptFilter.INSTANCE.filter(unfilteredScript.load());
			scripts.add(script);
		}
	}
	
	@Get("xml")
	public Representation getResource() {
		if (!isAuthenticated()) {
    		setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
    		return null;
    	}
    	
		setStatus(Status.SUCCESS_OK);
		DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, XmlFormatter.xprocScriptsToXml(scripts));
		return dom;
		
	}
}
