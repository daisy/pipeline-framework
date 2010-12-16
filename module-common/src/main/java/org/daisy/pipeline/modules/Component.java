package org.daisy.pipeline.modules;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Component {

	public enum Space {
		XSLT, XPROC
	}

	private URI uri;
	private String path;
	private Space space;
	private ResourceLoader loader;

	// TODO content field: as URL ? as stream ? as handler ?

	public Component(URI uri, String path, Space space, ResourceLoader loader) {
		this.uri = uri;
		this.path = path;
		this.space = space;
		this.loader = loader;
	}

	public URI getURI() {
		return uri;
	}

	public Space getSpace() {
		return space;
	}

	public URI getResource() {
		try {
			URL url= loader.loadResource(path);
			if(url!=null)
				return url.toURI();
			else
				return null;
			
		} catch (URISyntaxException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return space + "[" + uri + "]";
	}

}
