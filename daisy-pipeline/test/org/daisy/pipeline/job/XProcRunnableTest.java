package org.daisy.pipeline.job;

import org.daisy.pipeline.modules.converter.Executor;
import org.daisy.pipeline.modules.converter.XProcRunnable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class XProcRunnableTest {

	@Before
	public void setUp() throws Exception {
	}
	@Test
	public void testExecutorFail(){
		XProcRunnable runnable= new XProcRunnable();
		try{
			runnable.run();
			Assert.fail("executor is null a IllegalStateException should've been thrown");
		}catch (IllegalStateException e) {
			
		}
	} 
	
	@Test
	public void testExecutorDummy(){
		XProcRunnable runnable= new XProcRunnable();
		runnable.setExecutor(new Executor() {
			
			@Override
			public void execute(XProcRunnable runnable) {
				//execute! 
			}
		});
		runnable.run();
	} 

}
