package com.sparrow.data.service.exports.handler;

import com.sparrow.data.service.exports.format.ExportFormat;

/**
 * 抽象方法，写序号，和获取数据记录
 *
 * @author YZC
 * @version 1.0 (2014-3-31)
 * @modify
 */
public abstract class ExportHandler<T> {
    /**
     * excel每行的索引对应resultset数据的索引 ，<br/>
     * 如: relations[0]=3 excel对应第一列 数据 result对应的是getResultSet(3)
     */
    int relations[];
    /**
     * 记录列长度
     */
    int columnSize;
    /**
     * excel列头信息
     */
    String headers[];
    /**
     * 数据格式化
     */
    ExportFormat formats[];

    public ExportFormat[] getFormats() {
        return formats;
    }

    public void setFormats(ExportFormat[] formats) {
        this.formats = formats;
    }

    public int[] getRelations() {
        return relations;
    }

    public void setRelations(int[] relations) {
        this.relations = relations;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    /**
     * 从t获取每行数据记录
     *
     * @param t    一个导出数据记录对象
     * @param data 导出行的数据列表避免创建重复的数据
     * @author YZC
     */
    public void fillData(T t, String[] data, int row) {
        int relations[] = this.relations;
        ExportFormat ft[] = this.formats;
        int idx;
        for (int i = 0; i < relations.length; i++) {
            idx = relations[i];
            if (idx == -1)
                data[i] = null;
            else if (idx == -2)
                data[i] = String.valueOf(row);
            else {
                String d = this.fetchValue(t, idx);
                if (ft != null) {
                    ExportFormat f = ft[i];
                    if (f != null)
                        d = f.format(d);
                }
                data[i] = d;
            }
        }
    }

    /**
     * 从t获取每行数据记录
     *
     * @param t   一个导出数据记录对象
     * @param row 记录当前导出row计数器
     * @return 一行的数据列
     * @author YZC
     */
    public String[] fetchData(T t, int row) {
        String data[] = new String[this.columnSize];
        this.fillData(t, data, row);
        return data;
    }

    /**
     * 根据数据索引值，获取t的具体值
     *
     * @param t         一个导出数据记录对象
     * @param dataIndex 数据索引值
     * @return 每列的值
     * @author YZC
     */
    public abstract String fetchValue(T t, int dataIndex);

}
