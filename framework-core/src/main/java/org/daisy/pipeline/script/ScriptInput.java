package org.daisy.pipeline.script;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Source;

import org.daisy.common.transform.LazySaxSourceProvider;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ScriptInput {

	public static class Builder {

		private final Map<String,SourceSequence> inputs = Maps.newHashMap();
		private final Map<String,List<String>> options = Maps.newHashMap();

		/**
		 * Put a single document on the specified input port. All documents that are put on a port
		 * form a sequence.
		 */
		public Builder withInput(String port, Source source) {
			getSequence(port).add(source);
			return this;
		}

		/**
		 * Put a single document on the specified input port. All documents that are put on a port
		 * form a sequence.
		 */
		public Builder withInput(String port, URI source) {
			return withInput(port, new LazySaxSourceProvider(source.toASCIIString()));
		}

		/**
		 * Put a single document on the specified input port. All documents that are put on a port
		 * form a sequence.
		 *
		 * The {@link Supplier} serves as a proxy and must always return the same object.
		 */
		private Builder withInput(String port, Supplier<Source> source) {
			getSequence(port).add(source);
			return this;
		}

		private SourceSequence getSequence(String port) {
			SourceSequence sources = inputs.get(port);
			if (sources == null) {
				sources = new SourceSequence();
				inputs.put(port, sources);
			}
			return sources;
		}

		/**
		 * Set a single value for an option. All values that are set on an option form a sequence.
		 */
		public Builder withOption(String name, String value) {
			if (options.containsKey(name)) {
				options.get(name).add(value);
			} else {
				List<String> values = Lists.newLinkedList();
				values.add(value);
				options.put(name, values);
			}
			return this;
		}

		/**
		 * Build the {@link ScriptInput}
		 */
		public ScriptInput build() {
			return new ScriptInput(inputs, options);
		}
	}

	private final Map<String,SourceSequence> inputs;
	private final Map<String,List<String>> options;
	private final static List<Source> emptySources = ImmutableList.of();
	private final static List<String> emptyValues = ImmutableList.of();

	private ScriptInput(Map<String,SourceSequence> inputs, Map<String,List<String>> options) {
		this.inputs = inputs;
		this.options = options;
	}

	/**
	 * Get all documents on an input port.
	 *
	 * The returned {@link Source} should only be consumed once.
	 */
	public Iterable<Source> getInput(String port) {
		return inputs.containsKey(port)
			? ImmutableList.copyOf(inputs.get(port))
			: emptySources;
	}

	/**
	 * Get the sequence of values for an option.
	 */
	public Iterable<String> getOption(String name) {
		return options.containsKey(name)
			? ImmutableList.copyOf(options.get(name))
			: emptyValues;
	}

	private static class SourceSequence implements Iterable<Source> {

		private Iterable<Source> iterable;
		private List<Source> sources;
		private List<Supplier<Source>> suppliers;
		private boolean locked = false;

		public void add(Source source) {
			if (locked) throw new UnsupportedOperationException("already iterated");
			if (suppliers != null) {
				iterable = concat(iterable, Iterables.transform(suppliers, Supplier::get));
				suppliers = null;
			}
			if (sources == null)
				sources = new ArrayList<>();
			sources.add(source);
		}

		public void add(Supplier<Source> source) {
			if (locked) throw new UnsupportedOperationException("already iterated");
			if (sources != null) {
				iterable = concat(iterable, sources);
				sources = null;
			}
			if (suppliers == null)
				suppliers = new ArrayList<>();
			suppliers.add(source);
		}

		@Override
		public Iterator<Source> iterator() {
			if (sources != null) {
				iterable = concat(iterable, sources);
				sources = null;
			} else if (suppliers != null) {
				iterable = concat(iterable, Iterables.transform(suppliers, Supplier::get));
				suppliers = null;
			}
			if (iterable == null)
				iterable = Collections.emptyList();
			locked = true;
			return iterable.iterator();
		}

		private static <T> Iterable<T> concat(Iterable<T> head, Iterable<T> tail) {
			if (head == null)
				return tail;
			else if (tail == null)
				return head;
			else
				return Iterables.concat(head, tail);
		}
	}
}
