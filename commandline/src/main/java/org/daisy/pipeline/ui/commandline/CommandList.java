package org.daisy.pipeline.ui.commandline;

import java.net.URI;
import java.util.Properties;

import org.daisy.pipeline.ui.commandline.provider.ServiceProvider;

public class CommandList extends Command {

	public static final String PROVIDER = "provider";

	public CommandList(Properties args) {
		super(args);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() throws IllegalArgumentException {
		if (!mArgs.containsKey(PROVIDER)) {
			throw new IllegalArgumentException(
					"Exepecting provider as an argument");
		}
		
		ServiceProvider provider = null;
		
		try {
			provider = (ServiceProvider) mArgs.get(PROVIDER);
		} catch (ClassCastException cce) {
			throw new IllegalArgumentException(
					"Provider is not a ServiceProvider");
		}
		
		Iterable<URI> uris = provider.getModuleRegistry().getComponents();
		System.out.println("Available URIs:");
		for (URI uri : uris) {
			System.out.println(uri);
		}

	}

}
