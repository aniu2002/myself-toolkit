package com.sparrow.data.tools.validate;

public class ValidateError {
    private String rowId;
    private String name;
    private String value;
    private String error;
    private int sheet;
    private int row;
    private int column;

    public ValidateError() {

    }

    public ValidateError(String rowId, String name, String value, String error,
                         int sheet, int row, int column) {
        this.rowId = rowId;
        this.name = name;
        this.value = value;
        this.sheet = sheet;
        this.row = row;
        this.column = column;
        this.error = error;
    }

    public void errorSet(String rowId, String name, String value, String error,
                         int sheet, int row, int column) {
        this.rowId = rowId;
        this.name = name;
        this.value = value;
        this.sheet = sheet;
        this.row = row;
        this.column = column;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSheet() {
        return sheet;
    }

    public void setSheet(int sheet) {
        this.sheet = sheet;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }
}
