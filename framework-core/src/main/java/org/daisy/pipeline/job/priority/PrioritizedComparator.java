package org.daisy.pipeline.job.priority;

import java.util.Comparator;


public class PrioritizedComparator implements
                Comparator<PrioritizedRunnable> {

       
        @Override
        public int compare(PrioritizedRunnable arg0, PrioritizedRunnable arg1) {
                return Double.compare(arg0.getPriority(),arg1.getPriority());
        }
        
}
