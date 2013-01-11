package org.daisy.pipeline.job;

import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;

public class ResultsTest {


	Result res=null;

	@Before
	public void setUp(){
		String mime="mime";
		String idx="id";
		String name="paco";
		res =new Result(name,idx,mime){

			@Override
			public InputStream getInputStream() {
				return null;
			}

		};

	}
	/**
	 * Tests 'children'.
	 *
	 * @see org.daisy.pipeline.job.Results#children()
	 */
	@Test
	public void children() throws Exception {
	}

	/**
	 * Tests 'getInputStream'.
	 *
	 * @see org.daisy.pipeline.job.Results#getInputStream()
	 */
	@Test
	public void getInputStream() throws Exception {
	}

}
