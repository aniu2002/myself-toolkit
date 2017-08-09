package com.sparrow.app.information.command;

import com.sparrow.app.PathSetting;
import com.sparrow.app.information.domain.GifInfo;
import com.sparrow.app.information.service.GifInfoService;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.BeanWrapper;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.FreeMarkerResponse;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.OkResponse;
import org.apache.lucene.utils.StringUtil;

import java.io.File;

/**
 * 完成(gif_info-)的基本操作<br/>
 * <p>
 * gif_info:
 *
 * @author YZC
 * @version 2.0
 *          date: 2017-07-26 24:59:04
 */
public class GifInfoCommand extends BaseCommand {

    GifInfoService gifInfoService;

    public void setGifInfoService(GifInfoService gifInfoService) {
        this.gifInfoService = gifInfoService;
    }

    public Response doPost(Request request) {
        GifInfo gifInfo = BeanWrapper.wrapBean(GifInfo.class, request);
        this.gifInfoService.add(gifInfo);
        return OkResponse.OK;
    }

    public Response doDelete(Request request) {
        String t = request.get("_pt");
        if (StringUtils.isNotEmpty(t)) {
            File f = new File(PathSetting.GIF_DIR, t);
            if (f.exists())
                f.delete();
            return OkResponse.OK;
        }
        boolean m = request.hasMuiltVal("id");
        if (m)
            this.gifInfoService.batchDelete(request.getLongList("id"));
        else
            this.gifInfoService.delete(request.getLong("id"));
        return OkResponse.OK;
    }

    public Response doPut(Request request) {
        GifInfo gifInfo = BeanWrapper.wrapBean(GifInfo.class, request);
        this.gifInfoService.update(gifInfo);
        return OkResponse.OK;
    }

    public Response doGet(Request request) {
        String t = request.get("_t");
        if ("et".equals(t)) {
            return new FreeMarkerResponse("gif/gif_info/edit", request.getParas());
        } else if ("dt".equals(t)) {
            return new FreeMarkerResponse("gif/gif_info/detail", request.getParas());
        } else if ("da".equals(t)) {
            long id = request.getLong("id");
            GifInfo gifInfo = this.gifInfoService.get(id);
            return new JsonResponse(gifInfo);
        } else {
            int page = request.getInt("page"), limit = request.getInt("limit", 20);
            GifInfo gifInfo = BeanWrapper.wrapBean(GifInfo.class, request);
            Object data = this.gifInfoService.pageQuery(gifInfo, page, limit);
            return new JsonResponse(data);
        }
    }
}