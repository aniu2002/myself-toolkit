package com.sparrow.data.tools.imports.extract;

import java.sql.SQLException;

import com.sparrow.data.tools.imports.ImpSetting;

public interface DataExtractor {

	void setImpSetting(ImpSetting impSetting);

	void extract() throws SQLException;

	void setExtractCallback(ExtractCallback callback);
}
