package org.daisy.common.file;

import java.io.File;
import java.net.URI;
import java.net.URL;

public final class URIs {

	private URIs() {
	}
	
	/* If object is a String, it is assumed to represent a URI */
	public static URI asURI(Object o) {
		if (o == null)
			return null;
		try {
			if (o instanceof String)
				return new URI((String)o);
			if (o instanceof File)
				return ((File)o).toURI();
			if (o instanceof URL) {
				URL url = (URL)o;
				if (url.getProtocol().equals("jar"))
					return new URI("jar:" + new URI(null, url.getAuthority(), url.getPath(), url.getQuery(), url.getRef()).toASCIIString());
				String authority = (url.getPort() != -1) ?
					url.getHost() + ":" + url.getPort() :
					url.getHost();
				return new URI(url.getProtocol(), authority, url.getPath(), url.getQuery(), url.getRef()); }
			if (o instanceof URI)
				return (URI)o; }
		catch (Exception e) {}
		throw new RuntimeException("Object can not be converted to URI: " + o);
	}
	
	public static URI resolve(Object base, Object uri) {
		URI baseURI = asURI(base);
		if (baseURI.toString().startsWith("jar:"))
			return asURI("jar:" + asURI(baseURI.toString().substring(4)).resolve(asURI(uri)).toASCIIString());
		else
			return baseURI.resolve(asURI(uri));
	}
	
	public static URI relativize(Object base, Object uri) {
		return asURI(base).relativize(asURI(uri));
	}
}
