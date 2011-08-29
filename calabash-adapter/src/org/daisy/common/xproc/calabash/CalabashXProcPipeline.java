package org.daisy.common.xproc.calabash;

import java.net.URI;

import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;

import net.sf.saxon.s9api.DocumentBuilder;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.XdmNode;

import org.daisy.common.base.Provider;
import org.daisy.common.xproc.XProcInput;
import org.daisy.common.xproc.XProcOptionInfo;
import org.daisy.common.xproc.XProcPipeline;
import org.daisy.common.xproc.XProcPipelineInfo;
import org.daisy.common.xproc.XProcPortInfo;
import org.daisy.common.xproc.XProcResult;
import org.xml.sax.EntityResolver;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.core.XProcRuntime;
import com.xmlcalabash.model.DeclareStep;
import com.xmlcalabash.model.Input;
import com.xmlcalabash.model.Option;
import com.xmlcalabash.model.Output;
import com.xmlcalabash.model.RuntimeValue;
import com.xmlcalabash.runtime.XPipeline;

public class CalabashXProcPipeline implements XProcPipeline {

	private final URI uri;
	private final XProcConfiguration configuration;
	private final URIResolver uriResolver;
	private final EntityResolver entityResolver;
	private final Supplier<XPipeline> xpipeline = new Supplier<XPipeline>() {

		@Override
		public XPipeline get() {
			XProcRuntime runtime = new XProcRuntime(configuration);
			runtime.setPhoneHome(false);
			runtime.setMessageListener(new slf4jXProcMessageListener());
			if (uriResolver != null) {
				runtime.setURIResolver(uriResolver);
			}
			if (entityResolver != null) {
				runtime.setEntityResolver(entityResolver);
			}

			XPipeline xpipeline = null;

			try {
				xpipeline = runtime.load(uri.toString());
			} catch (SaxonApiException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			return xpipeline;
		}
	};
	private final Supplier<XProcPipelineInfo> info = Suppliers
			.memoize(new Supplier<XProcPipelineInfo>() {

				@Override
				public XProcPipelineInfo get() {
					XProcPipelineInfo.Builder builder = new XProcPipelineInfo.Builder();
					builder.withURI(uri);
					DeclareStep pipeline = xpipeline.get().getDeclareStep();
					// input and parameter ports
					for (Input input : pipeline.inputs()) {
						if (input.getParameterInput()) {
							builder.withPort(XProcPortInfo.newInputPort(
									input.getPort(), input.getSequence(),
									input.getPrimary()));
						} else {
							builder.withPort(XProcPortInfo.newParameterPort(
									input.getPort(), input.getPrimary()));
						}
					}
					// output ports
					for (Output output : pipeline.outputs()) {
						builder.withPort(XProcPortInfo.newOutputPort(
								output.getPort(), output.getSequence(),
								output.getPrimary()));
					}
					// options
					for (Option option : pipeline.options()) {
						builder.withOption(new XProcOptionInfo(new QName(option
								.getName().getNamespaceURI(), option.getName()
								.getLocalName(), option.getName().getPrefix()),
								option.getRequired(), option.getSelect()));
					}
					return builder.build();
				}
			});

	public CalabashXProcPipeline(URI uri, XProcConfiguration conf,
			URIResolver uriResolver, EntityResolver entityResolver) {
		this.uri = uri;
		this.configuration = conf;
		this.uriResolver = uriResolver;
		this.entityResolver = entityResolver;
	}

	@Override
	public XProcPipelineInfo getInfo() {
		return info.get();
	}

	@Override
	public XProcResult run(XProcInput data) {
		XPipeline xpipe = this.xpipeline.get();
		// bind inputs
		for (String name : xpipe.getInputs()) {
			for (Provider<Source> source : data.getInputs(name)) {
				xpipe.writeTo(
						name,
						asXdmNode(configuration.getProcessor(),
								source.provide()));
			}
		}
		// bind options
		for (QName optname : data.getOptions().keySet()) {
			RuntimeValue value = new RuntimeValue(data.getOptions()
					.get(optname));
			xpipe.passOption(new net.sf.saxon.s9api.QName(optname), value);
		}

		// bind parameters
		for (String port : info.get().getParameterPorts()) {
			for (QName name : data.getParameters(port).keySet()) {
				RuntimeValue value = new RuntimeValue(data.getParameters(port)
						.get(name), null, null);
				xpipe.setParameter(port, new net.sf.saxon.s9api.QName(name),
						value);
			}
		}

		// run
		try {
			xpipe.run();
		} catch (SaxonApiException e) {
			e.printStackTrace();
		}
		return CalabashXProcResult.newInstance(xpipe, configuration);
	}

	private static XdmNode asXdmNode(Processor processor, Source source) {
		//TODO set entity resolver
		DocumentBuilder builder = processor.newDocumentBuilder();
		builder.setDTDValidation(false);
		builder.setLineNumbering(true);
		try {
			return builder.build(source);
		} catch (SaxonApiException sae) {
			// TODO better exception handling
			throw new RuntimeException(sae.getMessage(), sae);
		}
	}
}
