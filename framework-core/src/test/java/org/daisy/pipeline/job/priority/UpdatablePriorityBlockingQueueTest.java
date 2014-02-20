package org.daisy.pipeline.job.priority;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpdatablePriorityBlockingQueueTest{
       @Mock private PrioritizedRunnable  task1; 
       @Mock private PrioritizedRunnable  task2; 
       @Mock private PrioritizedRunnable  task3; 

       @Before
       public void setUp(){
       }

       @Test
       public void checkOrder(){
               when(task1.getPriority()).thenReturn(-5.0);
               when(task2.getPriority()).thenReturn(-10.0);
               when(task3.getPriority()).thenReturn(-1.0);
              
               UpdatablePriorityBlockingQueue queue = 
                       new UpdatablePriorityBlockingQueue(); 
               queue.offer(task1);
               queue.offer(task2);
               queue.offer(task3);

               Assert.assertEquals("First is task 2",task2,queue.poll());
               Assert.assertEquals("Second is task 1",task1,queue.poll());
               Assert.assertEquals("Third is task 3",task3,queue.poll());
       }

       @Test
       public void update(){
               when(task1.getPriority()).thenReturn(-5.0).thenReturn(-20.0);
               when(task2.getPriority()).thenReturn(-10.0).thenReturn(-10.0);
               when(task3.getPriority()).thenReturn(-1.0).thenReturn(-5.0);
              
               UpdatablePriorityBlockingQueue queue = 
                       new UpdatablePriorityBlockingQueue(); 
               queue.offer(task1);
               queue.offer(task2);
               queue.offer(task3);
               queue.update();

               Assert.assertEquals("First is task 1",task1,queue.poll());
               Assert.assertEquals("Second is task 2",task2,queue.poll());
               Assert.assertEquals("Third is task 3",task3,queue.poll());
       }
       @Test
       public void moveUp(){
               when(task1.getPriority()).thenReturn(-5.0);
               when(task2.getPriority()).thenReturn(-10.0);
               when(task3.getPriority()).thenReturn(-1.0).thenReturn(-7.5);

               when(task1.toString()).thenReturn("task1");
               when(task2.toString()).thenReturn("task 2");
               when(task3.toString()).thenReturn("task 3");
              
               UpdatablePriorityBlockingQueue queue = 
                       new UpdatablePriorityBlockingQueue(); 
               queue.offer(task1);
               queue.offer(task2);
               queue.offer(task3);

               queue.moveUp(task3);
               verify(task3).forcePriority(-7.5); 
               Assert.assertEquals("First is task 2",task2,queue.poll());
               Assert.assertEquals("Second is task 3",task3,queue.poll());
               Assert.assertEquals("Third is task 1",task1,queue.poll());
       }

       @Test
       public void moveUpFirst(){
               when(task1.getPriority()).thenReturn(-5.0);
               when(task2.getPriority()).thenReturn(-10.0);
               when(task3.getPriority()).thenReturn(-1.0);
              
               UpdatablePriorityBlockingQueue queue = 
                       new UpdatablePriorityBlockingQueue(); 
               queue.offer(task1);
               queue.offer(task2);
               queue.offer(task3);

               queue.moveUp(task2);

               Assert.assertEquals("First is task 2",task2,queue.poll());
               Assert.assertEquals("Second is task 1",task1,queue.poll());
               Assert.assertEquals("Third is task 3",task3,queue.poll());
       }

       @Test
       public void moveUpSecond(){
               when(task1.getPriority()).thenReturn(-5.0).thenReturn(-11.0);
               when(task2.getPriority()).thenReturn(-10.0);
               when(task3.getPriority()).thenReturn(-1.0);

               UpdatablePriorityBlockingQueue queue = 
                       new UpdatablePriorityBlockingQueue(); 
               queue.offer(task1);
               queue.offer(task2);
               queue.offer(task3);

               queue.moveUp(task1);
               verify(task1).forcePriority(-11.0); 
               Assert.assertEquals("First is task 1",task1,queue.poll());
               Assert.assertEquals("Second is task 2",task2,queue.poll());
               Assert.assertEquals("Third is task 3",task3,queue.poll());
       }

       @Test
       public void moveDown(){
               when(task1.getPriority()).thenReturn(-5.0);
               when(task2.getPriority()).thenReturn(-10.0).thenReturn(-3.0);
               when(task3.getPriority()).thenReturn(-1.0);

               UpdatablePriorityBlockingQueue queue = 
                       new UpdatablePriorityBlockingQueue(); 
               queue.offer(task1);
               queue.offer(task2);
               queue.offer(task3);

               queue.moveDown(task2);
               verify(task2).forcePriority(-3.0); 
               Assert.assertEquals("First is task 1",task1,queue.poll());
               Assert.assertEquals("Second is task 2",task2,queue.poll());
               Assert.assertEquals("Third is task 3",task3,queue.poll());
       }

       @Test
       public void moveSecondDown(){
               when(task1.getPriority()).thenReturn(-5.0).thenReturn(0.0);
               when(task2.getPriority()).thenReturn(-10.0);
               when(task3.getPriority()).thenReturn(-1.0);

               UpdatablePriorityBlockingQueue queue = 
                       new UpdatablePriorityBlockingQueue(); 
               queue.offer(task1);
               queue.offer(task2);
               queue.offer(task3);

               queue.moveDown(task1);
               verify(task2).forcePriority(0.0); 
               Assert.assertEquals("First is task 2",task2,queue.poll());
               Assert.assertEquals("Second is task 3",task3,queue.poll());
               Assert.assertEquals("Third is task 1",task1,queue.poll());
       }

       @Test
       public void moveLastDown(){
               when(task1.getPriority()).thenReturn(-5.0);
               when(task2.getPriority()).thenReturn(-10.0);
               when(task3.getPriority()).thenReturn(-1.0);

               UpdatablePriorityBlockingQueue queue = 
                       new UpdatablePriorityBlockingQueue(); 
               queue.offer(task1);
               queue.offer(task2);
               queue.offer(task3);

               queue.moveDown(task3);//nothing happens
               Assert.assertEquals("First is task 2",task2,queue.poll());
               Assert.assertEquals("Second is task 1",task1,queue.poll());
               Assert.assertEquals("Third is task 3",task3,queue.poll());
       }

       @Test
       public void asCollection(){
               when(task1.getPriority()).thenReturn(-5.0);
               when(task2.getPriority()).thenReturn(-10.0);
               when(task3.getPriority()).thenReturn(-1.0);
              
               UpdatablePriorityBlockingQueue queue = 
                       new UpdatablePriorityBlockingQueue(); 
               queue.offer(task1);
               queue.offer(task2);
               queue.offer(task3);
               Collection<PrioritizedRunnable> col=queue.asCollection();

               Assert.assertEquals("First is task 2",task2,col.toArray()[0]);
               Assert.assertEquals("Second is task 1",task1,col.toArray()[1]);
               Assert.assertEquals("Third is task 3",task3,col.toArray()[2]);
       }
}

