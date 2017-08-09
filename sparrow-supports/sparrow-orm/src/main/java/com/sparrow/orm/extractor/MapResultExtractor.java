/**  
 * Project Name:http-server  
 * File Name:BeanResultExtractor.java  
 * Package Name:com.sparrow.orm.mapper
 * Date:2013-12-23上午9:02:27  
 *  
 */

package com.sparrow.orm.extractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * bean的result的包装器
 * 
 * @author YZC
 * @version 1.0 (2013-12-23)
 * @modify
 */
public class MapResultExtractor extends
		AbstractResultExtractor<Map<String, Object>> {

	protected Map<String, Object> mapRow(ResultSet rs) throws SQLException {
		Map<String, Object> mappedObject = new HashMap<String, Object>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols = rsmd.getColumnCount();
		for (int i = 1; i <= cols; i++) {
			mappedObject.put(rsmd.getColumnName(i), rs.getObject(i));
		}
		return mappedObject;
	}
}
