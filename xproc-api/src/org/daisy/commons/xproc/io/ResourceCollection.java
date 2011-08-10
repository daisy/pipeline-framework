package org.daisy.commons.xproc.io;



public interface ResourceCollection {
	Iterable<Resource> getResources();
	Iterable<String> getPaths();
	Resource getResource(String path);
}
