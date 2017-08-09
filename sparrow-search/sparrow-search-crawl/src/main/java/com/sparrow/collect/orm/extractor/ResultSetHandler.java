package com.sparrow.collect.orm.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2017/8/8 0008.
 */
public interface ResultSetHandler {
    void handle(ResultSet rs) throws SQLException;
}
