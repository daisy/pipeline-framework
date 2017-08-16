import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.SaxonApiException;
import org.daisy.common.xproc.calabash.XProcStepProvider;

import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.core.XProcStep;
import com.xmlcalabash.library.Identity;
import com.xmlcalabash.runtime.XAtomicStep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaStep extends Identity {
	
	private static final Logger logger = LoggerFactory.getLogger(JavaStep.class);
		
	private JavaStep(XProcRuntime runtime, XAtomicStep step) {
		super(runtime, step);
	}

	@Override
	public void run() throws SaxonApiException {
		logger.info("going to throw an exception");
		throw new RuntimeException("foobar");
	}
	
	public static class Provider implements XProcStepProvider {
		public XProcStep newStep(XProcRuntime runtime, XAtomicStep step) {
			return new JavaStep(runtime, step);
		}
	}
}
