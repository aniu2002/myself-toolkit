/**
 * Project Name:http-server  
 * File Name:BeanResultExtractor.java  
 * Package Name:com.sparrow.orm.mapper
 * Date:2013-12-23上午9:02:27  
 *
 */

package com.sparrow.collect.orm.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * bean的result的包装器
 *
 * @author YZC
 * @version 1.0 (2013-12-23)
 * @modify
 */
public abstract class AbstractResultExtractor<T> implements ResultExtractor<T>,
        SingleExtractor<T> {

    public final List<T> extract(ResultSet rs, final int maxRows)
            throws SQLException {
        List<T> results = (maxRows > 0 ? new ArrayList<T>(maxRows)
                : new ArrayList<T>());
        int rowNum = 0;
        while (rs.next() && rowNum < maxRows) {
            results.add(this.mapRow(rs));
            rowNum++;
        }
        return results;
    }

    @Override
    public final T singleExtract(ResultSet rs) throws SQLException {
        if (rs.next())
            return this.mapRow(rs);
        else
            return null;
    }

    protected abstract T mapRow(ResultSet rs) throws SQLException;
}
