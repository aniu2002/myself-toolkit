package com.sparrow.app.information.command;

/* package com.sparrow.app.play.command.lf; */
 


/* import com.sparrow.app.play.domain.lf.LfMembers; */

import com.sparrow.app.data.DataImportHolder;
import com.sparrow.app.data.DataToolCommand;
import com.sparrow.app.data.validators.QQHolder;
import com.sparrow.app.information.domain.LfMembers;
import com.sparrow.app.information.service.LfMembersService;
import com.sparrow.app.tool.StringTool;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.date.DateUtils;
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.BeanWrapper;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.FreeMarkerResponse;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.MsgResponse;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.httpclient.HttpTool;
import com.sparrow.orm.session.RowCallbackHandler;
import com.sparrow.orm.session.Session;
import com.sparrow.orm.template.ExecuteCallback;
import com.sparrow.orm.template.simple.OperateTemplate;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
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
public class LfMembersCommand extends BaseCommand {
    private LfMembersService lfMembersService;
    private OperateTemplate operateTemplate;
    private DataToolCommand dataToolCommand;

    public void setDataToolCommand(DataToolCommand dataToolCommand) {
        this.dataToolCommand = dataToolCommand;
    }

    public void setOperateTemplate(OperateTemplate operateTemplate) {
        this.operateTemplate = operateTemplate;
    }

    public void initialize() {
        if (this.operateTemplate != null) {
            QQHolder.init();
            this.operateTemplate.query(new ExecuteCallback<Integer>() {
                @Override
                public Integer execute(Session session) throws Exception {
                    return session.queryBack("select qq from lf_members", null, new RowCallbackHandler() {
                        @Override
                        public void processRow(ResultSet rs) throws SQLException {
                            QQHolder.addQQNumber(rs.getString(1));
                        }
                    });
                }
            });
        }
    }

    public void setLfMembersService(LfMembersService lfMembersService) {
        this.lfMembersService = lfMembersService;
    }

    public Response doPost(Request request) {
        String t = request.get("_t");
        if ("up".equals(t)) {
            Long id = request.getLong("id");
            String files = request.get("_files_");
            this.lfMembersService.updateMembersImg(id, files);
        } else if ("imp".equals(t)) {
            try {
                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("createDate", new Date());
                DataImportHolder.set(variables);
                return this.dataToolCommand.doPost(request);
            } finally {
                DataImportHolder.clear();
            }
        } else if ("exp".equals(t)) {
            return this.dataToolCommand.doPost(request);
        } else {
            LfMembers lfMembers = BeanWrapper.wrapBean(LfMembers.class, request);
            this.resetLfMembers(lfMembers, false);
            int st = this.lfMembersService.add(lfMembers);
            if (st == 0)
                return new MsgResponse(1, "qq用户已经存在");
            return new JsonResponse(new ReturnData(lfMembers.getId()));
        }
        return OkResponse.OK;
    }

    void resetLfMembers(LfMembers lfMembers, boolean update) {
        if (lfMembers == null)
            return;
        String district = lfMembers.getDistrict();
        if (StringUtils.isNotEmpty(district)) {
            String args[] = district.split("-");
            int len = args.length;
            if (len == 1) {
                lfMembers.setProvince(district);
                lfMembers.setCity(district);
            } else if (len == 2) {
                lfMembers.setProvince(args[0]);
                lfMembers.setCity(args[0]);
            } else if (len == 3) {
                lfMembers.setProvince(args[0]);
                lfMembers.setCity(args[1]);
            }
        }
        lfMembers.setCreateDate(new Timestamp(System.currentTimeMillis()));
        lfMembers.setMark(1);
        if (update)
            lfMembers.setUpdateDate(new Timestamp(System.currentTimeMillis()));
        String imgPath = this.downloadImg(lfMembers.getImages());
        if (imgPath != null)
            lfMembers.setImages(imgPath);
    }

    String downloadImg(String url) {
        if (StringUtils.isEmpty(url))
            return null;
        if (StringUtils.startsWithIgnoreCase(url, "http://")) {
            String fileName = DateUtils.currentTime("yyMMddHHmmss") + System.nanoTime() + ".png";
            String relative = "/lf/" + DateUtils.currentTime(DateUtils.PATTERN_YEAR_TIME);
            File file = new File(SystemConfig.getProperty("web.store.path"), relative);
            if (!file.exists())
                file.mkdirs();
            HttpTool.downStream(url, new File(file, fileName));
            return "/store" + relative + "/" + fileName;
        }
        return null;
    }

    public Response doDelete(Request request) {
        boolean m = request.hasMuiltVal("id");
        if (m)
            this.lfMembersService.batchDelete(request.getLongList("id"));
        else
            this.lfMembersService.delete(request.getLong("id"));
        return OkResponse.OK;
    }

    public Response doPut(Request request) {
        LfMembers lfMembers = BeanWrapper.wrapBean(LfMembers.class, request);
        this.resetLfMembers(lfMembers, true);
        this.lfMembersService.update(lfMembers);
        return OkResponse.OK;
    }

    public Response doGet(Request request) {
        String t = request.get("_t");
        if ("et".equals(t)) {
            return new FreeMarkerResponse("lf/edit", request.getParas());
        } else if ("dt".equals(t)) {
            return new FreeMarkerResponse("lf/detail", request.getParas());
        } else if ("dts".equals(t)) {
            return new FreeMarkerResponse("lf/detailShow", request.getParas());
        } else if ("cp".equals(t)) {
            return new FreeMarkerResponse("lf/copy", request.getParas());
        } else if ("up".equals(t)) {
            return new FreeMarkerResponse("lf/img-upload", request.getParas());
        } else if ("da".equals(t)) {
            long id = request.getLong("id");
            LfMembers lfMembers = this.lfMembersService.get(id);
            return new JsonResponse(lfMembers);
        } else {
            int page = request.getInt("page"), limit = request.getInt("limit", 20);
            LfMembers lfMembers = BeanWrapper.wrapBean(LfMembers.class, request);
            if (StringUtils.isNotEmpty(lfMembers.getName())) {
                String e = lfMembers.getName();
                lfMembers.setName(null);
                if (StringTool.isMobile(e)) {
                    lfMembers.setPhone(e);
                } else if (StringTool.isDit(e)) {
                    lfMembers.setQq(e);
                } else if (StringTool.containsChinese(e))
                    lfMembers.setName(e);
            }
            Object data = this.lfMembersService.pageQuery(lfMembers, page, limit);
            return new JsonResponse(data);
        }
    }
}