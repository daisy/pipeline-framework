package org.daisy.expath.parser;

import javax.xml.namespace.QName;

public final class EXPathConstants {
	private EXPathConstants() {
		// no instantiations
	}

	public static String EXPATH_NS = "http://expath.org/ns/pkg";

	public static final class Elements {
		public static QName DEPENDENCY = new QName(EXPATH_NS, "dependency");
		public static QName FILE = new QName(EXPATH_NS, "file");
		public static QName IMPORT_URI = new QName(EXPATH_NS, "import-uri");
		public static QName MODULE = new QName(EXPATH_NS, "module");
		public static QName PACKAGE = new QName(EXPATH_NS, "package");
		public static QName TITLE = new QName(EXPATH_NS, "title");
		public static QName XPROC = new QName(EXPATH_NS, "xproc");
		public static QName XSLT = new QName(EXPATH_NS, "xslt");
		public static QName NG = new QName(EXPATH_NS, "ng");
		public static QName XSD = new QName(EXPATH_NS, "xsd");
		public static QName RNC = new QName(EXPATH_NS, "rnc");
		public static QName XQUERY = new QName(EXPATH_NS, "xquery");

		// TODO add other EXPath elements

		private Elements() {
			// no instantiations
		}
	}

	public static final class Attributes {
		public static final QName HREF = new QName("href");
		public static final QName NAME = new QName("name");
		public static final QName VERSION = new QName("version");
		public static final QName VERSIONS = new QName("versions");

		// TODO add other EXPath attributes

		private Attributes() {
			// no instantiations
		}
	}
}
