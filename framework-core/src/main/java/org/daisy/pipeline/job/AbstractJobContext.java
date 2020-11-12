package org.daisy.pipeline.job;

import java.net.URI;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.common.xproc.XProcResult;
import org.daisy.pipeline.clients.Client;
import org.daisy.pipeline.job.impl.JobUtils;
import org.daisy.pipeline.job.impl.JobResultSetBuilder;
import org.daisy.pipeline.script.XProcScript;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class defines the common behaviour to jobs contexts, the context will mainly differ depending on the mode of 
 * the WS, local or remote. 
 * The subclasses of JobContext MUST define some fine grained behaviour regarding how the job interacts with the fs and 
 * input,output,option redirections.
 */
public abstract class AbstractJobContext implements JobContext{

        private static final Logger logger = LoggerFactory.getLogger(AbstractJobContext.class);

        protected XProcInput input;
        private XProcOutput output;
        protected XProcScript script;
        protected JobId id;
        protected JobBatchId batchId;
        protected JobMonitor monitor;
        protected URI logFile;
        protected URIMapper resultMapper;
        protected JobResultSet results;
        protected String niceName;
        protected Client client;

        // used by JobContextFactory
        AbstractJobContext(Client client, JobId id, JobBatchId batchId, String niceName,
                           XProcScript script, XProcInput input, XProcOutput output,
                           URIMapper resultMapper, JobMonitor monitor) {
                if (client == null ||
                    id == null ||
                    niceName == null ||
                    script == null ||
                    input == null ||
                    output == null ||
                    resultMapper == null ||
                    monitor == null)
                        throw new IllegalArgumentException();
                this.client = client;
                this.id = id;
                this.batchId = batchId;
                this.niceName = niceName;
                this.logFile = JobURIUtils.getLogFile(id.toString()).toURI();
                this.results = new JobResultSet.Builder().build();
                this.script = script;
                this.input = input;
                this.output = output;
                this.resultMapper = resultMapper;
                this.monitor = monitor;
        }

        // used by PersistentJobContext
        protected AbstractJobContext() {
        }

        // used by PersistentJobContext and VolatileContext
        protected AbstractJobContext(AbstractJobContext from) {
                if (from == null)
                        throw new IllegalArgumentException();
                this.client = from.client;
                this.id = from.id;
                this.batchId = from.batchId;
                this.niceName = from.niceName;
                this.logFile = from.logFile;
                this.results = from.results;
                this.script = from.script;
                this.input = from.input;
                this.output = from.output;
                this.resultMapper = from.resultMapper;
                this.monitor = from.monitor;
        }

        @Override
        public URI getLogFile() {
                return logFile;
        }

        @Override
        public JobMonitor getMonitor() {
                return monitor;
        }

        @Override
        public XProcScript getScript() {
                return script;
        }

        @Override
        public JobId getId() {
                return id;
        }

        @Override
        public JobResultSet getResults() {
                return results;
        }

        /**
         * @return the status: true if the job succeeded, false if the job failed
         */
        protected boolean collectResults(XProcResult result) {
                if (output == null)
                        // This means we've tried to execute a PersistentJob (which does not persist
                        // output) that was read from the database. This should not happen because
                        // upon creation jobs are immediately submitted to
                        // DefaultJobExecutionService, which keeps them in memory, and old idle jobs
                        // (created but not executed before a shutdown) are not added to the
                        // execution queue upon launching Pipeline.
                        throw new UnsupportedOperationException();
                result.writeTo(output);
                this.results = JobResultSetBuilder.newResultSet(script, input, output, resultMapper);
                return JobUtils.checkStatusPort(script, output);
        }

        public void cleanUp() {
                logger.info(String.format("Deleting context for job %s", this.id));
                JobURIUtils.cleanJobBase(this.id.toString());
        }

        @Override
        public String getName() {
                return niceName;
        }

        @Override
        public Client getClient() {
                return client;
        }

        @Override
        public JobBatchId getBatchId() {
                return batchId;
        }
}
