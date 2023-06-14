package org.daisy.pipeline.persistence.impl.job;

import java.io.File;
import java.io.IOException;

import javax.persistence.TypedQuery;

import org.apache.commons.io.FileUtils;

import org.daisy.pipeline.job.AbstractJob;
import org.daisy.pipeline.persistence.impl.Database;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.io.Files;

@RunWith(MockitoJUnitRunner.class)
public class IdFilterTest   {
        AbstractJob job;
        AbstractJob job2;
        File tempDir;
        File tempDir2;
        Database db;
        @Before
        public void setUp(){
		db=DatabaseProvider.getDatabase();

		tempDir = Files.createTempDir();
		tempDir2 = Files.createTempDir();
		job = new PersistentJob(db, Mocks.buildJob(tempDir), null);
		job2 = new PersistentJob(db, Mocks.buildJob(tempDir2), null);

        }
	@After
	public void tearDown(){
		try {
			db.deleteObject(job);
			db.deleteObject(job.getContext().getClient());
			db.deleteObject(job2);
			db.deleteObject(job2.getContext().getClient());
		} finally {
			if (tempDir != null)
				try {
					FileUtils.deleteDirectory(tempDir);
				} catch (IOException e) {
				}
			if (tempDir2 != null)
				try {
					FileUtils.deleteDirectory(tempDir2);
				} catch (IOException e) {
				}
		}
	}


        @Test
        public void getSelect(){
                QueryDecorator<PersistentJob> dec=new IdFilter(db.getEntityManager(),job2.getId());
                TypedQuery<PersistentJob> q=dec.getQuery(PersistentJob.class); 
                AbstractJob fromDb = q.getSingleResult();
                Assert.assertEquals("Finds the appropriate job",fromDb.getId(),job2.getId());
        }

        @Test
        public void getSelectIsUnique(){
                QueryDecorator<PersistentJob> dec=new IdFilter(db.getEntityManager(),job2.getId());
                TypedQuery<PersistentJob> q=dec.getQuery(PersistentJob.class); 
                Assert.assertEquals("Only one result",1,q.getResultList().size());
        }
        
}
