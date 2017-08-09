package com.sparrow.orm.param;

import com.sparrow.orm.meta.MappingField;

/**
 * NameParameterFill sql 参数填充格式为 ':id'
 * 
 * @author YZC (2013-10-10-下午3:46:47)
 */
public class NameParameterFill implements ParameterFill {

	@Override
	public String fillChar(MappingField mapField) {
		return ":" + mapField.getField();
	}
}
