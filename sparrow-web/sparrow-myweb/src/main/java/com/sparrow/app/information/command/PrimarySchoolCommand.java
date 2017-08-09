package com.sparrow.app.information.command;

/* package com.sparrow.app.play.command.primary; */
 
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.BeanWrapper;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.FreeMarkerResponse;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.app.information.domain.PrimarySchool;
import com.sparrow.app.information.service.PrimarySchoolService;
import com.sparrow.orm.page.PageResult;

import java.util.HashMap;
import java.util.Map;

/**
 * 完成(primary_school-)的基本操作<br/>
 * <p/>
 * primary_school:
 *
 * @author YZC
 * @version 2.0
 *          date: 2016-03-02 17:50:20
 */
public class PrimarySchoolCommand extends BaseCommand {

    PrimarySchoolService primarySchoolService;

    public void setPrimarySchoolService(PrimarySchoolService primarySchoolService) {
        this.primarySchoolService = primarySchoolService;
    }

    public Response doPost(Request request) {
        String t = request.get("_t");
        if ("up".equals(t)) {
            this.primarySchoolService.updateInfo(request.get("phone"), request.get("name"), request.get("openid"));
        } else {
            PrimarySchool primarySchool = BeanWrapper.wrapBean(PrimarySchool.class, request);
            this.primarySchoolService.add(primarySchool);
        }
        return OkResponse.OK;
    }

    public Response doDelete(Request request) {
        boolean m = request.hasMuiltVal("id");
        if (m)
            this.primarySchoolService.batchDelete(request.getLongList("id"));
        else
            this.primarySchoolService.delete(request.getLong("id"));
        return OkResponse.OK;
    }

    public Response doPut(Request request) {
        PrimarySchool primarySchool = BeanWrapper.wrapBean(PrimarySchool.class, request);
        this.primarySchoolService.update(primarySchool);
        return OkResponse.OK;
    }

    public Response doGet(Request request) {
        String t = request.get("_t");
        if ("et".equals(t)) {
            return new FreeMarkerResponse("primary/edit", request.getParas());
        } else if ("dt".equals(t)) {
            return new FreeMarkerResponse("primary/detail", request.getParas());
        } else if ("set".equals(t)) {
            PrimarySchool school = this.primarySchoolService.getUser(request.get("openid"));
            Map<String, String> paras = new HashMap<String, String>();
            paras.put("openid", request.get("openid"));
            if (school != null) {
                paras.put("name", school.getName());
                paras.put("phone", school.getPhone());
            }
            return new FreeMarkerResponse("primary/userSet", paras);
        } else if ("ls".equals(t)) {
            PrimarySchool primarySchool = BeanWrapper.wrapBean(PrimarySchool.class, request);
            PageResult data = this.primarySchoolService.pageQuery(primarySchool, 1, 50);
            return new FreeMarkerResponse("primary/userList", data);
        } else if ("da".equals(t)) {
            long id = request.getLong("id");
            PrimarySchool primarySchool = this.primarySchoolService.get(id);
            return new JsonResponse(primarySchool);
        } else {
            int page = request.getInt("page"), limit = request.getInt("limit", 20);
            PrimarySchool primarySchool = BeanWrapper.wrapBean(PrimarySchool.class, request);
            Object data = this.primarySchoolService.pageQuery(primarySchool, page, limit);
            return new JsonResponse(data);
        }
    }
}