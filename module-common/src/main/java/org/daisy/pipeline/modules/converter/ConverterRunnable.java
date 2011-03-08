package org.daisy.pipeline.modules.converter;

public abstract class ConverterRunnable implements Runnable{
	
	ConverterExecutor mExecutor;
	
	
	
	public void run(){
		mExecutor.execute(this);
	}
	
	public interface ConverterExecutor{
		public void execute(ConverterRunnable runnable);
	}
	
}
