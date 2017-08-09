package com.sparrow.data.service.imports.validate;

import java.util.ArrayList;
import java.util.List;

import com.sparrow.data.tools.validate.ValidateError;
import com.sparrow.data.tools.validate.ValidateErrorCallback;

public class ImportValidateErrorCallback implements ValidateErrorCallback {
	private List<ValidateError> list;
	private int maxErrorSize;
	private int items;

	public ImportValidateErrorCallback() {
		this(200);
	}

	public ImportValidateErrorCallback(int maxErrorSize) {
		this.maxErrorSize = maxErrorSize;
	}

	@Override
	public void error(String label, String value, Object extra) {
		if (extra == null)
			return;
		if (extra instanceof ValidateError) {
			this.addValidateError((ValidateError) extra);
		}
	}

	void addValidateError(ValidateError error) {
		if (this.list == null)
			this.list = new ArrayList<ValidateError>();
		if (this.items >= this.maxErrorSize)
			return;
		this.list.add(error);
		this.items++;
	}

	@Override
	public boolean hasError() {
		return (this.list != null) && (!this.list.isEmpty());
	}

	@Override
	public Object getResult() {
		return this.list;
	}

	@Override
	public int getErrors() {
		return this.items;
	}

	@Override
	public ValidateError createValidateError(String[] rowData,
			String columnLabel, String value, String error, int sheet, int row,
			int column) {
		return new ValidateError(rowData[0], columnLabel, value, error, sheet,
				row, column);
	}

}
