package com.sparrow.app.data.app;

import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.BeanWrapper;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.FreeMarkerResponse;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.orm.page.PageResult;
import com.sparrow.app.services.provider.*;
import com.sparrow.common.source.SourceManager;

import java.util.List;

public class DataProviderCommand extends BaseCommand {
    public static ProviderStore providerStore = new ProviderStore();

    public DataProviderCommand() {
        SourceManager.regSourceHandler("srcCfg", providerStore);
    }

    @Override
    public Response doPost(Request request) {
        String _t = request.get("_t");
        if ("sp".equals(_t)) {
            providerStore.saveProviderSource(request.get("app"));
            return OkResponse.OK;
        } else if ("ss".equals(_t)) {
            providerStore.saveSourceConfig(request.get("app"));
            return OkResponse.OK;
        } else if ("sf".equals(_t)) {
            SourceConfig item = BeanWrapper.wrapBean(SourceConfig.class, request);
            providerStore.addSourceConfig(request.get("app"), item);
            return OkResponse.OK;
        } else {
            ProviderItem item = BeanWrapper.wrapBean(ProviderItem.class, request);
            providerStore.addProviderItem(request.get("app"), item);
            providerStore.saveProviderSource(request.get("app"));
            return OkResponse.OK;
            //new RedirectResponse("/cmd/sys/pdc?_t=gp&source=" + item.getSource() + "&label=" + item.getDesc() + "&app=" + item.getName());
        }
    }

    @Override
    public Response doPut(Request request) {
        String _t = request.get("_t");
        if ("sf".equals(_t)) {
            SourceConfig item = BeanWrapper.wrapBean(SourceConfig.class, request);
            providerStore.updateSourceConfig(request.get("app"), item);
            return OkResponse.OK;
        } else {
            ProviderItem item = BeanWrapper.wrapBean(ProviderItem.class, request);
            providerStore.updateProviderItem(request.get("app"), item);
            providerStore.saveProviderSource(request.get("app"));
            return OkResponse.OK;
        }
    }

    @Override
    public Response doDelete(Request request) {
        String _t = request.get("_t");
        List<String> names = request.getStringList("id");
        if ("sf".equals(_t)) {
            if (names != null) {
                for (String name : names)
                    providerStore.deleteSourceConfig(request.get("app"), name);
            }
            return OkResponse.OK;
        } else {
            if (names != null) {
                for (String name : names)
                    providerStore.deleteProviderItem(request.get("app"), name);
            }
            return OkResponse.OK;
        }
    }

    @Override
    public Response doGet(Request request) {
        String t = request.get("_t");
        if ("sl".equals(t)) {
            return new FreeMarkerResponse("#provider/source_list", request.getParas());
        } else if ("se".equals(t)) {
            return new FreeMarkerResponse("#provider/source_edit", request.getParas());
        } else if ("sa".equals(t)) {
            return new FreeMarkerResponse("#provider/source_add", request.getParas());
        } else if ("sd".equals(t)) {
            return new FreeMarkerResponse("#provider/source_detail", request.getParas());
        } else if ("list".equals(t)) {
            return new FreeMarkerResponse("#provider/provider_list", request.getParas());
        } else if ("add".equals(t)) {
            return new FreeMarkerResponse("#provider/provider_add", request.getParas());
        } else if ("edit".equals(t)) {
            return new FreeMarkerResponse("#provider/provider_edit", request.getParas());
        } else if ("detail".equals(t)) {
            return new FreeMarkerResponse("#provider/provider_detail", request.getParas());
        } else if ("ds".equals(t)) {
            SourceConfig info = providerStore.getSourceConfig(request.get("app"), request.get("name"));
            return new JsonResponse(info);
        } else if ("dp".equals(t)) {
            ProviderItem info = providerStore.getProviderItem(request.get("app"), request.get("name"));
            return new JsonResponse(info);
        } else if ("sf".equals(t)) {
            SourceConfigWrapper sw = providerStore.getSourceConfig(request.get("app"));
            if (sw == null)
                return new JsonResponse(PageResult.EMPTY);
            List<?> rows = sw.getSources();
            PageResult page = new PageResult();
            page.setRows(rows);
            page.setTotal(rows.size());
            return new JsonResponse(page);
        } else {
            ProviderSource ps = providerStore.getProviderSource(request.get("app"));
            if (ps == null)
                return new JsonResponse(PageResult.EMPTY);
            List<?> rows = ps.getItems();
            PageResult page = new PageResult();
            page.setRows(rows);
            page.setTotal(rows.size());
            return new JsonResponse(page);
        }
    }
}