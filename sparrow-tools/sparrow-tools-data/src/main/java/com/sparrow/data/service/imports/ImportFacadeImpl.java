package com.sparrow.data.service.imports;

import com.sparrow.core.config.SystemConfig;
import com.sparrow.data.service.imports.config.ImportConfigHelper;
import com.sparrow.data.service.imports.config.ImportConfiguration;
import com.sparrow.data.service.imports.dao.BatchPreparedStatementCallback;
import com.sparrow.data.service.imports.dao.ImportTemplateDao;
import com.sparrow.data.service.imports.data.ImportColumn;
import com.sparrow.data.service.imports.data.ImportResult;
import com.sparrow.data.service.imports.data.ImportTemplate;
import com.sparrow.data.service.imports.validate.ImportValidateErrorCallback;
import com.sparrow.data.tools.concurrency.StatusManager;
import com.sparrow.data.tools.imports.extract.DataExtractor;
import com.sparrow.data.tools.imports.extract.ExtractorBuilder;
import com.sparrow.data.tools.sql.ParsedSql;
import com.sparrow.data.tools.sql.SqlTool;
import com.sparrow.data.tools.store.FileType;
import com.sparrow.data.tools.validate.ValidateErrorCallback;
import com.sparrow.data.tools.validate.ValidatorManager;

import java.io.File;
import java.util.Map;

/**
 * 导入导出门面类，提供保存模板方法、提取excel模板输入文件、抽取excel文件数据根据模板配置，批量导入数据
 *
 * @author YZC
 * @version 1.0 (2014-3-17)
 * @modify
 */
public class ImportFacadeImpl implements ImportFacade {
    private static final String IMPORT_BIZ_NAME = "import";
    private StatusManager statusManager;
    private ValidatorManager validatorManager;
    private ImportConfiguration importConfiguration;
    private ImportTemplateDao importTemplateDao;

    public StatusManager getStatusManager() {
        return statusManager;
    }

    public void setStatusManager(StatusManager statusManager) {
        this.statusManager = statusManager;
    }

    public ValidatorManager getValidatorManager() {
        return validatorManager;
    }

    public void setValidatorManager(ValidatorManager validatorManager) {
        this.validatorManager = validatorManager;
    }

    public ImportConfiguration getImportConfiguration() {
        return importConfiguration;
    }

    public void setImportConfiguration(ImportConfiguration importConfiguration) {
        this.importConfiguration = importConfiguration;
    }

    public ImportTemplateDao getImportTemplateDao() {
        return importTemplateDao;
    }

    public void setImportTemplateDao(ImportTemplateDao importTemplateDao) {
        this.importTemplateDao = importTemplateDao;
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.imports.ImportFacade#saveTemplate(java.lang.String,
     * java.io.File, java.io.File)
     */
    @Override
    public void saveTemplate(String name, File importConfig, File excelTemplate) {
        this.importConfiguration.saveImportTemplate(name, importConfig,
                excelTemplate);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.imports.ImportFacade#getExcelTemplate(java.lang.String)
     */
    @Override
    public File getExcelTemplate(String name) throws Exception {
        return this.importConfiguration.getExcelTemplateFile(name);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.imports.ImportFacade#batchImport(java.io.File,
     * java.lang.String)
     */
    @Override
    public void batchImport(File excelImportFile, String name) {
        this.batchImport(excelImportFile, name, FileType.Excel, null);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.imports.ImportFacade#batchImport(java.io.File,
     * java.lang.String, java.util.Map)
     */
    @Override
    public ImportResult batchImport(File excelImportFile, String name,
                                    Map<String, Object> variables) {
        return this.batchImport(excelImportFile, name, FileType.Excel,
                variables);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.imports.ImportFacade#batchImport(java.io.File,
     * java.lang.String, com.sparrow.data.tools.store.FileType)
     */
    @Override
    public ImportResult batchImport(File importFile, String name,
                                    FileType importFileType) {
        return this.batchImport(importFile, name, importFileType, null);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.imports.ImportFacade#batchImport(java.io.File,
     * java.lang.String, com.sparrow.data.tools.store.FileType, java.util.Map)
     */
    @Override
    public ImportResult batchImport(File importFile, String name,
                                    FileType importFileType, Map<String, Object> variables) {
        ImportResult result = null;
        // 试图标记占有资源，若导入状态计数超过固定限制个数
        // 状态管理器视图标记该工作
        statusManager.mark(IMPORT_BIZ_NAME);
        try {
            ImportTemplate template = this.importConfiguration
                    .getImportTemplate(name);
            result = new ImportResult();
            // 解析import的批量sql，分析配置参数和全局参数
            ParsedSql parsedSql = SqlTool.parseSqlStatement(template.getSql());
            String actualSql = parsedSql.getActualSql();
            // 通过extractor够建器，创建文件拆分器
            DataExtractor extractor = ExtractorBuilder.create(importFile)
                    .fileType(importFileType)
                    .excelStartSheet(template.getStartSheet())
                    .maxRows(template.getSheetRows())
                    .columnLimit(template.getLimit())
                    .excelStartRow(template.getStartRow())
                    .excelStartColumn(template.getStartCol()).build();
            // 设置最大导入限制
            int maxImportSize = SystemConfig.getInt("max.import.size", 10000);
            // 错误校验回调类，错误处理和校验器分离
            ValidateErrorCallback callback = new ImportValidateErrorCallback(
                    1000);
            // 根据模板的sql参数和item配置，构建导入关系表，并设置校验handler
            ImportColumn[] importColumns = ImportConfigHelper.getParaRelations(
                    template, parsedSql.getParameters(), this.validatorManager,
                    callback);
            // JDBC批量处理callback
            BatchPreparedStatementCallback statementCallback = new BatchPreparedStatementCallback(
                    importColumns, extractor, variables);
            statementCallback.setMaxImportSize(maxImportSize);
            this.importTemplateDao.batchImport(actualSql, statementCallback);
            // 响应信息记录
            result.setName(template.getLabel());
            result.setOk(!callback.hasError());
            result.setTotalRecords(statementCallback.getRecords());
            // validateError错误列表
            result.setResult(callback.getResult());
            result.setSuccessNum(statementCallback.getEffects());
            result.setFailureNum(callback.getErrors());
        } finally {
            // 取消标记，释放资源
            statusManager.unmark(IMPORT_BIZ_NAME);
        }
        return result;
    }
}
