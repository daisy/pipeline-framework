package org.daisy.pipeline.job.fuzzy;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobContext;
import org.daisy.pipeline.job.fuzzy.InferenceEngine;
import org.daisy.pipeline.job.priority.Priority;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

//@RunWith(MockitoJUnitRunner.class)
//public class FuzzyJobRunnableTest   {
        //@Mock InferenceEngine engine;
        //@Mock Job job;
        //@Mock JobContext ctxt;
        //@Mock Client client;
        //@Mock Runnable runnable;
        //FuzzyJobRunnable fuzzyJob;

        //@Before
        //public void setUp(){
                
        //}
        
       //@Test
       //public void forcePriority(){
               //fuzzyJob= new FuzzyJobRunnable(job,runnable,engine); 
               //fuzzyJob.forcePriority(100.0);
               //Assert.assertEquals("Forzing priority",100.0,fuzzyJob.getPriority(),0.0);

       //}

       //@Test
       //public void getPriority(){
               //fuzzyJob= Mockito.spy(new FuzzyJobRunnable(job,runnable,engine)); 
               //Mockito.doReturn(10.0).when(fuzzyJob).getScore();
               //Assert.assertEquals("Getting priority, has to be negative",-10.0,fuzzyJob.getPriority(),0.0);

       //}

       //@Test
       //public void getPriorities(){ fuzzyJob= Mockito.spy(new FuzzyJobRunnable(job,runnable,engine)); 
               //Mockito.when(client.getPriority()).thenReturn(Priority.LOW);
               //Mockito.when(ctxt.getClient()).thenReturn(client);
               //Mockito.when(job.getContext()).thenReturn(ctxt);
               //Mockito.when(job.getPriority()).thenReturn(Priority.HIGH);
               //double []res=fuzzyJob.getPriorities();

               //Assert.assertArrayEquals("Check first client prio and then job prio",new double[]{0.0,1.0},res,0.0);


       //}

//}
