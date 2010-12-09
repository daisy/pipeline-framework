package org.daisy.pipeline.test.ui.commandline;

import java.util.Properties;

import joptsimple.OptionParser;
import junit.framework.Assert;

import org.daisy.pipeline.ui.commandline.CommandUsage;
import org.junit.Test;


public class CommandUsageTest {

	
		
	
		@Test
		public void simpleTest(){
			Properties prop = new Properties();
			prop.put(CommandUsage.PARSER,new OptionParser());
			CommandUsage cmd = new CommandUsage(prop);
			cmd.execute();
			//ok nothing happened, this is cool
		}
		
		@Test
		public void noParser(){
			CommandUsage cmd = new CommandUsage(new Properties());
			try{
				cmd.execute();
				Assert.fail("IllegalArgumentException should have been thrown");
			}catch(IllegalArgumentException iae){
				
			}

		}
		
		@Test
		public void invalidParser(){
			
			Properties prop = new Properties();
			prop.put(CommandUsage.PARSER,"muahaha");
			CommandUsage cmd = new CommandUsage(prop);
			try{
				cmd.execute();
				Assert.fail("IllegalArgumentException should have been thrown");
			}catch(IllegalArgumentException iae){
				
			}

		}
		
		@Test
		public void messageTest(){
			
			Properties prop = new Properties();
			prop.put(CommandUsage.PARSER,new OptionParser());
			prop.put(CommandUsage.ERR,"ooopps");
			CommandUsage cmd = new CommandUsage(prop);
			cmd.execute();
			Assert.assertEquals("ooopps", cmd.getError());
			

		}
		
		@Test
		public void messageCastTest(){
			
			Properties prop = new Properties();
			prop.put(CommandUsage.PARSER,new OptionParser());
			prop.put(CommandUsage.ERR,new Exception());
			CommandUsage cmd = new CommandUsage(prop);
			try{
				cmd.execute();
				Assert.fail("IllegalArgumentException should have been thrown");
			}catch(IllegalArgumentException iae){
				
			}
			

		}
}
