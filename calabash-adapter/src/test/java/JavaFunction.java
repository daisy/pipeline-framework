import com.xmlcalabash.core.XProcException;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaFunction extends ExtensionFunctionDefinition {
	
	private static final StructuredQName funcname = new StructuredQName("pf",
			"http://www.daisy.org/ns/pipeline/functions", "java-function");
	
	@Override
	public StructuredQName getFunctionQName() {
		return funcname;
	}
	
	@Override
	public int getMinimumNumberOfArguments() {
		return 0;
	}
	
	@Override
	public int getMaximumNumberOfArguments() {
		return 0;
	}
	
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {};
	}
	
	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.SINGLE_STRING;
	}
	
	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new ExtensionFunctionCall() {
			public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
				try {
					logger.info("going to throw an exception");
					throw new RuntimeException("foobar");
				} catch (Throwable e) {
					throw new XPathException(XProcException.javaError(e, 0));
				}
			}
		};
	}
	
	private static final Logger logger = LoggerFactory.getLogger(JavaFunction.class);
	
}
