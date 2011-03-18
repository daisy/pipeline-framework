package org.daisy.converter.registry;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.daisy.pipeline.modules.converter.Converter.ConverterArgument.Type;
import org.daisy.pipeline.modules.converter.ConverterDescriptor;
import org.daisy.pipeline.modules.converter.ConverterRunnable;
import org.daisy.pipeline.xproc.XProcessor;

public class OSGIConverterRunner extends ConverterRunnable {

	OSGIConverterRegistry mRegistry;

	protected OSGIConverterRunner(OSGIConverter conv) {
		super(conv);
		mRegistry = conv.getRegistry();
		mExecutor = new OSGIConverterExecutor();
	}

	class OSGIConverterExecutor implements ConverterExecutor {

		@Override
		public void execute(ConverterRunnable runnable) {
			ConverterDescriptor desc = mRegistry.getDescriptor(runnable
					.getConverter().getName());
			Source src;
			try {
				src = mRegistry.getUriResolver().resolve(
						desc.getFile().toString(), "");
			} catch (TransformerException e) {
				throw new RuntimeException(
						"Error while getting the converter file:"
								+ e.getLocalizedMessage(), e);
			}
			XProcessor proc = mRegistry.getXprocFactory().getProcessor(src);
			proc.setURIResolver(mRegistry.getUriResolver());
			bindInputs(proc, runnable);
			bindOutputs(proc, runnable);
			bindParams(proc, runnable);
			bindOptions(proc, runnable);
			// lets start the fun
			proc.run();

		}

		private void bindInputs(XProcessor proc, ConverterRunnable runnable) {
			try {
				for (ValuedConverterArgument arg : runnable.getValues()) {
					if (arg.getArgument().getType() == Type.INPUT) {
						Source src;

						src = mRegistry.getUriResolver().resolve(
								arg.getValue(), "");
						proc.bindInputPort(arg.getArgument().getPort(), src);
					}
				}
			} catch (TransformerException e) {
				throw new RuntimeException("Error binding port:"
						+ e.getLocalizedMessage(), e);
			}

		}

		private void bindOutputs(XProcessor proc, ConverterRunnable runnable) {

			for (ValuedConverterArgument arg : runnable.getValues()) {
				if (arg.getArgument().getType() == Type.OUTPUT) {

					proc.bindOutputPort(arg.getArgument().getPort(),
							getSaxResult(arg.getValue()));
				}
			}

		}

		private void bindParams(XProcessor proc, ConverterRunnable runnable) {
			for (ValuedConverterArgument arg : runnable.getValues()) {
				if (arg.getArgument().getType() == Type.PARAMETER) {

					proc.setParameter(arg.getArgument().getPort(), arg
							.getArgument().getBind(), arg.getValue());
				}
			}
		}

		private void bindOptions(XProcessor proc, ConverterRunnable runnable) {
			for (ValuedConverterArgument arg : runnable.getValues()) {
				if (arg.getArgument().getType() == Type.OPTION) {
					proc.setOption(arg.getArgument().getBind(), arg.getValue());
				}
			}
		}

		private Result getSaxResult(String output)
				throws IllegalArgumentException {
			// return new StreamResult(System.out);
			if (output == null || output.isEmpty()) {
				return new StreamResult(System.out);
			} else {

				try {
					return new StreamResult(new FileOutputStream(output));
				} catch (FileNotFoundException e) {
					throw new IllegalArgumentException("Output file not found:"
							+ e);
				}

			}

		}

	}
}