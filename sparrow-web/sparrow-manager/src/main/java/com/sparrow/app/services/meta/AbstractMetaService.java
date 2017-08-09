package com.sparrow.app.services.meta;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sparrow.core.log.LoggerManager;
import com.sparrow.app.common.jdbc.JDBCHelper;
import com.sparrow.app.common.table.Table;
import com.sparrow.app.common.table.TableColumn;
import com.sparrow.app.common.table.TableData;
import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.config.SystemConfig;

public abstract class AbstractMetaService implements MetaInterface {
    public static final String TEXT = "text";
    public static final String BOOLEAN = "bool";
    public static final String DATE = "date";
    public static final String TIME = "time";

    private Pattern pattern;
    private Pattern excludePattern;
    private String include;
    private String exclude;

    {
        include = createRegexString(SystemConfig.getProps("table.filter"));
        this.pattern = StringUtils.isEmpty(include) ? null : Pattern
                .compile(include);
        exclude = createRegexString(SystemConfig.getProps("table.exclude"));
        this.excludePattern = StringUtils.isEmpty(exclude) ? null : Pattern
                .compile(exclude);
    }

    protected boolean check(String table) {
        if (this.pattern == null)
            return true;
        Matcher m1 = this.pattern.matcher(table.toLowerCase());
        return m1.matches();
    }

    protected boolean exclude(String table) {
        if (this.excludePattern == null)
            return false;
        Matcher m1 = this.excludePattern.matcher(table.toLowerCase());
        return m1.matches();
    }

    public abstract TableData getTableData(String tablename, int no, int limit);

    @Override
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

    @Override
    public String getDatabaseType() {
        return null;
    }

    @Override
    public long getNextval(String sequnece) {
        return 0;
    }

    @Override
    public String getPKNames(String tableName) {
        String pkNames = "";
        Connection conn = null;
        ResultSet resultSet = null;
        try {
            conn = getConnection();
            DatabaseMetaData metadata = conn.getMetaData();
            resultSet = metadata.getPrimaryKeys(null, null,
                    tableName.toUpperCase());
            boolean first = true;
            while (resultSet.next()) {
                if (first)
                    first = !first;
                else
                    pkNames += ",";
                String pkName = resultSet.getString("COLUMN_NAME");
                pkNames += pkName;
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

        return pkNames;
    }

    public String getSchema() {
        return null;
    }

    private String getPrimaryKeysx(DatabaseMetaData metadata, String tableName) {
        String pkName = null;
        ResultSet resultSet = null;
        try {
            resultSet = metadata.getPrimaryKeys(null, this.getSchema(), tableName);
            if (!resultSet.isAfterLast()) {
                if (resultSet.next())
                    pkName = resultSet.getString("COLUMN_NAME");
            }
            if ("id".equalsIgnoreCase(pkName))
                return "id";
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

    private String getPrimaryKeys(DatabaseMetaData metadata, String tableName) {
        String pkNames = "";
        ResultSet resultSet = null;
        try {
            resultSet = metadata.getPrimaryKeys(null, this.getSchema(), tableName);
            boolean first = true;
            while (resultSet.next()) {
                if (first)
                    first = !first;
                else
                    pkNames += ",";
                String pkName = resultSet.getString("COLUMN_NAME");
                pkNames += pkName;
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
        }

        return pkNames;
    }

    static String getRender(int sqlType, int columnSize, int decimalDigits) {
        String render = TEXT;
        if (sqlType == Types.CHAR || sqlType == Types.VARCHAR
                || sqlType == Types.CLOB) {
        } else if (sqlType == Types.BOOLEAN) {
            render = BOOLEAN;
        } else if (sqlType == Types.DATE) {
            render = DATE;
        } else if (sqlType == Types.TIMESTAMP) {
            render = DATE;
        } else if (sqlType == Types.TIME) {
            render = TIME;
        }
        return render;
    }

    private List<TableColumn> getTableItems(DatabaseMetaData metadata,
                                            String tableName, String primaryKey) {
        ResultSet rs = null;
        try {
            rs = metadata.getColumns(null, null, tableName, null);
            List<TableColumn> columns = new ArrayList<TableColumn>();
            TableColumn item;
            Class<?> javatype;
            short sqltype;
            int columnLenth, decimalLength;
            // int count = rsmd.getColumnCount();
            while (rs.next()) {
                item = new TableColumn();
                item.setName(rs.getString(4));// rs.getString("COLUMN_NAME")
                item.setType(rs.getString("TYPE_NAME"));
                item.setSize(rs.getInt("COLUMN_SIZE"));
                item.setField(NameRule.columnTofield(item.getName()));
                // for (int i = 0; i < count; i++)
                // + rs.getString(i + 1));
                sqltype = rs.getShort(5);
                columnLenth = rs.getInt(7);
                decimalLength = rs.getInt(9);
                javatype = JDBCHelper.getJavaType(sqltype, columnLenth,
                        decimalLength);
                item.setJavaType(javatype == null ? "java.lang.String"
                        : javatype.getName());
                item.setSimpleType(JDBCHelper.getSimpleType(sqltype,
                        columnLenth, decimalLength));
                item.setDesc(rs.getString("REMARKS"));
                item.setNotNull("NO".equals(rs.getString("IS_NULLABLE"))); // ("NO".equals(rs.getString(18)));
                if (!StringUtils.isEmpty(primaryKey)
                        && primaryKey.indexOf(item.getName()) != -1) {
                    item.setPrimary(true);
                    item.setUi("noe");
                } else
                    item.setUi(getRender(sqltype, columnLenth, decimalLength));
                columns.add(item);
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
        }
        return null;
    }

    private List<TableColumn> getTableItems(Connection conn, String scripts) throws SQLException {
        ResultSet rs = null;

        String mt = scripts.toLowerCase();
        if (mt.indexOf("where") != -1)
            scripts = scripts + " and 1=0";
        else
            scripts = scripts + " where 1=0";

        try {
            conn = this.getConnection();
            PreparedStatement prep = conn.prepareStatement(scripts);

            rs = prep.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            List<TableColumn> columns = new ArrayList<TableColumn>();
            TableColumn item;
            String name;
            int count = metaData.getColumnCount();
            int sqlType, columnLen, decimalLen;
            Class<?> javaType;
            for (int i = 0; i < count; i++) {
                int nx = i + 1;
                item = new TableColumn();
                name = metaData.getColumnName(nx);
                item.setName(name);

                // rsmd.get
                item.setField(NameRule.columnTofield(name));

                sqlType = metaData.getColumnType(nx);
                columnLen = metaData.getColumnDisplaySize(nx);
                decimalLen = metaData.getScale(nx);

                item.setType(metaData.getColumnTypeName(nx));
                item.setSize(metaData.getColumnDisplaySize(nx));
                javaType = JDBCHelper.getJavaType(sqlType, columnLen,
                        decimalLen);
                item.setJavaType(javaType == null ? "java.lang.String"
                        : javaType.getName());
                item.setSimpleType(JDBCHelper.getSimpleType(sqlType,
                        columnLen, decimalLen));
                item.setDesc(metaData.getColumnLabel(nx));
                boolean fg = !(metaData.isNullable(nx) > 0);
                item.setNotNull(fg);
                String n = metaData.getColumnLabel(nx);
                if (StringUtils.isEmpty(n))
                    n = name;
                item.setDesc(n);
                item.setUi(getRender(sqlType, columnLen, decimalLen));
                columns.add(item);
            }
            return columns;
        } catch (SQLException e) {
            throw e;
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Table getScriptDescriptor(String script, String name) {
        Connection conn = null;
        // ResultSet rs = null;
        DatabaseMetaData metadata;
        try {
            conn = getConnection();
            //if(LoggerManager.getSysLog())
            LoggerManager.getSysLog().info(" - provider : {} , script : {}", name, script);
            //System.out.println(conn.getClass().getName());
            // metadata = conn.getMetaData();
            // rs = metadata.getColumns(null, null, tableName, null);
            Table table = new Table(name);
            // table.setPrimaryKeys(this.getPrimaryKeys(metadata, tableName));
            table.setItems(this.getTableItems(conn, script));
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // try {
            // if (rs != null)
            // rs.close();
            // } catch (SQLException e) {
            // e.printStackTrace();
            // }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public Table getTable(String tableName) {
        Connection conn = null;
        // ResultSet rs = null;
        DatabaseMetaData metadata;
        try {
            conn = getConnection();
            //System.out.println(conn.getClass().getName());
            metadata = conn.getMetaData();
            // rs = metadata.getColumns(null, null, tableName, null);
            Table table = new Table(tableName);
            table.setPrimaryKeys(this.getPrimaryKeys(metadata, tableName));
            table.setItems(this.getTableItems(metadata, tableName,
                    table.getPrimaryKeys()));
            return table;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // try {
            // if (rs != null)
            // rs.close();
            // } catch (SQLException e) {
            // e.printStackTrace();
            // }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static Pattern createRegexPattern(String express) {
        return Pattern.compile(express);
    }

    public static String createRegexString(String filter) {
        if (StringUtils.isNotEmpty(filter) && !"*".equals(filter)) {
            StringBuilder sb = new StringBuilder();
            String regex;
            boolean notFirst = false;
            for (StringTokenizer tokenizer = new StringTokenizer(filter, ","); tokenizer
                    .hasMoreElements(); ) {
                if (!notFirst)
                    notFirst = true;
                else
                    sb.append('|');
                regex = tokenizer.nextToken().toLowerCase().replace(".", "\\.")
                        .replace("?", ".?").replace("*", ".*");
                sb.append("(").append(regex).append(")");
            }
            return sb.toString();
        }
        return null;
    }

    @Override
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<String>();
        SysLogger.info(" - Include '{}' , Exclude '{}'", this.include,
                this.exclude);
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            String[] types = {"TABLE"};
            rs = metaData.getTables(null, null, null, types); // "SMM_%"
            while (rs.next()) {
                String tb = rs.getString(3);
                if (this.exclude(tb) || !this.check(tb))
                    continue;
                tableNames.add(tb);
            }
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
}
