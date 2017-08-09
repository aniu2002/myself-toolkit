package com.sparrow.orm.param;

import com.sparrow.orm.meta.MappingField;

/**
 * DefaultParameterFill 参数填充格式为 '?'
 * 
 * @author YZC (2013-10-10-下午3:46:54)
 */
public class DefaultParameterFill implements ParameterFill {
	static final String FILL_CHAR = "?";

	@Override
	public String fillChar(MappingField mapField) {
		return FILL_CHAR;
	}
}
