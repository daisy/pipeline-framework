package org.daisy.pipeline.webservice.restlet.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.Collection;

import org.daisy.pipeline.job.Job;
import org.daisy.pipeline.job.JobId;
import org.daisy.pipeline.job.JobIdFactory;
import org.daisy.pipeline.job.JobManager;
import org.daisy.pipeline.job.JobResult;
import org.daisy.pipeline.webservice.restlet.AuthenticatedResource;

import org.restlet.data.Digest;
import org.restlet.data.Disposition;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

/**
 * The Class ResultResource.
 */
public abstract class NamedResultResource extends AuthenticatedResource {
        /** The job. */
        private Optional<Job> job=Optional.absent();
        private String idx;
        private String name;
        private static Logger logger = LoggerFactory
                        .getLogger(NamedResultResource.class.getName());

        /*
         * (non-Javadoc)
         *
         * @see org.restlet.resource.Resource#doInit()
         */
        @Override
        public void doInit() {
                super.doInit();
                if (!isAuthenticated()) {
                        return;
                }
                JobManager jobMan = getJobManager(this.getClient());
                String idParam = (String) getRequestAttributes().get("id");
                try {
                        JobId id = JobIdFactory.newIdFromString(idParam);
                        job = jobMan.getJob(id);
                } catch (Exception e) {
                        logger.warn("Job Id malformed - Job not found: " + idParam);
                }
                if (getRequestAttributes().get("name")!=null){
                        name = NamedResultResource.decode((String) getRequestAttributes().get("name"));
                }
                if ( getRequestAttributes().get("idx")!=null){
                        idx = NamedResultResource.decode((String)getRequestAttributes().get("idx"));
                }
        }

        /**
         * Gets the resource.
         *
         * @return the resource
         */
        @Get
        public Representation getResource() {
                logRequest();
                maybeEnableCORS();
                if (!isAuthenticated()) {
                        setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                        return null;
                }

                if (!job.isPresent()) {
                        setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                        return this.getErrorRepresentation("Job not found");
                }

                if (!(job.get().getStatus().equals(Job.Status.SUCCESS) || job.get().getStatus().equals(Job.Status.FAIL))) {
                        setStatus(Status.CLIENT_ERROR_NOT_FOUND);
                        return this.getErrorRepresentation("Job status different to SUCCESS or FAIL");
                }
                if (!(name!=null&&!name.isEmpty())) {
                        setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        return this.getErrorRepresentation("No name provided");
                }
                if (idx!=null&&!idx.isEmpty()){
                        return this.singleResult();

                }else{
                        return this.zippedResult();
                }
        }

        private Representation singleResult(){
                Collection<JobResult> results=this.gatherResults(this.job.get(),this.name);
                logger.debug(String.format("Getting single result for %s idx: %s",this.name,this.idx));
                results=Collections2.filter(results, new Predicate<JobResult>(){
                        @Override
                        public boolean apply(JobResult res) {
                                return res.getIdx().toString().equals(NamedResultResource.this.idx);
                        }
                });
                if(results.size()==0){
                        setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
                        return this.getErrorRepresentation("Result not found");
                }

                try{
                        JobResult res=Lists.newArrayList(results).get(0);
                        InputStream is = res.asStream();
                        is = new BufferedInputStream(is, 8192);
                        Integer size = ResultResource.getSize(is, 32768);
                        Representation rep = new InputRepresentation(
                                        is,
                                        MediaType.APPLICATION_ALL);//TODO get media type from the file
                        if (size != null) { // if > 32 Mb
                                byte[] bytes = new byte[size];
                                is.mark(size);
                                is.read(bytes);
                                rep.setDigest(new Digest(MessageDigest.getInstance("MD5").digest(bytes)));
                                is.reset();
                        }
                        Disposition disposition = new Disposition();
                        disposition.setFilename(res.getIdx().toString());
                        disposition.setType(Disposition.TYPE_ATTACHMENT);
                        if (size != null) // if > 32 Mb
                                disposition.setSize(size);
                        rep.setDisposition(disposition);
                        return rep;
                }catch(Exception e){
                                setStatus(Status.SERVER_ERROR_INTERNAL);
                                return this.getErrorRepresentation(e);
                }
                        
        }

        private Representation zippedResult(){
                Collection<JobResult> results=this.gatherResults(this.job.get(),this.name);
                results=Collections2.transform(results,new Function<JobResult,JobResult>(){
                        public JobResult apply(JobResult result){
                                return result.strip();
                        }
                });
                logger.debug(String.format("Getting result for %s", this.name));
                if (results.size() == 0) {
                        setStatus(Status.SERVER_ERROR_INTERNAL);
                        return this.getErrorRepresentation("No results available");
                }
                try{
                        return ResultResource.getZippedRepresentation(results,this.job.get());
                }catch(Exception e){
                                setStatus(Status.SERVER_ERROR_INTERNAL);
                                return this.getErrorRepresentation(e);
                }

        }

        protected abstract Collection<JobResult> gatherResults(Job job,String name);

        private static String decode(String urlPart){
                try {
                        return  new URI(urlPart).getPath();
                } catch (URISyntaxException e) {
                        throw new RuntimeException("Error decoding url",e);
                }
        }
        
}
