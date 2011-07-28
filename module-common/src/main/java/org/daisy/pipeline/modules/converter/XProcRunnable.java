package org.daisy.pipeline.modules.converter;

import java.net.URI;
import java.util.LinkedList;

import javax.xml.transform.ErrorListener;

import org.daisy.pipeline.xproc.InputPort;
import org.daisy.pipeline.xproc.NamedValue;
import org.daisy.pipeline.xproc.OutputPort;
import org.daisy.pipeline.xproc.ParameterPort;

public class XProcRunnable implements Runnable,Executable{
	private ErrorListener mErrorListener;
	private URI mResult;
	private URI mLog;
	private Executor mExecutor;
	private URI mPipelineUri;
	protected LinkedList<InputPort> mInputPorts= new LinkedList<InputPort>();
	protected LinkedList<OutputPort> mOutputPorts= new LinkedList<OutputPort>();
	protected LinkedList<ParameterPort> mParameterPorts= new LinkedList<ParameterPort>();
	protected LinkedList<NamedValue> mOptions= new LinkedList<NamedValue>();
	
	public void run(){
		if (mExecutor!=null){
			mExecutor.execute(this);
		}else{
			throw new IllegalStateException("Executor is null");
		}
	}
	
	protected void setErrorListener(ErrorListener errorListener) {
		mErrorListener = errorListener;
	}
	public ErrorListener getErrorListener() {
		return mErrorListener;
	}
	protected void setResult(URI result) {
		mResult = result;
	}
	public URI getResult() {
		return mResult;
	}
	protected void setLog(URI log) {
		mLog = log;
	}
	public URI getLog() {
		return mLog;
	}
		
	public void setExecutor(Executor executor) {
		mExecutor = executor;
	}
	public Executor getExecutor() {
		return mExecutor;
	}
	public void addOutputPort(OutputPort output){
		mOutputPorts.add(output);
	}
	public void addInputPort(InputPort input){
		mInputPorts.add(input);
	}
	public void addParameterPort(ParameterPort params){
		mParameterPorts.add(params);
	}
	public void addOption(NamedValue option){
		mOptions.add(option);
	}
	
	public Iterable<InputPort> getInputPorts(){
		return mInputPorts;
	}
	public Iterable<OutputPort> getOutputPorts(){
		return mOutputPorts;
	}
	public Iterable<ParameterPort> getParamterPorts(){
		return mParameterPorts;
	}
	public Iterable<NamedValue> getOptions(){
		return mOptions;
	}

	public void setPipelineUri(URI pipelineUri) {
		mPipelineUri = pipelineUri;
	}

	public URI getPipelineUri() {
		return mPipelineUri;
	}

}
