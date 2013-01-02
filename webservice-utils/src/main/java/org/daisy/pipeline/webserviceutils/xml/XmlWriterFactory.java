package org.daisy.pipeline.webserviceutils.xml;

import java.util.List;

import org.daisy.common.properties.Property;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.webserviceutils.clients.Client;

public class XmlWriterFactory {
	
	// TODO: use Iterable<..> or List<? extends ..>, but not both
	
	public static JobXmlWriter createXmlWriterForJob(Job job) {
		return new JobXmlWriter(job);
	}
	
	public static JobsXmlWriter createXmlWriterForJobs(Iterable<? extends Job> jobs) {
		return new JobsXmlWriter(jobs);
	}
	
	public static ScriptXmlWriter createXmlWriterForScript(XProcScript script) {
		return new ScriptXmlWriter(script);
	}
	
	public static ScriptsXmlWriter createXmlWriterForScripts(Iterable<XProcScript> scripts) {
		return new ScriptsXmlWriter(scripts);
	}
	
	public static ClientXmlWriter createXmlWriterForClient(Client client) {
		return new ClientXmlWriter(client);
	}
	
	public static ClientsXmlWriter createXmlWriterForClients(List<? extends Client> clients) {
		return new ClientsXmlWriter(clients);
	}
	
	public static PropertyXmlWriter createXmlWriterForProperty(Property property) {
		return new PropertyXmlWriter(property);
	}

	public static PropertiesXmlWriter createXmlWriterForProperties(List<? extends Property> properties) {
		return new PropertiesXmlWriter(properties);
	}

	public static AliveXmlWriter createXmlWriter() {
		return new AliveXmlWriter();
	}
	
 }
