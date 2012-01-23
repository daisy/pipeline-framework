package org.daisy.pipeline.xpath.hello;



import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.ArrayIterator;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.TextFragmentValue;

public class HelloCall extends ExtensionFunctionCall {
	private static final long serialVersionUID = 1L;

	@Override
	public SequenceIterator call(SequenceIterator[] arguments, XPathContext context) throws XPathException {
		Item[] hello = new Item[1];

		if (arguments.length == 1) {
			Item item = arguments[0].next();
			if (item instanceof StringValue) {
				hello[0] = new TextFragmentValue("NOW IMPROVED: Hello, "+item.getStringValue()+"!", null);
			} else {
				throw new XPathException("Invalid string value"+(item!=null?(": "+item.toString()):""));
			}

		} else {
			throw new XPathException("Incorrect number of params: "+arguments.length);
		}

		return new ArrayIterator(hello);
	}

}

