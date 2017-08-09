/**  
 * Project Name:http-server  
 * File Name:BeanResultExtractor.java  
 * Package Name:com.sparrow.orm.mapper
 * Date:2013-12-23上午9:02:27  
 *  
 */

package com.sparrow.orm.extractor;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sparrow.core.utils.BeanUtils;
import com.sparrow.orm.meta.MappingField;
import com.sparrow.orm.meta.MappingFieldsWrap;
import com.sparrow.orm.util.JdbcUtil;


/**
 * 
 * bean的result的包装器
 * 
 * @author YZC
 * @version 1.0 (2013-12-23)
 * @modify
 */
public class NormalResultExtractor extends AbstractResultExtractor<Object> {
	private final Class<?> mappedClass;
	private final MappingField[] columnFields;

	public NormalResultExtractor(Class<?> clazz,
			MappingFieldsWrap mappingFieldsWrap) {
		this.mappedClass = clazz;
		this.columnFields = mappingFieldsWrap.getFields();
	}

	protected Object mapRow(ResultSet rs) throws SQLException {
		MappingField[] columnFields = this.columnFields;
		Object mappedObject = BeanUtils.instantiate(this.mappedClass);
		int len = columnFields.length;

		for (int index = 0; index < len; index++) {
			MappingField cf = columnFields[index];
			PropertyDescriptor pd = cf.getProp();
			if (pd != null) {
				Object value = JdbcUtil.getJavaObject(rs, index + 1,
						cf.getJavaType());
				BeanUtils.setValue(pd, mappedObject, value);
			}
		}
		return mappedObject;
	}

}
