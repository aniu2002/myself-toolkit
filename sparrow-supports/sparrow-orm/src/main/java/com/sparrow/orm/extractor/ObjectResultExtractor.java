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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.utils.BeanUtils;
import com.sparrow.core.utils.CharUtils;
import com.sparrow.orm.util.JdbcUtil;


/**
 * 
 * bean的result的包装器
 * 
 * @author YZC
 * @version 1.0 (2013-12-23)
 * @modify
 */
public class ObjectResultExtractor extends AbstractResultExtractor<Object> {
	private Class<?> mappedClass;
	private Map<String, PropertyDescriptor> mappedFields;

	public ObjectResultExtractor() {
	}

	public ObjectResultExtractor(Class<?> mappedClass) {
		initialize(mappedClass);
	}

	protected void initialize(Class<?> mappedClass) {
		this.mappedClass = mappedClass;
		this.mappedFields = new HashMap<String, PropertyDescriptor>();
		PropertyDescriptor[] pds = PropertyUtils
				.getPropertyDescriptors(mappedClass);
		for (PropertyDescriptor pd : pds) {
			if (pd.getWriteMethod() != null) {
				this.mappedFields.put(pd.getName().toLowerCase(), pd);
				String underscoredName = underscoreName(pd.getName());
				if (!pd.getName().toLowerCase().equals(underscoredName)) {
					this.mappedFields.put(underscoredName, pd);
				}
			}
		}
	}

	protected Object mapRow(ResultSet rs) throws SQLException {
		Object mappedObject = BeanUtils.instantiate(this.mappedClass);
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();

		for (int index = 1; index <= columnCount; index++) {
			String column = JdbcUtil.lookupColumnName(rsmd, index);
			PropertyDescriptor pd = this.mappedFields.get(column.toLowerCase());
			if (pd != null) {
				Object value = getColumnValue(rs, index, pd);
				BeanUtils.setValue(pd, mappedObject, value);
			}
		}

		return mappedObject;
	}

	private String underscoreName(String name) {
		if (StringUtils.isEmpty(name)) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		char charArr[] = name.toCharArray();
		char ch = charArr[0];
		if (CharUtils.isUpperCase(ch))
			ch = CharUtils.toLowerCase(ch);
		result.append(ch);
		for (int i = 1; i < charArr.length; i++) {
			ch = charArr[i];
			if (CharUtils.isUpperCase(ch)) {
				result.append("_").append(CharUtils.toLowerCase(ch));
			} else {
				result.append(ch);
			}
		}
		return result.toString();
	}

	protected Object getColumnValue(ResultSet rs, int index,
			PropertyDescriptor pd) throws SQLException {
		return JdbcUtil.getResultSetValue(rs, index, pd.getPropertyType());
	}
}
