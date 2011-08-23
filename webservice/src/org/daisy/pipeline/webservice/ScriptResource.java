package org.daisy.pipeline.webservice;

import java.net.URI;
import java.net.URISyntaxException;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.ext.xml.DomRepresentation;  
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import org.daisy.pipeline.job.XProcInfoFilter;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;

public class ScriptResource extends ServerResource {
	private XProcScript script = null;
	
	@Override
	public void doInit() {
		super.doInit();
		URI scriptUri = null;
		
		try {
			scriptUri = new URI((String) getQuery().getFirstValue("id"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		ScriptRegistry scriptRegistry = ((PipelineWebService)this.getApplication()).getScriptRegistry();
		XProcScriptService unfilteredScript = scriptRegistry.getScript(scriptUri);
		// TODO filter should accept and return XProcScript objects
		script = XProcInfoFilter.INSTANCE.filterScript(unfilteredScript.load());
	}

	@Get("xml")
	public Representation getResource() {
		if (script != null) {
			setStatus(Status.SUCCESS_OK);
			DomRepresentation dom = new DomRepresentation(MediaType.APPLICATION_XML, XmlFormatter.xprocScriptToXml(script));
			return dom;
		}
		else {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			return null;
		}
	}
}
