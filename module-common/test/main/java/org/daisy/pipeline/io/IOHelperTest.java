package org.daisy.pipeline.io;

import java.net.URI;

import org.junit.Assert;
import org.junit.Test;


public class IOHelperTest {
	final String base="file:/home/javi";
	final String customFold="my/folder";

	@Test
	public void testMap(){
		String uri="path/to/file.xml";
		URI res = IOHelper.map(base, uri);
		Assert.assertEquals(base+"/"+uri, res.toString());

	}
	
	@Test
	public void testFolderDefault(){
		IOHelper help =  new IOHelper();
		URI folder1 = help.getNewOutputFolder(base);
		URI folder2 = help.getNewOutputFolder(base);
		Assert.assertEquals(base+"/"+"output_1/", folder1.toString());
		Assert.assertEquals(base+"/"+"output_2/", folder2.toString());
	}
	
	@Test
	public void testFolderCustom(){
		IOHelper help =  new IOHelper();
		help.setFolderOutputPreffix(customFold);
		URI folder1 = help.getNewOutputFolder(base);
		URI folder2 = help.getNewOutputFolder(base);
		Assert.assertEquals(base+"/"+"my/folder_1/", folder1.toString());
		Assert.assertEquals(base+"/"+"my/folder_2/", folder2.toString());
	}
	
	@Test
	public void testFileDefault(){
		IOHelper help =  new IOHelper();
		URI file1 = help.getNewOutputFile(base,".xml");
		URI file2 = help.getNewOutputFile(base,".xml");
		Assert.assertEquals(base+"/"+"file_1.xml", file1.toString());
		Assert.assertEquals(base+"/"+"file_2.xml", file2.toString());
	}
	@Test
	public void testFileCustom(){
		IOHelper help =  new IOHelper();
		help.setFileOutputPreffix("myfile");
		URI file1 = help.getNewOutputFile(base,".xml");
		URI file2 = help.getNewOutputFile(base,".xml");
		Assert.assertEquals(base+"/"+"myfile_1.xml", file1.toString());
		Assert.assertEquals(base+"/"+"myfile_2.xml", file2.toString());
	}
}
