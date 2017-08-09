package com.sparrow.data.service.imports.dao;

import com.sparrow.orm.session.StatementCallbackHandler;

import java.io.File;

/**
 * 导入导出配置管理类
 *
 * @author YZC
 * @version 1.0 (2014-3-27)
 * @modify
 */
public interface ImportTemplateDao {
    /**
     * 保存导入导出配置模板，<br/>
     * 1)name查询名，<br/>
     * 2)importConfig是xml的配置参数，可以携带多个配置，查询的时候 name:config1定位xml的config配置<br/>
     * 3)excelTemplate主要是人工指定的excel导入导出模板<br/>
     *
     * @param name         name查询名
     * @param templateFile xml的配置文件
     * @param excelFile    excel导入导出模板文件
     * @author YZC
     */
    void saveImportTemplate(String name, File templateFile, File excelFile);

    /**
     * 根据name获取导入导出模板文本,描述相关导入参数配置,
     *
     * @param name 模板名
     * @return 模板配置信息
     * @author YZC
     */
    String getImportTemplateContent(String name);

    /**
     * 根据name删除模板信息
     *
     * @param name 模板名
     * @author YZC
     */
    void deleteImportTemplate(String name);

    /**
     * 更新保存导入导出配置模板，<br/>
     *
     * @param name         name查询名
     * @param templateFile xml的配置文件
     * @param excelFile    excel导入导出模板文件
     * @author YZC
     */
    void updateImportTemplate(String name, File templateFile, File excelFile);

    /**
     * 根据模板名称获取excel模板的文件，并保存在本地
     *
     * @param name     name查询名
     * @param destFile excel的导入模板
     * @throws Exception 可能抛出io异常
     * @author YZC
     */
    void loadExcelTemplate(String name, File destFile) throws Exception;

    /**
     * 批量导入
     *
     * @param sql                       导入sql
     * @param preparedStatementCallback preparedSatemet预编译处理
     * @author YZC
     */
    void batchImport(String sql, StatementCallbackHandler preparedStatementCallback);

}
