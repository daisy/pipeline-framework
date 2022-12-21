package org.daisy.pipeline.job;

import java.net.URI;

import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.script.XProcScript;

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
    public JobId getId();

    public String getNiceName();

    public XProcScript getScript();

    /**
     * @return the job status
     */
    public Status getStatus();

    public JobMonitor getMonitor();

    public URI getLogFile();

    public JobResultSet getResults();

    public JobBatchId getBatchId();

    public Client getClient();

}
