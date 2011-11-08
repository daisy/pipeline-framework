package org.daisy.pipeline.ui.commandline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;


/**
 * Some Sax utilities.
 */
public class SAXHelper {
	
	/**
	 * Gets the sax source.
	 *
	 * @param path the path
	 * @return the sax source
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Source getSaxSource(String path) throws IllegalArgumentException {
		File file = new File(path);
		if (!file.exists() || !file.canRead()) {
			throw new IllegalArgumentException(
					"Error: file not found or its not readable:" + path);
		}
		return new SAXSource(new InputSource(file.toURI().toString()));
	}

	/**
	 * Gets the sax result.
	 *
	 * @param output the output
	 * @return the sax result
	 * @throws IllegalArgumentException the illegal argument exception
	 */
	public static Result getSaxResult(String output) throws IllegalArgumentException {
		if (output == null || output.isEmpty()) {
			return new StreamResult(System.out);
		} else {

			try {
				return new StreamResult(new FileOutputStream(output));
			} catch (FileNotFoundException e) {
				throw new IllegalArgumentException("Output file not found:" + e);
			}

		}

	}
}
