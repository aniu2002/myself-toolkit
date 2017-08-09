package com.sparrow.data.service.imports.dao;

import com.sparrow.core.log.SysLogger;
import com.sparrow.core.utils.file.FileToolHelper;
import com.sparrow.core.utils.file.FileUtils;
import com.sparrow.data.tools.store.FileStore;
import com.sparrow.orm.session.Session;
import com.sparrow.orm.session.StatementCallbackHandler;
import com.sparrow.orm.template.ExecuteCallback;
import com.sparrow.orm.template.simple.OperateTemplate;

import java.io.File;

/**
 * 导出模板类
 *
 * @author YZC
 * @version 1.0 (2014-3-14)
 * @modify
 */

public class ImportTemplateDaoImpl implements ImportTemplateDao {

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
     * @see com.sparrow.data.service.imports.dao.ImportTemplateDao#saveImportTemplate(java.lang.String,
     * java.io.File, java.io.File)
     */
    @Override
    public void saveImportTemplate(final String name, final File templateFile,
                                   final File excelFile) {
        if (templateFile.exists() && excelFile.exists())
            return;
        File file = FileStore.getXmlTemplateFile(name);
        FileToolHelper.copy(templateFile, file);
        file = FileStore.getExcelTemplateFile(name);
        FileToolHelper.copy(excelFile, file);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.imports.dao.ImportTemplateDao#getImportTemplateContent(java.lang.String)
     */
    @Override
    public String getImportTemplateContent(String name) {
        File file = FileStore.getXmlTemplateFile(name);
        SysLogger.info(" template file : " + file.getPath());
        if (file.exists())
            return FileUtils.readFileString(file);
        else
            return null;
    }

    /**
     * 判断模板名是否存在
     *
     * @param name 模板名
     * @return 存在与否
     * @author YZC
     */
    boolean existsTemplate(String name) {
        File file = FileStore.getExcelTemplateFile(name);
        return file.exists();
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.imports.dao.ImportTemplateDao#deleteImportTemplate(java.lang.String)
     */
    @Override
    public void deleteImportTemplate(String name) {
        File file = FileStore.getExcelTemplateFile(name);
        if (file.exists())
            file.delete();
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.imports.dao.ImportTemplateDao#updateImportTemplate(java.lang.String,
     * java.io.File, java.io.File)
     */
    @Override
    public void updateImportTemplate(final String name,
                                     final File templateFile, final File excelFile) {
        File file = FileStore.getXmlTemplateFile(name);
        FileToolHelper.copy(templateFile, file);
        file = FileStore.getExcelTemplateFile(name);
        FileToolHelper.copy(excelFile, file);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.imports.dao.ImportTemplateDao#loadExcelTemplate(java.lang.String,
     * java.io.File)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void loadExcelTemplate(String name, File destFile) throws Exception {

    }

    /**
     * 参照父类说明
     */
    @Override
    public void batchImport(final String sql, final StatementCallbackHandler callbackHandler) {
        this.operateTemplate.execute(new ExecuteCallback<Integer>() {
            @Override
            public Integer execute(Session session) throws Exception {
                return session.executeCallback(sql, callbackHandler);
            }
        });
    }
}
