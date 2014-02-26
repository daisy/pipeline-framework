package org.daisy.pipeline.job.priority.timetracking;

import org.daisy.pipeline.job.priority.PrioritizedRunnable;
import org.daisy.pipeline.job.priority.UpdatablePriorityBlockingQueue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TimeTrackerTest {
        @Mock PrioritizedRunnable r1;
        @Mock PrioritizedRunnable r2;
        @Mock PrioritizedRunnable r3;
       
        @Test
        public void maximum(){
                TimeTracker tracker = Mockito.spy(new TimeTracker(3,new UpdatablePriorityBlockingQueue(),TimeFunctions.newLinearTimeFunctionFactory())); 
                Mockito.doNothing().when(tracker).update();
                tracker.executing(r1);
                tracker.executing(r2);
                tracker.executing(r3);
                Mockito.verify(tracker,Mockito.times(1)).update();
        }

        @Test
        public void noUpdate(){
                TimeTracker tracker = Mockito.spy(new TimeTracker(3,new UpdatablePriorityBlockingQueue(),TimeFunctions.newLinearTimeFunctionFactory())); 
                Mockito.doNothing().when(tracker).update();
                tracker.executing(r1);
                tracker.executing(r2);
                Mockito.verify(tracker,Mockito.times(0)).update();
        }
}
