package org.daisy.pipeline.job;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.job.fuzzy.InferenceEngine;
import org.daisy.pipeline.job.priority.Priority;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Monitor;

@RunWith(MockitoJUnitRunner.class)
public class DefaultJobExecutionServiceTest {

        @Rule
        public TestRule benchmarkRun = new BenchmarkRule();
        static int execCount=0;
        static char[] animation=new char[]{'|','/','-','\\'};
        Job[] jobs = new Job[100];
        DefaultJobExecutionService service;
        RunnableTracker tracker;
        Monitor mon;
        Monitor.Guard guard;
        @Mock Runnable runnable;
        @Mock InferenceEngine engine;
        
        private static char getChar(){
                execCount++;
                return animation[execCount%4];

        }

        @BeforeClass
        static public void warning(){
                System.out.println("Checking thread safety, this may take a while...");
        }
        @Before
        public void setUp() {
                mon = new Monitor();
                tracker = new RunnableTracker();
                service = Mockito.spy(new DefaultJobExecutionService());
                jobs = new Job[100];
                for (int i=0;i<100;i++){
                        JobContext ctxt = Mockito.mock(JobContext.class);
                        Client client = Mockito.mock(Client.class);
                        Job job = Mockito.mock(Job.class);
                        JobId id = Mockito.mock(JobId.class);

                        Mockito.when(client.getPriority()).thenReturn(Priority.LOW);
                        Mockito.when(ctxt.getClient()).thenReturn(client);
                        Mockito.when(job.getContext()).thenReturn(ctxt);
                        Mockito.when(job.getPriority()).thenReturn(Priority.HIGH);
                        Mockito.when(job.getId()).thenReturn(id);
                        Mockito.when(id.toString()).thenReturn(
                                        String.format("%d",i));
                        //Mockito.when(id.equals(Mockito.any(JobId.class))).thenAnswer(new Answer<Boolean>() {
                                //public Boolean answer(InvocationOnMock invocation) {
                                        //Object[] args = invocation.getArguments();
                                        //return args[0].toString().equals(invocation.getMock().toString());
                                //}});
                        jobs[i] = job;
                        Mockito.doReturn(tracker.getRunnable(jobs[i])).when(service)
                                .getRunnable(jobs[i]);
                }

        }

        @Test
        public void simpleJobSubmission() {
                service.submit(jobs[0]);
                int executed = waitForSize(1, 100);
                Assert.assertEquals("One task wasn't executed", 1, executed);

        }
        
        @Test
        @BenchmarkOptions(benchmarkRounds = 50, warmupRounds = 0)
        public void submitALot() {
                System.out.print(getChar());
                for (int i = 0; i < 100; i++) {
                        service.submit(jobs[i]);
                }
                int executed = waitForSize(100, 2000);
                System.out.print("\b");
                Assert.assertEquals("One hundred tasks weren't executed", 100, executed);

        }

        @Test
        @BenchmarkOptions(benchmarkRounds = 50, warmupRounds = 0)
        public void submitALotAsynch() {
                System.out.print(getChar());
                for (int i = 0; i < 100; i++) {
                        final int j = i;
                        new Thread() {
                                @Override
                                public void run() {
                                        service.submit(jobs[j]);
                                }
                        }.start();
                }
                int executed = waitForSize(100, 2000);
                System.out.print("\b");
                Assert.assertEquals("One hundred async tasks weren't executed", 100, executed);

        }


        public int waitForSize(final int size, long micro) {
                 guard = new Monitor.Guard(mon) {

                        @Override
                        public boolean isSatisfied() {
                                return tracker.visited().size() == size;

                        }

                };
                boolean done=false;
                try {
                        done=mon.enterWhen(guard,2,TimeUnit.SECONDS);
                } catch (InterruptedException e) {

                        throw new RuntimeException(e);
                }finally{
                        mon.leave();
                        if(!done){
                                throw new RuntimeException("Waited for too long");
                        }
                }

                return tracker.visited().size();
        }

        class RunnableTracker {

                List<Job> visited = Lists.newLinkedList();
                List<Integer> ids= Lists.newLinkedList();

                public Runnable getRunnable(final Job job) {
                        return new Runnable(){
                                @Override
                                public void run(){

                                        mon.enter();
                                        synchronized(RunnableTracker.this.visited){
                                                visited.add(job);
                                        }
                                        ids.add(Integer.valueOf(job.getId().toString()));
                                        mon.leave();
                                }
                        };
                }

                public List<Job> visited(){
                        synchronized(this.visited){
                                return ImmutableList.copyOf(this.visited);
                        }
                }
        }

        //@Test
        //@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0)
        //public void findBingo() {

                //LinkedList <PrioritizableRunnable> collection= Lists.newLinkedList();
                //for (Job j: this.jobs){
                        //collection.add(new FuzzyJobRunnable(j,runnable,engine));
                //}
                
                //Optional<PrioritizableRunnable> res=DefaultJobExecutionService.find(this.jobs[0].getId(),collection);
                //Assert.assertTrue("We found a job",res.isPresent());
                //Assert.assertEquals("And the ids are the same",this.jobs[0].getId().toString(),((PrioritizedJob)res.get()).get().getId().toString());

        //}

        //@Test
        //@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0)
        //public void findFail() {

                //LinkedList <PrioritizableRunnable> collection= Lists.newLinkedList();
                //for (Job j: this.jobs){
                        //collection.add(new FuzzyJobRunnable(j,runnable,engine));
                //}
                //JobId id = Mockito.mock(JobId.class);
                //Mockito.when(id.toString()).thenReturn(
                                        //String.format("%d",0));
                 
                //Optional<PrioritizableRunnable> res=DefaultJobExecutionService.find(id,collection);
                //Assert.assertFalse("We did not found the job",res.isPresent());

        //}

        //@Test
        //@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0)
        //public void moveUp() {
                //LinkedList <PrioritizableRunnable> collection= Lists.newLinkedList();
                //for (Job j: this.jobs){
                        //collection.add(new FuzzyJobRunnable(j,runnable,engine));
                //}
                //PriorityThreadPoolExecutor executor=Mockito.mock(PriorityThreadPoolExecutor.class);
                //Mockito.when(service.getExecutor()).thenReturn(executor);
                //Mockito.when(executor.asCollection()).thenReturn(collection);

                //this.service.moveUp(this.jobs[0].getId());
                //Mockito.verify(executor,Mockito.times(1)).moveUp(collection.get(0));
                //this.service.moveUp(Mockito.mock(JobId.class));
                //Mockito.verify(executor,Mockito.times(1)).moveUp(Mockito.any(PrioritizableRunnable.class));
        //}

        //@Test
        //@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0)
        //public void moveDown() {
                //LinkedList <PrioritizableRunnable> collection= Lists.newLinkedList();
                //for (Job j: this.jobs){
                        //collection.add(new FuzzyJobRunnable(j,runnable,engine));
                //}
                //PriorityThreadPoolExecutor executor=Mockito.mock(PriorityThreadPoolExecutor.class);
                //Mockito.when(service.getExecutor()).thenReturn(executor);
                //Mockito.when(executor.asCollection()).thenReturn(collection);

                //this.service.moveDown(this.jobs[0].getId());
                //Mockito.verify(executor,Mockito.times(1)).moveDown(collection.get(0));
                //this.service.moveDown(Mockito.mock(JobId.class));
                //Mockito.verify(executor,Mockito.times(1)).moveDown(Mockito.any(PrioritizableRunnable.class));
        //}

        //@Test
        //@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0)
        //public void cancel() {
                //LinkedList <PrioritizableRunnable> collection= Lists.newLinkedList();
                //for (Job j: this.jobs){
                        //collection.add(new FuzzyJobRunnable(j,runnable,engine));
                //}
                //PriorityThreadPoolExecutor executor=Mockito.mock(PriorityThreadPoolExecutor.class);
                //Mockito.when(service.getExecutor()).thenReturn(executor);
                //Mockito.when(executor.asCollection()).thenReturn(collection);

                //this.service.cancel(this.jobs[0].getId());
                //Mockito.verify(executor,Mockito.times(1)).remove(collection.get(0));
                //this.service.cancel(Mockito.mock(JobId.class));
                //Mockito.verify(executor,Mockito.times(1)).remove(Mockito.any(PrioritizableRunnable.class));
        //}

}
