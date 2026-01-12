package org.daisy.common.file;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Optional;

/**
 * A readable resource (file) with a certain path and media type. Does not need to be stored on disk.
 *
 * Java counterpart of {@code <d:file>} in XProc.
 *
 * This class is immutable.
 */
public class Resource {

	private final URI path;
	private final Optional<String> mediaType;

	private final FileData data;

	private Resource(FileData data, URI path, Optional<String> mediaType) {
		this.data = data;
		this.path = URLs.normalize(path);
		this.mediaType = mediaType;
	}

	/**
	 * The path as a URI.
	 *
	 * May be absolute or relative.
	 */
	public URI getPath() {
		return path;
	}

	/**
	 * The path as a URI relative to the given base URI.
	 */
	public URI getPath(URI base) {
		if (base != null)
			return URLs.relativize(URLs.normalize(base), path);
		else
			return path;
	}

	public Optional<String> getMediaType() {
		return mediaType;
	}

	/**
	 * @param from Must be an absolute, hierarchical URI with a scheme equal to "file",
	 *             a non-empty path component, and undefined authority, query, and
	 *             fragment components.
	 * @throws IllegalArgumentException if the URI does not point to a file on disk.
	 */
	public static Resource load(URI from, String mediaType) {
		return load(from, from, mediaType);
	}

	public static Resource load(URI from, URI path, String mediaType) {
		return load(new File(from), path, mediaType);
	}

	public static Resource load(File from, String mediaType) {
		return load(from, from.toURI(), mediaType);
	}

	public static Resource load(File from, URI path, String mediaType) {
		return new Resource(from, path, Optional.ofNullable(mediaType));
	}

	public static Resource load(byte[] data, URI path, String mediaType) {
		return new Resource(new FileDataInMemory(data), path, Optional.ofNullable(mediaType));
	}

	/**
	 * Get the contents of the resource as an {@link InputStream}.
	 */
	public InputStream read() throws IOException {
		return data.read();
	}

	/**
	 * If this resource is stored on disk or was loaded from disk, return the path as a {@link File}.
	 *
	 * May be different from {@link #getPath()} if the file was copied from an original location.
	 */
	public File readAsFile() throws UnsupportedOperationException {
		if (data instanceof FileDataOnDisk)
			return ((FileDataOnDisk)data).file;
		else
			throw new UnsupportedOperationException("not stored on disk");
	}

	public Resource copy(URI path) {
		return new Resource(data, path, mediaType);
	}

	public Resource copy(File path) {
		return copy(path.toURI());
	}

	/**
	 * Store file to disk
	 *
	 * @throws IOException if "path" does not point to a file on disk or if the file can not be
	 *                     written to.
	 */
	public Resource store() throws IOException {
		File file; {
			try {
				file = new File(path);
			} catch (IllegalArgumentException e) {
				throw new IOException("path is not a file URI: " + path);
			}
		}
		return new Resource(data.store(file), path, mediaType);
	}

	private static abstract class FileData {

		public abstract InputStream read() throws IOException;

		public FileData store(File destination) throws IOException {
			try (OutputStream os = new FileOutputStream(destination)) {
				try (InputStream is = read()) {
					byte[] bucket = new byte[1024];
					int bytesRead = 0;
					while ((bytesRead = is.read(bucket, 0, bucket.length)) != -1) {
						os.write(bucket, 0, bytesRead);
					}
				}
			}
			return new FileDataOnDisk(destination);
		}
	}

	private static class FileDataOnDisk extends FileData {

		private final File file;

		/**
		 * @param file is read lazily, but we already check whether it exists
		 */
		private FileDataOnDisk(File file) {
			if (!(file.isFile() && file.exists()))
				throw new IllegalArgumentException(new FileNotFoundException("file does not exist: " + file));
			this.file = file;
		}

		public InputStream read() throws IOException {
			return new FileInputStream(file);
		}

		@Override
		public FileData store(File destination) throws IOException {
			if (destination.equals(file))
				return this;
			return super.store(destination);
		}
	}

	private static class FileDataInMemory extends FileData {

		private final byte[] data;

		/**
		 * @param data is read lazily
		 */
		private FileDataInMemory(byte[] data) {
			this.data = data;
		}

		public InputStream read() {
			return new ByteArrayInputStream(data);
		}
	}
}
