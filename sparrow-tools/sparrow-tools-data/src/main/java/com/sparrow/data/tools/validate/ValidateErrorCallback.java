package com.sparrow.data.tools.validate;

public interface ValidateErrorCallback {
	ValidateError createValidateError(String[] rowData, String name,
			String value, String error, int sheet, int row, int column);

	boolean hasError();

	int getErrors();

	void error(String label, String value, Object extra);

	Object getResult();
}
