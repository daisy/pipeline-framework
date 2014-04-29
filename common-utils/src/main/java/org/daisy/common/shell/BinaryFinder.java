package org.daisy.common.shell;

import java.io.File;

public class BinaryFinder {

	/**
	 * Look for a given executable in the PATH environment variable.
	 * 
	 * @return null if @param executableName cannot be found.
	 */
	public static String find(String executableName) {
		String os = System.getProperty("os.name");
		String[] extensions;
		if (os != null && os.startsWith("Windows"))
			extensions = winExtensions;
		else
			extensions = nixExtensions;

		String systemPath = System.getenv("PATH");
		String[] pathDirs = systemPath.split(File.pathSeparator);
		for (String ext : extensions) {
			String fullname = executableName + ext;
			for (String pathDir : pathDirs) {
				File file = new File(pathDir, fullname);
				if (file.isFile()) {
					return file.getAbsolutePath();
				}
			}
		}
		return null;
	}

	private static final String[] winExtensions = {
	        ".exe", ".bat", ".cmd", ".bin", ""
	};
	private static final String[] nixExtensions = {
	        "", ".run", ".bin", ".sh"
	};

}
