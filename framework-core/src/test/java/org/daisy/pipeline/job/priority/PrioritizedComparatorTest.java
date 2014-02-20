package org.daisy.pipeline.job.priority;

import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PrioritizedComparatorTest   {
       @Mock private PrioritizedRunnable task1; 
       @Mock private PrioritizedRunnable task2; 


       @Before
       public void setUp(){
       }
       @Test
       public void testCompare(){
               when(task1.getPriority()).thenReturn(0.0);
               when(task2.getPriority()).thenReturn(0.1);
               PrioritizedComparator comparator= new PrioritizedComparator();
               Assert.assertTrue("Comparator sanity 1",comparator.compare(task1,task2)<0);
               Assert.assertTrue("Comparator sanity 2",comparator.compare(task2,task1)>0);

       }
                
}
