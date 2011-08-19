package org.daisy.pipeline.modules.converter;

public class XProcId {
	String mId;
	
	public XProcId(String id) {
		super();
		mId = id;
	}
	@Override
	public String toString(){
		return mId;
	}
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof XProcId){
			return ((XProcId)other).mId.equals(mId);
		}else{
			return false;
		}
		
	}
	@Override
	public int hashCode() {
		return mId.hashCode();
	}
	
	
}
