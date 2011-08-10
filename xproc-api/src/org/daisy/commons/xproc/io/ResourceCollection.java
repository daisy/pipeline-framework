package org.daisy.commons.xproc.io;



public interface ResourceCollection {
	Iterable<Resource> getResources();
	Iterable<String> getResourcesNames();
	Resource getResource(String name);
}
