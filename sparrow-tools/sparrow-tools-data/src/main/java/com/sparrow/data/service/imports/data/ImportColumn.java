package com.sparrow.data.service.imports.data;

import com.sparrow.data.tools.validate.ValidateHandler;

public class ImportColumn {
	private String name;
	private String label;
	private String validator;
	private int type;
	private int dataIndex;
	private boolean gloabParam;
    private boolean format;
	private ValidateHandler validateHandler;

    public boolean isFormat() {
        return format;
    }

    public void setFormat(boolean format) {
        this.format = format;
    }

    public int getDataIndex() {
		return dataIndex;
	}

	public void setDataIndex(int dataIndex) {
		this.dataIndex = dataIndex;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getValidator() {
		return validator;
	}

	public void setValidator(String validator) {
		this.validator = validator;
	}

	public ValidateHandler getValidateHandler() {
		return validateHandler;
	}

	public void setValidateHandler(ValidateHandler validateHandler) {
		this.validateHandler = validateHandler;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public boolean isGloabParam() {
		return gloabParam;
	}

	public void setGloabParam(boolean gloabParam) {
		this.gloabParam = gloabParam;
	}

	public void setType(int type) {
		this.type = type;
	}
}
