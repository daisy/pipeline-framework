package org.daisy.pipeline.modules;

import java.net.URI;

public class Component {

	public enum Space {
		XSLT, XPROC
	}

	private URI uri;
	private Space space;

	// TODO content field: as URL ? as stream ? as handler ?

	public Component(URI uri, Space space) {
		this.uri = uri;
		this.space = space;
	}

	public URI getURI() {
		return uri;
	}

	public Space getSpace() {
		return space;
	}

	@Override
	public String toString() {
		return space + "[" + uri + "]";
	}

}
