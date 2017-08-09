package com.sparrow.data.service.imports.concurrency;

import com.sparrow.data.service.imports.config.ImportConfiguration;
import com.sparrow.data.service.imports.data.ImportTemplate;
import com.sparrow.data.tools.imports.ImpSetting;
import com.sparrow.data.tools.imports.reader.DataReader;
import com.sparrow.data.tools.imports.reader.DataReaderBuilder;
import com.sparrow.data.tools.store.FileType;
import com.sparrow.orm.template.simple.OperateTemplate;

import java.io.File;
import java.util.Map;


public class ConcurrentImportFacade {

    private OperateTemplate operateTemplate;

    public OperateTemplate getOperateTemplate() {
        return operateTemplate;
    }

    public void setOperateTemplate(OperateTemplate operateTemplate) {
        this.operateTemplate = operateTemplate;
    }

    private ImportConfiguration importConfiguration;

    public void _concurrentImport(File excelImportFile, String name,
                                  Map<String, Object> variables) {
        ImportTemplate template = this.importConfiguration
                .getImportTemplate(name);
        ImpSetting setting = new ImpSetting();
        setting.setLimit(template.getLimit());
        setting.setStartCol(template.getStartCol());
        setting.setStartRow(template.getStartRow());
        setting.setStartSheet(template.getStartSheet());

        DataReader reader = DataReaderBuilder.create(excelImportFile)
                .setType(FileType.Excel).setSetting(setting).build();

        ConcurrentImport concurrentImport = new ConcurrentImport(template,
                variables);
        concurrentImport.setOperateTemplate(this.operateTemplate);
        concurrentImport.setReader(reader);
        concurrentImport.doImport();
    }

    public void _concurrentImportX(File excelImportFile, String name,
                                   Map<String, Object> variables) {
        ImportTemplate template = this.importConfiguration
                .getImportTemplate(name);

        ImpSetting setting = new ImpSetting();
        setting.setLimit(template.getLimit());
        setting.setStartCol(template.getStartCol());
        setting.setStartRow(template.getStartRow());
        setting.setStartSheet(template.getStartSheet());

        DataReader reader = DataReaderBuilder.create(excelImportFile)
                .setType(FileType.Excel).setSetting(setting).build();
        reader.setImpSetting(setting);

        ConcurrentStatementsImport concurrentImport = new ConcurrentStatementsImport(
                template, variables);
        concurrentImport.setConnection(this.operateTemplate.getSessionFactory().getConnection());
        concurrentImport.setReader(reader);
        concurrentImport.doImport();
    }
}
