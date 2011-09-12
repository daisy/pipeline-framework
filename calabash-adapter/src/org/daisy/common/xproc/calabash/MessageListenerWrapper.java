package org.daisy.common.xproc.calabash;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.messaging.MessageListener;

import com.xmlcalabash.core.XProcMessageListener;
import com.xmlcalabash.core.XProcRunnable;

public class MessageListenerWrapper implements XProcMessageListener{
	MessageListener mListener;
	public MessageListenerWrapper(MessageListener messageListener){
		mListener=messageListener;
	} 
	public MessageAccessor getAccessor(){
		return mListener.getAccessor();
	}
	@Override
	public void error(Throwable arg0) {
		mListener.error(XprocMessageHelper.errorMessage(arg0), arg0);
		
	}

	@Override
	public void error(XProcRunnable step, XdmNode node, String message, QName qName) {
		 mListener.error(XprocMessageHelper.message(step, node, message));
		
	}

	@Override
	public void fine(XProcRunnable step, XdmNode node, String message) {
		mListener.debug(XprocMessageHelper.message(step, node, message));
		
	}

	@Override
	public void finer(XProcRunnable step, XdmNode node, String message) {
		mListener.debug(XprocMessageHelper.message(step, node, message));
		
	}

	@Override
	public void finest(XProcRunnable step, XdmNode node, String message) {
		mListener.trace(XprocMessageHelper.message(step, node, message));
		
	}

	@Override
	public void info(XProcRunnable step, XdmNode node, String message) {
		mListener.info(XprocMessageHelper.message(step, node, message));
		
	}

	@Override
	public void warning(XProcRunnable step, XdmNode node, String message) {
		 mListener.warn(XprocMessageHelper.message(step, node, message));
		
	}

	@Override
	public void warning(Throwable arg0) {
		mListener.error(XprocMessageHelper.errorMessage(arg0), arg0);
		
	}

}
