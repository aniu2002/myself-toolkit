package com.sparrow.data.service.imports.data;

import java.util.Map;

/**
 * excel:导入导出模板 <br/>
 * name:模板名称，方便关联使用的模板<br/>
 * sql:批量导入excel的模板sql ， 如： insert into table(id,name) values(?,?);
 *
 * @author YZC
 * @version 1.0 (2014-3-17)
 * @modify
 */
public class ImportTemplate {
    /**
     * 导入sql参数中每个字段列的映射信息
     */
    private Map<String, ImportTemplateItem> paraItemMap;
    /**
     * 导出时数据属性对应每行列下标索引的映射信息
     */
    private Map<String, ImportTemplateItem> expParaItemMap;
    /**
     * 导入模板名
     */
    private String name;
    /**
     * 导入模板名
     */
    private String label;
    /**
     * 批量导入sql
     */
    private String sql;
    /**
     * 批量导出sql
     */
    private String exportSql;
    /**
     * 导入导出时数据填充从哪个电子薄开始
     */
    private int startSheet;
    /**
     * 导入导出时数据填充从哪行开始
     */
    private int startRow;
    /**
     * 导入导出时数据填充从哪列开始
     */
    private int startCol;
    /**
     * 导入导出时列数据数限制
     */
    private int limit;
    /**
     * 导出默认单个文件的总数据数， 默认是65535
     */
    private int exportMax;
    /**
     * 导出默认每个sheet的数据数 ，该值大于max的值 多余的无效，默认是65535,超过该值设定小于总数的话，多余的数据插入到 下一个sheet中
     */
    private int sheetRows;
    /**
     * 记录导出列中最大的映射索引，以便在limit为0的情况下，设置导出每行数组大小的参考值
     */
    private int expMaxIdx;
    /**
     * 是否是导入
     */
    private String imp;
    /**
     * excel列头信息
     */
    private String headers[];

    public String getImp() {
        return imp;
    }

    public void setImp(String imp) {
        this.imp = imp;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getExpMaxIdx() {
        return expMaxIdx;
    }

    public void setExpMaxIdx(int expMaxIdx) {
        this.expMaxIdx = expMaxIdx;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public Map<String, ImportTemplateItem> getParaItemMap() {
        return paraItemMap;
    }

    public void setParaItemMap(Map<String, ImportTemplateItem> paraItemMap) {
        this.paraItemMap = paraItemMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartSheet() {
        return startSheet;
    }

    public void setStartSheet(int startSheet) {
        this.startSheet = startSheet;
    }

    public String getExportSql() {
        return exportSql;
    }

    public void setExportSql(String exportSql) {
        this.exportSql = exportSql;
    }

    public int getExportMax() {
        return exportMax;
    }

    public void setExportMax(int exportMax) {
        this.exportMax = exportMax;
    }

    public int getSheetRows() {
        return sheetRows;
    }

    public void setSheetRows(int sheetRows) {
        this.sheetRows = sheetRows;
    }

    public Map<String, ImportTemplateItem> getExpParaItemMap() {
        return expParaItemMap;
    }

    public void setExpParaItemMap(Map<String, ImportTemplateItem> expParaItemMap) {
        this.expParaItemMap = expParaItemMap;
    }
}
