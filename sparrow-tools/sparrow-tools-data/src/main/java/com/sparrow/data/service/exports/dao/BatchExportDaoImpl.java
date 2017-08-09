package com.sparrow.data.service.exports.dao;

import com.sparrow.data.service.exports.handler.ResultSetExportHandler;
import com.sparrow.data.service.imports.config.ImportConfigHelper;
import com.sparrow.data.service.imports.data.ImportTemplate;
import com.sparrow.data.tools.exports.writer.DataWriter;
import com.sparrow.orm.dao.simple.NormalDao;
import com.sparrow.orm.session.RowCallbackHandler;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * 批量导出模板类，根据模板配置的sql导出
 *
 * @author YZC
 * @version 1.0 (2014-3-14)
 * @modify
 */
public class BatchExportDaoImpl extends NormalDao implements BatchExportDao {
    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.exports.dao.BatchExportDao#batchExport(java.lang.String,
     * java.util.Map, com.sparrow.data.tools.exports.writer.DataWriter)
     */
    @Override
    public void batchExport(String sql, Map<String, Object> paramMap,
                            final DataWriter dataWriter) {
        RowCallbackHandler rowCallbackHandler = new RowCallbackHandler() {
            int columns = -1;

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                if (this.columns == -1)
                    this.columns = rs.getMetaData().getColumnCount();
                String data[] = new String[this.columns];
                for (int i = 0; i < data.length; i++) {
                    data[i] = rs.getString(i + 1);
                }
                try {
                    dataWriter.writeRow(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        this.doExport(sql, paramMap, rowCallbackHandler);
    }

    void doExport(String sql, Map<String, Object> paramMap,
                  RowCallbackHandler rowCallbackHandler) {
        if (paramMap == null || paramMap.isEmpty())
            this.query(sql, rowCallbackHandler);
        else
            this.query(sql, paramMap, rowCallbackHandler);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.exports.dao.BatchExportDao#batchExport(java.lang.String,
     * java.util.Map, com.sparrow.data.tools.exports.writer.DataWriter,
     * com.sparrow.data.service.imports.data.ImportTemplate)
     */
    @Override
    public void batchExport(String sql, Map<String, Object> paramMap,
                            final DataWriter dataWriter, final ImportTemplate template) {
        RowCallbackHandler rowCallbackHandler = new RowCallbackHandler() {
            ResultSetExportHandler handler = null;
            String data[];
            int columns = -1;
            int row = 1;

            @Override
            public void processRow(ResultSet rs) throws SQLException {
                if (this.columns == -1) {
                    this.handler = ImportConfigHelper.getExportConfig(rs,
                            template);
                    this.columns = this.handler.getColumnSize();
                    this.data = new String[this.columns];
                    String headers[] = this.handler.getHeaders();
                    // doOpen的时候writeHeader，但未设置header，所以在此先设置headers，然后再写入header
                    if (headers != null && headers.length > 0) {
                        dataWriter.setHeaders(headers);
                        try {
                            dataWriter.writeRow(headers);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    String data[] = this.data;
                    this.handler.fillData(rs, data, this.row++);
                    dataWriter.writeRow(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        this.doExport(sql, paramMap, rowCallbackHandler);
    }

    @Override
    public void batchExport(String sql, Object[] arguments,
                            DataWriter dataWriter, ImportTemplate template) {
    }
}
