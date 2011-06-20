package org.daisy.calabash;

import net.sf.saxon.s9api.QName;

import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.core.XProcStep;
import com.xmlcalabash.runtime.XAtomicStep;

public interface XProcStepRegistry {

	boolean hasStep(QName type);

	XProcStep newStep(QName type, XProcRuntime runtime, XAtomicStep step);
}
