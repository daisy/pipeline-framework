package org.daisy.pipeline.ui.commandline;

import java.net.URI;

import org.daisy.pipeline.modules.ModuleRegistry;

// TODO: Auto-generated Javadoc
/**
 * Lists the available URIs loaded from the module components
 */
public final class CommandListURIs implements Command {

	/** The module registry. */
	private final ModuleRegistry moduleRegistry;

	/**
	 * New instance.
	 *
	 * @param moduleRegistry the module registry
	 * @return the command
	 */
	public static Command newInstance(ModuleRegistry moduleRegistry) {
		if (moduleRegistry == null) {
			throw new IllegalArgumentException();
		}
		return new CommandListURIs(moduleRegistry);
	}

	/**
	 * Instantiates a new command list ur is.
	 *
	 * @param moduleRegistry the module registry
	 */
	private CommandListURIs(ModuleRegistry moduleRegistry) {
		this.moduleRegistry = moduleRegistry;
	}

	/* (non-Javadoc)
	 * @see org.daisy.pipeline.ui.commandline.Command#execute()
	 */
	@Override
	public void execute() throws IllegalArgumentException {
		Iterable<URI> uris = moduleRegistry.getComponents();
		System.out.println("Available URIs:");
		for (URI uri : uris) {
			System.out.println(uri);
		}

	}

}
