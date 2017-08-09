package com.sparrow.tools.pogen;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.core.utils.CharUtils;
import com.sparrow.tools.common.DbSetting;
import com.sparrow.tools.mapper.NameRule;
import com.sparrow.tools.mapper.container.ProxyConnection;
import com.sparrow.tools.mapper.data.STable;
import com.sparrow.tools.mapper.data.Table;
import com.sparrow.tools.mapper.data.TableColumn;
import com.sparrow.tools.mapper.type.Type;
import com.sparrow.tools.pogen.generator.IdGeneratorDefine;

public class MapperGenerator {
    public static final String TEXT = "text";
    public static final String NUMBER = "number";
    public static final String NUMBER_INTEGER = "integer";
    public static final String NUMBER_REAL = "real";
    public static final String REQUIRED = "required";
    public static final String BOOLEAN = "bool";
    public static final String DATE = "date";
    public static final String TIME = "time";
    private final DbSetting dbs;
    private final Log log;
    String dbType;
    String schema;
    String catalog;
    boolean initialized = false;
    boolean initializedXet = false;
    private ProxyConnection connection;
    private TableFilters filters;

    public MapperGenerator(DbSetting dbs) {
        this(dbs, new DefaultLog());
    }

    public MapperGenerator(DbSetting dbs, Log log) {
        this.dbs = dbs;
        this.log = (log == null ? new DefaultLog() : log);
    }

    public static String getFilePath(String pakage) {
        return pakage.replace('.', File.separatorChar);
    }

    public static File getJavaPackFile(String base, String pack, String name) {
        File file = new File(base, getFilePath(pack));
        if (!file.exists())
            file.mkdirs();
        return new File(file, name + ".java");
    }

    public static File getPackFile(String base, String pack, String name) {
        File file = new File(base, getFilePath(pack));
        if (!file.exists())
            file.mkdirs();
        return new File(file, name);
    }

    static void setColumnType(TableColumn column, int sqlType, int columnSize,
                              int decimalDigits) {
        Class<?> rv = String.class;
        String render = TEXT;
        if (sqlType == Types.CHAR || sqlType == Types.VARCHAR
                || sqlType == Types.CLOB) {
            rv = String.class;
        } else if (sqlType == Types.FLOAT || sqlType == Types.REAL) {
            rv = Float.class;
            render = NUMBER;
        } else if (sqlType == Types.INTEGER) {
            rv = Integer.class;
            render = NUMBER;
        } else if (sqlType == Types.DOUBLE) {
            rv = Double.class;
            render = NUMBER;
        } else if (sqlType == Types.BOOLEAN) {
            rv = Boolean.class;
            render = BOOLEAN;
        } else if (sqlType == Types.DATE) {
            rv = java.util.Date.class;
            render = DATE;
        } else if (sqlType == Types.TIMESTAMP) {
            rv = java.util.Date.class;
            render = DATE;
        } else if (sqlType == Types.TIME) {
            rv = java.util.Date.class;
            render = TIME;
        } else if (sqlType == Types.SMALLINT) {
            rv = Short.class;
            render = NUMBER;
        } else if (sqlType == Types.BIT) {
            rv = Byte.class;
            render = NUMBER;
        } else if (sqlType == Types.BIGINT) {
            rv = Long.class;
            render = NUMBER;
        } else if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL) {
            if (decimalDigits == 0) {
                // if (columnSize == 1) {
                // rv = Byte.class;
                // } else
                if (columnSize < 5) {
                    rv = Short.class;
                } else if (columnSize < 10) {
                    rv = Integer.class;
                } else {
                    rv = Long.class;
                }
                column.setNumberType(NUMBER_INTEGER);
            } else {
                if (columnSize < 9) {
                    rv = Float.class;
                } else {
                    rv = Double.class;
                }
                column.setNumberType(NUMBER_REAL);
            }
            render = NUMBER;
        }
        column.setRender(render);
        column.setClassType(rv);
        column.setJavaType(rv == null ? "java.lang.String" : rv.getName());
    }

    public TableFilters getFilters() {
        return filters;
    }

    public void setFilters(TableFilters filters) {
        this.filters = filters;
    }

    public Log getLog() {
        return log;
    }

    public String getSchema() {
        return schema;
    }

    public Connection getConnectionEx() throws SQLException {
        if (!initialized) {
            try {
                Class.forName(dbs.driver);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new SQLException("No driver setting ... ");
            }
        }
        return DriverManager.getConnection(dbs.url, dbs.user, dbs.password);
    }

    public void setConnection(Connection connection) {
        if (connection instanceof ProxyConnection)
            this.connection = (ProxyConnection) connection;
        else
            this.connection = new ProxyConnection(connection);
        this.initialized = true;
    }

    public Connection getConnection() throws SQLException {
        if (this.connection != null) {
            Connection con = this.connection.getConnection();
            if (!this.initializedXet) {
                this.schema = this.dbs.user.toUpperCase();
                this.catalog = con.getCatalog();
                this.initializedXet = true;
            }
            return con;
        }
        try {
            Class.forName(dbs.driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("No driver setting ... ");
        }
        Properties props = new Properties();
        // props.put("remarksReporting", "true");
        props.put("user", dbs.user);
        props.put("password", dbs.password);
        this.connection = new ProxyConnection(DriverManager.getConnection(
                dbs.url, props));
        this.schema = this.dbs.user.toUpperCase();
        this.catalog = this.connection.getConnection().getCatalog();
        return this.connection.getConnection();
    }

    public String getCatalog() {
        return catalog;
    }

    public void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkDatabase() throws SQLException {
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

        this.log.info("---------------------------------------------");
        this.log.info(" -- DB Name    : " + dbname);
        this.log.info(" -- DB Version : " + dbversion);
        this.log.info(" -- Driver     : " + md.getDriverName());
        this.log.info(" -- Driver Ver : " + md.getDriverVersion());
        this.log.info("---------------------------------------------");
    }

    public List<Map<String, Object>> getColumnMetaData(String tableName) {
        Connection conn = null;
        ResultSet rs = null;
        DatabaseMetaData metadata;
        try {
            conn = getConnection();
            metadata = conn.getMetaData();
            rs = metadata.getColumns(null, null, tableName, null);

            List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();

            ResultSetMetaData rsmd = rs.getMetaData();
            Map<String, Object> metaData;
            String name;
            int count = rsmd.getColumnCount();
            while (rs.next()) {
                metaData = new HashMap<String, Object>();
                for (int i = 0; i < count; i++) {
                    name = rsmd.getColumnName(i + 1);
                    metaData.put(name, rs.getString(name));
                    this.log.info("name:" + name + " ,v:" + metaData.get(name));
                    // metaData.put("COLUMN_NAME", rs.getString("COLUMN_NAME"));
                    // metaData.put("TYPE_NAME", rs.getString("TYPE_NAME"));
                    // metaData.put("COLUMN_SIZE", rs.getInt("COLUMN_SIZE"));
                }
                columns.add(metaData);
            }
            return columns;
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

    public String getPKNames(String tableName) {
        String pkName = null;
        Connection conn = null;
        ResultSet resultSet = null;
        try {
            conn = getConnection();
            DatabaseMetaData metadata = conn.getMetaData();
            resultSet = metadata.getPrimaryKeys(null, null,
                    tableName.toUpperCase());
            if (!resultSet.isAfterLast()) {
                resultSet.next();
                pkName = resultSet.getString("COLUMN_NAME");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
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

        return pkName;
    }

    private String getPrimaryKeys(DatabaseMetaData metadata, String tableName) {
        String pkName = null;
        ResultSet resultSet = null;
        try {
            resultSet = metadata.getPrimaryKeys(null, this.getSchema(), tableName);
            if (!resultSet.isAfterLast()) {
                if (resultSet.next())
                    pkName = resultSet.getString("COLUMN_NAME");
            }
            // System.out.println("----------" + pkName);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return pkName;
    }

    boolean hasIn(String args[], String item) {
        if (args == null || args.length == 0)
            throw new RuntimeException("无主键信息");
        for (String ags : args)
            if (StringUtils.equals(ags, item))
                return true;
        return false;
    }

    private List<TableColumn> getTableItems$Oracle(DatabaseMetaData metadata,
                                                   Table table, String[] primaryKeys, IDAliasGenerator generator) {
        ResultSet rs = null;
        String tableName = table.getName();
        try {
            Map<String, String> colsMap = this.getColumnsDescMap(tableName);
            rs = metadata.getColumns(null, this.getSchema(), tableName, null);
            List<TableColumn> columns = new ArrayList<TableColumn>();
            TableColumn item;
            short sqltype;
            int columnLenth, decimalLength;
            // ResultSetMetaData rsmd = rs.getMetaData();
            // int count = rsmd.getColumnCount();
            while (rs.next()) {
                item = new TableColumn();
                item.setName(rs.getString(4));// rs.getString("COLUMN_NAME")
                item.setFieldName(NameRule.columnTofield(item.getName()));
                item.setFieldNameX(NameRule.toBeanName(item.getName()));
                item.setType(rs.getString("TYPE_NAME"));
                item.setSize(rs.getInt("COLUMN_SIZE"));
                // for (int i = 0; i < count; i++)
                // this.log.info(rsmd.getColumnName(i + 1) + " "
                // + rs.getString(i + 1));
                sqltype = rs.getShort(5);
                columnLenth = rs.getInt(7);
                decimalLength = rs.getInt(9);
                setColumnType(item, sqltype, columnLenth, decimalLength);
                item.setSqlType(sqltype);
                item.setDesc(colsMap.get(item.getName()));
                // this.log.info(" column desc : "+item.getDesc());
                // item.setDesc(rs.getString("REMARKS")); // mysql COMMENTS
                item.setNotNull("NO".equals(rs.getString("IS_NULLABLE"))); // ("NO".equals(rs.getString(18)));
                if (this.hasIn(primaryKeys, item.getName())) {
                    item.setPrimary(true);
                    table.setKeyJavaType(item.getJavaType());
                    table.setKeySimpleJavaType(item.getSampleType());
                    if (generator != null) {
                        IdGeneratorDefine igd = generator.getAlias(tableName,
                                item.getName(), sqltype);
                        if (igd != null
                                && igd.getType() != null
                                && igd.getType().equalsIgnoreCase(
                                item.getSampleType())) {
                            item.setGenerator(igd.getAlias());
                            table.setKeyGenerator(item.getFieldName() + "-"
                                    + igd.getGenerator());
                        } else if ("string".equalsIgnoreCase(item
                                .getSampleType())) {
                            if (item.getSize() > 30) {
                                item.setGenerator("uuid");
                                table.setKeyGenerator(item.getFieldName()
                                        + "-uuid");
                            }
                        }
                    }
                }
                columns.add(item);
            }
            colsMap.clear();
            colsMap = null;
            return columns;
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

    static final char UNDER_LINE = '_';

    public static String toBeanName(String field) {
        if (null == field) {
            return "";
        }
        char[] chars = field.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (i == 0) {
                sb.append(CharUtils.toUpperCase(c));
                continue;
            } else if (c == UNDER_LINE) {
                int j = i + 1;
                if (j < chars.length) {
                    sb.append(CharUtils.toUpperCase(chars[j]));
                    i++;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public Table getTableDescriptor(String tableName, IDAliasGenerator generator) {
        Connection conn = null;
        // ResultSet rs = null;
        DatabaseMetaData metadata;
        try {
            conn = getConnection();
            metadata = conn.getMetaData();
            Table table = new Table(tableName);
            table.setObjName(toBeanName(tableName));
            table.setPrimaryKeys(this.getPrimaryKeys(metadata, tableName));
            table.setItems(this.getTableItems(metadata, table,
                    com.sparrow.core.utils.StringUtils.tokenizeToStringArray(
                            table.getPrimaryKeys(), ","), generator
            ));
            table.caculate();
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private List<TableColumn> getTableItems(DatabaseMetaData metadata,
                                            Table table, String[] primaryKeys, IDAliasGenerator generator) {
        if (StringUtils.equals(Type.oracle, this.dbType))
            return this.getTableItems$Oracle(metadata, table, primaryKeys,
                    generator);
        else
            return this.getTableItem$MySql(metadata, table, primaryKeys,
                    generator);
    }

    private List<STable> getTables4Oracle() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        List<STable> tableNames = new ArrayList<STable>();
        String table;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT a.TABLE_NAME,b.COMMENTS FROM USER_TABLES a,USER_TAB_COMMENTS b WHERE a.TABLE_NAME=b.TABLE_NAME";
            rs = stmt.executeQuery(sql);
            while (rs.next()) {
                table = rs.getString(1);
                if (this.filters != null && this.filters.filter(table)) {
                    STable stable = new STable(table);
                    stable.setDesc(this.formatChar(rs.getString(2)));
                    // this.log.info("Table desc : " + stable.getDesc());
                    tableNames.add(stable);
                }
            }
            this.log.info("Table size : " + tableNames.size());
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
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
        return tableNames;
    }

    String formatChar(String s) {
        if (StringUtils.isEmpty(s))
            return s;
        return s.replace('\r', ' ').replace('\n', ' ');
    }

    /**
     * @param tableName
     * @return
     * @author YZC
     * @since JDK 1.6
     */
    Map<String, String> getColumnsDescMap(String tableName) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        String sql = "SELECT a.COLUMN_NAME,a.COMMENTS FROM USER_COL_COMMENTS a "
                + "WHERE a.TABLE_NAME='" + tableName + "'";
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            Map<String, String> map = new HashMap<String, String>();
            while (rs.next()) {
                map.put(rs.getString(1), this.formatChar(rs.getString(2)));
            }
            return map;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<STable> getTables() {
        if (StringUtils.equals(Type.oracle, this.dbType))
            return this.getTables4Oracle();
        else
            return this.getTable4MySql();

    }

    private List<STable> getTable4MySql() {
        List<STable> tableNames = new ArrayList<STable>();
        Connection conn = null;
        ResultSet rs = null;
        String table;
        try {
            conn = getConnection();
            Map<String, String> tableDescMap = this.getTableDesc4Mysql();
            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};
            rs = metaData.getTables(this.getCatalog(), this.getSchema(), null,
                    types); // "SMM_%"
            while (rs.next()) {
                table = rs.getString(3);
                if (this.filters != null && this.filters.filter(table)) {
                    STable stable = new STable(table);
                    String desc = tableDescMap.get(table.toLowerCase());
                    if (StringUtils.isNotEmpty(desc) && !"null".equals(desc))
                        stable.setDesc(desc);
                    tableNames.add(stable);
                }
            }
            this.log.info("Table size : " + tableNames.size());
            rs.close();
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
        return tableNames;
    }

    private List<TableColumn> getTableItem$MySql(DatabaseMetaData metadata,
                                                 Table table, String[] primaryKeys, IDAliasGenerator generator) {
        System.out.println("table='" + table.getName() + "'");
        System.out.println("primaryKey='" + primaryKeys + "'");
        ResultSet rs = null;
        try {
            rs = metadata.getColumns(this.getCatalog(), this.getSchema(),
                    table.getName(), null);
            List<TableColumn> columns = new LinkedList<TableColumn>();
            TableColumn item;
            short sqltype;
            int columnLenth, decimalLength;

            while (rs.next()) {
                item = new TableColumn();
                item.setName(rs.getString(4));// rs.getString("COLUMN_NAME")
                item.setFieldName(NameRule.columnTofield(item.getName()));
                // item.setFieldName(toBeanNamex(item.getName()));
                item.setFieldNameX(NameRule.toBeanName(item.getName()));
                item.setType(rs.getString("TYPE_NAME"));
                item.setSize(rs.getInt("COLUMN_SIZE"));
                // for (int i = 0; i < count; i++)
                // this.log.info(rsmd.getColumnName(i + 1) + " "
                // + rs.getString(i + 1));
                sqltype = rs.getShort(5);
                columnLenth = rs.getInt(7);
                decimalLength = rs.getInt(9);
                setColumnType(item, sqltype, columnLenth, decimalLength);
                item.setSqlType(sqltype);
                // item.setDesc(colsMap.get(item.getName()));
                // this.log.info(" column desc : "+item.getDesc());
                item.setDesc(rs.getString("REMARKS")); // mysql COMMENTS
                item.setNotNull("NO".equals(rs.getString("IS_NULLABLE"))); // ("NO".equals(rs.getString(18)));
                if (this.hasIn(primaryKeys, item.getName())) {
                    item.setPrimary(true);
                    table.setKeyJavaType(item.getJavaType());
                    table.setKeySimpleJavaType(item.getSampleType());
                    if (generator != null) {
                        IdGeneratorDefine igd = generator.getAlias(
                                table.getName(), item.getName(), sqltype);
                        if (igd != null && igd.getType() != null) {
                            if (StringUtils.equals(igd.getType(), "*")
                                    || igd.getType().equalsIgnoreCase(
                                    item.getSampleType())) {
                                item.setGenerator(igd.getAlias());
                                table.setKeyGenerator(item.getFieldName() + "-"
                                        + igd.getGenerator());
                            }
                        }
                    }
                }
                columns.add(item);
            }
            return columns;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    Map<String, String> getTableDesc4Mysql() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            stmt = conn.createStatement();
            String sql = "select TABLE_NAME,TABLE_COMMENT from INFORMATION_SCHEMA.TABLES "
                    + "WHERE TABLE_SCHEMA = '" + this.getCatalog() + "'";
            rs = stmt.executeQuery(sql);
            Map<String, String> map = new HashMap<String, String>();
            while (rs.next()) {
                map.put(rs.getString(1).toLowerCase(), this.formatChar(rs.getString(2)));
            }
            return map;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
