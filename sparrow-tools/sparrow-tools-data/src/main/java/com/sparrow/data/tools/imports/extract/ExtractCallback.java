package com.sparrow.data.tools.imports.extract;

import java.sql.SQLException;

public interface ExtractCallback {
	void handle(String data[], int sheet, int row) throws SQLException;
}
