package org.daisy.pipeline.xproc;

import java.util.Map;

public class NamedValue implements Map.Entry<String,String>{
	String mName;
	String mValue;

	public NamedValue(String name, String value) {
		super();
		mName = name;
		mValue = value;
	}

	public String getValue() {
		return mValue;
	}

	public String setValue(String value) {
		String oldVal= mValue;
		mValue = value;
		return oldVal;
	}

	public String getName() {
		return mName;
	}

	@Override
	public String getKey() {
		return mName;
	}
	
}
