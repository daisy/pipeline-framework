package org.daisy.converter.parser;

import javax.xml.namespace.QName;

public final class ConverterDescriptorConstants {
	private ConverterDescriptorConstants() {
		// no instantiations
	}

	public static String CD_NS = "http://www.daisy.org/daisypipeline/converter_descriptor";

	public static final class Elements {
		public static QName CONVERTER = new QName(CD_NS, "converter");
		public static QName DESC = new QName(CD_NS, "description");
		public static QName ARG = new QName(CD_NS, "arg");

	
		private Elements() {
			// no instantiations
		}
	}

	public static final class Attributes {
		public static final QName NAME = new QName("name");
		public static final QName VERSION = new QName("version");
		public static final QName TYPE = new QName("type");
		public static final QName BIND = new QName("bind");
		public static final QName PORT = new QName("port");
		public static final QName DESC = new QName("desc");
		public static final QName OPTIONAL = new QName("optional");

		

		private Attributes() {
			// no instantiations
		}
	}
}
