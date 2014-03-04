package org.daisy.pipeline.persistence.jobs;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.Job.JobBuilder;
import org.daisy.pipeline.persistence.Database;
import org.daisy.pipeline.persistence.jobs.PersistentJob.PersistentJobBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class QueryDecoratorTest {
        static class QueryDecoratorImpl extends QueryDecorator {

                public QueryDecoratorImpl(CriteriaBuilder cb) {
                        super(cb);
                        // TODO Auto-generated constructor stub
                }


                @Override
                <T> Predicate doDecorateWhere(Predicate pred, Root<T> root,
                                CriteriaQuery<T> query) {
                        return pred;
                }
        }
        @Mock
        CriteriaBuilder cb;

        QueryDecorator dec1;
        QueryDecorator dec2;
        @Mock Root<PersistentJob> root;
        @Mock CriteriaQuery<PersistentJob> cq;
        @Mock Predicate pred;
        Job job; 
        Database db;
        @Before
        public void setUp(){
                dec1=Mockito.spy(new QueryDecoratorImpl(cb));
                dec2=Mockito.spy(new QueryDecoratorImpl(cb));
		db=DatabaseProvider.getDatabase();

		System.setProperty("org.daisy.pipeline.iobase",System.getProperty("java.io.tmpdir"));
		PersistentJobContext.setScriptRegistry(new Mocks.DummyScriptService(Mocks.buildScript()));
		JobBuilder builder= new PersistentJobBuilder(db).withContext(Mocks.buildContext());
		job =(PersistentJob) builder.build();//new PersistentJob(Job.newJob(Mocks.buildContext()),db);

        }
	@After
	public void tearDown(){
		db.deleteObject(job);
		db.deleteObject(job.getContext().getClient());
        }


        @Test
        @SuppressWarnings({"unchecked"})
        public void decorate(){
                //Mockito.doCallRealMethod().when(dec1).decorateWhere(pred,root,cq);
                //Mockito.doCallRealMethod().when(dec2).decorateWhere(pred,root,cq);
                //Mockito.doCallRealMethod().when(dec1).setNext(Mockito.any(QueryDecorator.class));
                Mockito.when(cb.and((Predicate)Mockito.anyVararg())).thenReturn(pred);
                dec1.setNext(dec2);
                dec1.decorateWhere(pred,root,cq);
                Mockito.verify(dec1,Mockito.times(1)).doDecorateWhere(pred,root,cq);
                Mockito.verify(dec2,Mockito.times(1)).doDecorateWhere(pred,root,cq);
                
        }

        @Test
        @SuppressWarnings({"unchecked"})
        public void getSelect(){
                QueryDecorator dec=Mockito.spy(new QueryDecoratorImpl(db.getEntityManager().getCriteriaBuilder()));
                CriteriaQuery<PersistentJob> cq=dec.getSelect(PersistentJob.class); 
                Mockito.verify(dec,Mockito.times(1)).doDecorateWhere(Mockito.any(Predicate.class),Mockito.any(Root.class),Mockito.any(CriteriaQuery.class));
                Query q=db.getEntityManager().createQuery(cq);
                Assert.assertEquals("Finds the job",1,q.getResultList().size());
        }
        
}
