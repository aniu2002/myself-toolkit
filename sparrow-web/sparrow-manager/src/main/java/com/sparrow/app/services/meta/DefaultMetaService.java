package com.sparrow.app.services.meta;

import com.sparrow.orm.shema.DbSetting;
import com.sparrow.orm.util.SQLUtils;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.app.common.jdbc.JDBCHelper;
import com.sparrow.app.common.table.TableData;
import com.sparrow.app.common.table.TableHeader;
import com.sparrow.app.services.source.SourceInfo;
import com.sparrow.tools.mapper.container.ProxyConnection;
import com.sparrow.tools.mapper.type.Type;

import java.sql.*;
import java.util.*;


public class DefaultMetaService extends AbstractMetaService {
    private ProxyConnection connection;
    private DbSetting dbs;
    private String dbType;
    private String schema;
    private String catalog;
    private SourceInfo sourceInfo;

    public DefaultMetaService(SourceInfo sourceInfo) {
        this.dbs = new DbSetting(
                sourceInfo.getDriver(),
                sourceInfo.getUrl(),
                sourceInfo.getUser(), sourceInfo.getPassword());
        this.dbType = sourceInfo.getType();
        this.sourceInfo=sourceInfo;
        try {
            this.checkDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public void close() {
        this.dbs = null;
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.connection = null;
    }

    void checkDatabase() throws SQLException {
        Connection con = this.getConnection();
        DatabaseMetaData md = con.getMetaData();
        String dbname = md.getDatabaseProductName().toLowerCase();
        String dbversion = md.getDatabaseProductVersion();
        if (dbname.indexOf("mysql") != -1) {
            this.dbType = Type.mysql;
        } else if (dbname.indexOf("oracle") != -1) {
            this.dbType = Type.oracle;
        } else if (dbname.indexOf("sqlserver") != -1
                || dbname.indexOf("sql server") != -1) {
            this.dbType = Type.mssql;
        } else if (dbname.indexOf("db2") != -1) {
            this.dbType = Type.db2;
        }

        System.out.println("---------------------------------------------");
        System.out.println(" -- DB Name    : " + dbname);
        System.out.println(" -- DB Version : " + dbversion);
        System.out.println(" -- Driver     : " + md.getDriverName());
        System.out.println(" -- Driver Ver : " + md.getDriverVersion());
        System.out.println("---------------------------------------------");
    }

    @Override
    public String getSchema() {
        return schema;
    }

    @Override
    public Connection getConnection() {
        if (this.connection != null)
            return this.connection.getConnection();
        try {
            Class.forName(dbs.driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("No driver setting ... ");
        }
        Properties props = new Properties();
        // props.put("remarksReporting", "true");
        props.put("user", dbs.user);
        props.put("password", dbs.password);
        try {
            this.connection = new ProxyConnection(DriverManager.getConnection(
                    dbs.url, props));

            this.schema = this.dbs.user.toUpperCase();
            this.catalog = this.connection.getConnection().getCatalog();

            return this.connection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public TableData getTableData(String tablename, int no, int limit) {
        // Table tb=this.getTable(tablename);
        String sql = "select * from " + tablename;
        Connection conn = null;
        ResultSet rs = null;

        int start = limit * (no - 1); // limit * (no - 1) + 1
        int end = limit * no;

        try {
            sql = SQLUtils.getPrePagedSql(sql, this.dbType);
            conn = this.getConnection();

            List<TableHeader> hlist = this.getTableColumns(conn.getMetaData(),
                    tablename);
            TableHeader[] headers = hlist
                    .toArray(new TableHeader[hlist.size()]);
            PreparedStatement prep = conn.prepareStatement(sql);
            prep.setInt(1, start);
            prep.setInt(2, end);
            rs = prep.executeQuery();

            Map<String, String> data;
            List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
            String name;
            int count = headers.length;
            while (rs.next()) {
                data = new HashMap<String, String>();
                for (int i = 0; i < count; i++) {
                    name = headers[i].getField();
                    data.put(name, rs.getString(name));
                }
                datas.add(data);
            }

            TableData td = new TableData(tablename);
            td.setHeader(hlist);
            td.setData(datas);
            return td;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<TableHeader> getTableColumns(DatabaseMetaData metadata,
                                              String tableName) {
        ResultSet rs = null;
        try {
            rs = metadata.getColumns(null, null, tableName, null);
            Class<?> javatype;
            short sqltype;
            int columnLenth, decimalLength;

            List<TableHeader> headers = new ArrayList<TableHeader>();
            TableHeader header;
            String name;
            while (rs.next()) {
                header = new TableHeader();
                name = rs.getString("COLUMN_NAME");
                // rsmd.get
                header.setField(name);

                sqltype = rs.getShort(5);
                columnLenth = rs.getInt(7);
                decimalLength = rs.getInt(9);

                javatype = JDBCHelper.getJavaType(sqltype, columnLenth,
                        decimalLength);
                if (javatype == String.class)
                    header.setWidth("180");
                else
                    header.setWidth("120");
                String n = rs.getString("REMARKS");
                if (StringUtils.isEmpty(n))
                    n = name;
                header.setHeader(n);
                headers.add(header);
            }
            return headers;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void old(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        List<TableHeader> headers = new ArrayList<TableHeader>();
        TableHeader header;
        String names[];
        String name;
        int count = rsmd.getColumnCount();

        names = new String[count];

        for (int i = 0; i < count; i++) {
            int ind = i + 1;
            header = new TableHeader();
            name = rsmd.getColumnName(ind);
            // rsmd.get
            names[i] = name;
            header.setField(name);
            String n = rsmd.getColumnLabel(ind);
            Class<?> type = JDBCHelper.getJavaType(rsmd.getColumnType(ind),
                    rsmd.getPrecision(ind), rsmd.getScale(ind));
            if (StringUtils.isEmpty(n))
                n = name;
            if (type == String.class)
                header.setWidth("180");
            else
                header.setWidth("120");
            header.setHeader(n);
            headers.add(header);
        }
    }
}
