package org.daisy.pipeline.job;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Assert;
import org.junit.Test;


public class IOHelperTest {
	final String base="file:/home/javi/";
	final String customFold="my/folder";

	@Test
	public void testMap(){
		String uri="path/to/file.xml";
		URI res = IOHelper.map(base, uri);
		Assert.assertEquals(base+uri, res.toString());

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
	@Test
	public void testTreeFileList() throws URISyntaxException{
		File dir=new File(this.getClass().getClassLoader()
				.getResource("test").toURI());
		HashSet<File> files=new HashSet<File>();
		files.addAll(IOHelper.treeFileList(dir));
		Assert.assertEquals(3,files.size());
		
		Assert.assertTrue(files.contains(new File(dir.getAbsolutePath()+"/1.txt")));
		Assert.assertTrue(files.contains(new File(dir.getAbsolutePath()+"/folder/2.txt")));
		Assert.assertTrue(files.contains(new File(dir.getAbsolutePath()+"/folder/3.txt")));
		
	}
	@Test
	public void testZipOutput() throws URISyntaxException, IOException{
		File dir=new File(this.getClass().getClassLoader()
				.getResource("test").toURI());
		String mask=dir.getAbsolutePath()+File.separator;
		List<File> file=IOHelper.treeFileList(dir);
		File ziptmp = File.createTempFile("dp2", ".zip");
		URI fileOut=IOHelper.zipFromEntries(file, ziptmp, mask);
		ZipFile zipFile=new ZipFile(ziptmp);
		Assert.assertEquals(fileOut, ziptmp.toURI());
		Assert.assertEquals(3, zipFile.size());
		
		
		Enumeration entries=zipFile.entries();
		HashSet<String> set = new HashSet<String>();
		while(entries.hasMoreElements()){
			ZipEntry entry=(ZipEntry)entries.nextElement();
			
			set.add(entry.getName());
		}
		
		Assert.assertTrue(set.contains("1.txt"));
		Assert.assertTrue(set.contains("folder/2.txt"));
		Assert.assertTrue(set.contains("folder/3.txt"));
	}
}
