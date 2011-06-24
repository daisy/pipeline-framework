package org.daisy.pipeline.jobmanager;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.daisy.pipeline.jobmanager.JobStatus.Status;
import org.daisy.pipeline.modules.converter.ConverterRunnable;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobService implements JobManager{
	//TODO get this from some kind of configuration
	private static final int MAX_JOBS = 100;
	IDFactory mIdFactory=new StringJobID.StringIDFactory();;
	BlockingQueue<Job> mJobQueue = new LinkedBlockingQueue<Job>(MAX_JOBS);
	JobRunner mRunner = new JobRunner();
	HashMap<JobID, SimpleJob> mJobs= new HashMap<JobID, SimpleJob>(); 
	Logger mLogger = LoggerFactory.getLogger(this.getClass().getName());
	
	
	public void init(BundleContext ctxt){
		//TODO rely on some conf to set this
		//mIdFactory=new StringJobID.StringIDFactory();
		mRunner.start();
	}
	public void stop(BundleContext ctxt){
		mRunner.endConsuming();
		try {
			mLogger.warn("Waiting for all the jobs to finish");
			mRunner.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public Iterable<Job> getJobList() {
		return mJobQueue;
	}

	@Override
	public JobID addJob(ConverterRunnable conv) {
			SimpleJob job = new SimpleJob(mIdFactory.getNewID(conv.getConverter().getName()),conv);
			mLogger.debug("adding job:"+job.getId());
			mJobs.put(job.getId(), job);
			mJobQueue.add(job);
			mRunner.wakeUp();
			return job.getId();
	}

	@Override
	public boolean deleteJob(JobID id) {
		if(mJobs.containsKey(id)){
			mJobQueue.remove(mJobs.get(id));
			mJobs.remove(id);
			return true;
		}else{
			return false;
		}
		
		
	}

	@Override
	public Job getJob(JobID id) {
		return mJobs.get(id);
	}
	
	public JobRunner getRunner(){
		return mRunner;
	}
	//TODO not very sophisticated they queue should notify this thread when the something new comes in
	//now it's done by hand
	 class JobRunner extends Thread{
		private boolean mRun;
		
		public void run(){
			mRun=true;
			mLogger.debug("Starting the job runner");
			while(mRun){
				if (!Thread.interrupted()&&mRun) {// otherwhise check the q
	                try {
	                        synchronized (this) {
	                                this.wait();
	                        }
	                } catch (InterruptedException e) {
	                }
				}
				while(!mJobQueue.isEmpty()){
					//TODO thread pool or something more fancy
					Job toExec= mJobQueue.peek();
					toExec.getMutableStatus().setStatus(Status.PROCESSING);
					try{
						mLogger.debug("about run: "+toExec.getId());
						mJobQueue.poll().getRunnable().run();
						mLogger.debug(toExec.getId()+" job done.");
						toExec.getMutableStatus().setStatus(Status.COMPLETED);
					}catch(Exception e){
						toExec.getMutableStatus().setStatus(Status.FAILED);
						toExec.getMutableStatus().addError(new JobStatus.JobError(Error.Level.ERROR, e.getMessage()));
						mLogger.error("Error executing job:"+toExec.getId()+" - "+e.getMessage());
					}
					//checking the queue I don't care
					Thread.interrupted();
				}
				
				
			}
			
		}
		
		 /**
         * Wake up and work.
         */
        public synchronized void wakeUp() {
                this.interrupt();

        }
        /**
         * End waiting and set running to false.
         */
        public void endConsuming() {
                this.mRun = false;
                this.interrupt();
        }

		
		
		
	}
}
