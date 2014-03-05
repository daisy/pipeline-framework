package org.daisy.pipeline.job;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.job.JobManager.JobBuilder;
import org.daisy.pipeline.script.BoundXProcScript;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.base.Optional;
@RunWith(MockitoJUnitRunner.class)
public class DefaultJobManagerTest{
        @Mock JobStorage storage; 
        @Mock JobExecutionService service; 
        @Mock JobContextFactory factory;
        @Mock Client client;
        @Mock BoundXProcScript script;
        @Mock JobContext ctxt;
        @Mock Job job;
        @Mock ResourceCollection resources;


        DefaultJobManager jobManager;
        @Before
        public void setUp(){
                jobManager=Mockito.spy(new DefaultJobManager(storage,service,factory));
        }

        @Test
        public void builderOptions(){
                JobBuilder builder= Mockito.spy(jobManager.newJob(script));
                Mockito.when(factory.newJobContext(Mockito.anyBoolean(),Mockito.anyString(),Mockito.any(BoundXProcScript.class),Mockito.any(ResourceCollection.class))).thenReturn(ctxt);
                Mockito.doReturn(Optional.of(job)).when(jobManager).newJob(ctxt);
                //by default
                builder.build();
                Mockito.verify(factory,Mockito.times(1)).newJobContext(false,"",script,null);
                Mockito.verify(jobManager,Mockito.times(1)).newJob(ctxt);
                Mockito.when(factory.newJobContext(false,"",script,null)).thenReturn(ctxt);
                //mapping
                builder= Mockito.spy(jobManager.newJob(script).isMapping(true));
                builder.build();
                Mockito.verify(factory,Mockito.times(1)).newJobContext(true,"",script,null);
                //nice name
                builder= Mockito.spy(jobManager.newJob(script).withNiceName("my name"));
                builder.build();
                Mockito.verify(factory,Mockito.times(1)).newJobContext(false,"my name",script,null);
                //Resource collection
                builder= Mockito.spy(jobManager.newJob(script).withResources(this.resources));
                builder.build();
                Mockito.verify(factory,Mockito.times(1)).newJobContext(false,"",script,this.resources);
        }

}
