package com.sparrow.orm.dyna.parser;

public class NamedParameter {
	final String name;
	final int index;
	final boolean indexPara;
	int paraIndex = -1;

	public NamedParameter(String name, int index, boolean indexPara) {
		this.name = name;
		this.index = index;
		this.indexPara = indexPara;
		if (indexPara)
			this.paraIndex = Integer.parseInt(name);
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public int getParaIndex() {
		return paraIndex;
	}

	public boolean isIndexPara() {
		return indexPara;
	}
}
