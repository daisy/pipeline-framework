package org.daisy.pipeline.test.ui.commandline;

import junit.framework.Assert;

import org.daisy.pipeline.ui.commandline.Command;
import org.daisy.pipeline.ui.commandline.CommandLine;
import org.daisy.pipeline.ui.commandline.CommandUsage;
import org.junit.Test;

public class CommandLineTest {

	@Test
	public void testParseHelp() {
		CommandLine cmd = new CommandLine(null);
		//like the ol'times
		Command com = cmd.parse("-h");
		Assert.assertEquals(CommandUsage.class, com.getClass());
		
	}
	
	@Test
	public void testUnkOption() {
		CommandLine cmd = new CommandLine(null);
		//like the ol'times
		Command com = cmd.parse("-u");
		Assert.assertEquals(CommandUsage.class, com.getClass());
		
	}
	
	@Test
	public void testNoOption() {
		CommandLine cmd = new CommandLine(null);
		//like the ol'times
		Command com = cmd.parse("");
		Assert.assertEquals(CommandUsage.class, com.getClass());
		
	}

}
