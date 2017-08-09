package com.sparrow.data.service.imports.data;

/**
 * 
 * 记录导入导出字段映射信息（包括属性名以及列的下标索引）
 * 
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
public class ImportTemplateItem {
	/** 属性名字段名 */
	private String name;
	/** 标注信息 */
	private String label;
	/** 插入的字段类型 */
	private DataType type;
	/** 字段列的索引关系 */
	private int index;
	/** 校验handler名 */
	private String validate;
    /** 校验handler名 */
    private String render;
    private boolean format;

    public String getRender() {
        return render;
    }

    public void setRender(String render) {
        this.render = render;
    }

    public boolean isFormat() {
        return format;
    }

    public void setFormat(boolean format) {
        this.format = format;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public DataType getType() {
		return type;
	}

	public void setType(DataType type) {
		this.type = type;
	}

	public String getValidate() {
		return validate;
	}

	public void setValidate(String vadidate) {
		this.validate = vadidate;
	}
}
