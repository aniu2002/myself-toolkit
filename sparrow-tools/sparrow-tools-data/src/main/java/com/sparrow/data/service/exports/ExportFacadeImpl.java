package com.sparrow.data.service.exports;

import com.sparrow.data.service.MessageException;
import com.sparrow.data.service.exports.dao.BatchExportDao;
import com.sparrow.data.service.exports.format.ExportFormat;
import com.sparrow.data.service.exports.handler.MapExportHandler;
import com.sparrow.data.service.exports.handler.ObjectExportHandler;
import com.sparrow.data.service.imports.config.ImportConfigHelper;
import com.sparrow.data.service.imports.config.ImportConfiguration;
import com.sparrow.data.service.imports.data.ImportTemplate;
import com.sparrow.data.tools.compress.ZipCompress;
import com.sparrow.data.tools.concurrency.StatusManager;
import com.sparrow.data.tools.exports.writer.DataWriter;
import com.sparrow.data.tools.exports.writer.DataWriterBuilder;
import com.sparrow.data.tools.store.FileType;
import com.sparrow.data.tools.store.StoreManager;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 导入导出门面类，提供保存模板方法、提取excel模板输入文件、抽取excel文件数据根据模板配置，批量导入数据
 *
 * @author YZC
 * @version 1.0 (2014-3-17)
 * @modify
 */
public class ExportFacadeImpl implements ExportFacade {
    private static final String EXPORT_BIZ_NAME = "export";
    private StatusManager statusManager;
    private StoreManager storeManager;
    private ImportConfiguration importConfiguration;
    private BatchExportDao batchExportDao;
    private Map<String, ExportFormat> formatMap;

    public Map<String, ExportFormat> getFormatMap() {
        return formatMap;
    }

    public void setFormatMap(Map<String, ExportFormat> formatMap) {
        this.formatMap = formatMap;
        ImportConfigHelper.setFormatMap(formatMap);
    }

    public StatusManager getStatusManager() {
        return statusManager;
    }

    public void setStatusManager(StatusManager statusManager) {
        this.statusManager = statusManager;
    }

    public StoreManager getStoreManager() {
        return storeManager;
    }

    public void setStoreManager(StoreManager storeManager) {
        this.storeManager = storeManager;
    }

    public ImportConfiguration getImportConfiguration() {
        return importConfiguration;
    }

    public void setImportConfiguration(ImportConfiguration importConfiguration) {
        this.importConfiguration = importConfiguration;
    }

    public BatchExportDao getBatchExportDao() {
        return batchExportDao;
    }

    public void setBatchExportDao(BatchExportDao batchExportDao) {
        this.batchExportDao = batchExportDao;
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.exports.ExportFacade#batchExport(java.io.File,
     * java.lang.String)
     */
    @Override
    public void batchExport(File excelExportFile, String name) {
        this.batchExport(excelExportFile, name, null);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.exports.ExportFacade#batchExport(java.io.File,
     * java.lang.String, java.util.Map)
     */
    @Override
    public void batchExport(File excelExportFile, String name,
                            Map<String, Object> variables) {
        this.batchExport(excelExportFile, name, FileType.Excel, variables);
    }

    @Override
    public void batchExport(File excelExportFile, String name,
                            FileType exportFileType, Map<String, Object> variables) {
        try {
            // 状态管理器视图标记该工作
            statusManager.mark(EXPORT_BIZ_NAME);
            this.doBatchExport(excelExportFile, name, exportFileType, variables);
        } finally {
            // 取消标记，释放资源
            statusManager.unmark(EXPORT_BIZ_NAME);
        }
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.exports.ExportFacade#batchExport(java.lang.String)
     */
    @Override
    public File batchExport(String name) {
        return this.batchExport(name, FileType.Excel, null);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.exports.ExportFacade#batchExport(java.lang.String,
     * java.util.Map)
     */
    @Override
    public File batchExport(String name, Map<String, Object> variables) {
        return this.batchExport(name, FileType.Excel, variables);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.exports.ExportFacade#batchExport(java.lang.String,
     * com.sparrow.data.tools.store.FileType)
     */
    @Override
    public File batchExport(String name, FileType exportFileType) {
        return this.batchExport(name, exportFileType, null);
    }

    /**
     * 参照父类说明
     *
     * @see com.sparrow.data.service.exports.ExportFacade#batchExport(java.lang.String,
     * com.sparrow.data.tools.store.FileType, java.util.Map)
     */
    @Override
    public File batchExport(String name, FileType exportFileType,
                            Map<String, Object> variables) {
        File exportFile = null;
        try {
            // 状态管理器视图标记该工作,当进入的个数过多并且未释放时，抛出异常，提示等待处理
            statusManager.mark(EXPORT_BIZ_NAME);
            exportFile = storeManager.getFile(name, exportFileType.getType());
            this.doBatchExport(exportFile, name, exportFileType, variables);
        } finally {
            // 取消标记，释放资源
            statusManager.unmark(EXPORT_BIZ_NAME);
        }
        return exportFile;
    }

    /**
     * 抽取excel文件数据根据模板配置，执行批量导出数据
     *
     * @param excelExportFile excel导出文件
     * @param name            导出文件配置模板名
     * @param exportFileType  文件类型（csv,excel)
     * @param variables       导出模板配置sql的参数
     * @author YZC
     */
    void doBatchExport(File excelExportFile, String name,
                       FileType exportFileType, Map<String, Object> variables) {
        if (StringUtils.isEmpty(name))
            throw new MessageException("批量导出", "导入导出模板名为空");
        ImportTemplate template = this.importConfiguration
                .getImportTemplate(name);
        if (template == null)
            throw new MessageException("批量导出", "导入导出模板[" + name + "]不存在");
        // 有模板文件，就根据模板文件导出,拷贝模板到指定目录
        try {
            DataWriter dataWriter = this.createDataWriter(template,
                    excelExportFile, exportFileType);
            this.batchExportDao.batchExport(template.getExportSql(), variables,
                    dataWriter, template);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void doBatchExportWithSql(String sql, Object arguments[],
                              DataWriter dataWriter, ImportTemplate template) {
        // 有模板文件，就根据模板文件导出,拷贝模板到指定目录
        try {
            this.batchExportDao.batchExport(sql, arguments, dataWriter,
                    template);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public File export(List<?> list) {
        return this.export(null, FileType.Excel, list);
    }

    @Override
    public File export(String name, List<?> list) {
        return this.export(name, FileType.Excel, list);
    }

    @Override
    public File export(String name, FileType exportFileType, List<?> list) {
        File exportFile = null;
        try {
            // 状态管理器视图标记该工作,当进入的个数过多并且未释放时，抛出异常，提示等待处理
            statusManager.mark(EXPORT_BIZ_NAME);
            exportFile = storeManager.getFile(name, exportFileType.getType());
            this.doExport(exportFile, name, exportFileType, list);
        } finally {
            // 取消标记，释放资源
            statusManager.unmark(EXPORT_BIZ_NAME);
        }
        return exportFile;
    }

    void doExport(MapExportHandler handler, DataWriter dataWriter,
                  List<Map<String, Object>> list, int len) throws IOException {
        String data[] = new String[len];
        int row = 1;
        for (Map<String, Object> obj : list) {
            handler.fillData(obj, data, row++);
            dataWriter.writeRow(data);
        }
    }

    void doExport(ObjectExportHandler handler, DataWriter dataWriter,
                  List<?> list, int len) throws IOException {
        String data[] = new String[len];
        int row = 1;
        for (Object obj : list) {
            handler.fillData(obj, data, row++);
            dataWriter.writeRow(data);
        }
    }

    /**
     * 导出数据列表
     *
     * @param excelExportFile
     * @param name
     * @param exportFileType
     * @param list
     * @author YZC
     */
    @SuppressWarnings("unchecked")
    void doExport(File excelExportFile, String name, FileType exportFileType,
                  List<?> list) {
        if (list == null || list.isEmpty())
            return;
        ImportTemplate template = StringUtils.isEmpty(name) ? null
                : this.importConfiguration.getImportTemplate(name);
        // 有模板文件，就根据模板文件导出,拷贝模板到指定目录
        try {
            Class<?> clz = list.get(0).getClass();
            DataWriter dataWriter = this.createDataWriter(template,
                    excelExportFile, exportFileType);
            String headers[];
            if (Map.class.isAssignableFrom(clz)) {
                if (template == null)
                    throw new RuntimeException("导出模板未配置");
                MapExportHandler h = ImportConfigHelper
                        .getMapExportConfig(template);
                if (h == null)
                    throw new RuntimeException("导出模板配置导出项错误:"
                            + template.getName());
                headers = h.getHeaders();
                dataWriter.setHeaders(headers);
                dataWriter.open();
                this.doExport(h, dataWriter, (List<Map<String, Object>>) list,
                        headers.length);
            } else {
                ObjectExportHandler h = ImportConfigHelper.getExportConfig(clz,
                        template);
                headers = h.getHeaders();
                dataWriter.setHeaders(headers);
                dataWriter.open();
                this.doExport(h, dataWriter, list, headers.length);
            }
            dataWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    DataWriter createDataWriter(String name, File excelExportFile,
                                FileType exportFileType) throws Exception {
        ImportTemplate template = StringUtils.isEmpty(name) ? null
                : this.importConfiguration.getImportTemplate(name);
        File templateFile = null;
        if (template != null) {
            templateFile = this.importConfiguration.getExcelTemplateFile(name);
            // templateFile=storeManager.getFile(name);
            // FileUtils.copyFile(file, excelExportFile);
        }
        return this.createDataWriter(excelExportFile, templateFile, template,
                exportFileType);
    }

    DataWriter createDataWriter(ImportTemplate template, File excelExportFile,
                                FileType exportFileType) throws Exception {
        File templateFile = null;
        if (template != null) {
            templateFile = this.importConfiguration
                    .getExcelTemplateFile(template.getName());
        }
        return this.createDataWriter(excelExportFile, templateFile, template,
                exportFileType);
    }

    DataWriter createDataWriter(File excelExportFile, File templateFile,
                                ImportTemplate template, FileType exportFileType) {
        DataWriter writer;
        boolean hasTemplate = (templateFile != null && templateFile.exists());
        if (templateFile != null)
            writer = DataWriterBuilder.create(excelExportFile)
                    .excelStartSheet(template.getStartSheet())
                    .excelStartRow(template.getStartRow())
                    .exportMax(template.getExportMax())
                    .excelSheetRows(template.getSheetRows())
                    .fileType(exportFileType).excelModify(hasTemplate)
                    .excelTemplateFile(templateFile).build();
        else
            writer = DataWriterBuilder.create(excelExportFile)
                    .fileType(exportFileType).excelModify(hasTemplate)
                    .excelTemplateFile(templateFile).build();
        return writer;
    }

    @Override
    public File exportWithSql(String sql, FileType exportFileType, boolean zip) {
        return this.exportWithSql(sql, exportFileType, null, zip);
    }

    @Override
    public File exportWithSql(String sql, FileType exportFileType,
                              Object arguments[], boolean zip) {
        String name = "BatchExport";
        File exportFile = storeManager.getFile(name, exportFileType.getType());
        DataWriter dataWriter = this.createDataWriter(exportFile, null, null,
                exportFileType);
        this.doBatchExportWithSql(sql, arguments, dataWriter, null);
        if (zip && exportFileType == FileType.Csv) {
            File nFile = this.storeManager.getFile(name, ".zip");
            try {
                ZipCompress.doCompress(exportFile, nFile);
                exportFile.delete();
                return nFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return exportFile;
    }

    @Override
    public File export(List<?> list, FileType exportFileType, boolean enableZip) {
        return null;
    }
}
