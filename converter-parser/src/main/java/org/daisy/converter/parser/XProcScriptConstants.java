package org.daisy.converter.parser;

import javax.xml.namespace.QName;

public final class XProcScriptConstants {

	private XProcScriptConstants() {
		// no instantiations
	}

	public static String CD_NS = "http://www.daisy.org/ns/pipeline/converter";
	public static String P_NS = "http://www.w3.org/ns/xproc";
	public static String PX_NS = "http://www.daisy.org/ns/pipeline/xproc";
	public static String XD_NS = "http://www.daisy.org/ns/pipeline/doc";

	public static final class Elements {

		public static QName P_DECLARE_STEP = new QName(P_NS, "declare-step");
		public static QName P_DOCUMENTATION = new QName(P_NS, "documentation");
		public static QName P_INPUT = new QName(P_NS, "input");
		public static QName P_OUTPUT = new QName(P_NS, "output");
		public static QName P_OPTION = new QName(P_NS, "option");
		public static QName P_PARAMS = new QName(P_NS, "params");

		public static QName XD_AUTHOR = new QName(XD_NS, "author");
		public static QName XD_DETAIL = new QName(XD_NS, "detail");
		public static QName XD_MAILTO = new QName(XD_NS, "mailto");
		public static QName XD_NAME = new QName(XD_NS, "name");
		public static QName XD_ORGANIZATION = new QName(XD_NS, "organization");
		public static QName XD_SHORT = new QName(XD_NS, "short");
		public static QName XD_HOMEPAGE = new QName(XD_NS, "homepage");

		private Elements() {
			// no instantiations
		}
	}

	public static final class Attributes {

		public static final QName KIND = new QName("kind");
		public static final QName NAME = new QName("name");
		public static final QName PORT = new QName("port");
		public static final QName PRIMARY = new QName("primary");
		public static final QName REQUIRED = new QName("required");
		public static final QName SELECT = new QName("select");
		public static final QName SEQUENCE = new QName("sequence");

		public static final QName PX_DIR = new QName(PX_NS, "dir");
		public static final QName PX_MEDIA_TYPE = new QName(PX_NS, "media-type");
		public static final QName PX_TYPE = new QName(PX_NS, "type");

		private Attributes() {
			// no instantiations
		}
	}

	public static final class Values {

		public static final String PARAMETER = "parameter";
		public static final String TRUE = "true";

		private Values() {
			// no instantiations
		}
	}
}
