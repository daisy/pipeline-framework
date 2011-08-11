package org.daisy.common.xproc;

import javax.xml.namespace.QName;

public final class XProcOptionInfo {

	public static XProcOptionInfo newOption(QName name, boolean isRequired,
			String select) {
		return new XProcOptionInfo(name, isRequired, select);
	}

	private final QName name;
	private final boolean isRequired;
	private final String select;

	public XProcOptionInfo(QName name, boolean isRequired, String select) {
		this.name = name;
		this.isRequired = isRequired;
		this.select = select;
	}

	public QName getName() {
		return name;
	}

	public boolean isRequired() {
		return isRequired;
	}

	public String getSelect() {
		return select;
	}
}
