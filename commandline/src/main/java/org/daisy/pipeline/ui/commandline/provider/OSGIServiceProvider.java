package org.daisy.pipeline.ui.commandline.provider;

import org.daisy.pipeline.modules.ModuleRegistry;
import org.daisy.pipeline.modules.UriResolverDecorator;
import org.daisy.pipeline.modules.converter.ConverterRegistry;
import org.daisy.pipeline.xproc.XProcessorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class OSGIServiceProvider implements ServiceProvider {

	BundleContext mCtxt;
	ServiceTracker mModuleRegistryTracker;
	public OSGIServiceProvider(BundleContext ctxt) {

		mCtxt = ctxt;
	}
	@Override
	public ModuleRegistry getModuleRegistry() {

		return this.getService(ModuleRegistry.class);
	}
	@Override
	public XProcessorFactory getXProcessorFactory() {
		return this.getService(XProcessorFactory.class);
	}
	@Override
	public UriResolverDecorator getUriResolver() {
		return this.getService(UriResolverDecorator.class);
	} 
	@Override
	public ConverterRegistry getConverterRegistry() {
		return this.getService(ConverterRegistry.class);
	}
	private <T> T getService(Class<T> clazz){
		T service=null;
		ServiceTracker tracker;
	
		tracker= new ServiceTracker(mCtxt, clazz.getName(), null);
		tracker.open();
		
		try {
			service = (T) tracker.waitForService(5000);
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted");
		}
		if(service==null){
			throw new RuntimeException("No service found for "+clazz.getName());
		}
		tracker.close();
		return service;
	}
	

}
