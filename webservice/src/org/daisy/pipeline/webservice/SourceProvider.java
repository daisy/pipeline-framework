package org.daisy.pipeline.webservice;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.daisy.common.base.Provider;

public class SourceProvider implements Provider<Source> {

	String xml = null;
	public SourceProvider(String xml) {
		this.xml = xml;
	}
	@Override
	public Source provide() {
		Source src = new StreamSource(new java.io.StringReader(xml));
		return src;
	}

}
