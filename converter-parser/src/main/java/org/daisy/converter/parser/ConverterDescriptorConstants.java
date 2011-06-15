package org.daisy.converter.parser;

import javax.xml.namespace.QName;

// TODO: Auto-generated Javadoc
/**
 * The Class ConverterDescriptorConstants.
 */
public final class ConverterDescriptorConstants {
	
	/**
	 * Instantiates a new converter descriptor constants.
	 */
	private ConverterDescriptorConstants() {
		// no instantiations
	}

	/** The C d_ ns. */
	public static String CD_NS = "http://www.daisy.org/ns/pipeline/converter";

	/**
	 * The Class Elements.
	 */
	public static final class Elements {
		
		/** The CONVERTER. */
		public static QName CONVERTER = new QName(CD_NS, "converter");
		
		/** The DESC. */
		public static QName DESC = new QName(CD_NS, "description");
		
		/** The ARG. */
		public static QName ARG = new QName(CD_NS, "arg");

	
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
		
		/** The Constant NAME. */
		public static final QName NAME = new QName("name");
		
		/** The Constant VERSION. */
		public static final QName VERSION = new QName("version");
		
		/** The Constant TYPE. */
		public static final QName TYPE = new QName("type");
		
		/** The Constant BIND. */
		public static final QName BIND = new QName("bind");
		
		/** The Constant PORT. */
		public static final QName PORT = new QName("port");
		
		/** The Constant DESC. */
		public static final QName DESC = new QName("desc");
		
		/** The Constant OPTIONAL. */
		public static final QName OPTIONAL = new QName("optional");

		

		/**
		 * Instantiates a new attributes.
		 */
		private Attributes() {
			// no instantiations
		}
	}
}
