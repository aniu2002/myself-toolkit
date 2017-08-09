package com.sparrow.app.weixin;

import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.RedirectResponse;
import com.sparrow.http.command.resp.TextResponse;
import com.sparrow.app.information.domain.PrimarySchool;
import com.sparrow.app.information.service.PrimarySchoolService;
import com.sparrow.weixin.common.JsonMapper;
import com.sparrow.weixin.common.WeiXinTool;
import com.sparrow.weixin.entity.BaseResponse;
import com.sparrow.weixin.entity.SnsUserInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by Administrator on 2016/3/13 0013.
 */
public class WeiXinConfigCommand extends BaseCommand {
    private TextResponse ERROR_RESP = new TextResponse("配置参数异常");
    private PrimarySchoolService primarySchoolService;

    public WeiXinConfigCommand(PrimarySchoolService primarySchoolService) {
        this.primarySchoolService = primarySchoolService;
    }

    int saveUser(String accessToken, String openId) {
        String rs = StringUtils.isEmpty(accessToken) ? WeiXinTool.getUserInfo(openId) : WeiXinTool.getUserInfo(accessToken, openId);
        System.out.println(" save user info : " + rs);
        SnsUserInfo snsUserInfo = JsonMapper.readJson(rs, SnsUserInfo.class);
        if (snsUserInfo != null && this.primarySchoolService != null) {
            PrimarySchool school = new PrimarySchool();
            school.setName(snsUserInfo.getNickname());
            school.setNickName(snsUserInfo.getNickname());
            school.setCity(snsUserInfo.getCity());
            school.setCountry(snsUserInfo.getCountry());
            school.setHeadImage(snsUserInfo.getHeadimgurl());
            school.setOpenId(snsUserInfo.getOpenid());
            school.setLanguage(snsUserInfo.getLanguage());
            school.setProvince(snsUserInfo.getProvince());
            school.setSex(snsUserInfo.getSex());
            System.out.println(" save user info");
            int n = this.primarySchoolService.add(school);
            System.out.println(" save user info - " + n);
            return n;
        }
        return 0;
    }

    int saveUser(SnsUserInfo snsUserInfo) {
        if (snsUserInfo != null && this.primarySchoolService != null) {
            PrimarySchool school = new PrimarySchool();
            school.setName(snsUserInfo.getNickname());
            school.setNickName(snsUserInfo.getNickname());
            school.setCity(snsUserInfo.getCity());
            school.setCountry(snsUserInfo.getCountry());
            school.setHeadImage(snsUserInfo.getHeadimgurl());
            school.setOpenId(snsUserInfo.getOpenid());
            school.setLanguage(snsUserInfo.getLanguage());
            school.setProvince(snsUserInfo.getProvince());
            school.setSex(snsUserInfo.getSex());
            System.out.println(" save user info");
            int n = this.primarySchoolService.add(school);
            System.out.println(" save user info - " + n);
            return n;
        }
        return 0;
    }


    protected Response doGet(Request request) {
        String action = request.get("action");
        if ("at".equals(action)) {
            String s = WeiXinTool.getCodeRequest("ft");
            System.out.println(" redirect url : " + s);
            return new RedirectResponse(s);
        } else if ("st".equals(action)) {
            String s = WeiXinTool.getCodeRequest("syn");
            System.out.println(" redirect url : " + s);
            return new RedirectResponse(s);
        } else if ("et".equals(action)) {
            String s = WeiXinTool.getCodeRequest("ed");
            System.out.println(" redirect url : " + s);
            return new RedirectResponse(s);
        } else if ("ed".equals(action)) {
            BaseResponse resp = WeiXinTool.getAccessToken(request.get("code"));
            return new RedirectResponse("http://firebird.5166.info/cmd/primary/primary_school?_t=set&openid=" + resp.getOpenid());
        } else if ("ft".equals(action)) {
            BaseResponse resp = WeiXinTool.getAccessToken(request.get("code"));
            //WeiXinTool.setUserRemark(resp.getOpenid(), "yb2bsb");
            if (org.apache.commons.lang3.StringUtils.equals("snsapi_userinfo", resp.getScope())) {
                int n = this.saveUser(resp.getAccess_token(), resp.getOpenid());
                if (n == 0)
                    return new RedirectResponse("http://firebird.5166.info/app/views/primary/list.html");
                else
                    return new RedirectResponse("http://firebird.5166.info/cmd/primary/primary_school?_t=set&openid=" + resp.getOpenid());
            } else
                System.out.println(" openId : " + resp.getOpenid());
            return new RedirectResponse("http://firebird.5166.info/app/idx.html");
        } else if ("syn".equals(action)) {
            BaseResponse resp = WeiXinTool.getAccessToken(request.get("code"));
            //WeiXinTool.setUserRemark(resp.getOpenid(), "yb2bsb");
            if (org.apache.commons.lang3.StringUtils.equals("snsapi_userinfo", resp.getScope())) {
                String openIds[] = WeiXinTool.getUserList();
                int n = 0;
                for (String openId : openIds) {
                    boolean bf = this.primarySchoolService.hasOpenId(openId);
                    System.out.println(bf + " = " + openId);
                    if (!bf) {
                        n = +this.saveUser(null, openId);
                    }
                }
                if (n == 0)
                    return new RedirectResponse("http://firebird.5166.info/app/views/primary/list.html");
                else
                    return new RedirectResponse("http://firebird.5166.info/cmd/primary/primary_school?_t=ls");
            } else
                System.out.println(" openId : " + resp.getOpenid());
            return new RedirectResponse("http://firebird.5166.info/app/idx.html");
        } else if ("synx".equals(action)) {
            String openIds[] = WeiXinTool.getUserList();
            SnsUserInfo snsUserInfo[] = WeiXinTool.batchGetUser(openIds);
            int n = 0;
            for (SnsUserInfo userInfo : snsUserInfo) {
                boolean bf = this.primarySchoolService.hasOpenId(userInfo.getOpenid());
                System.out.println(bf + " = " + userInfo.getOpenid());
                if (!bf) {
                    n = +this.saveUser(userInfo);
                }
            }
            if (n == 0)
                return new RedirectResponse("http://firebird.5166.info/app/views/primary/list.html");
            else
                return new RedirectResponse("http://firebird.5166.info/cmd/primary/primary_school?_t=ls");
        } else if ("binding".equals(action)) {
            System.out.println(" binding openId : " + request.get("openId"));
            return new TextResponse("Hello world !" + request.get("openId"));
        }
        return ERROR_RESP;
    }

    protected Response doPost(Request request) {
        /** 判断是否是微信接入激活验证，只有首次接入验证时才会收到echostr参数，此时需要把它直接返回 */
        String action = request.get("action");
        String s = "success";
        boolean fg = false;
        if ("confMenu".equals(action)) {
            fg = WeiXinTool.configMenu();
            if (!fg)
                s = "错误";
        } else if ("syn".equals(action)) {

        }
        return new TextResponse(s);
    }
}
