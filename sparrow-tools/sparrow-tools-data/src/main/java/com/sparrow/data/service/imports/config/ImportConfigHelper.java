package com.sparrow.data.service.imports.config;

import com.sparrow.core.utils.date.TimeUtils;
import com.sparrow.data.service.exports.format.ExportFormat;
import com.sparrow.data.service.exports.handler.MapExportHandler;
import com.sparrow.data.service.exports.handler.ObjectExportHandler;
import com.sparrow.data.service.exports.handler.ResultSetExportHandler;
import com.sparrow.data.service.imports.data.ImportColumn;
import com.sparrow.data.service.imports.data.ImportTemplate;
import com.sparrow.data.service.imports.data.ImportTemplateItem;
import com.sparrow.data.tools.sql.NamedParameter;
import com.sparrow.data.tools.validate.ValidateErrorCallback;
import com.sparrow.data.tools.validate.ValidateHandler;
import com.sparrow.data.tools.validate.Validator;
import com.sparrow.data.tools.validate.ValidatorManager;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 批量导入操作帮助类
 *
 * @author YZC
 * @version 1.0 (2014-3-23)
 * @modify
 */
public abstract class ImportConfigHelper {
    static Logger log = LoggerFactory.getLogger(ImportConfigHelper.class);

    private static Map<String, ExportFormat> formatMap;

    public static Map<String, ExportFormat> getFormatMap() {
        return formatMap;
    }


    public static void addExportFormat(String key, ExportFormat format) {
        if (StringUtils.isEmpty(key) || format == null)
            return;
        if (formatMap == null)
            formatMap = new ConcurrentHashMap<String, ExportFormat>();
        formatMap.put(key, format);
    }

    public static void setFormatMap(Map<String, ExportFormat> nFormatMap) {
        if (formatMap == null)
            formatMap = new ConcurrentHashMap<String, ExportFormat>();
        if (formatMap != null && !formatMap.isEmpty()) {
            Iterator<Map.Entry<String, ExportFormat>> iterator = nFormatMap
                    .entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, ExportFormat> entry = iterator.next();
                formatMap.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public static ExportFormat getExportFormat(String key) {
        if (StringUtils.isEmpty(key))
            return null;
        if (formatMap == null || formatMap.isEmpty())
            return null;
        return formatMap.get(key);
    }

    /**
     * 1)记录批量sql里的参数位置，根据参数名与模板配置的item的name 一一 建立联系，最终映射成import column关系数组 <br/>
     * 2)import column 数组包含： 该数组的索引对应sql参数索引<br/>
     * 3)数据索引即excel或者csv里某列的索引，根据这个索引去取值
     *
     * @param importTemplate        导入模板配置
     * @param parameters            解析过的sql，包括jdbc的actual sql 和参数， <br/>
     *                              如： #name 和 :name 井号表示外部传入的参数 冒号表示模板中配置对应的参数映射
     * @param validatorManager
     * @param validateErrorCallback
     * @return 导入列映射关系数组
     * @author YZC
     */
    public static ImportColumn[] getParaRelations(
            ImportTemplate importTemplate, NamedParameter parameters[],
            ValidatorManager validatorManager,
            ValidateErrorCallback validateErrorCallback) {
        ImportColumn cls[] = null;
        // 解析后的参数列表
        Map<String, ImportTemplateItem> paraItemMap = importTemplate
                .getParaItemMap();
        if (parameters != null && parameters.length > 0) {
            cls = new ImportColumn[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                NamedParameter para = parameters[i];
                ImportTemplateItem itm = paraItemMap.get(para.getName());

                ImportColumn col = new ImportColumn();
                col.setGloabParam(para.isGloabParas());
                col.setName(para.getName());
                if (itm != null) {
                    col.setType(itm.getType().getType());
                    col.setDataIndex(itm.getIndex());
                    col.setValidator(itm.getValidate());
                    col.setFormat(itm.isFormat());
                    if (StringUtils.isNotEmpty(itm.getValidate()) && validatorManager != null) {
                        Validator validator = validatorManager.getValidator(itm.getValidate());
                        if (validator == null) {
                            log.warn("Validator is null :  " + itm.getValidate());
                        } else
                            col.setValidateHandler(createValidateHandler(validator,
                                    validateErrorCallback));
                    }
                    col.setLabel(itm.getLabel());
                }
                cls[i] = col;
            }
        }
        return cls;
    }

    public static ValidateHandler createValidateHandler(Validator validator,
                                                        ValidateErrorCallback validateErrorCallback) {
        return new ValidateHandler(validator, validateErrorCallback);
    }

    /**
     * 1)data数组是从excel或者csv文本提出来的一行数据数组<br/>
     * 2)根据导入映射配置importColumn设置批量操作中每条记录，批量加入一行数据
     *
     * @param ps         批量操作statement
     * @param items      数据导入映射列表
     * @param data       一行的数据列表
     * @param gloabParas 全局参数-外部参数
     * @throws SQLException 批量操作时可能抛出异常
     * @author YZC
     */
    public static void prepareRecordSet(PreparedStatement ps,
                                        ImportColumn items[], Object data[], Map<String, Object> gloabParas)
            throws SQLException {
        ImportColumn column;
        int dataIdx;
        for (int i = 0; i < items.length; i++) {
            column = items[i];
            if (column.isGloabParam()) {
                if (gloabParas == null || gloabParas.isEmpty())
                    throw new RuntimeException("全局参数为空，但sql中使用了全局参数["
                            + column.getName() + "]");
                ps.setObject(i + 1, gloabParas.get(column.getName()));
                continue;
            }
            dataIdx = column.getDataIndex();
            if (column.isFormat() && column.getType() == 0)
                prepareRecord(ps, i + 1, formatString((String) data[dataIdx]), column.getType());
            else
                prepareRecord(ps, i + 1, data[dataIdx], column.getType());
        }
        ps.addBatch();
    }

    /**
     * 1)批量更新操作时，为了避免数据记录数无线增加最终导致内存溢出，<br/>
     * 2)提供该方法将内存中已有的记录数提交并清空列表，返回影响的记录
     *
     * @param ps 批量操作statement
     * @return 返回影响记录数
     * @throws SQLException 批量操作时可能抛出异常
     * @author YZC
     */
    public static int executeBatchAndClear(PreparedStatement ps)
            throws SQLException {
        int effects[] = ps.executeBatch();
        // ps.clearBatch();
        return getSuccessEffects(effects);
    }

    /**
     * 计算批量提交后数组中操作成功的记录数，一般-2的表示成功
     *
     * @param efs 批量提交后返回的数组
     * @return 影响记录总数
     * @author YZC
     */
    public static int getSuccessEffects(int efs[]) {
        // if (e == Statement.SUCCESS_NO_INFO)  oracle
        int i = 0;
        for (int e : efs) {
            if (e == 1)
                i++;
        }
        return i;
    }

    static String formatString(String str) {
        if (StringUtils.isEmpty(str))
            return str;
        char[] chars = str.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char n = chars[i];
            if (isIgnoreChar(n))
                continue;
            sb.append(n);
        }
        return sb.toString();
    }

    static boolean isIgnoreChar(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.HIGH_SURROGATES || ub == Character.UnicodeBlock.LOW_SURROGATES)
            return true;
        return false;
    }

    /**
     * 具体设置每列参数值
     *
     * @param ps     批量操作statement
     * @param index  sql参数索引即导入映射列表的索引
     * @param object 某列的数据
     * @param type   数据插入类型 ，如：int string long 等
     * @throws SQLException 批量操作时可能抛出异常
     * @author YZC
     */
    static void prepareRecord(PreparedStatement ps, int index, Object object,
                              int type) throws SQLException {
        String data = (String) object;
        // if (StringUtils.isEmpty(data)) {
        // ps.setNull(parameterIndex, sqlType);
        // }
        switch (type) {
            case 0:
                ps.setString(index, data);
                break;
            case 1:
                ps.setDate(
                        index,
                        new java.sql.Date(TimeUtils.string2Date(
                                TimeUtils.YYYY_MM_DD_FORMAT, data).getTime())
                );
                break;
            case 2:
                ps.setTimestamp(index, new java.sql.Timestamp(TimeUtils
                        .string2Date(TimeUtils.DATE_STANDARD_FORMAT, data)
                        .getTime()));
                break;
            case 3:
                if (StringUtils.isEmpty(data))
                    ps.setInt(index, 0);
                else
                    ps.setInt(index, Integer.valueOf(data));
                break;
            case 4:
                if (StringUtils.isEmpty(data))
                    ps.setLong(index, 0);
                else
                    ps.setLong(index, Long.valueOf(data));
                break;
            case 5:
                if (StringUtils.isEmpty(data))
                    ps.setFloat(index, 0);
                else
                    ps.setFloat(index, Float.valueOf(data));
                break;
            case 6:
                if (StringUtils.isEmpty(data))
                    ps.setDouble(index, 0);
                else
                    ps.setDouble(index, Double.valueOf(data));
                break;
            case 7:
                if (StringUtils.isEmpty(data))
                    ps.setLong(index, 0);
                else
                    ps.setLong(index, Long.parseLong(data));
                break;
            default:
                ps.setObject(index, data);
        }
    }

    static boolean mapIsEmpty(Map<String, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 如果按照内存列表数据进行导出的时候，根据对象class的meta信息来构建导出数据每行的列映射数据<br/>
     * 比如:column[0]=2 ： 0表示导出第一列，2表示第一列导出resultSet的数据索引
     *
     * @param claz     resultSet信息
     * @param template 模板信息
     * @return 返回导出设置信息
     * @throws SQLException
     * @author YZC
     */
    public static ObjectExportHandler getExportConfig(Class<?> claz,
                                                      ImportTemplate template) throws SQLException {
        ObjectExportHandler cfg;
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(claz);
        // 如果模板配置为空，直接采用object的元数据设置导出配置
        if (template == null
                || (mapIsEmpty(template.getExpParaItemMap()) && mapIsEmpty(template
                .getParaItemMap()))) {
            cfg = getExportConfig(pds, true);
            if (cfg != null)
                cfg.setPropertyDescriptors(pds);
            return cfg;
        }
        Map<String, ImportTemplateItem> mp = template.getExpParaItemMap();
        String headers[] = template.getHeaders();
        boolean isEmpty = (mp == null || mp.isEmpty());
        boolean hasHeader = false;
        // 模板设置的列记录限制参数
        int limit = template.getLimit();
        // 如果设置了headers参数，以headers的长度作为列的size
        if (headers != null && headers.length > 0) {
            limit = headers.length;
            // 如果列的索引最大值大于limit的话，认为配置有问题，抛出异常
            if (template.getExpMaxIdx() >= limit) {
                throw new RuntimeException("导出的header设置长度不能满足列位置的最大索引下标："
                        + template.getExpMaxIdx());
            }
            hasHeader = true;
        }
        // 根据列位置配置的最大索引值，设置数组的长度
        if (limit == 0 && !isEmpty && template.getExpMaxIdx() > 0)
            limit = template.getExpMaxIdx() + 1;
        if (limit > 0) {
            // 如果导出配置item为空，那么默认选择导入配置的
            cfg = (isEmpty ? getExportConfig(limit, template.getParaItemMap(),
                    pds, !hasHeader) : getExportConfig(limit, mp, pds,
                    !hasHeader));
        } else {
            // 如果没设置limit，也没设置headers，或者没设置导出列的话，那limit为0
            // 直接根据对象的meta信息构建cfg
            cfg = getExportConfig(pds, !hasHeader);
        }
        // 手工设置headers
        if (hasHeader && cfg != null)
            cfg.setHeaders(headers);
        cfg.setPropertyDescriptors(pds);
        return cfg;
    }

    /**
     * 如果按照exportSql进行导出的时候，根据resultSet响应的meta信息来构建导出数据每行的列映射数据<br/>
     * 比如:column[0]=2 ： 0表示导出第一列，2表示第一列导出resultSet的数据索引
     *
     * @param rs       resultSet信息
     * @param template 模板信息
     * @return 返回导出设置信息
     * @throws SQLException
     * @author YZC
     */
    public static ResultSetExportHandler getExportConfig(ResultSet rs,
                                                         ImportTemplate template) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        // 如果模板配置为空，直接采用resultSet的元数据设置导出配置
        if (template == null
                || (mapIsEmpty(template.getExpParaItemMap()) && mapIsEmpty(template
                .getParaItemMap())))
            return getExportConfig(rsmd, true);
        Map<String, ImportTemplateItem> mp = template.getExpParaItemMap();
        ResultSetExportHandler cfg;
        String headers[] = template.getHeaders();
        boolean isEmpty = (mp == null || mp.isEmpty());
        boolean hasHeader = false;
        // 模板设置的列记录限制参数
        int limit = template.getLimit();
        // 如果设置了headers参数，以headers的长度作为列的size
        if (headers != null && headers.length > 0) {
            limit = headers.length;
            // 如果列的索引最大值大于limit的话，认为配置有问题，抛出异常
            if (template.getExpMaxIdx() >= limit) {
                throw new RuntimeException("导出的header设置长度不能满足列位置的最大索引下标："
                        + template.getExpMaxIdx());
            }
            hasHeader = true;
        }
        // 根据列位置配置的最大索引值，设置数组的长度
        if (limit == 0 && !isEmpty && template.getExpMaxIdx() > 0)
            limit = template.getExpMaxIdx() + 1;
        if (limit > 0) {
            // 如果导出配置item为空，那么默认选择导入配置的
            cfg = (isEmpty ? getExportConfig(limit, template.getParaItemMap(),
                    rsmd, !hasHeader) : getExportConfig(limit, mp, rsmd,
                    !hasHeader));
        } else {
            // 如果没设置limit，也没设置headers，或者没设置导出列的话，那limit为0
            // 直接根据数据库响应记录的meta信息构建cfg
            cfg = getExportConfig(rsmd, !hasHeader);
        }
        // 手工设置headers
        if (hasHeader && cfg != null)
            cfg.setHeaders(headers);
        return cfg;
    }

    public static MapExportHandler getMapExportConfig(ImportTemplate template)
            throws SQLException {
        // 如果模板配置为空，直接采用resultSet的元数据设置导出配置
        if (template == null
                || (mapIsEmpty(template.getExpParaItemMap()) && mapIsEmpty(template
                .getParaItemMap())))
            return null;
        Map<String, ImportTemplateItem> mp = template.getExpParaItemMap();
        MapExportHandler cfg;
        String headers[] = template.getHeaders();
        boolean isEmpty = (mp == null || mp.isEmpty());
        boolean hasHeader = false;
        // 模板设置的列记录限制参数
        int limit = template.getLimit();
        // 如果设置了headers参数，以headers的长度作为列的size
        if (headers != null && headers.length > 0) {
            limit = headers.length;
            // 如果列的索引最大值大于limit的话，认为配置有问题，抛出异常
            if (template.getExpMaxIdx() >= limit) {
                throw new RuntimeException("导出的header设置长度不能满足列位置的最大索引下标："
                        + template.getExpMaxIdx());
            }
            hasHeader = true;
        }
        // 根据列位置配置的最大索引值，设置数组的长度
        if (limit == 0 && !isEmpty && template.getExpMaxIdx() > 0)
            limit = template.getExpMaxIdx() + 1;
        if (limit > 0) {
            // 如果导出配置item为空，那么默认选择导入配置的
            cfg = (isEmpty ? getMapExportConfig(template.getParaItemMap(),
                    limit, !hasHeader) : getMapExportConfig(mp, limit,
                    !hasHeader));
        } else {
            // 如果没设置limit，也没设置headers，或者没设置导出列的话，那limit为0
            // 直接根据数据库响应记录的meta信息构建cfg
            cfg = null;
            // getMapExportConfig(template, !hasHeader);
        }
        // 手工设置headers
        if (hasHeader && cfg != null)
            cfg.setHeaders(headers);
        return cfg;
    }

    /**
     * 通过resultSet的元数据构造导出配置设置
     *
     * @param rsmd        resultSet元数据信息，包括列名和数据索引，列名配置的item名对应
     * @param needHeaders 是否需要设置headers信息
     * @return 导出配置
     * @throws SQLException
     * @author YZC
     */
    public static ResultSetExportHandler getExportConfig(
            ResultSetMetaData rsmd, boolean needHeaders) throws SQLException {
        int n = rsmd.getColumnCount();
        ResultSetExportHandler cfg = new ResultSetExportHandler();
        String headers[] = null;
        if (needHeaders)
            headers = new String[n];
        int relations[] = new int[n];
        for (int i = 0; i < n; i++) {
            relations[i] = i;
            if (headers != null) {
                headers[i] = rsmd.getColumnLabel(i + 1);
            }
        }
        cfg.setRelations(relations);
        cfg.setColumnSize(n);
        if (headers != null)
            cfg.setHeaders(headers);
        return cfg;
    }

    /**
     * 通过配置，构造导出配置信息，header信息和行列位置索引与resultset数据索引映射
     *
     * @param n           导出列数
     * @param mp          导出列的配置信息，列的索引信息
     * @param rsmd        数据库响应数据信息，通过column名和item的配置名 建立映射
     * @param needHeaders 是否需要设置headers信息
     * @return 导出配置
     * @throws SQLException
     * @author YZC
     */
    public static ResultSetExportHandler getExportConfig(int n,
                                                         Map<String, ImportTemplateItem> mp, ResultSetMetaData rsmd,
                                                         boolean needHeaders) throws SQLException {
        ResultSetExportHandler cfg = new ResultSetExportHandler();
        String headers[] = null;
        if (needHeaders)
            headers = new String[n];
        int relations[] = new int[n];
        ExportFormat formats[] = new ExportFormat[n];
        // 全部置入忽略标记
        for (int i = 0; i < n; i++) {
            relations[i] = -1;
        }
        ImportTemplateItem itm;
        String head, name;
        int size = rsmd.getColumnCount();
        int idx;
        for (int i = 0; i < size; i++) {
            name = rsmd.getColumnName(i + 1);
            name = name.toLowerCase();
            itm = mp.get(name);
            // excel每行的索引对应resultset数据的索引
            if (itm != null && itm.getIndex() != -1) {
                idx = itm.getIndex();
                relations[idx] = i;
                if (StringUtils.isNotEmpty(itm.getRender()))
                    formats[idx] = getExportFormat(itm.getRender());
                if (headers != null) {
                    head = itm.getLabel();
                    if (StringUtils.isEmpty(head))
                        head = rsmd.getColumnName(i + 1);
                    headers[idx] = head;
                }
            }
        }
        // 设置序号列，特殊id名为"_idx"
        itm = mp.get("_idx");
        if (itm != null) {
            // 设置 关联索引为-2 ,写入的时候专门针对-2写入序号
            idx = itm.getIndex();
            // 如果还是默认值，则设置-2标记是序号列
            if (relations[idx] == -1)
                relations[idx] = -2;
        }
        cfg.setRelations(relations);
        cfg.setFormats(formats);
        cfg.setColumnSize(n);
        if (headers != null)
            cfg.setHeaders(headers);
        return cfg;
    }

    static int getDescriptCount(PropertyDescriptor[] pds) {
        int n = pds.length;
        PropertyDescriptor pd;
        String name;
        int t = 0;
        for (int i = 0; i < n; i++) {
            pd = pds[i];
            name = pd.getName();
            if ("class".equals(name))
                continue;
            t++;
        }
        return t;
    }

    /**
     * 通过java对象的class元数据构造导出配置设置
     *
     * @param pds         class的setter和getter元数据信息，包括列名和数据索引，列名配置的item名对应
     * @param needHeaders 是否需要设置headers信息
     * @return 导出配置
     * @throws SQLException
     * @author YZC
     */
    public static ObjectExportHandler getExportConfig(PropertyDescriptor[] pds,
                                                      boolean needHeaders) throws SQLException {
        int n = pds.length;
        ObjectExportHandler cfg = new ObjectExportHandler();
        String headers[] = null;
        if (needHeaders)
            headers = new String[n - 1];
        int relations[] = new int[n - 1];
        int j = 0;
        PropertyDescriptor pd;
        String name;
        for (int i = 0; i < n; i++) {
            pd = pds[i];
            name = pd.getName();
            if ("class".equals(name)) {
                continue;
            }
            relations[j] = i;
            if (headers != null)
                headers[j] = name;
            j++;
        }
        cfg.setRelations(relations);
        cfg.setColumnSize(n);
        if (headers != null)
            cfg.setHeaders(headers);
        return cfg;
    }

    public static String underScoreName(String name) {
        if (StringUtils.isEmpty(name))
            return name;
        StringBuilder result = new StringBuilder();
        char charArr[] = name.toCharArray();
        char ch = charArr[0];
        if (Character.isUpperCase(ch))
            ch = Character.toLowerCase(ch);
        result.append(ch);
        for (int i = 1; i < charArr.length; i++) {
            ch = charArr[i];
            if (Character.isUpperCase(ch)) {
                result.append("_").append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * 通过配置，构造导出配置信息，header信息和行列位置索引与resultset数据索引映射
     *
     * @param n           导出列数
     * @param mp          导出列的配置信息，列的索引信息
     * @param pds         数据库响应数据信息，通过column名和item的配置名 建立映射
     * @param needHeaders 是否需要设置headers信息
     * @return 导出配置
     * @throws SQLException
     * @author YZC
     */
    public static ObjectExportHandler getExportConfig(int n,
                                                      Map<String, ImportTemplateItem> mp, PropertyDescriptor[] pds,
                                                      boolean needHeaders) throws SQLException {
        ObjectExportHandler cfg = new ObjectExportHandler();
        String headers[] = null;
        if (needHeaders)
            headers = new String[n];
        int relations[] = new int[n];
        ExportFormat formats[] = new ExportFormat[n];
        ImportTemplateItem itm;
        int size = pds.length;
        // 全部置入忽略标记
        for (int i = 0; i < n; i++) {
            relations[i] = -1;
        }
        PropertyDescriptor pd;
        String head, name;
        int idx;
        for (int i = 0; i < size; i++) {
            pd = pds[i];
            // name = pd.getName().toLowerCase();
            name = pd.getName();
            if ("class".equals(name))
                continue;
            name = underScoreName(name);
            itm = mp.get(name);
            // excel每行的索引对应resultset数据的索引
            if (itm != null && itm.getIndex() != -1) {
                idx = itm.getIndex();
                relations[idx] = i;
                if (StringUtils.isNotEmpty(itm.getRender()))
                    formats[idx] = getExportFormat(itm.getRender());
                if (headers != null) {
                    head = itm.getLabel();
                    if (StringUtils.isEmpty(head))
                        head = pd.getName();
                    headers[idx] = head;
                }
            }
        }
        // 设置序号列，特殊id名为"_idx"
        itm = mp.get("_idx");
        if (itm != null) {
            // 设置 关联索引为-2 ,写入的时候专门针对-2写入序号
            idx = itm.getIndex();
            headers[idx] = itm.getLabel();
            // 如果还是默认值，则设置-2标记是序号列
            if (relations[idx] == -1)
                relations[idx] = -2;
        }
        cfg.setRelations(relations);
        cfg.setFormats(formats);
        cfg.setColumnSize(n);
        if (headers != null)
            cfg.setHeaders(headers);
        return cfg;
    }

    public static MapExportHandler getMapExportConfig(String headers[],
                                                      String fields[]) throws SQLException {
        int n = headers.length;
        MapExportHandler cfg = new MapExportHandler();

        int relations[] = new int[n + 1];
        for (int i = 0; i < n; i++) {
            relations[i] = i;
        }
        cfg.setKeyIndexes(fields);
        cfg.setRelations(relations);
        cfg.setColumnSize(n);
        if (headers != null)
            cfg.setHeaders(headers);
        return cfg;
    }

    /**
     * 通过配置，构造导出配置信息，header信息和行列位置索引与resultset数据索引映射
     *
     * @param n           导出列数
     * @param mp          导出列的配置信息，列的索引信息
     * @param needHeaders 是否需要设置headers信息
     * @return 导出配置
     * @throws SQLException
     * @author YZC
     */
    public static MapExportHandler getMapExportConfig(
            Map<String, ImportTemplateItem> mp, int n, boolean needHeaders)
            throws SQLException {
        MapExportHandler cfg = new MapExportHandler();
        String headers[] = null;
        if (needHeaders)
            headers = new String[n];
        int relations[] = new int[n];
        ExportFormat formats[] = new ExportFormat[n];

        Collection<ImportTemplateItem> items = mp.values();
        ImportTemplateItem idxItm = mp.get("_idx");
        ImportTemplateItem itm;
        // 全部置入忽略标记
        for (int i = 0; i < n; i++) {
            relations[i] = -1;
        }
        String head;
        String fields[] = new String[n];
        Iterator<ImportTemplateItem> iterator = items.iterator();
        int i = 0;
        int idx;
        while (iterator.hasNext()) {
            itm = iterator.next();
            if (idxItm == itm) {
                idx = itm.getIndex();
                // 如果还是默认值，则设置-2标记是序号列
                if (relations[idx] == -1)
                    relations[idx] = -2;
                continue;
            }
            if (itm != null && itm.getIndex() != -1) {
                idx = itm.getIndex();
                relations[idx] = i;
                if (StringUtils.isNotEmpty(itm.getRender()))
                    formats[idx] = getExportFormat(itm.getRender());
                fields[i] = itm.getName();
                if (headers != null) {
                    head = itm.getLabel();
                    if (StringUtils.isEmpty(head))
                        head = itm.getLabel();
                    headers[idx] = head;
                }
            }
            i++;
        }
        cfg.setKeyIndexes(fields);
        cfg.setRelations(relations);
        cfg.setFormats(formats);
        cfg.setColumnSize(n);
        if (headers != null)
            cfg.setHeaders(headers);
        return cfg;
    }
}
