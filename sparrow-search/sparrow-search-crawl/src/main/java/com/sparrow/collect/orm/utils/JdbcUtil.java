package com.sparrow.collect.orm.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.Date;


/**
 * @author Administrator
 */
public class JdbcUtil {
    public static final int TYPE_UNKNOWN = Integer.MIN_VALUE;
    static final Logger logger = LoggerFactory.getLogger(JdbcUtil.class);

    public static class Column {
        public String name;
        public int sqlType;
        public int sqlColumnLength;
        public int sqlDecimalLength;
        public boolean sqlNotNull;
        public Class<?> javaType;

        public boolean equals(Object o) {
            boolean rv = false;
            if (o != null && o instanceof JdbcUtil.Column) {
                rv = (name.equals(((JdbcUtil.Column) o).name));
            }
            return rv;
        }

        public int hashCode() {
            int returnint = (name != null) ? name.hashCode() : 0;
            return returnint;
        }
    }

    public static List<String> getCatalogs(Connection c) throws SQLException {
        DatabaseMetaData dmd = c.getMetaData();
        ResultSet rs = null;
        try {
            rs = dmd.getCatalogs();
            List<String> l = new LinkedList<String>();
            ResultSetMetaData rsmd = (ResultSetMetaData) rs.getMetaData();
            logger.info(String.valueOf(rsmd.getColumnCount()));
            while (rs.next()) {
                logger.info(rs.getString(1));
                l.add(rs.getString(1));
            }
            return l;
        } finally {
            if (rs != null)
                rs.close();
        }
    }

    public static Map<String, List<String>> getSchemas(Connection c)
            throws SQLException {
        DatabaseMetaData dmd = c.getMetaData();
        ResultSet rs = null;
        try {
            rs = dmd.getSchemas();
            Map<String, List<String>> map = new HashMap<String, List<String>>();
            List<String> l;
            while (rs.next()) {
                String schema = rs.getString(1);
                String catalog = null;
                logger.info(schema);
                if (rs.getMetaData().getColumnCount() > 1) {
                    catalog = rs.getString(2);
                    logger.info(catalog);
                }
                l = map.get(catalog);
                if (l == null) {
                    l = new LinkedList<String>();
                    map.put(catalog, l);
                }
                l.add(schema);
            }
            return map;
        } finally {
            if (rs != null)
                rs.close();
        }
    }

    public static List<String> getTables(Connection c, String catalog,
                                         String schema, String tablePattern, String type)
            throws SQLException {
        DatabaseMetaData dmd = c.getMetaData();
        ResultSet rs = null;
        try {
            rs = dmd.getTables(catalog, schema, tablePattern,
                    new String[]{type});
            List<String> l = new LinkedList<String>();
            while (rs.next()) {
                logger.info(rs.getString(3));
                l.add(rs.getString(3));
            }
            return l;
        } finally {
            if (rs != null)
                rs.close();
        }
    }

    public static Set<String> getForeignKeyColumns(Connection c,
                                                   String catalog, String schema, String table) throws SQLException {
        DatabaseMetaData dmd = c.getMetaData();
        ResultSet rs = null;
        try {
            rs = dmd.getImportedKeys(catalog, schema, table);
            HashSet<String> columns = new HashSet<String>();
            while (rs.next())
                columns.add(rs.getString(8));
            return columns;
        } finally {
            if (rs != null)
                rs.close();
        }
    }

    public static List<Column> getPrimaryKeyColumns(Connection c,
                                                    String catalog,
                                                    String schema,
                                                    String table) throws SQLException {
        DatabaseMetaData dmd = c.getMetaData();
        ResultSet rs = null;
        try {
            rs = dmd.getPrimaryKeys(catalog, schema, table);
            List<Column> pkColumns = new LinkedList<Column>();
            while (rs.next()) {
                List<Column> tmp = getTableColumns(c, catalog, schema, table,
                        rs.getString(4));
                Column pkColumn = tmp.get(0);
                pkColumns.add(pkColumn);
            }
            return pkColumns;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (rs != null)
                rs.close();
        }
    }

    public static List<String> getPrimaryKeyColumnNames(Connection c,
                                                        String catalog, String schema, String table) throws SQLException {
        DatabaseMetaData dmd = c.getMetaData();
        ResultSet rs = null;
        try {
            rs = dmd.getPrimaryKeys(catalog, schema, table);
            List<String> pkColumnNames = new LinkedList<String>();
            while (rs.next()) {
                List<Column> tmp = getTableColumns(c, catalog, schema, table,
                        rs.getString(4));
                Column pkColumn = tmp.get(0);
                pkColumnNames.add(pkColumn.name);
            }
            return pkColumnNames;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (rs != null)
                rs.close();
        }
    }

    public static List<Column> getTableColumns(Connection c, String catalog,
                                               String schema, String table) throws SQLException {
        List<Column> returnList = getTableColumns(c, catalog, schema, table,
                null);
        return returnList;
    }

    public static List<Column> getTableColumns(Connection c, String catalog,
                                               String schema, String table, String columnPattern)
            throws SQLException {
        DatabaseMetaData dmd = c.getMetaData();
        ResultSet rs = null;
        try {
            rs = dmd.getColumns(catalog, schema, table, columnPattern);
            List<Column> list = new LinkedList<Column>();
            while (rs.next()) {
                JdbcUtil.Column col = new JdbcUtil.Column();
                col.name = rs.getString(4);
                col.sqlType = rs.getShort(5);
                col.sqlColumnLength = rs.getInt(7);
                col.sqlDecimalLength = rs.getInt(9);
                col.sqlNotNull = ("NO".equals(rs.getString(18)));
                col.javaType = getJavaType(col.sqlType, col.sqlColumnLength, col.sqlDecimalLength);
                list.add(col);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } finally {
            if (rs != null)
                rs.close();
        }

    }

    public static String getTableofView(Connection c, String viewName,
                                        String schema, String dbtype) {
        String tableName = null;
        try {
            String sql;
            if (dbtype.equals("DB2")) {
                sql = new StringBuilder("SELECT DISTINCT A.TABNAME FROM SYSCAT.TABLES A, SYSCAT.VIEWDEP B WHERE (A.TYPE='T' AND A.TABNAME=B.BNAME AND A.TABSCHEMA=B.BSCHEMA AND B.BTYPE='T' AND B.VIEWNAME='")
                        .append(viewName)
                        .append("'AND B.VIEWSCHEMA='")
                        .append(schema)
                        .append("') FOR FETCH ONLY").toString();
            } else {
                sql = new StringBuilder("select t.referenced_name from sys.all_dependencies t where t.name = '")
                        .append(viewName)
                        .append("' and t.referenced_type = 'TABLE'").toString();
            }
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                tableName = rs.getString(1);
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tableName;
    }

    public static List<String> getTablesofView(Connection c, String viewName,
                                               String schema, String dbtype) {
        List<String> list = new ArrayList<String>();
        try {
            String sql;
            if (dbtype.equals("DB2")) {
                sql = new StringBuilder("SELECT DISTINCT A.TABNAME FROM SYSCAT.TABLES A, SYSCAT.VIEWDEP B WHERE (A.TYPE='T' AND A.TABNAME=B.BNAME AND A.TABSCHEMA=B.BSCHEMA AND B.BTYPE='T' AND B.VIEWNAME='")
                        .append(viewName)
                        .append("'AND B.VIEWSCHEMA='")
                        .append(schema)
                        .append("') FOR FETCH ONLY").toString();
            } else {
                sql = new StringBuilder("select t.referenced_name from sys.all_dependencies t where t.name = '")
                        .append(viewName)
                        .append("' and t.referenced_type = 'TABLE'").toString();
            }
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            rs.close();
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void closeStatement(PreparedStatement ps) {
        if (ps != null)
            try {
                ps.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null)
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex.getMessage());
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String lookupColumnName(ResultSetMetaData resultSetMetaData,
                                          int columnIndex) throws SQLException {
        String name = resultSetMetaData.getColumnLabel(columnIndex);
        if (name == null || name.length() < 1) {
            name = resultSetMetaData.getColumnName(columnIndex);
        }
        return name;
    }

    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    public static String trimAllWhitespace(String str) {
        if (!hasLength(str))
            return str;
        StringBuilder sb = new StringBuilder(str);
        int index = 0;
        while (sb.length() > index) {
            if (Character.isWhitespace(sb.charAt(index)))
                sb.deleteCharAt(index);
            else
                index++;
        }
        return sb.toString();
    }

    private static boolean isHexNumber(String value) {
        int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value
                .startsWith("#", index));
    }

    private static BigInteger decodeBigInteger(String value) {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        // Handle minus sign, if present.
        if (value.startsWith("-")) {
            negative = true;
            index++;
        }
        // Handle radix specifier, if present.
        if (value.startsWith("0x", index)
                || value.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (value.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (value.startsWith("0", index)
                && value.length() > 1 + index) {
            index++;
            radix = 8;
        }
        BigInteger result = new BigInteger(value.substring(index), radix);
        return (negative ? result.negate() : result);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> T parseNumber(String text,
                                                   Class<T> targetClass) {
        String trimmed = trimAllWhitespace(text);
        if (targetClass.equals(Byte.class)) {
            return (T) (isHexNumber(trimmed) ? Byte.decode(trimmed) : Byte
                    .valueOf(trimmed));
        } else if (targetClass.equals(Short.class)) {
            return (T) (isHexNumber(trimmed) ? Short.decode(trimmed) : Short
                    .valueOf(trimmed));
        } else if (targetClass.equals(Integer.class)) {
            return (T) (isHexNumber(trimmed) ? Integer.decode(trimmed)
                    : Integer.valueOf(trimmed));
        } else if (targetClass.equals(Long.class)) {
            return (T) (isHexNumber(trimmed) ? Long.decode(trimmed) : Long
                    .valueOf(trimmed));
        } else if (targetClass.equals(BigInteger.class)) {
            return (T) (isHexNumber(trimmed) ? decodeBigInteger(trimmed)
                    : new BigInteger(trimmed));
        } else if (targetClass.equals(Float.class)) {
            return (T) Float.valueOf(trimmed);
        } else if (targetClass.equals(Double.class)) {
            return (T) Double.valueOf(trimmed);
        } else if (targetClass.equals(BigDecimal.class)
                || targetClass.equals(Number.class)) {
            return (T) new BigDecimal(trimmed);
        } else {
            throw new IllegalArgumentException("Cannot convert String [" + text
                    + "] to target class [" + targetClass.getName() + "]");
        }
    }

    private static void raiseOverflowException(Number number,
                                               Class<?> targetClass) {
        throw new IllegalArgumentException("Could not convert number ["
                + number + "] of type [" + number.getClass().getName()
                + "] to target class [" + targetClass.getName() + "]: overflow");
    }

    @SuppressWarnings("unchecked")
    public static <T extends Number> T convertNumberToTargetClass(
            Number number, Class<T> targetClass)
            throws IllegalArgumentException {
        if (targetClass.isInstance(number)) {
            return (T) number;
        } else if (targetClass.equals(Byte.class)) {
            long value = number.longValue();
            if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) new Byte(number.byteValue());
        } else if (targetClass.equals(Short.class)) {
            long value = number.longValue();
            if (value < Short.MIN_VALUE || value > Short.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) new Short(number.shortValue());
        } else if (targetClass.equals(Integer.class)) {
            long value = number.longValue();
            if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                raiseOverflowException(number, targetClass);
            }
            return (T) new Integer(number.intValue());
        } else if (targetClass.equals(Long.class)) {
            return (T) new Long(number.longValue());
        } else if (targetClass.equals(BigInteger.class)) {
            if (number instanceof BigDecimal) {
                // do not lose precision - use BigDecimal's own conversion
                return (T) ((BigDecimal) number).toBigInteger();
            } else {
                // original value is not a Big* number - use standard long
                // conversion
                return (T) BigInteger.valueOf(number.longValue());
            }
        } else if (targetClass.equals(Float.class)) {
            return (T) new Float(number.floatValue());
        } else if (targetClass.equals(Double.class)) {
            return (T) new Double(number.doubleValue());
        } else if (targetClass.equals(BigDecimal.class)) {
            // always use BigDecimal(String) here to avoid unpredictability of
            // BigDecimal(double)
            // (see BigDecimal javadoc for details)
            return (T) new BigDecimal(number.toString());
        } else {
            throw new IllegalArgumentException("Could not convert number ["
                    + number + "] of type [" + number.getClass().getName()
                    + "] to unknown target class [" + targetClass.getName()
                    + "]");
        }
    }

    public static Object getResultSetValue(ResultSet rs, int index,
                                           Class<?> requiredType) throws SQLException {
        if (requiredType == null) {
            return getResultSetValue(rs, index);
        }

        Object value = null;
        boolean wasNullCheck = false;

        // Explicitly extract typed value, as far as possible.
        if (String.class.equals(requiredType)) {
            value = rs.getString(index);
        } else if (boolean.class.equals(requiredType)
                || Boolean.class.equals(requiredType)) {
            value = rs.getBoolean(index);
            wasNullCheck = true;
        } else if (byte.class.equals(requiredType)
                || Byte.class.equals(requiredType)) {
            value = rs.getByte(index);
            wasNullCheck = true;
        } else if (short.class.equals(requiredType)
                || Short.class.equals(requiredType)) {
            value = rs.getShort(index);
            wasNullCheck = true;
        } else if (int.class.equals(requiredType)
                || Integer.class.equals(requiredType)) {
            value = rs.getInt(index);
            wasNullCheck = true;
        } else if (long.class.equals(requiredType)
                || Long.class.equals(requiredType)) {
            value = rs.getLong(index);
            wasNullCheck = true;
        } else if (float.class.equals(requiredType)
                || Float.class.equals(requiredType)) {
            value = rs.getFloat(index);
            wasNullCheck = true;
        } else if (double.class.equals(requiredType)
                || Double.class.equals(requiredType)
                || Number.class.equals(requiredType)) {
            value = rs.getDouble(index);
            wasNullCheck = true;
        } else if (byte[].class.equals(requiredType)) {
            value = rs.getBytes(index);
        } else if (java.sql.Date.class.equals(requiredType)) {
            value = rs.getDate(index);
        } else if (Time.class.equals(requiredType)) {
            value = rs.getTime(index);
        } else if (Timestamp.class.equals(requiredType)
                || Date.class.equals(requiredType)) {
            value = rs.getTimestamp(index);
        } else if (BigDecimal.class.equals(requiredType)) {
            value = rs.getBigDecimal(index);
        } else if (Blob.class.equals(requiredType)) {
            value = rs.getBlob(index);
        } else if (Clob.class.equals(requiredType)) {
            value = rs.getClob(index);
        } else {
            // Some unknown type desired -> rely on getObject.
            value = getResultSetValue(rs, index);
        }

        // Perform was-null check if demanded (for results that the
        // JDBC driver returns as primitives).
        if (wasNullCheck && value != null && rs.wasNull()) {
            value = null;
        }
        return value;
    }

    public static Object getResultSetValue(ResultSet rs, int index)
            throws SQLException {
        Object obj = rs.getObject(index);
        String className = null;
        if (obj != null) {
            className = obj.getClass().getName();
        }
        if (obj instanceof Blob) {
            obj = rs.getBytes(index);
        } else if (obj instanceof Clob) {
            obj = rs.getString(index);
        } else if (className != null
                && ("oracle.sql.TIMESTAMP".equals(className) || "oracle.sql.TIMESTAMPTZ"
                .equals(className))) {
            obj = rs.getTimestamp(index);
        } else if (className != null && className.startsWith("oracle.sql.DATE")) {
            String metaDataClassName = rs.getMetaData().getColumnClassName(
                    index);
            if ("java.sql.Timestamp".equals(metaDataClassName)
                    || "oracle.sql.TIMESTAMP".equals(metaDataClassName)) {
                obj = rs.getTimestamp(index);
            } else {
                obj = rs.getDate(index);
            }
        } else if (obj != null && obj instanceof java.sql.Date) {
            if ("java.sql.Timestamp".equals(rs.getMetaData()
                    .getColumnClassName(index))) {
                obj = rs.getTimestamp(index);
            }
        }
        return obj;
    }

    public static Object getJavaObject(ResultSet rs, String index,
                                       Class<?> javaType) throws SQLException {
        if (javaType == null)
            return rs.getObject(index);
        else if (javaType == String.class)
            return rs.getString(index);
        else if (javaType == Boolean.class || javaType == boolean.class)
            return rs.getBoolean(index);
        else if (javaType == Integer.class || javaType == int.class)
            return rs.getInt(index);
        else if (javaType == Long.class || javaType == long.class)
            return rs.getLong(index);
        else if (javaType == Float.class || javaType == float.class)
            return rs.getFloat(index);
        else if (javaType == Double.class || javaType == double.class)
            return rs.getDouble(index);
        else if (java.sql.Date.class.equals(javaType)) {
            return rs.getDate(index);
        } else if (Time.class.equals(javaType)) {
            return rs.getTime(index);
        } else if (Timestamp.class.equals(javaType)
                || Date.class.equals(javaType)) {
            return rs.getTimestamp(index);
        } else if (BigDecimal.class.equals(javaType)) {
            return rs.getBigDecimal(index);
        } else if (Blob.class.equals(javaType)) {
            return rs.getBlob(index);
        } else if (Clob.class.equals(javaType)) {
            return rs.getClob(index);
        } else
            return rs.getObject(index);
    }

    public static Object getJavaObject(ResultSet rs, int index,
                                       Class<?> javaType) throws SQLException {
        if (javaType == null)
            return rs.getObject(index);
        else if (javaType == String.class)
            return rs.getString(index);
        else if (javaType == Boolean.class || javaType == boolean.class)
            return rs.getBoolean(index);
        else if (javaType == Integer.class || javaType == int.class)
            return rs.getInt(index);
        else if (javaType == Long.class || javaType == long.class)
            return rs.getLong(index);
        else if (javaType == Float.class || javaType == float.class)
            return rs.getFloat(index);
        else if (javaType == Double.class || javaType == double.class)
            return rs.getDouble(index);
        else if (java.sql.Date.class.equals(javaType)) {
            return rs.getDate(index);
        } else if (Time.class.equals(javaType)) {
            return rs.getTime(index);
        } else if (Timestamp.class.equals(javaType)
                || Date.class.equals(javaType)) {
            return rs.getTimestamp(index);
        } else if (BigDecimal.class.equals(javaType)) {
            return rs.getBigDecimal(index);
        } else if (Blob.class.equals(javaType)) {
            return rs.getBlob(index);
        } else if (Clob.class.equals(javaType)) {
            return rs.getClob(index);
        } else
            return rs.getObject(index);
    }

    public static Class<?> getJavaType(int sqlType, int columnSize,
                                       int decimalDigits) {
        Class<?> rv = String.class;
        if (sqlType == Types.CHAR || sqlType == Types.VARCHAR) {
            rv = String.class;
        } else if (sqlType == Types.FLOAT || sqlType == Types.REAL) {
            rv = Float.class;
        } else if (sqlType == Types.INTEGER) {
            rv = Integer.class;
        } else if (sqlType == Types.DOUBLE) {
            rv = Double.class;
        } else if (sqlType == Types.DATE) {
            rv = Date.class;
        } else if (sqlType == Types.TIMESTAMP) {
            rv = Date.class;
        } else if (sqlType == Types.TIME) {
            rv = Date.class;
        } else if (sqlType == Types.SMALLINT) {
            rv = Short.class;
        } else if (sqlType == Types.BIT) {
            rv = Byte.class;
        } else if (sqlType == Types.BIGINT) {
            rv = Long.class;
        } else if (sqlType == Types.NUMERIC || sqlType == Types.DECIMAL) {
            if (decimalDigits == 0) {
                if (columnSize == 1) {
                    rv = Byte.class;
                } else if (columnSize < 5) {
                    rv = Short.class;
                } else if (columnSize < 10) {
                    rv = Integer.class;
                } else {
                    rv = Long.class;
                }
            } else {
                if (columnSize < 9) {
                    rv = Float.class;
                } else {
                    rv = Double.class;
                }
            }
        }
        return rv;
    }

    public static String getJavaTypeString(Class<?> javaType) {
        if (javaType == null)
            return "string";
        else if (javaType == Boolean.class || javaType == boolean.class)
            return "boolean";
        else if (javaType == Integer.class || javaType == int.class)
            return "int";
        else if (javaType == Long.class || javaType == long.class)
            return "long";
        else if (javaType == Float.class || javaType == float.class)
            return "float";
        else if (javaType == Double.class || javaType == double.class)
            return "double";
        else if (javaType == Date.class)
            return "datetime";
        else if (javaType == Timestamp.class)
            return "time";
        else if (javaType == String.class)
            return "string";
        else if (javaType == Clob.class)
            return "string";
        else if (javaType == Blob.class)
            return "byte";
        else if (javaType == Character.class)
            return "char";
        return "string";
    }

    public static int getSqlType(Object object) {
        if (object == null)
            return TYPE_UNKNOWN;
        return getSqlType(object.getClass());
    }

    public static int getSqlType(Class<?> javaType) {
        if (javaType == null)
            return TYPE_UNKNOWN;
        else if (javaType.isPrimitive() || javaType == Integer.class)
            return Types.NUMERIC;
        else if (javaType == Boolean.class)
            return Types.BOOLEAN;
        else if (javaType == Long.class)
            return Types.NUMERIC;
        else if (javaType == Float.class)
            return Types.NUMERIC;
        else if (javaType == Double.class)
            return Types.NUMERIC;
        else if (javaType == Date.class)
            return Types.DATE;
        else if (javaType == Time.class)
            return Types.TIME;
        else if (javaType == Timestamp.class)
            return Types.TIMESTAMP;
        else if (javaType == String.class)
            return Types.VARCHAR;
        else if (javaType == Clob.class)
            return Types.CLOB;
        else if (javaType == Blob.class)
            return Types.BLOB;
        else if (javaType == Character.class)
            return Types.CHAR;
        return TYPE_UNKNOWN;
    }

    public static final int getSqlType(String javaType) {
        if (StringUtils.isEmpty(javaType) || "string".equals(javaType)
                || "text".equals(javaType))
            return Types.VARCHAR;
        else if ("bool".equals(javaType) || "boolean".equals(javaType))
            return Types.BOOLEAN;
        else if ("integer".equals(javaType) || "int".equals(javaType))
            return Types.INTEGER;
        else if ("long".equals(javaType))
            return Types.DECIMAL;
        else if ("float".equals(javaType))
            return Types.FLOAT;
        else if ("double".equals(javaType))
            return Types.DOUBLE;
        else if ("datetime".equals(javaType) || "timestamp".equals(javaType))
            return Types.TIMESTAMP;
        else if ("date".equals(javaType))
            return Types.DATE;
        else if ("time".equals(javaType))
            return Types.TIME;
        else if ("string".equals(javaType))
            return Types.VARBINARY;
        else if ("byte".equals(javaType))
            return Types.ARRAY;
        else if ("char".equals(javaType))
            return Types.CHAR;
        return Types.VARCHAR;
    }
}