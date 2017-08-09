package com.sparrow.app.data.source;

import com.sparrow.common.source.SourceManager;
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.BeanWrapper;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.FreeMarkerResponse;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.orm.page.PageResult;
import com.sparrow.app.services.source.DataSourceStore;
import com.sparrow.app.services.source.SourceInfo;

import java.util.List;

public class DataSourceCommand extends BaseCommand {
    private DataSourceStore dbStore;

    public DataSourceCommand() {
        dbStore = new DataSourceStore();
        SourceManager.regSourceHandler("dbs", dbStore);
    }

    @Override
    public Response doPost(Request request) {
        SourceInfo sourceInfo = BeanWrapper.wrapBean(SourceInfo.class, request);
        this.dbStore.addDataSource(sourceInfo);
        return OkResponse.OK;
    }

    @Override
    public Response doPut(Request request) {
        SourceInfo sourceInfo = BeanWrapper.wrapBean(SourceInfo.class, request);
        this.dbStore.updateDataSource(sourceInfo);
        return OkResponse.OK;
    }

    @Override
    public Response doDelete(Request request) {
        long id = request.getLong("id");
        this.dbStore.removeDataSource(id);
        return OkResponse.OK;
    }

    @Override
    public Response doGet(Request request) {
        String t = request.get("_t");
        if ("edit".equals(t)) {
            return new FreeMarkerResponse("#db/sys_db_edit", request.getParas());
        } else if ("detail".equals(t)) {
            return new FreeMarkerResponse("#db/sys_db_detail", request.getParas());
        } else if ("data".equals(t)) {
            long id = request.getLong("id");
            SourceInfo info = this.dbStore.getDataSource(id);
            return new JsonResponse(info);
        } else {
            List<?> rows = this.dbStore.list();
            PageResult page = new PageResult();
            page.setRows(rows);
            page.setTotal(rows.size());
            return new JsonResponse(page);
        }
    }
}


