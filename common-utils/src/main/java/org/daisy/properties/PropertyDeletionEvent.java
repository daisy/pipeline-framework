package org.daisy.properties;

class PropertyDeletionEvent {
	private Property prop;

	/**
	 * Constructs a new instance.
	 *
	 * @param prop The prop for this instance.
	 */
	public PropertyDeletionEvent(Property prop) {
		this.prop = prop;
	}

	/**
	 * Gets the prop for this instance.
	 *
	 * @return The prop.
	 */
	public Property getProperty() {
		return this.prop;
	}
}
