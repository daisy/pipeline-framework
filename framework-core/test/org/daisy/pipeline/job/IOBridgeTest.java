package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipFile;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.pipeline.script.XProcOptionMetadata;
import org.daisy.pipeline.script.XProcScript;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IOBridgeTest {

	File tmpDir;
	XProcScript script;
	private IOBridge bridge;

	@After
	public void tearDown() {
		tmpDir.delete();
	}

	@Before
	public void setUp() throws IOException {
		tmpDir = File.createTempFile("dp2", "");
		String name = tmpDir.getAbsolutePath();
		tmpDir.delete();
		tmpDir = new File(name);
		tmpDir.mkdir();
		bridge = new IOBridge(tmpDir);
		XProcPortInfo portInf = XProcPortInfo.newInputPort("source", false,
				true);
		XProcOptionInfo optionInf = XProcOptionInfo.newOption(new QName(
				"myinput"), false, "");
		XProcOptionInfo optionInf2 = XProcOptionInfo.newOption(new QName(
				"myopt"), false, "");
		XProcOptionInfo optionInf3 = XProcOptionInfo.newOption(
				new QName("out1"), false, "");
		XProcOptionInfo optionInf4 = XProcOptionInfo.newOption(
				new QName("out2"), false, "");
		XProcOptionInfo optionInf5 = XProcOptionInfo.newOption(
				new QName("out3"), false, "cosa");
		XProcPipelineInfo info = new XProcPipelineInfo.Builder()
				.withPort(portInf).withURI(URI.create(""))
				.withOption(optionInf).withOption(optionInf2).withOption(optionInf3).
				withOption(optionInf4).withOption(optionInf5).build();
		XProcOptionMetadata meta1 = new XProcOptionMetadata.Builder()
				.withDirection("input").withType("anyFileURI").build();
		XProcOptionMetadata meta2 = new XProcOptionMetadata.Builder()
				.withDirection("output").withType("anyFileURI").build();
		XProcOptionMetadata meta3 = new XProcOptionMetadata.Builder()
				.withDirection("output").withType("anyFileURI").build();
		XProcOptionMetadata meta4 = new XProcOptionMetadata.Builder()
				.withDirection("output").withType("anyDirURI").build();
		XProcOptionMetadata meta5 = new XProcOptionMetadata.Builder()
				.withDirection("output").withType("anyDirURI").build();
		HashMap<QName, XProcOptionMetadata> ometas = new HashMap<QName, XProcOptionMetadata>();
		ometas.put(new QName("myinput"), meta1);
		ometas.put(new QName("myopt"), meta2);
		ometas.put(new QName("out1"), meta3);
		ometas.put(new QName("out2"), meta4);
		ometas.put(new QName("out3"), meta5);
		script = new XProcScript(info, null, null, null, ometas);

	}

	@Test
	public void testStoreContext() throws Exception {
		ZipFile mFile = new ZipFile(new File(this.getClass().getClassLoader()
				.getResource("test.zip").toURI()));
		ResourceCollection ctxt = new ZipResourceContext(mFile);
		bridge.storeResources(ctxt);
		Assert.assertTrue(new File(this.tmpDir + "/"
				+ IOConstants.IO_DATA_SUBDIR, "1.txt").exists());
		Assert.assertTrue(this.tmpDir.toString(), new File(this.tmpDir + "/"
				+ IOConstants.IO_DATA_SUBDIR, "/folder/3.txt").exists());
	}

	@Test
	public void testResolveOptionsInput() throws IOException {
		XProcInput input = new XProcInput.Builder()
				.withOption(new QName("myinput"), "dir/myfile.xml")
				.withOption(new QName("myopt"), "dir/outfile.xml").build();
		XProcInput.Builder builder = new XProcInput.Builder();
		bridge.resolveOptions(script, input, builder);
		XProcInput newInput = builder.build();
		String res1 = newInput.getOptions().get(new QName("myinput"));
		String res2 = newInput.getOptions().get(new QName("myopt"));
		Assert.assertEquals(tmpDir.toURI().toString()
				+ IOConstants.IO_DATA_SUBDIR + "/dir/myfile.xml", res1);
		Assert.assertEquals(tmpDir.toURI().toString()
				+ IOConstants.IO_OUTPUT_SUBDIR + "/dir/outfile.xml", res2);
	}

	@Test
	public void testResolveOptionsOutputs() throws IOException {
		XProcInput input = new XProcInput.Builder()
				.withOption(new QName("myinput"), "dir/myfile.xml")
				.withOption(new QName("out1"), "")
				.withOption(new QName("out2"), "")
				.withOption(new QName("out3"), "").build();
		XProcInput.Builder builder = new XProcInput.Builder();
		bridge.resolveOptions(script, input, builder);
		XProcInput newInput = builder.build();
		String res1 = newInput.getOptions().get(new QName("out1"));
		String res2 = newInput.getOptions().get(new QName("out2"));
		String res3 = newInput.getOptions().get(new QName("out3"));
		Assert.assertEquals(tmpDir.toURI().toString()
				+ IOConstants.IO_OUTPUT_SUBDIR + "/out1.xml", res1);
		Assert.assertEquals(tmpDir.toURI().toString()
				+ IOConstants.IO_OUTPUT_SUBDIR + "/out2/", res2);
		Assert.assertEquals(tmpDir.toURI().toString()
				+ IOConstants.IO_OUTPUT_SUBDIR + "/cosa", res3);
	}

	@Test
	public void testCheckInputsInline() throws IOException {
		final SAXSource source = new SAXSource();
		Provider<Source> prov = new Provider<Source>() {
			@Override
			public Source provide() {
				return source;
			}
		};
		final SAXSource source2 = new SAXSource();
		Provider<Source> prov2 = new Provider<Source>() {
			@Override
			public Source provide() {
				return source2;
			}
		};
		XProcInput input = new XProcInput.Builder().withInput("source", prov)
				.withInput("source", prov2).build();
		XProcInput.Builder builder = new XProcInput.Builder();
		bridge.resolveInputPorts(script, input, builder);
		XProcInput newInput = builder.build();
		Iterator<Provider<Source>> iter = newInput.getInputs("source")
				.iterator();
		Source res1 = iter.next().provide();
		Source res2 = iter.next().provide();
		Assert.assertEquals(tmpDir.toURI().toString()
				+ IOConstants.IO_DATA_SUBDIR + "/source-0.xml",
				res1.getSystemId());
		Assert.assertEquals(tmpDir.toURI().toString()
				+ IOConstants.IO_DATA_SUBDIR + "/source-1.xml",
				res2.getSystemId());
	}

	@Test
	public void testCheckInputsRelative() throws IOException {
		final SAXSource source = new SAXSource();
		source.setSystemId("dir/myfile.xml");
		Provider<Source> prov = new Provider<Source>() {
			@Override
			public Source provide() {
				return source;
			}
		};
		XProcInput input = new XProcInput.Builder().withInput("source", prov)
				.build();
		XProcInput.Builder builder = new XProcInput.Builder();
		bridge.resolveInputPorts(script, input, builder);
		XProcInput newInput = builder.build();
		Iterator<Provider<Source>> iter = newInput.getInputs("source")
				.iterator();
		Source res1 = iter.next().provide();
		Assert.assertEquals(tmpDir.toURI().toString()
				+ IOConstants.IO_DATA_SUBDIR + "/dir/myfile.xml",
				res1.getSystemId());
	}

	@Test
	public void testCheckInputsAbsolute() throws IOException {
		final SAXSource source = new SAXSource();
		String uri = "http://mypublicfile.xml";
		source.setSystemId(uri);
		Provider<Source> prov = new Provider<Source>() {
			@Override
			public Source provide() {
				return source;
			}
		};
		XProcInput input = new XProcInput.Builder().withInput("source", prov)
				.build();
		XProcInput.Builder builder = new XProcInput.Builder();
		bridge.resolveInputPorts(script, input, builder);
		XProcInput newInput = builder.build();
		Iterator<Provider<Source>> iter = newInput.getInputs("source")
				.iterator();
		Source res1 = iter.next().provide();
		Assert.assertEquals(uri, res1.getSystemId());
	}
}
