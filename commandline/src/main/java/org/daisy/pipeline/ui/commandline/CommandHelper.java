package org.daisy.pipeline.ui.commandline;

import java.util.HashMap;


//TODO move to CommandPipeline (only used there) ?
/**
 * Utilities for CLI command processing
 */
public class CommandHelper {
	
	/**
	 * Parses the input list.
	 *
	 * @param list the list
	 * @return the hash map
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static HashMap<String, String> parseInputList(String list)
			throws IllegalArgumentException {

		HashMap<String, String> pairs = new HashMap<String, String>();
		if (list.isEmpty())
			return pairs;

		String[] parts = list.split(",");
		for (String part : parts) {
			String pair[] = part.split("=");
			try {
				pairs.put(pair[0], pair[1]);
			} catch (Exception e) {
				throw new IllegalArgumentException("Error in list format:"
						+ list);
			}
		}
		if (pairs.containsKey(null) || pairs.containsKey("")) {
			throw new IllegalArgumentException("Error in list format:" + list);
		}
		if (pairs.containsValue(null) || pairs.containsValue("")) {
			throw new IllegalArgumentException("Error in list format:" + list);
		}
		return pairs;
	}
}
