package org.daisy.pipeline.job;

import org.daisy.common.xproc.XProcInput;

import org.daisy.pipeline.script.XProcScript;

public interface URITranslator   {
	public abstract XProcInput translateInputs(XProcInput input);	

}
