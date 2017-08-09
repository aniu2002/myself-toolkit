package com.sparrow.data.service.exports;

import com.sparrow.data.tools.store.FileType;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 导入导出门面类，提供保存模板方法、提取excel模板输入文件、抽取excel文件数据根据模板配置，批量导入数据
 *
 * @author YZC
 * @version 1.0 (2014-3-17)
 * @modify
 */
public interface ExportFacade {
    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param excelExportFile excel导出文件
     * @param name            导出文件配置模板名
     * @author YZC
     */
    void batchExport(File excelExportFile, String name);

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param excelExportFile excel导出文件
     * @param name            导出文件配置模板名
     * @param variables       导出模板配置sql的参数
     * @author YZC
     */
    void batchExport(File excelExportFile, String name,
                     Map<String, Object> variables);

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param excelExportFile excel导出文件
     * @param name            导出文件配置模板名
     * @param exportFileType  文件类型（csv,excel)
     * @param variables       导出模板配置sql的参数
     * @author YZC
     */
    void batchExport(File excelExportFile, String name,
                     FileType exportFileType, Map<String, Object> variables);

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param name 导出文件配置模板名
     * @author YZC
     */
    File batchExport(String name);

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param name      导出文件配置模板名
     * @param variables 导出模板配置sql的参数
     * @author YZC
     */
    File batchExport(String name, Map<String, Object> variables);

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param name           导出文件配置模板名
     * @param exportFileType 文件类型（csv,excel)
     * @author YZC
     */
    File batchExport(String name, FileType exportFileType);

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param name           导出文件配置模板名
     * @param exportFileType 文件类型（csv,excel)
     * @param variables      导出模板配置sql的参数
     * @author YZC
     */
    File batchExport(String name, FileType exportFileType,
                     Map<String, Object> variables);

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param list 导出对象列表，无模板就以自然顺序将可以访问的属性全部导出
     * @author YZC
     */
    File export(List<?> list);

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param list           导出对象列表，无模板就以自然顺序将可以访问的属性全部导出
     * @param exportFileType 文件类型（csv,excel)
     * @param enableZip      是否压缩成zip
     * @return 处理后的文件
     * @author YZC
     */
    @Deprecated
    File export(List<?> list, FileType exportFileType, boolean enableZip);

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param name 导出文件配置模板名
     * @param list 导出对象列表，无模板就以自然顺序将可以访问的属性全部导出
     * @author YZC
     */
    File export(String name, List<?> list);

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param name           导出文件配置模板名
     * @param exportFileType 文件类型（csv,excel)
     * @param list           导出对象列表，无模板就以自然顺序将可以访问的属性全部导出
     * @author YZC
     */
    File export(String name, FileType exportFileType, List<?> list);

    /**
     * 根据sql，执行批量导出数据;适合在sql查出的数据比较多的情况下
     *
     * @param sql            导出文件配置模板名
     * @param exportFileType 文件类型（csv,excel)
     * @param zip            是否zip压缩
     * @return 返回执行后的文件
     * @author YZC
     */
    File exportWithSql(String sql, FileType exportFileType, boolean zip);

    /**
     * 根据sql，执行批量导出数据;适合在sql查出的数据比较多的情况下
     *
     * @param sql            导出文件配置模板名
     * @param exportFileType 文件类型（csv,excel)
     * @param arguments      导出sql的参数
     * @param zip            是否zip压缩
     * @return 返回执行后的文件
     * @author YZC
     */
    File exportWithSql(String sql, FileType exportFileType, Object arguments[],
                       boolean zip);
}
