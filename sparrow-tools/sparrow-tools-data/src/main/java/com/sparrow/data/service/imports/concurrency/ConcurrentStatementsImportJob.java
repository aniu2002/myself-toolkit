package com.sparrow.data.service.imports.concurrency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.sparrow.data.service.imports.config.ImportConfigHelper;
import com.sparrow.data.service.imports.data.ImportColumn;

public class ConcurrentStatementsImportJob implements Runnable {
    private final String sql;
    private final ImportColumn parasRelations[];
    private final Map<String, Object> gloabParas;

    private Connection connection;
    private List<Object> data;

    public ConcurrentStatementsImportJob(String sql,
                                         ImportColumn parasRelations[]) {
        this(sql, parasRelations, null);
    }

    public ConcurrentStatementsImportJob(String sql,
                                         ImportColumn parasRelations[], Map<String, Object> gloabParas) {
        this.sql = sql;
        this.parasRelations = parasRelations;
        this.gloabParas = gloabParas;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        this.addBatch(this.data);
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    void addBatch(List<Object> dataList) {
        PreparedStatement ps = null;
        try {
            ps = this.connection.prepareStatement(this.sql);
            for (Object data : dataList) {
                ImportConfigHelper.prepareRecordSet(ps, this.parasRelations,
                        (Object[]) data, this.gloabParas);
            }
            ImportConfigHelper.executeBatchAndClear(ps);
        } catch (SQLException ex) {
            ps = null;
            this.connection = null;
            throw new RuntimeException(ex.getMessage());
        } finally {
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
