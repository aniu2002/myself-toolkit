package com.sparrow.orm.session;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by yuanzc on 2016/3/9.
 */
public interface RowCallbackHandler {
    void processRow(ResultSet rs) throws SQLException;
}
