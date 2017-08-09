package com.sparrow.app.data.app;

import com.sparrow.common.backend.ProcessJob;
import com.sparrow.common.backend.ProcessResult;
import com.sparrow.common.concurrency.PoolManger;
import com.sparrow.common.source.SourceManager;
import com.sparrow.core.log.LoggerManager;
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.BeanWrapper;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.FreeMarkerResponse;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.JsonStrResponse;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.app.services.app.AppInfo;
import com.sparrow.app.services.app.AppStore;
import com.sparrow.app.shell.SingleAppStarter;
import com.sparrow.orm.page.PageResult;
import com.sparrow.tools.pogen.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AppCommand extends BaseCommand {
    private AppStore appStore = new AppStore();
    private Map<String, SingleAppStarter> map = new HashMap<String, SingleAppStarter>();
    private Map<String, AppStartProcessJob> processCache = new HashMap<String, AppStartProcessJob>();

    public AppCommand() {
        appStore = new AppStore();
        SourceManager.regSourceHandler("apps", appStore);
    }

    private Response startApp(Long appId) {
        AppInfo appInfo = this.appStore.getAppInfo(appId);
        SingleAppStarter singleAppStarter = new SingleAppStarter(appInfo.getName(), appInfo.getAppHome(), appInfo.getWebRootPath(), appInfo.getWebPort());
        singleAppStarter.start();
        appInfo.setStarted(1);
        map.put(appInfo.getName(), singleAppStarter);
        this.appStore.updateAppInfo(appInfo);

        String token = appInfo.getName() + UUID.randomUUID().toString();
        AppStartProcessJob job = new AppStartProcessJob(token, "启动app");
        this.processCache.put(appInfo.getName(), job);
        PoolManger.threadPool.submit(job);

        return new JsonStrResponse("{\"token\":\"" + token + "\"}");
    }

    @Override
    public Response doPost(Request request) {
        String _t = request.get("_t");
        if ("start".equals(_t)) {
            return this.startApp(request.getLong("app"));
        } else if ("stop".equals(_t)) {
            AppInfo appInfo = this.appStore.getAppInfo(request.getLong("app"));
            SingleAppStarter singleAppStarter = map.remove(appInfo.getName());
            if (singleAppStarter != null) {
                singleAppStarter.stopNow();
                singleAppStarter = null;
            }
            appInfo.setStarted(0);
            appInfo.setProcessId(null);
            this.appStore.updateAppInfo(appInfo);
            return OkResponse.OK;
        } else if ("notify".equals(_t)) {
            AppInfo appInfo = this.appStore.getAppInfo(request.get("app"));
            appInfo.setProcessId(request.get("pid"));
            LoggerManager.getSysLog().info("process id : " + appInfo.getProcessId());
            this.appStore.updateAppInfo(appInfo);
            AppStartProcessJob job = this.processCache.remove(appInfo.getName());
            if (job != null) {
                job.setDone(true);
            }
            return OkResponse.OK;
        } else {
            AppInfo sourceInfo = BeanWrapper.wrapBean(AppInfo.class, request);
            this.setAppInfo(sourceInfo);
            this.appStore.addAppInfo(sourceInfo);
            return OkResponse.OK;
        }
    }

    @Override
    public Response doPut(Request request) {
        AppInfo sourceInfo = BeanWrapper.wrapBean(AppInfo.class, request);
        this.setAppInfo(sourceInfo);
        this.appStore.updateAppInfo(sourceInfo);
        return OkResponse.OK;
    }

    void setAppInfo(AppInfo appInfo) {
        String userDir = System.getProperty("user.home").replace('\\', '/');
        String appHome = userDir + "/.spe/" + appInfo.getName();
        String webRoot = appHome + "/webapp";
        String configPath = appHome + "/conf";
        appInfo.setConfigPath(configPath);
        appInfo.setWebRootPath(webRoot);
        appInfo.setAppHome(appHome);
    }

    @Override
    public Response doDelete(Request request) {
        long id = request.getLong("id");
        this.appStore.removeAppInfo(id);
        return OkResponse.OK;
    }

    @Override
    public Response doGet(Request request) {
        String t = request.get("_t");
        if ("edit".equals(t)) {
            return new FreeMarkerResponse("#app/app_edit", request.getParas());
        } else if ("detail".equals(t)) {
            return new FreeMarkerResponse("#app/app_detail", request.getParas());
        } else if ("data".equals(t)) {
            long id = request.getLong("id");
            AppInfo info = this.appStore.getAppInfo(id);
            return new JsonResponse(info);
        } else {
            List<?> rows = this.appStore.list();
            PageResult page = new PageResult();
            page.setRows(rows);
            page.setTotal(rows.size());
            return new JsonResponse(page);
        }
    }
}

class AppStartProcessJob extends ProcessJob implements Log {
    private boolean done;

    public AppStartProcessJob(String sid) {
        super(sid);
    }

    public AppStartProcessJob(String sid, String label) {
        super(sid, label);
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    @Override
    protected ProcessResult doExecute() {
        ProcessResult result = new ProcessResult();
        boolean er = false;

        try {
            while (!this.done) {
                synchronized (this) {
                    this.wait(300);
                    this.msg("启动中...");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            er = true;
            result.setResult("%" + e.getMessage());
        }
        //
        if (er) {
            result.setState(-1);
        } else {
            result.setResult("启动完成");
        }

        return result;
    }

    @Override
    public void info(Object msg) {

    }

    double percent = 0;

    @Override
    public void msg(Object msg) {
        this.percent += this.step;
        int p = (int) Math.rint(this.percent);
        this.notifyProcess(p, msg.toString());
        try {
            synchronized (this) {
                this.wait(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    double step = 3;

    @Override
    public void setStep(double step) {
        this.step = step;
    }
}