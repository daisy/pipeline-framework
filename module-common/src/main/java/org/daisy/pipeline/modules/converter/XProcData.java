package org.daisy.pipeline.modules.converter;

import java.util.HashMap;

import org.daisy.pipeline.xproc.InputPort;
import org.daisy.pipeline.xproc.NamedValue;
import org.daisy.pipeline.xproc.OutputPort;
import org.daisy.pipeline.xproc.ParameterPort;

public class XProcData {
	private XProcId mId;
	private XProcContext mContext;
	private HashMap<String , InputPort> mInputPorts = new HashMap<String, InputPort>();
	private HashMap<String , OutputPort> mOutputPorts = new HashMap<String, OutputPort>();
	private HashMap<String , ParameterPort> mParameters = new HashMap<String, ParameterPort>();
	private HashMap<String , NamedValue> mOptions = new HashMap<String, NamedValue>();
	public XProcId getId(){
		return mId;
	}
	public HashMap<String, InputPort> getInputPorts() {
		return mInputPorts;
	}
	public void setInputPorts(String name,InputPort port) {
		mInputPorts.put(name, port);
	}
	public HashMap<String, OutputPort> getOutputPorts() {
		return mOutputPorts;
	}
	public void setOutputPorts(String name,OutputPort port) {
		mOutputPorts.put(name, port);
	}
	public HashMap<String, ParameterPort> getParameters() {
		return mParameters;
	}
	public void setParameters(String name,ParameterPort port) {
		mParameters.put(name, port);;
	}
	public HashMap<String, NamedValue> getOptions() {
		return mOptions;
	}
	public void setOptions(String name,NamedValue option) {
		mOptions.put(name, option);
	}
	/**
	 * @param context the context to set
	 */
	public void setContext(XProcContext context) {
		mContext = context;
	}
	/**
	 * @return the context
	 */
	public XProcContext getContext() {
		return mContext;
	}

	
	

}
