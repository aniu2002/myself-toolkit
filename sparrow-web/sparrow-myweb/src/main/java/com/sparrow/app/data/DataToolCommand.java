package com.sparrow.app.data;

/* package com.sparrow.app.play.command.lf; */
 


/* import com.sparrow.app.play.domain.lf.LfMembers; */

import com.sparrow.data.service.exports.ExportFacade;
import com.sparrow.data.service.exports.format.ExportFormat;
import com.sparrow.data.service.imports.ImportFacade;
import com.sparrow.data.service.imports.config.ImportConfigHelper;
import com.sparrow.data.service.imports.config.ImportConfiguration;
import com.sparrow.data.tools.concurrency.ConcurrencyJobManager;
import com.sparrow.data.tools.message.MessageManager;
import com.sparrow.data.tools.message.ProcessMessage;
import com.sparrow.data.tools.message.ProcessStatus;
import com.sparrow.data.tools.store.FileType;
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.MsgResponse;
import com.sparrow.http.command.resp.OkResponse;

import java.io.File;
import java.util.Map;

/**
 * 完成(lf_members-)的基本操作<br/>
 * <p/>
 * lf_members:
 *
 * @author YZC
 * @version 2.0
 *          date: 2016-02-22 18:19:18
 */
public class DataToolCommand extends BaseCommand {
    private static final Map<String, Object> variables = null;
    private ImportFacade importFacade;
    private ExportFacade exportFacade;
    private ImportConfiguration importConfiguration;

    {
        ImportConfigHelper.addExportFormat("lev", new ExportFormat() {
            @Override
            public String format(String value) {
                if ("0".equals(value))
                    return "不推荐";
                else if ("1".equals(value))
                    return "一般";
                else if ("2".equals(value))
                    return "可以";
                else if ("3".equals(value))
                    return "很好";
                else
                    return "非常不错";
            }
        });
    }

    public void setExportFacade(ExportFacade exportFacade) {
        this.exportFacade = exportFacade;
    }

    public void setImportFacade(ImportFacade importFacade) {
        this.importFacade = importFacade;
    }

    public ImportConfiguration getImportConfiguration() {
        return importConfiguration;
    }

    public void setImportConfiguration(ImportConfiguration importConfiguration) {
        this.importConfiguration = importConfiguration;
    }

    public Response doPost(Request request) {
        String t = request.get("_t");
        String template = request.get("template");
        String type = request.get("type");
        boolean syn = "true".equals(request.get("syn"));
        FileType fileType = FileType.getFileType(type);
        if ("imp".equals(t)) {
            String importFile = request.get("file");
            ImportDataJob job;
            if (syn) {
                job = new ImportDataJob(this.importFacade, template, fileType, new File(importFile), DataImportHolder.get(), null);
                return new JsonResponse(job.doImport());
            } else {
                String id = String.valueOf(System.nanoTime());
                ProcessMessage processMessage = MessageManager.newMessage(id);
                job = new ImportDataJob(this.importFacade, template, fileType, new File(importFile), DataImportHolder.get(), processMessage);
                processMessage.begin();
                ConcurrencyJobManager.getImportJobManager().submit(job);
                return new MsgResponse(0, id);
            }
        } else if ("exp".equals(t))
            return this.doExport(template, fileType, syn);
        return OkResponse.OK;
    }

    Response doExport(String template, FileType fileType, boolean syn) {
        ExportDataJob job;
        if (syn) {
            job = new ExportDataJob(this.exportFacade, template, fileType, DataImportHolder.get());
            return new MsgResponse(0, job.doExport());
        } else {
            String id = String.valueOf(System.nanoTime());
            ProcessMessage processMessage = MessageManager.newMessage(id);
            job = new ExportDataJob(this.exportFacade, template, fileType, DataImportHolder.get(), processMessage);
            processMessage.begin();
            ConcurrencyJobManager.getImportJobManager().submit(job);
            return new MsgResponse(0, id);
        }
    }

    public Response doGet(Request request) {
        String t = request.get("_t");
        String template = request.get("template");
        String type = request.get("type");
        FileType fileType = FileType.getFileType(type);
        boolean syn = "true".equals(request.get("syn"));
        if ("exp".equals(t))
            return this.doExport(template, fileType, syn);
        else if ("imp".equals(t))
            return new JsonResponse(this.importConfiguration.getTemplates(true));
        else if ("emp".equals(t))
            return new JsonResponse(this.importConfiguration.getTemplates(false));
        else if ("q".equals(t)) {
            String id = request.get("sid");
            ProcessMessage processMessage = MessageManager.getMessage(id);
            if (processMessage != null) {
                ProcessStatus processStatus = processMessage.fetchData();
                if (processStatus != null)
                    return new JsonResponse(processStatus);
                else
                    return new MsgResponse(1, "process completed !");
            }
            return new MsgResponse(1, "no process");
        }
        return OkResponse.OK;
    }
}