package com.sparrow.data.service.imports.config;

import java.io.File;
import java.util.List;

import com.sparrow.common.source.OptionItem;
import com.sparrow.data.service.imports.data.ImportTemplate;

/**
 * 导入导出配置管理类
 *
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
public interface ImportConfiguration {
    /**
     * 保存导入导出配置模板，<br/>
     * 1)name查询名，<br/>
     * 2)importConfig是xml的配置参数，可以携带多个配置，查询的时候 name:config1定位xml的config配置<br/>
     * 3)excelTemplate主要是人工指定的excel导入导出模板<br/>
     *
     * @param name          name查询名
     * @param importConfig  importConfig是xml的配置参数
     * @param excelTemplate excel导入导出模板
     * @author YZC
     */
    void saveImportTemplate(String name, File importConfig, File excelTemplate);

    List<OptionItem> getTemplates(boolean imp);

    /**
     * 获取导入导出模板,描述相关导入参数配置,
     *
     * @param name 模板名
     * @return 模板配置信息
     * @author YZC
     */
    ImportTemplate getImportTemplate(String name);

    /**
     * 删除模板信息
     *
     * @param name 模板名
     * @author YZC
     */
    void deleteImportTemplate(String name);

    /**
     * 更新保存导入导出配置模板，<br/>
     *
     * @param name          name查询名
     * @param importConfig  importConfig是xml的配置参数
     * @param excelTemplate excel导入导出模板
     * @author YZC
     */
    void updateImportTemplate(String name, File importConfig, File excelTemplate);

    /**
     * 根据模板名称获取excel模板的文件，并保存在本地
     *
     * @param name name查询名
     * @return excel的导入模板
     * @throws Exception 可能抛出io异常
     * @author YZC
     */
    File getExcelTemplateFile(String name) throws Exception;
}
