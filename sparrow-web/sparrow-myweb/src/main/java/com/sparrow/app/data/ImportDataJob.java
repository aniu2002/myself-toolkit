package com.sparrow.app.data;

import com.sparrow.data.service.imports.ImportFacade;
import com.sparrow.data.service.imports.data.ImportResult;
import com.sparrow.data.tools.message.ProcessMessageHolder;
import com.sparrow.data.tools.message.ProcessMessage;
import com.sparrow.data.tools.message.ProcessResult;
import com.sparrow.data.tools.store.FileType;

import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/17 0017.
 */
public class ImportDataJob implements Runnable {
    private ImportFacade importFacade;
    private Map<String, Object> variables;
    private String template;
    private FileType fileType;
    private File importFile;
    private ProcessMessage processMessage;


    public ImportDataJob(ImportFacade importFacade, String template, FileType fileType, File importFile) {
        this(importFacade, template, fileType, importFile, null);
    }

    public ImportDataJob(ImportFacade importFacade, String template, FileType fileType, File importFile, Map<String, Object> variables) {
        this(importFacade, template, fileType, importFile, variables, null);
    }

    public ImportDataJob(ImportFacade importFacade, String template, FileType fileType, File importFile, Map<String, Object> variables, ProcessMessage processMessage) {
        this.importFacade = importFacade;
        this.template = template;
        this.fileType = fileType;
        this.importFile = importFile;
        this.variables = variables;
        this.processMessage = processMessage;
    }

    @Override
    public void run() {
        try {
            if (this.processMessage != null) {
                ProcessMessageHolder.set(this.processMessage);
                ImportResult importResult = this.doImport();
                ProcessResult result;
                if (importResult.isOk())
                    result = new ProcessResult(0, "导入" + importResult.getSuccessNum() + "条");
                else
                    result = new ProcessResult(-1, "导入失败" + importResult.getFailureNum() + "条");
                this.processMessage.end(result);
                ProcessMessageHolder.clear();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public ImportResult doImport() {
        return this.importFacade.batchImport(this.importFile, this.template, this.fileType, this.variables);
    }
}
