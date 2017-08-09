package com.sparrow.orm.session;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by yuanzc on 2016/3/9.
 */
public interface StatementCallbackHandler {
    Integer processStatement(PreparedStatement ps) throws SQLException;
}
