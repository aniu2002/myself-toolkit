package com.sparrow.data.service.imports.dao;

import com.sparrow.data.service.imports.config.ImportConfigHelper;
import com.sparrow.data.service.imports.data.ImportColumn;
import com.sparrow.data.tools.imports.extract.DataExtractor;
import com.sparrow.data.tools.imports.extract.ExtractCallback;
import com.sparrow.data.tools.message.ProcessMessageHolder;
import com.sparrow.data.tools.validate.ValidateError;
import com.sparrow.data.tools.validate.ValidateHandler;
import com.sparrow.orm.session.StatementCallbackHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * 实现一个批量处理的PreparedStatement回调函数
 *
 * @author YZC
 * @version 1.0 (2014-3-17)
 * @modify
 */
public class BatchPreparedStatementCallback implements StatementCallbackHandler {
    /**
     * 数据拆解器 ，拆解后通过extractor 回调函数通知 ， prepared statement 批量写入数据
     */
    private final DataExtractor dataExtractor;
    /**
     * 数据导入模板
     */
    private final ImportColumn parasRelations[];
    private final Map<String, Object> gloabParas;
    static final int BATCH_SIZE = 500;
    private int records = 0;
    private int effects = 0;
    private int maxImportSize = 0;

    public BatchPreparedStatementCallback(ImportColumn[] parasRelations,
                                          DataExtractor dataExtractor) {
        this(parasRelations, dataExtractor, null);
    }

    public void setMaxImportSize(int maxImportSize) {
        this.maxImportSize = maxImportSize;
    }

    public int getRecords() {
        return records;
    }

    public int getEffects() {
        return effects;
    }

    public BatchPreparedStatementCallback(ImportColumn[] parasRelations,
                                          DataExtractor dataExtractor, Map<String, Object> gloabParas) {
        this.dataExtractor = dataExtractor;
        this.parasRelations = parasRelations;
        this.gloabParas = gloabParas;
    }

    public DataExtractor getDataExtractor() {
        return dataExtractor;
    }


    @Override
    public Integer processStatement(final PreparedStatement ps)
            throws SQLException {
        final ImportColumn[] parasRelations = this.parasRelations;
        final BatchPreparedStatementCallback cb = this;
        final ValidateError error = new ValidateError();
        this.dataExtractor.setExtractCallback(new ExtractCallback() {
            @Override
            public void handle(String[] data, int sheet, int rows)
                    throws SQLException {
                cb.prepareRecordSet(ps, data, parasRelations, sheet, rows, error);
            }
        });
        this.dataExtractor.extract();
        this.effects += ImportConfigHelper.executeBatchAndClear(ps);
        return this.effects;
    }

    /**
     * 1)data数组是从excel或者csv文本提出来的一行数据数组<br/>
     * 2)根据导入映射配置importColumn设置批量操作中每条记录，批量加入一行数据
     *
     * @param ps    批量操作statement
     * @param items 数据导入映射列表
     * @param data  一行的数据列表
     * @param sheet 全局参数-外部参数
     * @param rows
     * @throws SQLException 批量操作时可能抛出异常
     * @author YZC
     */
    protected void prepareRecordSet(PreparedStatement ps, String data[],
                                    ImportColumn items[], int sheet, int rows, ValidateError error) throws SQLException {
        if (this.maxImportSize > 0 && this.records >= this.maxImportSize)
            throw new RuntimeException("数据导入数已经超过最大限制数:" + this.maxImportSize);
        //SysLogger.info("import -- records : " + this.records);
        if (!this.check(data, items, sheet, rows, error)) {
            ProcessMessageHolder.progressNotify(1, "%" + error.getError() + " , " + error.getName() + "=" + error.getValue());
            return;
        }
        ImportConfigHelper.prepareRecordSet(ps, this.parasRelations, data,
                this.gloabParas);
        ProcessMessageHolder.progressNotify(1, "导入成功一条数据");
        int n = this.records + 1;
        // 500条记录时批量提交一次
        if (n % 200 == 0) {
            this.effects += ImportConfigHelper.executeBatchAndClear(ps);
        }
        this.records = n;
    }

    boolean check(String data[], ImportColumn items[], int sheet, int rows, ValidateError error) {
        boolean ok = true;
        ImportColumn importColumn;
        ValidateHandler handler;
        int dataIdx;
        String val;
        for (int i = 0; i < items.length; i++) {
            importColumn = items[i];
            // 若是#号参数，即外部参数
            if (importColumn.isGloabParam())
                continue;
            // 导入列对应excel列的索引值
            dataIdx = importColumn.getDataIndex();
            if (dataIdx >= data.length) {
                error.errorSet(data[0],
                        importColumn.getLabel(), null, "-错误，数据长度过长 - " + data[0], sheet, rows, i
                );
                continue;
            }
            val = data[dataIdx];
            // 实施校验
            handler = importColumn.getValidateHandler();
            if (handler != null && !handler.check(val)) {
                ok = false;
                error.errorSet(data[0],
                        importColumn.getLabel(), val, handler.getDescription()
                                + "-错误", sheet, rows, i
                );
                if (!handler.skip()) {
                    error = handler.createValidateError(data,
                            importColumn.getLabel(), val, handler.getDescription()
                                    + "-错误", sheet, rows, i
                    );
                    handler.error(val, importColumn.getLabel(), error);
                }
                break;
            }
        }
        return ok;
    }
}
