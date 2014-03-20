package org.daisy.pipeline.job;

import org.daisy.pipeline.job.priority.Prioritizable;

public interface PrioritizedJob extends Prioritizable {
               
        public Job getJob();
        
}
