package org.daisy.expath.parser;

import javax.xml.namespace.QName;


/**
 * Useful constants for expath file parsing
 */
public final class EXPathConstants {
	
	/**
	 * Instantiates a new eX path constants.
	 */
	private EXPathConstants() {
		// no instantiations
	}

	/** The EXPATH namespace . */
	public static String EXPATH_NS = "http://expath.org/ns/pkg";

	/**
	 * The Class Elements.
	 */
	public static final class Elements {
		
		/** The DEPENDENCY. */
		public static QName DEPENDENCY = new QName(EXPATH_NS, "dependency");
		
		/** The FILE. */
		public static QName FILE = new QName(EXPATH_NS, "file");
		
		/** The IMPOR t_ uri. */
		public static QName IMPORT_URI = new QName(EXPATH_NS, "import-uri");
		
		/** The MODULE. */
		public static QName MODULE = new QName(EXPATH_NS, "module");
		
		/** The PACKAGE. */
		public static QName PACKAGE = new QName(EXPATH_NS, "package");
		
		/** The TITLE. */
		public static QName TITLE = new QName(EXPATH_NS, "title");
		
		/** The XPROC. */
		public static QName XPROC = new QName(EXPATH_NS, "xproc");
		
		/** The XSLT. */
		public static QName XSLT = new QName(EXPATH_NS, "xslt");
		
		/** The NG. */
		public static QName NG = new QName(EXPATH_NS, "ng");
		
		/** The XSD. */
		public static QName XSD = new QName(EXPATH_NS, "xsd");
		
		/** The RNC. */
		public static QName RNC = new QName(EXPATH_NS, "rnc");
		
		/** The XQUERY. */
		public static QName XQUERY = new QName(EXPATH_NS, "xquery");

		// TODO add other EXPath elements

		/**
		 * Instantiates a new elements.
		 */
		private Elements() {
			// no instantiations
		}
	}

	/**
	 * The Class Attributes.
	 */
	public static final class Attributes {
		
		/** The Constant HREF. */
		public static final QName HREF = new QName("href");
		
		/** The Constant NAME. */
		public static final QName NAME = new QName("name");
		
		/** The Constant VERSION. */
		public static final QName VERSION = new QName("version");
		
		/** The Constant VERSIONS. */
		public static final QName VERSIONS = new QName("versions");

		// TODO add other EXPath attributes

		/**
		 * Instantiates a new attributes.
		 */
		private Attributes() {
			// no instantiations
		}
	}
}
