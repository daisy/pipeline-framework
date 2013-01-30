package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOutput;


public interface URITranslator   {
	public abstract XProcInput translateInputs(XProcInput input);	
	public XProcOutput translateOutput(XProcOutput output); 

}
