package org.daisy.calabash;

import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.core.XProcStep;
import com.xmlcalabash.runtime.XAtomicStep;

public interface XProcStepProvider {

	XProcStep newStep(XProcRuntime runtime, XAtomicStep step);
}
