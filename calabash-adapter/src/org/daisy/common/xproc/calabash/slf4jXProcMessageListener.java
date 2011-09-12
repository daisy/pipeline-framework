package org.daisy.common.xproc.calabash;

import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.XdmNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xmlcalabash.core.XProcMessageListener;
import com.xmlcalabash.core.XProcRunnable;

public class slf4jXProcMessageListener implements XProcMessageListener {
    private static Logger defaultLogger = LoggerFactory.getLogger("com.xmlcalabash");
    private Logger log = defaultLogger;

    public void error(XProcRunnable step, XdmNode node, String message, QName code) {
    	
        if (step != null) {
            log = LoggerFactory.getLogger(step.getClass());
        } else {
            log = defaultLogger;
        }

        log.error(XprocMessageHelper.message(step, node, message, code));
    }

    public void error(Throwable exception) {
 

        log.error(XprocMessageHelper.errorMessage(exception));
    }

    public void warning(XProcRunnable step, XdmNode node, String message) {
        if (step != null) {
            log = LoggerFactory.getLogger(step.getClass());
        } else {
            log = defaultLogger;
        }
        log.warn(XprocMessageHelper.message(step, node, message));
    }

    public void info(XProcRunnable step, XdmNode node, String message) {
        if (step != null) {
            log = LoggerFactory.getLogger(step.getClass());
        } else {
            log = defaultLogger;
        }
        log.info(XprocMessageHelper.message(step, node, message));
    }

    public void fine(XProcRunnable step, XdmNode node, String message) {
        if (step != null) {
            log = LoggerFactory.getLogger(step.getClass());
        } else {
            log = defaultLogger;
        }
        log.debug(XprocMessageHelper.message(step, node, message));
    }

    public void finer(XProcRunnable step, XdmNode node, String message) {
        if (step != null) {
            log = LoggerFactory.getLogger(step.getClass());
        } else {
            log = defaultLogger;
        }
        log.debug(XprocMessageHelper.message(step, node, message));
    }

    public void finest(XProcRunnable step, XdmNode node, String message) {
        if (step != null) {
            log = LoggerFactory.getLogger(step.getClass());
        } else {
            log = defaultLogger;
        }
        log.info(XprocMessageHelper.message(step, node, message));
    }

	@Override
	public void warning(Throwable exception) {
		 log.error(XprocMessageHelper.errorMessage(exception));
		
	}




}
