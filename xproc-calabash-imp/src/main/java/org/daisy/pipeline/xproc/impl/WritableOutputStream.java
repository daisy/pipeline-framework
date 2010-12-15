package org.daisy.pipeline.xproc.impl;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;
import net.sf.saxon.s9api.XdmNode;

import com.xmlcalabash.core.XProcException;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.io.WritablePipe;
import com.xmlcalabash.model.Serialization;
import com.xmlcalabash.model.Step;


public class WritableOutputStream implements WritablePipe {
	private OutputStream mOs;
	private XProcRuntime runtime = null;
	private Serializer serializer = null;
	private Serialization serial = null;
	private Step writer = null;
    private boolean writeSeqOk = false;
    
	public WritableOutputStream(XProcRuntime xproc, OutputStream os,
			Serialization serial) {
		this.runtime = xproc;
		this.mOs = os;

		if (serial == null) {
			this.serial = new Serialization(xproc, null);
			this.serial.setIndent(xproc.getDebug()); // indent stdio by default
														// when debugging
		} else {
			this.serial = serial;
		}
	}

	public void write(XdmNode doc) {
		try {
			
			Processor qtproc = runtime.getProcessor();
			DocumentBuilder builder = qtproc.newDocumentBuilder();
			builder.setBaseURI(new URI("http://example.com/"));
			XQueryCompiler xqcomp = qtproc.newXQueryCompiler();
			XQueryExecutable xqexec = xqcomp.compile(".");
			XQueryEvaluator xqeval = xqexec.load();
			xqeval.setContextItem(doc);

			serializer = new Serializer();

			serializer.setOutputProperty(Serializer.Property.BYTE_ORDER_MARK,
					serial.getByteOrderMark() ? "yes" : "no");
			// FIXME: support CDATA_SECTION_ELEMENTS
			if (serial.getDoctypePublic() != null) {
				serializer.setOutputProperty(
						Serializer.Property.DOCTYPE_PUBLIC, serial
								.getDoctypePublic());
			}
			if (serial.getDoctypeSystem() != null) {
				serializer.setOutputProperty(
						Serializer.Property.DOCTYPE_SYSTEM, serial
								.getDoctypeSystem());
			}
			if (serial.getEncoding() != null) {
				serializer.setOutputProperty(Serializer.Property.ENCODING,
						serial.getEncoding());
			}
			serializer.setOutputProperty(
					Serializer.Property.ESCAPE_URI_ATTRIBUTES, serial
							.getEscapeURIAttributes() ? "yes" : "no");
			serializer.setOutputProperty(
					Serializer.Property.INCLUDE_CONTENT_TYPE, serial
							.getIncludeContentType() ? "yes" : "no");
			serializer.setOutputProperty(Serializer.Property.INDENT, serial
					.getIndent() ? "yes" : "no");
			if (serial.getMediaType() != null) {
				serializer.setOutputProperty(Serializer.Property.MEDIA_TYPE,
						serial.getMediaType());
			}
			if (serial.getMethod() != null) {
				serializer.setOutputProperty(Serializer.Property.METHOD, serial
						.getMethod().getLocalName());
			}
			if (serial.getNormalizationForm() != null) {
				serializer.setOutputProperty(
						Serializer.Property.NORMALIZATION_FORM, serial
								.getNormalizationForm());
			}
			serializer.setOutputProperty(
					Serializer.Property.OMIT_XML_DECLARATION, serial
							.getOmitXMLDeclaration() ? "yes" : "no");
			if (serial.getStandalone() != null) {
				String standalone = serial.getStandalone();
				if ("true".equals(standalone)) {
					serializer.setOutputProperty(
							Serializer.Property.STANDALONE, "yes");
				} else if ("false".equals(standalone)) {
					serializer.setOutputProperty(
							Serializer.Property.STANDALONE, "no");
				}
				// What about omit?
			}
			serializer.setOutputProperty(
					Serializer.Property.UNDECLARE_PREFIXES, serial
							.getUndeclarePrefixes() ? "yes" : "no");
			if (serial.getVersion() != null) {
				serializer.setOutputProperty(Serializer.Property.VERSION,
						serial.getVersion());
			}

		
		    serializer.setOutputStream(mOs);
		

			xqeval.setDestination(serializer);
			xqeval.run();

			
		} catch (URISyntaxException use) {
			use.printStackTrace();
			throw new XProcException(use);
		} catch (SaxonApiException sae) {
			sae.printStackTrace();
			throw new XProcException(sae);
		} 
	

		if (writer != null) {
			runtime.finest(null, writer.getNode(), writer.getName()
					+ " wrote '" + (doc == null ? "null" : doc.getBaseURI()));
		}
	}

    public void canWriteSequence(boolean sequence) {
        writeSeqOk = sequence;
    }

    public void resetWriter() {
        throw new UnsupportedOperationException("You can't resetWriter a WritableDocument");
    }

    public void close() {
        // nop;
    }

    public void setWriter(Step step) {
        writer = step;
    }
	
	
}
