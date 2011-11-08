package org.daisy.expath.parser;

import java.net.URL;

import org.daisy.pipeline.modules.Module;


/**
 *EXPathPackageParser for parsing expath descriptors
 */
public interface EXPathPackageParser {

	/**
	 * Parses the expath package file.
	 *
	 * @param url the url
	 * @param builder the builder
	 * @return the module
	 */
	Module parse(URL url, ModuleBuilder builder);

}