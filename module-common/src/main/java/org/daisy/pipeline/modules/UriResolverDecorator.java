package org.daisy.pipeline.modules;

import javax.xml.transform.URIResolver;


public interface UriResolverDecorator extends URIResolver {
	public UriResolverDecorator setDelegatedUriResolver(URIResolver uriResolver);
}
