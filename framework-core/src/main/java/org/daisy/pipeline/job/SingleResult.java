package org.daisy.pipeline.job;

import java.io.InputStream;

public class SingleResult extends Result {
	InputStream is;
	public SingleResult(String name,String idx, String mimeType,InputStream is) {
		super(name, idx,mimeType);
	}

	@Override
	public InputStream getInputStream(){
		return is;
	}

	
}
