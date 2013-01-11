package org.daisy.pipeline.job;

import java.io.InputStream;
import java.util.LinkedList;

public abstract class Result {


	//mime-type
	String mimeType;
	//The name
	String name;
	//
	String idx;
	

	/**
	 * Constructs a new instance.
	 *
	 * @param href The href for this instance.
	 * @param mimeType The mimeType for this instance.
	 */
	public Result(String name,String idx,String mimeType) {
		this.mimeType = mimeType;
		this.name=name;
		this.idx=idx;
	}

	/**
	 * Gets the mimeType for this instance.
	 *
	 * @return The mimeType.
	 */
	public String getMimeType() {
		return this.mimeType;
	}

	/**
	 * Gets the name for this instance.
	 *
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the idx for this instance.
	 *
	 * @return The idx.
	 */
	public String getIdx() {
		return this.idx;
	}

	/**
	 * Gets the file for this instance.
	 *
	 * @return The file.
	 */
	public abstract InputStream getInputStream(); 


	/* Sometimes you need to bring up some of the 
	 * composite methods up to the compoment...
	 */
	public Iterable<? extends Result> children(){
		return new LinkedList<Result>();	
	}

	public <T> T accept(Visitor<T> visitor,Object... params) {
		return visitor.visit(this,params);
	}
	
	public interface Visitor<T>{
		public T visit(Result result, Object... params); 
	}
}
