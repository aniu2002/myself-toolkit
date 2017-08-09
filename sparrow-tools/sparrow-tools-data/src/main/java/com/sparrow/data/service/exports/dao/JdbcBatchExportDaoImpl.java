package com.sparrow.data.service.exports.dao;

import com.sparrow.core.log.LoggerManager;
import com.sparrow.core.log.sql.SqlLog;
import com.sparrow.data.service.exports.handler.ResultSetExportHandler;
import com.sparrow.data.service.imports.config.ImportConfigHelper;
import com.sparrow.data.service.imports.data.ImportTemplate;
import com.sparrow.data.tools.exports.writer.DataWriter;
import com.sparrow.data.tools.sql.NamedParameter;
import com.sparrow.data.tools.sql.ParsedSql;
import com.sparrow.data.tools.sql.SqlTool;
import com.sparrow.orm.template.simple.OperateTemplate;

import java.io.IOException;
import java.sql.*;
import java.util.Map;

/**
 * 批量导出模板类，根据模板配置的sql导出
 *
 * @author YZC
 * @version 1.0 (2014-3-14)
 * @modify
 */
public class JdbcBatchExportDaoImpl implements BatchExportDao {
    protected static SqlLog LOG = LoggerManager.getSqlLog();

    private OperateTemplate operateTemplate;

    public OperateTemplate getOperateTemplate() {
        return operateTemplate;
    }

    public void setOperateTemplate(OperateTemplate operateTemplate) {
        this.operateTemplate = operateTemplate;
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.exports.dao.BatchExportDao#batchExport(java.lang.String,
     * java.util.Map, com.sparrow.data.tools.exports.writer.DataWriter)
     */
    @Override
    public void batchExport(String sql, Map<String, Object> paramMap,
                            DataWriter dataWriter) {
        LOG.prepare(sql, paramMap);
        int n = this.doExport(sql, paramMap, dataWriter, null);
        LOG.effects(n);
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
                            DataWriter dataWriter, ImportTemplate template) {
        LOG.prepare(sql, paramMap);
        int n = this.doExport(sql, paramMap, dataWriter, template);
        LOG.effects(n);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.exports.dao.BatchExportDao#batchExport(java.lang.String,
     * java.lang.Object[], com.sparrow.data.tools.exports.writer.DataWriter,
     * com.sparrow.data.service.imports.data.ImportTemplate)
     */
    @Override
    public void batchExport(String sql, Object[] arguments,
                            DataWriter dataWriter, ImportTemplate template) {
        LOG.prepare(sql);
        int n = this.doBatchExport(sql, arguments, dataWriter, template);
        LOG.effects(n);
    }

    /**
     * 根据sql导出excel，没有模板按照，resultSet响应的meta自然顺序作为输出列的顺序，如果有模板，
     * 则根据item配置构造列位置信息与结果集的索引
     *
     * @param sql        通常在模板中配置，导出sql语句，也可以外部传入
     * @param paramMap   sql命名参数信息
     * @param dataWriter 导出writer写入器
     * @param template   导出模板与导入模板一致
     * @author YZC
     */
    int doExport(String sql, Map<String, Object> paramMap,
                 DataWriter dataWriter, ImportTemplate template) {
        Connection connection = null;
        ResultSet rs = null;
        Statement statement = null;
        int n = 0;
        try {
            connection = this.operateTemplate.getSessionFactory().getConnection();
            if (paramMap == null || paramMap.isEmpty()) {
                statement = connection
                        .createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                ResultSet.CONCUR_READ_ONLY);
                rs = statement.executeQuery(sql);
            } else {
                ParsedSql parsedSql = SqlTool.parseSqlStatement(sql);
                PreparedStatement ps = connection.prepareStatement(
                        parsedSql.getActualSql(), ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_READ_ONLY);
                NamedParameter paras[] = parsedSql.getParameters();
                if (paras != null && paras.length > 0) {
                    for (int i = 0; i < paras.length; i++)
                        ps.setObject(i + 1, paramMap.get(paras[i].getName()));
                }
                statement = ps;
                rs = ps.executeQuery();
            }
            n = this.doExport(rs, dataWriter, template);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return n;
    }

    int doExport(ResultSet rs, DataWriter dataWriter, ImportTemplate template)
            throws SQLException, IOException {
        rs.setFetchSize(300);
        rs.setFetchDirection(ResultSet.FETCH_FORWARD);

        ResultSetExportHandler handler = ImportConfigHelper.getExportConfig(rs,
                template);
        String headers[] = handler.getHeaders();
        dataWriter.setHeaders(headers);
        // 打开
        dataWriter.open();
        String data[] = new String[headers.length];
        int row = 1;
        int n = 0;
        while (rs.next()) {
            handler.fillData(rs, data, row++);
            dataWriter.writeRow(data);
            n++;
        }
        dataWriter.close();
        return n;
    }

    int doBatchExport(String sql, Object[] arguments, DataWriter dataWriter,
                      ImportTemplate template) {
        Connection connection = null;
        ResultSet rs = null;
        Statement statement = null;
        int n = 0;
        try {
            connection = this.operateTemplate.getSessionFactory().getConnection();
            if (arguments == null || arguments.length == 0) {
                statement = connection
                        .createStatement(ResultSet.TYPE_FORWARD_ONLY,
                                ResultSet.CONCUR_READ_ONLY);
                rs = statement.executeQuery(sql);
            } else {
                PreparedStatement ps = connection
                        .prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY,
                                ResultSet.CONCUR_READ_ONLY);
                for (int i = 0; i < arguments.length; i++)
                    ps.setObject(i + 1, arguments[i]);
                statement = ps;
                rs = ps.executeQuery();
            }
            n = this.doExport(rs, dataWriter, template);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("SQL执行时异常：" + sql);
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (statement != null)
                    statement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return n;
    }
}
