package com.sparrow.data.service.imports.validate;

import com.sparrow.data.tools.validate.ValidateError;

public class ExtValidateError extends ValidateError {
	private String barcode;

	public ExtValidateError(String rowId, String name, String value,
			String error, int sheet, int row, int column) {
		super(rowId, name, value, error, sheet, row, column);
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

}
