package com.sparrow.tools.pogen;

import com.sparrow.tools.pogen.generator.IdGeneratorDefine;

public interface IDAliasGenerator {
	public final String DEFAULT_ALIAS = "auto";

	public IdGeneratorDefine getAlias(String table, String column, int type);
}
