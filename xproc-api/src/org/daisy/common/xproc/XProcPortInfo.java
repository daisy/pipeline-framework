package org.daisy.common.xproc;

public final class XProcPortInfo {

	public static enum Kind {
		INPUT, OUTPUT, PARAMETER
	};

	public static XProcPortInfo newInputPort(String name, boolean isSequence,
			boolean isPrimary) {
		return new XProcPortInfo(Kind.INPUT, name, isSequence, isPrimary);
	}

	public static XProcPortInfo newOutputPort(String name, boolean isSequence,
			boolean isPrimary) {
		return new XProcPortInfo(Kind.OUTPUT, name, isSequence, isPrimary);
	}

	public static XProcPortInfo newParameterPort(String name, boolean isPrimary) {
		return new XProcPortInfo(Kind.PARAMETER, name, true, isPrimary);
	}

	private final Kind kind;
	private final String name;
	private final boolean isPrimary;
	private final boolean isSequence;

	private XProcPortInfo(Kind kind, String name, boolean isSequence,
			boolean isPrimary) {
		this.kind = kind;
		this.name = name;
		this.isSequence = isSequence;
		this.isPrimary = isPrimary;
	}

	public Kind getKind() {
		return kind;
	}

	public String getName() {
		return name;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public boolean isSequence() {
		return isSequence;
	}

}
