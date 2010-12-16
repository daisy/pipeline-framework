package org.daisy.pipeline.ui.commandline.provider;
import org.daisy.pipeline.modules.ModuleRegistry;
import org.daisy.pipeline.modules.UriResolverDecorator;
import org.daisy.pipeline.xproc.XProcessorFactory;
public interface ServiceProvider {
	
	public ModuleRegistry getModuleRegistry();
	public XProcessorFactory getXProcessorFactory();
	public UriResolverDecorator getUriResolver();

}
