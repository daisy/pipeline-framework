package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;


public interface XProcTranslator   {
	public XProcInput translateInput(XProcInput input);	
	public XProcOutput translateOutput(XProcOutput output); 
}
