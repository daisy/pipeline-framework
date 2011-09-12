package org.daisy.common.xproc.calabash;

import java.util.LinkedList;
import java.util.List;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.common.messaging.MessageAccessor;

import com.xmlcalabash.core.XProcMessageListener;
import com.xmlcalabash.core.XProcRunnable;

public class XProcMessageListenerAggregator implements XProcMessageListener{
	List<XProcMessageListener> mListeners = new LinkedList<XProcMessageListener>();
	MessageAccessor mAsAccessor;
	public void add(XProcMessageListener listener){
		mListeners.add(listener);
	}
	public void addAsAccessor(MessageListenerWrapper listener){
		mListeners.add(listener);
		mAsAccessor=listener.getAccessor();
	}
	public MessageAccessor getAccessor(){
		return mAsAccessor;
	}
	
	public void remove(XProcMessageListener listener){
		mListeners.remove(listener);
	}
	@Override
	public void error(Throwable throwable) {
		for(XProcMessageListener l:mListeners){
			l.error(throwable);
		}
		
	}
	@Override
	public void error(XProcRunnable runnable, XdmNode xnode, String str, QName qname) {
		for(XProcMessageListener l:mListeners){
			l.error(runnable,xnode,str,qname);
		}
		
	}
	@Override
	public void fine(XProcRunnable arg0, XdmNode arg1, String arg2) {
		for(XProcMessageListener l:mListeners){
			l.fine(arg0,arg1,arg2);
		}
		
	}
	@Override
	public void finer(XProcRunnable arg0, XdmNode arg1, String arg2) {
		for(XProcMessageListener l:mListeners){
			l.finer(arg0,arg1,arg2);
		}
	}
	@Override
	public void finest(XProcRunnable arg0, XdmNode arg1, String arg2) {
		for(XProcMessageListener l:mListeners){
			l.finest(arg0,arg1,arg2);
		}
		
	}
	@Override
	public void info(XProcRunnable arg0, XdmNode arg1, String arg2) {
		for(XProcMessageListener l:mListeners){
			l.info(arg0,arg1,arg2);
		}
	}
	@Override
	public void warning(XProcRunnable arg0, XdmNode arg1, String arg2) {
		for(XProcMessageListener l:mListeners){
			l.warning(arg0,arg1,arg2);
		}
		
	}
	@Override
	public void warning(Throwable throwable) {
		for(XProcMessageListener l:mListeners){
			l.error(throwable);
		}
		
	}
	
}
