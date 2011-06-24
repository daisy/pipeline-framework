package org.daisy.pipeline.jobmanager;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.daisy.pipeline.jobmanager.JobService.JobRunner;
import org.daisy.pipeline.modules.converter.Converter;
import org.daisy.pipeline.modules.converter.ConverterFactory;
import org.daisy.pipeline.modules.converter.ConverterRunnable;
import org.junit.Before;
import org.junit.Test;


public class JobServiceTest {
	private JobService ser;
	private ConverterRunnableMock conv;
	private ConverterRunnableMock convErr;


	@Before
	public void setUp(){
		ser = new JobService();
		conv = new ConverterRunnableMock(false);
		convErr = new ConverterRunnableMock(true);


	}
	@Test 
	public void runnerStop(){
		
		JobRunner runner = ser.getRunner();
		runner.start();
		runner.endConsuming();
		try {
			runner.join(500);
		} catch (InterruptedException e) {
			fail("runner did not stop...");
		}
		
	}
	
	@Test 
	public void postingJob(){
		ser.getRunner().start();
		ser.addJob(conv);
		ser.getRunner().endConsuming();
		try {
			ser.getRunner().join();
		} catch (InterruptedException e) {
			fail("runner did not stop...");
		}
		assertTrue(conv.isExecuted());
	}
	@Test
	public void testJobQuery(){
		
		JobID id = ser.addJob(conv);
		Job job = ser.getJob(id);
		assertEquals(conv, job.getRunnable());
	}
	@Test
	public void testJobDeletion(){
	

		JobID id = ser.addJob(conv);
		assertTrue(ser.deleteJob(id));
		assertNull(ser.getJob(id));
		//assertEquals(conv, job.getRunnable());
	}
	@Test
	public void testJobList(){
		ConverterRunnableMock conv2 = new ConverterRunnableMock(false);
		ser.addJob(conv);
		ser.addJob(conv2);
		int counter=0;
		for(Job job:ser.getJobList()){
			counter++;
		}
		assertEquals(counter, 2);
	}
	@Test 
	public void jobStatusNotStarted(){
		JobID id = ser.addJob(conv);
		assertEquals(JobStatus.Status.NOT_STARTED,ser.getJob(id).getStatus().getStatus());
	}
	
	@Test 
	public void jobStatusCompleted(){
		ser.getRunner().start();
		JobID id = ser.addJob(conv);
		ser.getRunner().endConsuming();
		try {
			ser.getRunner().join(1000);
		} catch (InterruptedException e) {
			fail("runner did not stop...");
		}
		assertEquals(ser.getJob(id).getStatus().getStatus(), JobStatus.Status.COMPLETED);
	}
	
	@Test 
	public void jobStatusFail(){
		ser.getRunner().start();
		JobID id = ser.addJob(new ConverterRunnableMock(true));
		ser.getRunner().endConsuming();
		try {
			ser.getRunner().join(1000);
		} catch (InterruptedException e) {
			fail("runner did not stop...");
		}
		assertEquals(ser.getJob(id).getStatus().getStatus(), JobStatus.Status.FAILED);
	}
	
	@Test 
	public void postingJobStatus(){
		ser.getRunner().start();
		JobID id = ser.addJob(conv);
		ser.getRunner().endConsuming();
		try {
			ser.getRunner().join(1000);
		} catch (InterruptedException e) {
			fail("runner did not stop...");
		}
		assertTrue(conv.isExecuted());
	}

	
	
	
	
}

class ConverterRunnableMock extends ConverterRunnable{
	
	boolean executed=false;
	private boolean mFail;
	
	public boolean isExecuted() {
		return executed;
	}

	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	@Override
	public void run() {
		executed=true;
		if(mFail)
			throw new RuntimeException("error");
	}

	protected ConverterRunnableMock( boolean fail) {
		
		super(new Converter(){

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "Test";
			}

			@Override
			public String getVersion() {
				// TODO Auto-generated method stub
				return "1";
			}

			@Override
			public String getDescription() {
				// TODO Auto-generated method stub
				return "";
			}

			@Override
			public ConverterArgument getArgument(String name) {
				// TODO Auto-generated method stub
				return new ConverterArgument() {
				};
			}

			@Override
			public Iterable<ConverterArgument> getArguments() {
				// TODO Auto-generated method stub
				return new LinkedList<Converter.ConverterArgument>();
			}

			@Override
			public ConverterFactory getFactory() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ConverterRunnable getRunnable() {
				// TODO Auto-generated method stub
				return null;
			}
			
		});

		mFail=fail;
	}
	
}
