package org.daisy.expath.parser;

import java.net.URL;

import org.daisy.pipeline.modules.Module;

public interface EXPathPackageParser {

	Module parse(URL url);

}