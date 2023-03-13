package org.daisy.pipeline.job;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.transform.Result;

import org.apache.commons.io.FileUtils;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;
import org.daisy.pipeline.job.JobResult;
import org.daisy.pipeline.job.JobResultSet;
import org.daisy.pipeline.job.URIMapper;
import org.daisy.pipeline.job.impl.Mock;
import org.daisy.pipeline.job.impl.XProcDecorator;
import org.daisy.pipeline.script.XProcScript;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.Lists;

public class JobResultSetBuilderTest {

        URIMapper mapper;       
        XProcScript script;
        JobResultSet.Builder builder ;
        XProcOutput output;
        XProcInput input;
        File file1;
        File file2;
        String dir="option/";
        String oldIoBase="";

        @Before
        public void setUp() throws IOException{
                file1 = File.createTempFile("res1", null);
                file1.delete();
                file2 = File.createTempFile("res2", null);
                file2.delete();
                script= new Mock.ScriptGenerator.Builder().withOutputPort("sequence", "xml", true, false)
                                                          .withOutputPorts(2).withOptionOutputsFile(1).withOptionOutputsDir(1).build().generate();
                URI tmp=new File(System.getProperty("java.io.tmpdir")).toURI();
                oldIoBase=System.getProperty("org.daisy.pipeline.data");
                System.setProperty("org.daisy.pipeline.data", new File(tmp).toString());
                mapper = new URIMapper(tmp.resolve("inputs/"),tmp.resolve("outputs/"));
                String outName = Mock.ScriptGenerator.getOutputName(0);
                XProcOutput outs = new XProcOutput.Builder().withOutput(outName, Mock.getResultProvider(file1.toURI().toString())).build();
                XProcDecorator trans=XProcDecorator.from(script,mapper);
                output=trans.decorate(outs);
                QName optDir=Mock.ScriptGenerator.getOptionOutputDirName(0);
                QName optName=Mock.ScriptGenerator.getOptionOutputFileName(0);
                input = new XProcInput.Builder().withOption(optDir, dir).withOption(optName, file2.toURI().toString()).build();
                input=trans.decorate(input);
                writeResult(file2);
                Mock.populateDir((String)input.getOptions().get(optDir));
        }

        private static File writeResult(Result result) throws IOException {
                return writeResult(new File(URI.create(result.getSystemId())));
        }

        private static File writeResult(File result) throws IOException {
                if (result.exists())
                        throw new IOException("file already exists: " + result);
                result.getParentFile().mkdirs();
                result.createNewFile();
                char[] data = new char[1024];
                FileWriter fw = new FileWriter(result);
                fw.write(data);
                fw.close();
                return result;
        }

        @After
        public void tearDown() {
                if (file2 != null && file2.exists())
                        file2.delete();
                QName optDir=Mock.ScriptGenerator.getOptionOutputDirName(0);
                try {
                        FileUtils.deleteDirectory(new File((String)input.getOptions().get(optDir)));
                } catch (IOException e) {
                        throw new RuntimeException(e);
                }
                if(oldIoBase!=null)
                        System.setProperty("org.daisy.pipeline.data", oldIoBase);
        }

        @Test 
        public void outputPort() throws Exception{
                String outName = Mock.ScriptGenerator.getOutputName(0);
                Supplier<Result> res=output.getResultProvider(outName);
                File f = null;
                try {
                        f = writeResult(res.get());
                        JobResultSet rSet = AbstractJob.buildResultSet(script, input, output, mapper);
                        List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(outName));
                        Assert.assertEquals(mapper.mapOutput(file1.toURI()), jobs.get(0).getPath().toURI());
                        Assert.assertEquals(file1.toURI().toString(), jobs.get(0).getIdx().toString());
                } finally {
                        if (f != null) f.delete();
                }
        }

        @Test 
        public void outputPortNullCheck() throws Exception{
                String outName = Mock.ScriptGenerator.getOutputName(0);
                XProcOutput output = new XProcOutput.Builder().build();
                JobResultSet rSet = AbstractJob.buildResultSet(script, input, output, mapper);
                List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(outName));
                Assert.assertEquals(jobs.size(),0);
        }

        @Test 
        public void outputPortSequence() throws Exception{
                String outName = "sequence";
                Supplier<Result> res=output.getResultProvider(outName);
                File f1 = null;
                File f2 = null;
                try {
                        f1 = writeResult(res.get());
                        f2 = writeResult(res.get());
                        JobResultSet rSet = AbstractJob.buildResultSet(script, input, output, mapper);
                        List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(outName));
                        Assert.assertEquals(jobs.size(),2);
                        Assert.assertEquals(mapper.mapOutput(URI.create("sequence/sequence.xml")), jobs.get(0).getPath().toURI());
                        Assert.assertEquals("sequence/sequence.xml", jobs.get(0).getIdx().toString());
                        Assert.assertEquals("xml",jobs.get(0).getMediaType());
                } finally {
                        if (f1 != null) f1.delete();
                        if (f2 != null) f2.delete();
                }
        }

        @Test(expected=IllegalArgumentException.class)
        public void nonDynamicProviderResults() throws Exception{
                String outName = Mock.ScriptGenerator.getOutputName(0);
                // undecorated output
                XProcOutput output = new XProcOutput.Builder().withOutput(outName, Mock.getResultProvider(file1.toURI().toString())).build();
                AbstractJob.buildResultSet(script, input, output, mapper);
        }

        @Test
        public void optionsOutputFile() throws Exception{
                QName optName=Mock.ScriptGenerator.getOptionOutputFileName(0);
                JobResultSet rSet = AbstractJob.buildResultSet(script, input, output, mapper);
                List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(optName));
                Assert.assertEquals(mapper.mapOutput(file2.toURI()), jobs.get(0).getPath().toURI());
                Assert.assertEquals(file2.toURI().toString(), jobs.get(0).getIdx().toString());
        }

        @Test
        public void optionsOutputDirSize() throws Exception{
                QName optName=Mock.ScriptGenerator.getOptionOutputDirName(0);
                JobResultSet rSet = AbstractJob.buildResultSet(script, input, output, mapper);
                List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(optName));
                Assert.assertEquals(3,jobs.size());
        }

        @Test
        public void optionsOutputURIs() throws Exception{
                QName optName=Mock.ScriptGenerator.getOptionOutputDirName(0);
                JobResultSet rSet = AbstractJob.buildResultSet(script, input, output, mapper);
                List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(optName));
                HashSet<URI> uris= new HashSet<URI>();
                uris.add(mapper.mapOutput(URI.create(dir+"dos.xml")));
                uris.add(mapper.mapOutput(URI.create(dir+"uno.xml")));
                uris.add(mapper.mapOutput(URI.create(dir+"tres.xml")));
                Assert.assertTrue(uris.contains(jobs.get(0).getPath().toURI()));
                Assert.assertTrue(uris.contains(jobs.get(1).getPath().toURI()));
                Assert.assertTrue(uris.contains(jobs.get(2).getPath().toURI()));
        }

        @Test
        public void optionsOutputIdx() throws Exception{
                QName optName=Mock.ScriptGenerator.getOptionOutputDirName(0);
                JobResultSet rSet = AbstractJob.buildResultSet(script, input, output, mapper);
                List<JobResult> jobs=Lists.newLinkedList(rSet.getResults(optName));
                HashSet<String> uris= new HashSet<String>();
                uris.add(dir+"dos.xml");
                uris.add(dir+"uno.xml");
                uris.add(dir+"tres.xml");
                Assert.assertTrue(uris.contains(jobs.get(0).getIdx().toString()));
                Assert.assertTrue(uris.contains(jobs.get(1).getIdx().toString()));
                Assert.assertTrue(uris.contains(jobs.get(2).getIdx().toString()));
        }

        @Test
        public void buildResultSet() throws Exception{
                String outName = Mock.ScriptGenerator.getOutputName(0);
                Supplier<Result> res=output.getResultProvider(outName);
                File f = null;
                try {
                        f = writeResult(res.get());
                        JobResultSet rSet = AbstractJob.buildResultSet(script, input, output, mapper);
                         Assert.assertEquals(5,rSet.getResults().size());
                } finally {
                        if (f != null) f.delete();
                }
        }
}
