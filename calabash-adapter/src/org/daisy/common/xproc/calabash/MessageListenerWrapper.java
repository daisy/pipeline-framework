package org.daisy.common.xproc.calabash;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.common.messaging.MessageAccessor;
import org.daisy.common.messaging.MessageListener;

import com.xmlcalabash.core.XProcMessageListener;
import com.xmlcalabash.core.XProcRunnable;

// TODO: Auto-generated Javadoc
/**
 * Wrapps the org.daisy.common.messaging.MessageListener to a XProcMessageListener to be plugged in calabash
 */
public class MessageListenerWrapper implements XProcMessageListener{
	
	/** The listener. */
	MessageListener mListener;
	
	/**
	 * Instantiates a new message listener wrapper
	 *
	 * @param messageListener the message listener wrapped
	 */
	public MessageListenerWrapper(MessageListener messageListener){
		mListener=messageListener;
	} 
	
	/**
	 * Gets the message accessor to sneak into the messages sent by calabash.
	 *
	 * @return the accessor
	 */
	public MessageAccessor getAccessor(){
		return mListener.getAccessor();
	}
	
	/* (non-Javadoc)
	 * @see com.xmlcalabash.core.XProcMessageListener#error(java.lang.Throwable)
	 */
	@Override
	public void error(Throwable arg0) {
		mListener.error(XprocMessageHelper.errorMessage(arg0), arg0);
		
	}

	/* (non-Javadoc)
	 * @see com.xmlcalabash.core.XProcMessageListener#error(com.xmlcalabash.core.XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String, net.sf.saxon.s9api.QName)
	 */
	@Override
	public void error(XProcRunnable step, XdmNode node, String message, QName qName) {
		 mListener.error(XprocMessageHelper.message(step, node, message));
		
	}

	/* (non-Javadoc)
	 * @see com.xmlcalabash.core.XProcMessageListener#fine(com.xmlcalabash.core.XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void fine(XProcRunnable step, XdmNode node, String message) {
		mListener.debug(XprocMessageHelper.message(step, node, message));
		
	}

	/* (non-Javadoc)
	 * @see com.xmlcalabash.core.XProcMessageListener#finer(com.xmlcalabash.core.XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void finer(XProcRunnable step, XdmNode node, String message) {
		mListener.debug(XprocMessageHelper.message(step, node, message));
		
	}

	/* (non-Javadoc)
	 * @see com.xmlcalabash.core.XProcMessageListener#finest(com.xmlcalabash.core.XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void finest(XProcRunnable step, XdmNode node, String message) {
		mListener.trace(XprocMessageHelper.message(step, node, message));
		
	}

	/* (non-Javadoc)
	 * @see com.xmlcalabash.core.XProcMessageListener#info(com.xmlcalabash.core.XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void info(XProcRunnable step, XdmNode node, String message) {
		mListener.info(XprocMessageHelper.message(step, node, message));
		
	}

	/* (non-Javadoc)
	 * @see com.xmlcalabash.core.XProcMessageListener#warning(com.xmlcalabash.core.XProcRunnable, net.sf.saxon.s9api.XdmNode, java.lang.String)
	 */
	@Override
	public void warning(XProcRunnable step, XdmNode node, String message) {
		 mListener.warn(XprocMessageHelper.message(step, node, message));
		
	}

	/* (non-Javadoc)
	 * @see com.xmlcalabash.core.XProcMessageListener#warning(java.lang.Throwable)
	 */
	@Override
	public void warning(Throwable arg0) {
		mListener.error(XprocMessageHelper.errorMessage(arg0), arg0);
		
	}

}
