package org.daisy.pipeline.xproc;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Port<T> {
	String mName;
	boolean mSequectial;
	public boolean isSequectial() {
		return mSequectial;
	}

	LinkedList<T> mBinds;
	
	public Port(String name){
		this.mName=name;
		
		mBinds=new LinkedList<T>();
	}
	
	public String getName() {
		return mName;
	}
	
	public Port<T> addBind(T bind){
		mBinds.add(bind);
		return this;
	}
	
	public Queue<T> getBinds(){
		return new LinkedList<T>(mBinds);
	}
}
