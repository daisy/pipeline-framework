package org.daisy.commons.xproc.io;



public interface ResourceCollection {
	Iterable<Resource> getResources();
	Resource getResource(String path);
}
