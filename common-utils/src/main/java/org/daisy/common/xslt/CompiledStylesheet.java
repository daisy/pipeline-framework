package org.daisy.common.xslt;

import javax.xml.transform.URIResolver;

import net.sf.saxon.s9api.XsltExecutable;

/**
 * Allocate distinct ThreadUnsafeXslTransformer instances so that the same
 * transformation can be applied within multiple threads by invoking
 * newTransformer() in each thread.
 */
public class CompiledStylesheet {

	public CompiledStylesheet(XsltExecutable exec) {
		mSheet = exec;
	}

	public void setURIResolver(URIResolver uriResolver) {
		mURIResolver = uriResolver;
	}

	public ThreadUnsafeXslTransformer newTransformer() {
		ThreadUnsafeXslTransformer res = new ThreadUnsafeXslTransformer(mSheet.load());
		if (mURIResolver != null)
			res.setURIResolver(mURIResolver);
		return res;
	}

	private XsltExecutable mSheet;
	private URIResolver mURIResolver;
}
