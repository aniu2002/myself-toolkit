package com.sparrow.data.service.imports.concurrency;

import com.sparrow.data.service.imports.config.ImportConfigHelper;
import com.sparrow.data.service.imports.data.ImportColumn;
import com.sparrow.orm.template.simple.OperateTemplate;
import com.sparrow.orm.util.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ConcurrentImportJob implements Runnable {

    /**
     * 设置事务管理器，提供spring JDBC事务控制
     */
    private final String sql;
    private final ImportColumn parasRelations[];
    private final Map<String, Object> gloabParas;

    private List<Object> data;
    private OperateTemplate operateTemplate;

    public ConcurrentImportJob(String sql, ImportColumn parasRelations[]) {
        this(sql, parasRelations, null);
    }

    public ConcurrentImportJob(String sql, ImportColumn parasRelations[],
                               Map<String, Object> gloabParas) {
        this.sql = sql;
        this.parasRelations = parasRelations;
        this.gloabParas = gloabParas;
    }

    @Override
    public void run() {
        ConcurrentImportJob.this.addBatch(ConcurrentImportJob.this.data);
    }


    public OperateTemplate getOperateTemplate() {
        return operateTemplate;
    }

    public void setOperateTemplate(OperateTemplate operateTemplate) {
        this.operateTemplate = operateTemplate;
    }


    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data2) {
        this.data = data2;
    }

    void addBatch(List<Object> dataList) {
        Connection connection = this.operateTemplate.getSessionFactory().getConnection();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(this.sql);
            for (Object data : dataList) {
                ImportConfigHelper.prepareRecordSet(ps, this.parasRelations,
                        (Object[]) data, this.gloabParas);
            }
            ImportConfigHelper.executeBatchAndClear(ps);
        } catch (SQLException ex) {
            ps = null;
            connection = null;
            throw new RuntimeException(ex.getMessage());
        } finally {
            JdbcUtil.closeStatement(ps);
            JdbcUtil.closeConnection(connection);
        }
    }

}
