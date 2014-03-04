
package org.daisy.pipeline.persistence.jobs;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.Job.JobBuilder;
import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.persistence.jobs.PersistentJob.PersistentJobBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class IdFilterTest   {
        Job job; 
        Job job2; 
        Database db;
        @Before
        public void setUp(){
		db=DatabaseProvider.getDatabase();

		System.setProperty("org.daisy.pipeline.iobase",System.getProperty("java.io.tmpdir"));
		PersistentJobContext.setScriptRegistry(new Mocks.DummyScriptService(Mocks.buildScript()));
		JobBuilder builder= new PersistentJobBuilder(db).withContext(Mocks.buildContext());
		job =(PersistentJob) builder.build();//new PersistentJob(Job.newJob(Mocks.buildContext()),db);
		builder= new PersistentJobBuilder(db).withContext(Mocks.buildContext());
		job2 =(PersistentJob) builder.build();//new PersistentJob(Job.newJob(Mocks.buildContext()),db);

        }
	@After
	public void tearDown(){
		db.deleteObject(job);
		db.deleteObject(job.getContext().getClient());
		db.deleteObject(job2);
		db.deleteObject(job2.getContext().getClient());
        }


        @Test
        public void getSelect(){
                QueryDecorator<PersistentJob> dec=new IdFilter(db.getEntityManager(),job2.getId());
                CriteriaQuery<PersistentJob> cq=dec.getSelect(PersistentJob.class); 
                Query q=db.getEntityManager().createQuery(cq);
                Job fromDb=(Job)q.getSingleResult();
                Assert.assertEquals("Finds the appropriate job",fromDb.getId(),job2.getId());
        }

        @Test
        public void getSelectIsUnique(){
                QueryDecorator<PersistentJob> dec=new IdFilter(db.getEntityManager(),job2.getId());
                CriteriaQuery<PersistentJob> cq=dec.getSelect(PersistentJob.class); 
                Query q=db.getEntityManager().createQuery(cq);
                Assert.assertEquals("Only one result",1,q.getResultList().size());
        }
        
}
