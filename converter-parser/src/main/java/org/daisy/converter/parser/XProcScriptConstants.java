package org.daisy.converter.parser;

import javax.xml.namespace.QName;


/**
 * Constants used when parsing XProcScripts
 */
public final class XProcScriptConstants {

	
	private XProcScriptConstants() {
		// no instantiations
	}

	/**  CD  namespace. */
	public static String CD_NS = "http://www.daisy.org/ns/pipeline/converter";
	
	/** P namespace. */
	public static String P_NS = "http://www.w3.org/ns/xproc";
	
	/** PX namespace. */
	public static String PX_NS = "http://www.daisy.org/ns/pipeline/xproc";
	
	/** XD namespace. */
	public static String XD_NS = "http://www.daisy.org/ns/pipeline/doc";

	/**
	 * Elements.
	 */
	public static final class Elements {

		/** declare step. */
		public static QName P_DECLARE_STEP = new QName(P_NS, "declare-step");
		
		/** documentation. */
		public static QName P_DOCUMENTATION = new QName(P_NS, "documentation");
		
		/**  input. */
		public static QName P_INPUT = new QName(P_NS, "input");
		
		/**  output. */
		public static QName P_OUTPUT = new QName(P_NS, "output");
		
		/**  option. */
		public static QName P_OPTION = new QName(P_NS, "option");
		
		/**  params. */
		public static QName P_PARAMS = new QName(P_NS, "params");

		/**  author. */
		public static QName XD_AUTHOR = new QName(XD_NS, "author");
		
		/** detail. */
		public static QName XD_DETAIL = new QName(XD_NS, "detail");
		
		/** mailto. */
		public static QName XD_MAILTO = new QName(XD_NS, "mailto");
		
		/**  name. */
		public static QName XD_NAME = new QName(XD_NS, "name");
		
		/**  organization. */
		public static QName XD_ORGANIZATION = new QName(XD_NS, "organization");
		
		/**  short. */
		public static QName XD_SHORT = new QName(XD_NS, "short");
		
		/** homepage. */
		public static QName XD_HOMEPAGE = new QName(XD_NS, "homepage");

		/**
		 * Instantiates a new elements.
		 */
		private Elements() {
			// no instantiations
		}
	}

	/**
	 *  Attributes.
	 */
	public static final class Attributes {

		/** The Constant KIND. */
		public static final QName KIND = new QName("kind");
		
		/** The Constant NAME. */
		public static final QName NAME = new QName("name");
		
		/** The Constant PORT. */
		public static final QName PORT = new QName("port");
		
		/** The Constant PRIMARY. */
		public static final QName PRIMARY = new QName("primary");
		
		/** The Constant REQUIRED. */
		public static final QName REQUIRED = new QName("required");
		
		/** The Constant SELECT. */
		public static final QName SELECT = new QName("select");
		
		/** The Constant SEQUENCE. */
		public static final QName SEQUENCE = new QName("sequence");

		/** The Constant PX_DIR. */
		public static final QName PX_DIR = new QName(PX_NS, "dir");
		
		/** The Constant PX_MEDIA_TYPE. */
		public static final QName PX_MEDIA_TYPE = new QName(PX_NS, "media-type");
		
		/** The Constant PX_TYPE. */
		public static final QName PX_TYPE = new QName(PX_NS, "type");

		/**
		 * Instantiates a new attributes.
		 */
		private Attributes() {
			// no instantiations
		}
	}

	/**
	 * Values.
	 */
	public static final class Values {

		/** The Constant PARAMETER. */
		public static final String PARAMETER = "parameter";
		
		/** The Constant TRUE. */
		public static final String TRUE = "true";

		/**
		 * Instantiates a new values.
		 */
		private Values() {
			// no instantiations
		}
	}
}
