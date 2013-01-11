package org.daisy.pipeline.job;

import java.io.InputStream;
//For ports without nested elements
class SinglePortResult extends SingleResult {

	public SinglePortResult(String name, String idx,String mimeType, InputStream is) {
		super(name, idx,mimeType, is);
	}
	
}
