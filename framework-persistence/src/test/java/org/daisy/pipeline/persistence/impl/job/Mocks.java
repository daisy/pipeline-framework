package org.daisy.pipeline.persistence.impl.job;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;

import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.priority.Priority;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.clients.Client.Role;
import org.daisy.pipeline.job.AbstractJob;
import org.daisy.pipeline.job.AbstractJobContext;
import org.daisy.pipeline.job.JobBatchId;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobMonitor;
import org.daisy.pipeline.job.JobResult;
import org.daisy.pipeline.job.JobResultSet;
import org.daisy.pipeline.job.StatusNotifier;
import org.daisy.pipeline.job.URIMapper;
import org.daisy.pipeline.persistence.impl.webservice.PersistentClient;
import org.daisy.pipeline.script.Script;
import org.daisy.pipeline.script.ScriptInput;
import org.daisy.pipeline.script.ScriptRegistry;
import org.daisy.pipeline.script.ScriptService;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcPortMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.daisy.pipeline.script.XProcScriptService;

public class Mocks   {

	public static final String scriptUri= "http://daisy.com";
	public static final String scriptId= "foo-to-bar";
	public static final String testLogFile="http://daisy.com/log.txt";
	public static final String file1="file:/tmp/f1.xml";
	public static final String file2="file:/tmp/f2.xml";
	public static final String opt1Name = "opt1";
	public static final String opt2Name = "opt2";
	public static final String value1 = "value1";
	public static final File result1;
	public static final File result2;
	public static final URI in=URI.create("file:/tmp/in/");
	public static final URI out=URI.create("file:/tmp/out/");
	public static final String portResult="res"; 

	static {
		try {
			result1 = File.createTempFile("res", null);
			result2 = File.createTempFile("res", null);
			result1.deleteOnExit();
			result2.deleteOnExit();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static class DummyScriptService extends ScriptRegistry {

		private XProcScript script;

		/**
		 * Constructs a new instance.
		 *
		 * @param script The script for this instance.
		 */
		public DummyScriptService(XProcScript script) {
			this.script = script;
		}

		@Override
		public XProcScriptService getScript(String name) {
			return new XProcScriptService() {
				@Override
				public XProcScript load() {
					return script;
				}
			};
		}

		@Override
		public Iterable<ScriptService<?>> getScripts() {
			return null;
		}
	}


	static class SimpleSourceProvider implements Source {
		String sysId;

		/**
		 * Constructs a new instance.
		 *
		 * @param sysId The sysId for this instance.
		 */
		public SimpleSourceProvider(String sysId) {
			this.sysId = sysId;
		}

		@Override
		public String getSystemId() {
			return this.sysId;
		}

		@Override
		public void setSystemId(String systemId) {
			
		}
	}

	public static XProcScript buildScript(){
		XProcScript.Builder builder = new XProcScript.Builder(Mocks.scriptId, "", URI.create(Mocks.scriptUri));
		builder = builder.withInputPort(XProcPortInfo.newInputPort("source", true, false, true),
		                                new XProcPortMetadata("", "", ""));
		builder = builder.withOutputPort(XProcPortInfo.newOutputPort(portResult, true, true),
		                                 new XProcPortMetadata("", "", ""));
		builder = builder.withOutputPort(XProcPortInfo.newOutputPort(opt1Name, true, false),
		                                 new XProcPortMetadata("", "", ""));
		builder = builder.withOption(XProcOptionInfo.newOption(new QName(opt2Name), false, ""),
		                             new XProcOptionMetadata(null, null, null, null));
		return builder.build();
	}

	public static AbstractJob buildJob() {
		return buildJob(Priority.MEDIUM);
	}

	public static AbstractJob buildJob(Priority priority) {
		return new AbstractJob(buildContext(), priority, null, true) {};
	}

	public static AbstractJob buildJob(Client client) {
		return new AbstractJob(buildContext(client), Priority.MEDIUM, null, true) {};
	}

	public static AbstractJob buildJob(Client client, JobBatchId batchId) {
		return new AbstractJob(buildContext(client, batchId), Priority.MEDIUM, null, true) {};
	}

	public static AbstractJobContext buildContext(){  
                return buildContext(null,null);
	}

	public static AbstractJobContext buildContext(Client client){  
                return buildContext(client,null);
        }
	public static AbstractJobContext buildContext(Client client,JobBatchId batchId){  
		final Script script = Mocks.buildScript();
		//Input setup
		final ScriptInput input= new ScriptInput.Builder().withInput("source", new Mocks.SimpleSourceProvider(file1))
		                                                  .withInput("source", new Mocks.SimpleSourceProvider(file2))
		                                                  .withOption(opt2Name, value1)
		                                                  .build();
		final JobId id = JobIdFactory.newId();
		final URIMapper mapper= new URIMapper(in,out);
		final JobResultSet rSet=new JobResultSet.Builder().addResult(portResult, result1.getName(), result1, null)
		                                                  .addResult(opt1Name, result2.getName(), result2, null)
		                                                  .build();
                //add to the db
                if ( client ==null){
                        client=new PersistentClient("Client_"+Math.random(),"b",Role.ADMIN,"a@a",Priority.LOW);
                        DatabaseProvider.getDatabase().addObject(client);
                }
		//inception!
		return new MyHiddenContext(rSet,script,input,mapper,client,id,batchId);
	}

	static class MyHiddenContext extends AbstractJobContext{
			public MyHiddenContext(JobResultSet set, Script script, ScriptInput input, URIMapper mapper, Client client, JobId id, JobBatchId batchId){
				super();
				this.client = client;
				this.id = id;
				this.logFile = URI.create("/tmp/job.log");
				this.batchId = batchId;
				this.niceName = "hidden";
				this.script = script;
				this.input = input;
				this.uriMapper = mapper;
				this.results = set;
				this.monitor = new JobMonitor() {
						@Override
						public MessageAccessor getMessageAccessor() {
							return null;
						}
						@Override
						public StatusNotifier getStatusUpdates() {
							return null;
						}
					};
			}
		};

}
