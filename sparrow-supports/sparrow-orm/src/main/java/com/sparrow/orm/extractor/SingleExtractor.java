/**  
 * Project Name:http-server  
 * File Name:ResultExtractor.java  
 * Package Name:com.sparrow.orm.mapper
 * Date:2013-12-23上午8:55:36  
 * Copyright (c) 2013, Boco.com All Rights Reserved.  
 *  
 */

package com.sparrow.orm.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * result扯开器
 * 
 * @author YZC
 * @version 1.0 (2013-12-23)
 * @modify
 */
public interface SingleExtractor<T> {
	T singleExtract(ResultSet rs) throws SQLException;
}
