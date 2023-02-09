package org.daisy.pipeline.job;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.daisy.pipeline.job.impl.IOHelper;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public final class JobResultSet {

        public final static JobResultSet EMPTY = new Builder().build();

        public static class Builder{

                protected final Multimap<String,JobResult> outputPorts = LinkedListMultimap.create();

                public Builder addResult(String port, String idx, File path, String mediaType) {
                        outputPorts.put(port, new JobResult(idx, path, mediaType));
                        return this;
                }

                public JobResultSet build() {
                        return new JobResultSet(outputPorts);
                }
        }

        private final Multimap<String,JobResult> outputPorts;

        /**
         * Constructs a new instance.
         *
         * @param outputPorts The outputPorts for this instance.
         */
        public JobResultSet(Multimap<String, JobResult> outputPorts) {
                this.outputPorts = Multimaps.unmodifiableMultimap(outputPorts);
        }

        public static InputStream asZip(Collection<JobResult> results) throws IOException {
                return new InputStream() {
                        byte[] buffer = null;
                        int idx = 0;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        ZipOutputStream zipos = new ZipOutputStream(baos);
                        Iterator<JobResult> resultsIt = results.iterator();
                        boolean end = false;
                        @Override
                        public int read() throws IOException {
                                if (available() > 0)
                                        return buffer[idx++] & 0xff;
                                return -1;
                        }
                        @Override
                        public int available() throws IOException {
                                if (end)
                                        return 0;
                                if (buffer == null || buffer.length <= idx) {
                                        if (baos.size() == 0) {
                                                if (!resultsIt.hasNext()) {
                                                        close();
                                                        return 0;
                                                }
                                                JobResult result = resultsIt.next();
                                                ZipEntry entry = new ZipEntry(URI.create(result.getIdx()).getPath());
                                                zipos.putNextEntry(entry);
                                                InputStream is = result.asStream();
                                                IOHelper.dump(is, zipos);
                                                is.close();
                                                if (!resultsIt.hasNext())
                                                        zipos.finish();
                                        }
                                        buffer = baos.toByteArray();
                                        idx = 0;
                                        baos.reset();
                                }
                                return buffer.length - idx;
                        }
                        @Override
                        public void close() throws IOException {
                                if (!end) {
                                        zipos.close();
                                        baos.close();
                                        buffer = null;
                                        end = true;
                                }
                        }
                };
        }

        public InputStream asZip() throws IOException {
                return asZip(getResults());
        }

        public InputStream asZip(String port) throws IOException {
                return asZip(getResults(port));
        }

        public Collection<String> getPorts(){
                return outputPorts.keySet();
        }

        public Collection<JobResult> getResults(String port){
                return outputPorts.get(port);
        }

        public Collection<JobResult> getResults(){
                return Collections.unmodifiableList(Lists.newLinkedList(outputPorts.values()));
        }
}
