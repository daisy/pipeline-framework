package org.daisy.converter.parser;

import javax.xml.namespace.QName;

// TODO: Auto-generated Javadoc
/**
 * The Class ConverterDescriptorConstants.
 */
public final class XProcScriptConstants {
	
	/**
	 * Instantiates a new converter descriptor constants.
	 */
	private XProcScriptConstants() {
		// no instantiations
	}

	/** The C d_ ns. */
	public static String CD_NS = "http://www.daisy.org/ns/pipeline/converter";
	public static String PX_NS = "http://www.daisy.org/ns/pipeline/xproc";
	public static String XD_NS = "http://www.daisy.org/ns/pipeline/doc";
	public static String P_NS = "http://www.w3.org/ns/xproc";
	
	/**
	 * The Class Elements.
	 */
	public static final class Elements {
		
		public static QName P_DECLARE_STEP=new QName(P_NS,"declare-step");
		public static QName P_INPUT=new QName(P_NS,"input");
		public static QName P_OUTPUT=new QName(P_NS,"output");
		public static QName P_OPTION=new QName(P_NS,"option");
		public static QName P_PARAMS=new QName(P_NS,"params");
		public static QName P_DOCUMENTATION=new QName(P_NS,"documentation");
		
		public static QName XD_SHORT=new QName(XD_NS,"short");
		public static QName XD_DETAIL=new QName(XD_NS,"detail");
		public static QName XD_AUTHOR=new QName(XD_NS,"author");
		public static QName XD_NAME=new QName(XD_NS,"name");
		public static QName XD_MAILTO=new QName(XD_NS,"mailto");
		public static QName XD_ORGANIZATION=new QName(XD_NS,"organization");
				
		
		
		
		
		
		
			
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
		public static QName PX_MEDIA_TYPE = new QName(PX_NS, "media-type");
		public static QName PX_DIR = new QName(PX_NS, "dir");
		public static QName PX_TYPE = new QName(PX_NS, "type");
		
		/** The Constant NAME. */
		public static final QName NAME = new QName("name");
		

		/**
		 * Instantiates a new attributes.
		 */
		private Attributes() {
			// no instantiations
		}
	}
}
