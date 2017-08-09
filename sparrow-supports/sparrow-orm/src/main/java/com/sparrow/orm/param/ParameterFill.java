package com.sparrow.orm.param;

import com.sparrow.orm.meta.MappingField;

/**
 * 
 * ParameterFill 根据每个mapField信息设置参数填充信息<br/>
 * 比如：<br/>
 * insert into test(ID,NAME) values(?,?) <br/>
 * insert into test(ID,NAME) values(:id,:name)
 * 
 * @author YZC (2013-10-10-下午3:42:55)
 */
public interface ParameterFill {
	/**
	 * mapFiled 设置参数填充字符 , 比如: ? 或者 :id ，或者就是 sequence.next
	 * 
	 * @param mapField
	 * @return
	 */
	public String fillChar(MappingField mapField);

}
