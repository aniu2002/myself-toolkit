package com.sparrow.orm.metadata.obj;

public class TbColumn {
	public String name;
	public int sqlType;
	public int sqlColumnLength;
	public int sqlDecimalLength;
	public boolean sqlNotNull;
	public boolean sqlReadOnly;
	// public NullableType hibernateType;
	public Class<?> javaType;

	public boolean equals(Object o) {
		boolean rv = false;
		if (o != null && o instanceof TbColumn) {
			rv = (name.equals(((TbColumn) o).name));
		}

		return rv;
	}

	public int hashCode() {
		int returnint = (name != null) ? name.hashCode() : 0;
		return returnint;
	}
}
