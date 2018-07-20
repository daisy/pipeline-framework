package org.daisy.common.file;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

public final class URLs {

	private URLs() {
	}
	
	/* If object is a String, it is assumed to represent a URI */
	public static URL asURL(Object o) {
		if (o == null)
			return null;
		try {
			if (o instanceof String)
				return asURL(URIs.asURI(o));
			if (o instanceof File)
				return asURL(URIs.asURI(o));
			if (o instanceof URL)
				return (URL)o;
			if (o instanceof URI)
				return new URL(decode(o.toString())); }
		catch (Exception e) {}
		throw new RuntimeException("Object can not be converted to URL: " + o);
	}
	
	public static URL resolve(Object base, Object url) {
		if (url instanceof URI)
			return asURL(URIs.asURI(base).resolve((URI)url));
		if (url instanceof String) {
			try { return new URL(asURL(base), url.toString()); }
			catch (MalformedURLException e) { throw new RuntimeException(e); }}
		return asURL(url);
	}
	
	public static String relativize(Object base, Object url) {
		return decode(URIs.asURI(base).relativize(URIs.asURI(url)).toString());
	}
	
	@SuppressWarnings(
		"deprecation" // URLDecode.decode is deprecated
	)
	public static String decode(String uri) {
		// URIs treat the + symbol as is, but URLDecoder will decode both + and %20 into a space
		return URLDecoder.decode(uri.replace("+", "%2B"));
	}
}
