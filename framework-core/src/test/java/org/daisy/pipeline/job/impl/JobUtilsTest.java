package org.daisy.pipeline.job.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.daisy.pipeline.job.JobResult;
import org.daisy.pipeline.job.JobResultSet;
import org.daisy.pipeline.job.impl.JobUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class JobUtilsTest {
        private static final String XML_ERR = "<d:status xmlns:d='http://www.daisy.org/ns/pipeline/data' result='error'/>";
        private static final String XML_OK = "<d:status xmlns:d='http://www.daisy.org/ns/pipeline/data' result='ok'/>";
        private static final String XML_INVALID = "<d:status xmlns:d='http://www.daisy.org/ns/pipeline/data' invalid='ok'/>";

        File ok;
        File err;
        File invalid;

        @Before
        public void setUp() throws IOException {
                ok=File.createTempFile("dp2test",".xml");
                err=File.createTempFile("dp2test",".xml");
                invalid=File.createTempFile("dp2test",".xml");

                FileWriter okWriter=new FileWriter(ok);
                okWriter.write(XML_OK);
                okWriter.close();

                FileWriter errWriter=new FileWriter(err);
                errWriter.write(XML_ERR);
                errWriter.close();

                FileWriter invalidWriter=new FileWriter(invalid);
                invalidWriter.write(XML_INVALID);
                invalidWriter.close();
        }

        @After
        public void tearDown(){
                ok.delete();
                err.delete();
                invalid.delete();
        }

        @Test
        public void withoutStatusPort(){
                JobResultSet results=new JobResultSet.Builder().build();
                boolean ok=JobUtils.checkStatusPort(results);
                Assert.assertTrue("should return true when no status port is present",ok);
        }
        
        @Test
        public void statusPortOk(){
                JobResultSet results=new JobResultSet.Builder().addResult("status",new JobResult.Builder().withPath(ok.toURI()).build()).build();
                boolean ok=JobUtils.checkStatusPort(results);
                Assert.assertTrue("should return true when status document says 'ok'",ok);
        }

        @Test
        public void statusPortError(){
                JobResultSet results=new JobResultSet.Builder().addResult("status",new JobResult.Builder().withPath(err.toURI()).build()).build();
                boolean ok=JobUtils.checkStatusPort(results);
                Assert.assertFalse("should return false when status document says 'error'",ok);
        }

        @Test(expected =RuntimeException.class)
        public void invalidStatusXml(){
                JobResultSet results=new JobResultSet.Builder().addResult("status",new JobResult.Builder().withPath(invalid.toURI()).build()).build();
                JobUtils.checkStatusPort(results);
        }
        @Test
        public void multipleStatusOk(){
                
                JobResultSet results=new JobResultSet.Builder()
                        .addResult("status",new JobResult.Builder().withPath(ok.toURI()).build())
                        .addResult("status",new JobResult.Builder().withPath(ok.toURI()).build())
                        .build();
                boolean ok=JobUtils.checkStatusPort(results);
                Assert.assertTrue("should return true if all status documents say 'ok'",ok);
        }
        @Test
        public void multipleStatusErr(){
                
                JobResultSet results=new JobResultSet.Builder()
                        .addResult("status",new JobResult.Builder().withPath(ok.toURI()).build())
                        .addResult("status",new JobResult.Builder().withPath(err.toURI()).build())
                        .build();
                boolean ok=JobUtils.checkStatusPort(results);
                Assert.assertFalse("should return false if at least one status documents say 'error'",ok);
        }
}
