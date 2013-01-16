package org.daisy.pipeline.job;


import org.daisy.common.base.Provider;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JobStorageFactoryTest   {
	JobId id1;
	JobId id2;
	@Before
	public void setUp(){
		accessed=false;
		id1=JobIdFactory.newId();
		id2=JobIdFactory.newId();
	}


	@Test
	public void defaultStorageAdd(){
		JobStorage def=JobStorageFactory.getDefaultStorage();
		Job job= new Job(Mock.mockContext(id1));
		def.add(job);
		
		for(JobId id: def){
			Assert.assertEquals(id,id1);
		}

	}


	public void defaultStorageGet(){
		JobStorage def=JobStorageFactory.getDefaultStorage();
		Job job1= new Job(Mock.mockContext(id1));
		Job job2= new Job(Mock.mockContext(id2));
		def.add(job1);
		def.add(job2);

		Assert.assertNotNull(def.get(id1));
		Assert.assertEquals(def.get(id1).getContext().getId(),id1);

		Assert.assertNotNull(def.get(id2));
		Assert.assertEquals(def.get(id2).getContext().getId(),id2);

	}
	@Test
	public void defaultStorageDel(){
		JobStorage def=JobStorageFactory.getDefaultStorage();
		Job job1= new Job(Mock.mockContext(id1));
		Job job2= new Job(Mock.mockContext(id2));
		def.add(job1);
		def.add(job2);

		def.remove(id2);

		Assert.assertNotNull(def.get(id1));
		Assert.assertNull(def.get(id2));
	}


	@Test
	public void sanity(){
		Assert.assertNotNull(JobStorageFactory.getInstance());
	}

	boolean accessed;

	@Test
	public void addProvider(){
		Provider<JobStorage> p1 = new Provider<JobStorage>(){
			public JobStorage provide(){
				accessed=true;
				return JobStorageFactory.getDefaultStorage();
			}
		};
		JobStorageFactory.getInstance().add(p1);
		JobStorageFactory.getJobStorage();
		Assert.assertTrue(accessed);
	}
	@Test
	public void checkLastProvider(){
		Provider<JobStorage> p1 = new Provider<JobStorage>(){
			public JobStorage provide(){
				accessed=false;
				return JobStorageFactory.getDefaultStorage();
			}
		};
		Provider<JobStorage> p2 = new Provider<JobStorage>(){
			public JobStorage provide(){
				accessed=true;
				return JobStorageFactory.getDefaultStorage();
			}
		};
		JobStorageFactory.getInstance().add(p1);
		JobStorageFactory.getInstance().add(p2);
		JobStorageFactory.getJobStorage();
		Assert.assertTrue(accessed);
	}
	@Test
	public void remove(){
		Provider<JobStorage> p1 = new Provider<JobStorage>(){
			public JobStorage provide(){
				accessed=true;
				return JobStorageFactory.getDefaultStorage();
			}
		};

		Provider<JobStorage> p2 = new Provider<JobStorage>(){
			public JobStorage provide(){
			accessed=false;
				return JobStorageFactory.getDefaultStorage();
			}
		};
		JobStorageFactory.getInstance().add(p1);
		JobStorageFactory.getInstance().add(p2);
		Assert.assertTrue(JobStorageFactory.getInstance().remove(p2));
		JobStorageFactory.getJobStorage();
		Assert.assertTrue(accessed);
	}
}
