package org.daisy.pipeline.job;

import java.io.InputStream;

import java.util.LinkedList;

public class Results extends Result {
	//Composite children
	private LinkedList<Result> children;

	public Results(String name, String idx,String mimeType) {
		super(name,idx,mimeType);
		this.children=new LinkedList<Result>();
	}

	void addResult(Result result) {
		this.children.add(result);	
	}

	/* Sometimes you need to bring up some of the 
	 * composite methods up to the compoment...
	 */
	public Iterable<? extends Result> children(){
		return this.children;	
	}

	@Override
	public InputStream getInputStream() {
		return null;
	}
}
