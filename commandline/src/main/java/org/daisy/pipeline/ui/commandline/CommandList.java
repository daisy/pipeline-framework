package org.daisy.pipeline.ui.commandline;

import java.net.URI;

import org.daisy.pipeline.modules.ModuleRegistry;

public final class CommandList implements Command {

	public static Command newInstance(ModuleRegistry moduleRegistry) {
		if (moduleRegistry == null) {
			throw new IllegalArgumentException();
		}
		return new CommandList(moduleRegistry);
	}

	private final ModuleRegistry moduleRegistry;

	private CommandList(ModuleRegistry moduleRegistry) {
		this.moduleRegistry = moduleRegistry;
	}

	public void execute() throws IllegalArgumentException {
		Iterable<URI> uris = moduleRegistry.getComponents();
		System.out.println("Available URIs:");
		for (URI uri : uris) {
			System.out.println(uri);
		}

	}

}
