package org.daisy.pipeline.webservice;



import org.daisy.pipeline.job.ExecutionQueue;
import org.daisy.pipeline.job.JobId;


public class QueueUpResource extends QueueMoveResource {

        @Override
        public void move(ExecutionQueue queue, JobId id) {
                queue.moveUp(id);

        }

}
