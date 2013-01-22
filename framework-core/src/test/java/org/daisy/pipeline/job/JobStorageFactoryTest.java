package org.daisy.pipeline.job;



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
		for(Job j:  def){
			Assert.assertEquals(j.getContext().getId(),id1);
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
		JobStorageProvider p1 = new JobStorageProvider(){
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
		JobStorageProvider p1 = new JobStorageProvider(){
			public JobStorage provide(){
				accessed=false;
				return JobStorageFactory.getDefaultStorage();
			}
		};
		JobStorageProvider p2 = new JobStorageProvider(){
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
		JobStorageProvider p1 = new JobStorageProvider(){
			public JobStorage provide(){
				accessed=true;
				return JobStorageFactory.getDefaultStorage();
			}
		};

		JobStorageProvider p2 = new JobStorageProvider(){
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
