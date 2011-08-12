package org.daisy.common.xproc.calabash;

import java.util.EnumSet;

import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.Serializer.Property;

import com.google.common.base.Function;
import com.xmlcalabash.core.XProcConfiguration;
import com.xmlcalabash.model.Serialization;

public class SerializationUtils {

	private static enum SerializationOptions {
		BYTE_ORDER_MARK(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return Boolean.toString(serial.getByteOrderMark());
			}
		}, true), DOCTYPE_PUBLIC(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return serial.getDoctypePublic();
			}
		}, false), DOCTYPE_SYSTEM(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return serial.getDoctypeSystem();
			}
		}, false), ENCODING(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return serial.getEncoding();
			}
		}, false), ESCAPE_URI_ATTRIBUTES(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return Boolean.toString(serial.getEscapeURIAttributes());
			}
		}, true), INCLUDE_CONTENT_TYPE(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return Boolean.toString(serial.getIncludeContentType());
			}
		}, true), INDENT(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return Boolean.toString(serial.getIndent());
			}
		}, true), MEDIA_TYPE(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return serial.getMediaType();
			}
		}, false), METHOD(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return serial.getMethod().getLocalName();
			}
		}, false), NORMALIZATION_FORM(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return serial.getNormalizationForm();
			}
		}, false), OMIT_XML_DECLARATION(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return Boolean.toString(serial.getOmitXMLDeclaration());
			}
		}, true), STANDALONE(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return serial.getStandalone();
			}
		}, true), UNDECLARE_PREFIXES(new Function<Serialization, String>() {

			@Override
			public String apply(Serialization serial) {
				return Boolean.toString(serial.getUndeclarePrefixes());
			}
		}, true);

		private final boolean isBoolean;
		private final Function<Serialization, String> fromSerialization;

		private SerializationOptions(
				Function<Serialization, String> fromSerialization,
				boolean isBoolean) {
			this.isBoolean = isBoolean;
			this.fromSerialization = fromSerialization;
		};

		public Property asSaxonProp() {
			return Property.valueOf(name());
		}

		public String getValue(Serialization serial, XProcConfiguration config) {
			String value = (serial != null) ? fromSerialization.apply(serial)
					: config.serializationOptions.get(asSaxonProp().getQName()
							.getLocalName());
			if (isBoolean) {
				return Boolean.valueOf(value) ? "yes" : "no";
			} else {
				return value;
			}
		}
	}

	public static Serializer newSerializer(Serialization serialization,
			XProcConfiguration config) {
		Serializer serializer = new Serializer();
		for (SerializationOptions so : EnumSet
				.allOf(SerializationOptions.class)) {
			serializer.setOutputProperty(so.asSaxonProp(),
					so.getValue(serialization, config));
		}
		return serializer;
	}
}
