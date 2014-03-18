package org.daisy.pipeline.job;

import org.daisy.pipeline.job.priority.Prioritizable;

import com.google.common.base.Supplier;

public interface PrioritizedJob extends Prioritizable,Supplier<Job> {
                
        
}
