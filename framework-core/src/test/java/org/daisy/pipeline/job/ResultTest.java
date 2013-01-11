package org.daisy.pipeline.job;

import java.io.File;
import java.io.InputStream;

import java.net.URI;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ResultTest {
	
	/**
	 * Constructs a new instance.
	 */
	public ResultTest() {
	}
	Result res=null;
	String mime="mime";
	String idx="id";
	String name="paco";
	@Before
	public void setUp(){
		res =new Result(name,idx,mime){

			@Override
			public InputStream getInputStream() {
				return null;
			}

		};
	}


	@Test
	public void naiveTests() throws Exception {

		Assert.assertEquals(name,res.getName());
		Assert.assertEquals(mime,res.getMimeType());
		Assert.assertEquals(idx,res.getIdx());
	}

	/**
	 * Tests 'children'.
	 *
	 * @see org.daisy.pipeline.job.Result#children()
	 */
	@Test
	public void children() throws Exception {
		//no children
		int i=0;
		for( @SuppressWarnings("unused") Result r:res.children()){
			i++;
		}
		Assert.assertEquals(0,i);

	}
		
}
