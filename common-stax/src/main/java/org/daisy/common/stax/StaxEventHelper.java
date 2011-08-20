package org.daisy.common.stax;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.google.common.base.Predicate;

public final class StaxEventHelper {

	public static class EventPredicates {

		public static Predicate<XMLEvent> isElement(final QName name) {
			return new Predicate<XMLEvent>() {
				public boolean apply(XMLEvent event) {
					return event.isStartElement()
							&& event.asStartElement().getName().equals(name);
				}
			};
		}

		public static Predicate<XMLEvent> isStartOrStopElement(final QName name) {
			return new Predicate<XMLEvent>() {
				public boolean apply(XMLEvent event) {
					return (event.isStartElement() && event.asStartElement()
							.getName().equals(name))
							|| (event.isEndElement() && event.asEndElement()
									.getName().equals(name));
				}
			};
		}

		public static Predicate<XMLEvent> IS_START_ELEMENT = new Predicate<XMLEvent>() {
			public boolean apply(XMLEvent event) {
				return event.isStartElement();
			}
		};

		public static Predicate<XMLEvent> IS_END_ELEMENT = new Predicate<XMLEvent>() {
			public boolean apply(XMLEvent event) {
				return event.isEndElement();
			}
		};

		public static class ChildOrSiblingPredicate implements
				Predicate<XMLEvent> {
			private int opened = 1;

			public boolean apply(XMLEvent event) {
				switch (event.getEventType()) {
				case XMLEvent.START_ELEMENT:
					opened++;
					break;
				case XMLEvent.END_ELEMENT:
					opened--;
					break;
				default:
					break;
				}
				return opened > 0;
			}
		};

		public static Predicate<XMLEvent> getChildOrSiblingPredicate() {
			return new ChildOrSiblingPredicate();
		}

		public static class ChildPredicate implements Predicate<XMLEvent> {
			private int opened = 0;

			public boolean apply(XMLEvent event) {

				switch (event.getEventType()) {
				case XMLEvent.START_ELEMENT:
					opened++;
					break;
				case XMLEvent.END_ELEMENT:
					opened--;
					break;
				default:
					break;
				}

				return opened > 0;
			}

		}

		public static Predicate<XMLEvent> isChildPredicate() {
			return new ChildPredicate();
		}

	}

	public static StartElement peekNextElement(XMLEventReader reader, QName name)
			throws XMLStreamException {
		while (reader.hasNext()) {
			XMLEvent event = reader.peek();
			if (event.isStartElement()
					&& event.asStartElement().getName().equals(name)) {
				return event.asStartElement();
			}
			reader.next();
		}
		throw new IllegalStateException("Element " + name + " not found");
	}

	public static StartElement peekNextElement(XMLEventReader reader,
			Set<QName> names) throws XMLStreamException {

		while (reader.hasNext()) {
			XMLEvent event = reader.peek();
			if (event.isStartElement()
					&& names.contains(event.asStartElement().getName())) {
				return event.asStartElement();
			}
			reader.next();
		}
		throw new IllegalStateException("Element  not found");
	}

	public static synchronized void loop(XMLEventReader reader,
			Predicate<XMLEvent> filter, Predicate<XMLEvent> checker,
			EventProcessor processor) throws XMLStreamException {
		while (reader.hasNext()) {
			XMLEvent event = reader.peek();
			if (filter.apply(event)) {
				if (!checker.apply(event)) {
					break;
				}
				processor.process(event);
			}
			if (reader.hasNext())// nested loops
				reader.next();
		}
	}

	private StaxEventHelper() {
		// no instantiation
	}

}
