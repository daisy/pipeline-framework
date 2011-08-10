package org.daisy.pipeline.modules.converter;

import java.io.IOException;
import java.io.InputStream;

public interface XProcContext {
	public Iterable<String> resources();
	public InputStream getResource(String name) throws IOException;
}
