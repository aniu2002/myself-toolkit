package com.sparrow.data.tools.validate;

public class ValidateHandler {
    final Validator validator;
    ValidateErrorCallback validateCallback;

    public ValidateHandler(Validator validator) {
        this(validator, null);
    }

    public ValidateHandler(Validator validator,
                           ValidateErrorCallback validateCallback) {
        this.validator = validator;
        this.validateCallback = validateCallback;
    }

    public boolean check(String str) {
        return (this.validator == null ? false : this.validator.check(str));
    }

    public boolean skip() {
        return (this.validator == null ? false : this.validator.skip());
    }

    public void error(String str, String label, Object error) {
        this.validateCallback.error(label, str, error);
    }

    public String getDescription() {
        return this.validator.getDescription();
    }

    public ValidateError createValidateError(String[] rowData, String name,
                                             String value, String error, int sheet, int row, int column) {
        return this.validateCallback.createValidateError(rowData, name, value,
                error, sheet, row, column);
    }
}
