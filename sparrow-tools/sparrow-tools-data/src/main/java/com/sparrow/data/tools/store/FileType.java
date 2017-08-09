package com.sparrow.data.tools.store;

/**
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
public enum FileType {
    Excel(".xls", 0), Excel2003(".xls", 1), Excel2007(".xlsx", 2), Csv(".csv",
            3), Xml(".xml", 4);
    // 定义私有变量
    private String type;
    private int value;

    // 构造函数，枚举类型只能为私有
    FileType(String type, int value) {
        this.value = value;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public static FileType getFileType(String s) {
        if ("xls".equalsIgnoreCase(s))
            return Excel;
        else if ("csv".equalsIgnoreCase(s))
            return Csv;
        else if ("xlsx".equalsIgnoreCase(s))
            return Excel2007;
        else if ("xml".equalsIgnoreCase(s))
            return Xml;
        else if ("xls2003".equalsIgnoreCase(s))
            return Excel2003;
        else
            return Excel;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
