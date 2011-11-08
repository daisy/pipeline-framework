package org.daisy.pipeline.xpath.hello;

import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.type.AnyItemType;
import net.sf.saxon.value.SequenceType;

public class HelloFunction extends ExtensionFunctionDefinition {
	private static final long serialVersionUID = 1L;
	
	public final static String SAXON_TEST_NS_PREFIX = "saxontest";
    public final static String SAXON_TEST_NS_URI = "http://www.example.org/saxontest";
    public final static String LOCAL_NAME = "hello";
    
	
	@Override
	public SequenceType[] getArgumentTypes() {
		return new SequenceType[] {
				SequenceType.SINGLE_STRING
		};
	}
	
	@Override
	public StructuredQName getFunctionQName() {
		return new StructuredQName(SAXON_TEST_NS_PREFIX, SAXON_TEST_NS_URI, LOCAL_NAME);
	}
	
	@Override
	public int getMinimumNumberOfArguments() {
		return 1;
	}
	
	@Override
	public int getMaximumNumberOfArguments() {
		return 1;
	}
	
	@Override
	public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
		return SequenceType.makeSequenceType(AnyItemType.getInstance(), StaticProperty.ALLOWS_ONE);
	}
	
	@Override
	public ExtensionFunctionCall makeCallExpression() {
		return new HelloCall();
	}
}
