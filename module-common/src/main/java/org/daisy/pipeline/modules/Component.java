package org.daisy.pipeline.modules;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Component {

	public enum Space {
		XSLT, XPROC
	}

	private URI uri;
	private String path;
	private Space space;
	private ResourceLoader loader;
	private Module module;
    Logger mLogger = LoggerFactory.getLogger(getClass().getName());
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
			mLogger.debug("getting resource from component:"+this.module.getName()+"/"+path);
			URL url= loader.loadResource(this.module.getName()+"/"+path);
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
	public Module getModule() {
		return module;
	}

	public void setModule(Module module) {
		this.module = module;
	}
}
