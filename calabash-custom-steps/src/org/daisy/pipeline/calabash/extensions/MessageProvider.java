package org.daisy.pipeline.calabash.extensions;


import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.common.xproc.calabash.XProcStepProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.core.XProcStep;
import com.xmlcalabash.io.ReadablePipe;
import com.xmlcalabash.io.WritablePipe;
import com.xmlcalabash.library.DefaultStep;
import com.xmlcalabash.runtime.XAtomicStep;

/**
 * Created by IntelliJ IDEA.
 * User: ndw
 * Date: Oct 8, 2008
 * Time: 7:44:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class MessageProvider implements XProcStepProvider {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	public XProcStep newStep(XProcRuntime runtime, XAtomicStep step) {
		logger.debug("creating message provider");
		return new Message(runtime, step);
	}
}
class Message extends DefaultStep {
    private static final QName _message = new QName("","message");
    private ReadablePipe source = null;
    private WritablePipe result = null;

    /**
     * Creates a new instance of Identity
     */
    public Message(XProcRuntime runtime, XAtomicStep step) {
        super(runtime,step);
    }

    public void setInput(String port, ReadablePipe pipe) {
        source = pipe;
    }

    public void setOutput(String port, WritablePipe pipe) {
        result = pipe;
    }

    public void reset() {
        source.resetReader();
        result.resetWriter();
    }

    public void run() throws SaxonApiException {
        super.run();

        String message = getOption(_message).getString();
        //System.err.println("Message: " + "Message:"+message);
        runtime.info(this, step.getNode(),"Message:"+ message);
        while (source.moreDocuments()) {
            XdmNode doc = source.read();
            runtime.finest(this, step.getNode(), "Message step " + step.getName() + " read " + doc.getDocumentURI());
            result.write(doc);
        }
    }
}