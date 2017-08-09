package com.sparrow.app.data;

import com.sparrow.core.utils.PathResolver;
import com.sparrow.core.utils.date.DateUtils;
import com.sparrow.core.utils.file.FileToolHelper;
import com.sparrow.data.service.exports.ExportFacade;
import com.sparrow.data.tools.message.ProcessMessageHolder;
import com.sparrow.data.tools.message.ProcessMessage;
import com.sparrow.data.tools.message.ProcessResult;
import com.sparrow.data.tools.store.FileStore;
import com.sparrow.data.tools.store.FileType;

import java.io.File;
import java.util.Map;

/**
 * Created by Administrator on 2016/3/17 0017.
 */
public class ExportDataJob implements Runnable {
    private ExportFacade exportFacade;
    private Map<String, Object> variables;
    private String template;
    private FileType fileType;
    private ProcessMessage processMessage;


    public ExportDataJob(ExportFacade exportFacade, String template, FileType fileType) {
        this(exportFacade, template, fileType, null);
    }

    public ExportDataJob(ExportFacade exportFacade, String template, FileType fileType, Map<String, Object> variables) {
        this(exportFacade, template, fileType, variables, null);
    }

    public ExportDataJob(ExportFacade exportFacade, String template, FileType fileType, Map<String, Object> variables, ProcessMessage processMessage) {
        this.exportFacade = exportFacade;
        this.template = template;
        this.fileType = fileType;
        this.variables = variables;
        this.processMessage = processMessage;
    }

    @Override
    public void run() {
        try {
            if (this.processMessage != null) {
                ProcessMessageHolder.set(this.processMessage);
                String path = this.doExport();
                ProcessResult result = new ProcessResult(0, path);
                this.processMessage.end(result);
                ProcessMessageHolder.clear();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public String doExport() {
        File file = this.exportFacade.batchExport(this.template, this.fileType, this.variables);
        File to = FileStore.getExportFile("temp");
        if (!to.exists()) to.mkdir();
        String name = DateUtils.currentTime("yy-MM-dd-HHmmss") + "." + PathResolver.getExtension(file.getName());
        FileToolHelper.copy(file, new File(to, name));
        String path = "temp/" + name;
        return path;
    }
}
