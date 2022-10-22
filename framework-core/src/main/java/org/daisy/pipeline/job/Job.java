package org.daisy.pipeline.job;

import org.daisy.common.priority.Priority;

/**
 * The Class Job defines the execution unit.
 */
public interface Job extends Runnable {

    public enum Status {
        IDLE,
        RUNNING,
        SUCCESS,
        ERROR,
        FAIL
    }

    /**
     * @return the job ID.
     */
    public default JobId getId() {
        return getContext().getId();
    }

    /**
     * @return the job status
     */
    public Status getStatus();

    /**
     * @return the job priority
     */
    public Priority getPriority();

    /**
     * @return The context of this job.
     */
    public JobContext getContext();

}
