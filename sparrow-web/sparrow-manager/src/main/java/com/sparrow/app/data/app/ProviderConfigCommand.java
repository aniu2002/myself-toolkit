package com.sparrow.app.data.app;


import com.sparrow.common.backend.ProcessJob;
import com.sparrow.common.backend.ProcessResult;
import com.sparrow.common.concurrency.PoolManger;
import com.sparrow.common.source.SourceManager;
import com.sparrow.core.config.FileMnger;
import com.sparrow.core.json.JsonMapper;
import com.sparrow.core.utils.JsonFormat;
import com.sparrow.core.utils.PropertiesFileUtil;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.file.FileToolHelper;
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.FreeMarkerResponse;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.JsonStrResponse;
import com.sparrow.http.command.resp.OkResponse;
import com.sparrow.app.data.meta.MetaManageCommand;
import com.sparrow.app.common.InfoHolder;
import com.sparrow.app.common.SpeEnvironment;
import com.sparrow.app.services.app.AppInfo;
import com.sparrow.app.services.app.AppStore;
import com.sparrow.app.services.meta.DbScriptMetaService;
import com.sparrow.app.services.provider.ProviderItem;
import com.sparrow.app.services.provider.ProviderSource;
import com.sparrow.app.services.provider.ProviderStore;
import com.sparrow.app.services.provider.SourceConfig;
import com.sparrow.app.services.source.SourceInfo;
import com.sparrow.server.web.OpResult;
import com.sparrow.tools.cmd.XmdMapperGenerator;
import com.sparrow.tools.cmd.XmdPojoGenerateTask;
import com.sparrow.tools.cmd.eggs.GeneratorTool;
import com.sparrow.tools.cmd.freemark.XmdFreeMarker;
import com.sparrow.tools.compile.ClassCompiler;
import com.sparrow.tools.holder.ConnectionHolder;
import com.sparrow.tools.pogen.Log;
import com.sparrow.tools.utils.FileUtil;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.*;

public class ProviderConfigCommand extends BaseCommand {
    private static Map<String, DbScriptMetaService> metaServiceMap = new HashMap<String, DbScriptMetaService>();
    private Object synObject = new Object();

    public ProviderConfigCommand() {

    }

    DbScriptMetaService getSourceMetaInterface(String app, String name) {
        String key = app + "_" + name;
        DbScriptMetaService metaService = metaServiceMap.get(key);
        if (metaService == null) {
            ProviderItem item = DataProviderCommand.providerStore.getProviderItem(app, name);
            if (item == null)
                return null;
            SourceConfig sourceConfig = DataProviderCommand.providerStore.getSourceConfig(app, item.getSource());
            if (sourceConfig != null) {
                Properties properties = PropertiesFileUtil.getProps(sourceConfig.getProps());
                SourceInfo sourceInfo = new SourceInfo();
                sourceInfo.setDriver(properties.getProperty("jdbc.driver"));
                sourceInfo.setUrl(properties.getProperty("jdbc.url"));
                sourceInfo.setUser(properties.getProperty("jdbc.user"));
                sourceInfo.setPassword(properties.getProperty("jdbc.password"));
                metaService = new DbScriptMetaService(sourceInfo);
                metaServiceMap.put(key, metaService);
            }
        }
        return metaService;
    }

    DbScriptMetaService getSourceMetaService(String app, String source) {
        String key = app + "_" + source;
        DbScriptMetaService metaService = metaServiceMap.get(key);
        if (metaService == null) {
            if ("@".equals(source)) {
                SourceInfo sourceInfo = MetaManageCommand.getSourceInfoByApp(app);
                metaService = new DbScriptMetaService(sourceInfo);
                metaServiceMap.put(key, metaService);
            } else {
                SourceConfig sourceConfig = DataProviderCommand.providerStore.getSourceConfig(app, source);
                if (sourceConfig != null) {
                    Properties properties = PropertiesFileUtil.getProps(sourceConfig.getProps());
                    SourceInfo sourceInfo = new SourceInfo();
                    sourceInfo.setDriver(properties.getProperty("jdbc.driver"));
                    sourceInfo.setUrl(properties.getProperty("jdbc.url"));
                    sourceInfo.setUser(properties.getProperty("jdbc.user"));
                    sourceInfo.setPassword(properties.getProperty("jdbc.password"));
                    metaService = new DbScriptMetaService(sourceInfo);
                    metaServiceMap.put(key, metaService);
                }
            }
        }
        return metaService;
    }


    @Override
    public Response doPost(Request request) {
        String _t = request.get("_t");
        Object obj = null;
        if ("setting".equals(_t)) {
            obj = this.saveTableSetting(request.get("module"), request.get("provider"), request.getBody());
        } else if ("clear".equals(_t)) {
            obj = this.clearTableSetting(request.get("module"), request.get("provider"));
        } else if ("gps".equals(_t)) {
            System.out.println(" gps :  \r\n (x,y) : " + request.get("x") + "," + request.get("y"));
            return OkResponse.OK;
        } else if ("conf".equals(_t)) {
            try {
                Map<String, String> map = JsonMapper.mapper.readValue(request.getBody(), new TypeReference<Map<String, String>>() {
                });
                obj = this.saveColumnDef(request.get("module"), map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("install".equals(_t)) {
            return this.generateSourceEx(request.get("module"), request.get("provider"), request.get("label"), false, request.get("pack"));
        }

        if (obj != null)
            return new JsonResponse(obj);
        return super.doPost(request);
    }

    @Override
    public Response doPut(Request request) {

        return OkResponse.OK;
    }


    @Override
    public Response doDelete(Request request) {

        return OkResponse.OK;
    }

    @Override
    public Response doGet(Request request) {
        String _t = request.get("_t");
        Object obj = null;
        if ("gp".equals(_t)) {
            ProviderItem item = DataProviderCommand.providerStore.getProviderItem(request.get("app"), request.get("provider"));
            if (item == null) {
                request.getParas().put("script", "-");
            } else {
                request.getParas().put("script", item.getScript());
            }
            return new FreeMarkerResponse("#project/pdc-config", request.getParas());
        } else if ("setting".equals(_t)) {
            String str = this.getTableSetting(request.get("module"), request.get("provider"));
            return new JsonStrResponse(str);
        } else if ("clear".equals(_t)) {
            obj = this.clearTableSetting(request.get("module"), request.get("provider"));
            return new JsonResponse(obj);
        } else if ("conf".equals(_t)) {
            obj = this.getColumnDef(request.get("module"));
            return new JsonResponse(obj);
        } else if ("pdc".equals(_t)) {
            obj = this.getAppProviders(request.get("module"));
            return new JsonResponse(obj);
        } else if ("pdcInfo".equals(_t)) {
            obj = this.getTableInfo(request.get("module"), request.get("provider"));
            return new JsonResponse(obj);
        } else if ("pdcData".equals(_t)) {
            obj = this.getTableData(request.get("module"), request.get("provider"));
            return new JsonResponse(obj);
        } else if ("pdcColumns".equals(_t)) {
            obj = this.getTableColumns(request.get("module"), request.get("provider"));
            return new JsonResponse(obj);
        }

        return super.doGet(request);
    }


    Object saveTableSetting(String module, String table, String string) {
        // TableColumn column
        FileMnger.writeProviderText(module, table, JsonFormat.format(string));
        return OpResult.OK;
    }

    Object clearTableSetting(String module, String table) {
        // TableColumn column
        FileMnger.clearProviderModule(module, table);
        return OpResult.OK;
    }

    String getTableSetting(String module, String table) {
        return FileMnger.readProviderText(module, table);
    }

    Object saveColumnDef(String module, Map<String, String> map) {
        FileMnger.writeProviderMap(module, map);
        return OpResult.OK;
    }


    Object getColumnDef(String module) {
        return FileMnger.readProviderMap(module);
    }

    Object getAppProviders(String app) {
        ProviderSource providerSource = DataProviderCommand.providerStore.getProviderSource(app);
        if (providerSource == null)
            return null;
        List<ProviderItem> items = providerSource.getItems();
        if (items != null && !items.isEmpty()) {
            List<String> list = new ArrayList<String>();
            for (ProviderItem item : items) {
                list.add(item.getName());
            }
            return list;
        } else
            return Collections.EMPTY_LIST;
    }

    public Object getTableInfo(String app, String provider) {
        ProviderItem item = DataProviderCommand.providerStore.getProviderItem(app, provider);
        if (item == null)
            return null;
        DbScriptMetaService dbMetaSrv = this.getSourceMetaService(app, item.getSource());
        if (dbMetaSrv != null)
            return dbMetaSrv.getScriptDescriptor(item.getScript(), provider);
        else
            return Collections.EMPTY_LIST;
    }

    Object getTableData(String app, String provider) {
        ProviderItem item = DataProviderCommand.providerStore.getProviderItem(app, provider);
        if (item == null)
            return null;
        DbScriptMetaService dbMetaSrv = this.getSourceMetaService(app, item.getSource());
        if (dbMetaSrv != null)
            return dbMetaSrv.getTableData(item.getScript(), provider, 1, 100);
        else
            return Collections.EMPTY_LIST;
    }

    Object getTableColumns(String app, String provider) {
        ProviderItem item = DataProviderCommand.providerStore.getProviderItem(app, provider);
        if (item == null) {
            return null;
        }
        DbScriptMetaService dbMetaSrv = this.getSourceMetaService(app, item.getSource());
        if (dbMetaSrv != null)
            return dbMetaSrv.getScriptColumnMetaData(item.getScript());
        else
            return Collections.EMPTY_LIST;
    }

    Response generateSourceEx(String module, String provider,
                              String moduleLabel,
                              boolean reload,
                              String packPath) {
        String token = module + UUID.randomUUID().toString();
        AppStore appStore = (AppStore) SourceManager.getSourceHandler("apps");
        AppInfo info = appStore.getAppInfo(module);
        GenerateProcessJob job = new GenerateProcessJob(token, "资源同步");
        String userHome = FileMnger.STORE_DIR;
        String codePath = (info != null ? info.getAppHome() + "/src" : userHome.concat("/_tmp/src"));
        String bytePath = (info != null ? info.getAppHome() + "/classes" : userHome.concat("/_tmp/target"));
        job.setLabel(moduleLabel);
        job.setAppInfo(info);

        DbScriptMetaService df = this.getSourceMetaInterface(module, provider);
        job.setSourceInfo(df.getSourceInfo());
        job.setConnection(df.getConnection());
        job.setModule(module);
        job.setReload(reload);
        job.setPackPath(packPath);
        job.setCodePath(codePath);
        job.setBytePath(bytePath);
        PoolManger.threadPool.submit(job);
        return new JsonStrResponse("{\"token\":\"" + token + "\"}");
    }

}

class GenerateProcessJob extends ProcessJob implements Log {
    private String codePath;
    private String bytePath;
    private String module;
    private String label;
    private String packPath = "com.dili.dd.cornucopia.bps";
    private boolean reload;
    private SourceInfo sourceInfo;
    private AppInfo appInfo;
    private Connection connection;

    public SourceInfo getSourceInfo() {
        return sourceInfo;
    }

    public void setSourceInfo(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public GenerateProcessJob(String sid) {
        super(sid);
    }

    public GenerateProcessJob(String sid, String label) {
        super(sid, label);
    }

    public String getCodePath() {
        return codePath;
    }

    public void setCodePath(String codePath) {
        this.codePath = codePath;
    }

    public String getBytePath() {
        return bytePath;
    }

    public void setBytePath(String bytePath) {
        this.bytePath = bytePath;
    }

    public String getPackPath() {
        return packPath;
    }

    public void setPackPath(String packPath) {
        this.packPath = packPath;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isReload() {
        return reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }

    void genPojo() {
        XmdPojoGenerateTask task = new XmdPojoGenerateTask();
        task.setBasePath(this.codePath);
        task.setPackageName(this.packPath);
        task.setTableFilter("*");
        task.setJdbcConfig("classpath:conf/config4mysql.properties");
        task.setClearBefore(false);
        task.execute();
    }

    void copyPage(String path, String cfgPath) {
        FileToolHelper.copy(SystemConfig.WEB_ROOT + "/js", path);
        FileToolHelper.copy(SystemConfig.WEB_ROOT + "/css", path);
        FileToolHelper.copy(SystemConfig.WEB_ROOT + "/icons", path);
        FileToolHelper.copy(SystemConfig.WEB_ROOT + "/img", path);
        FileToolHelper.copy(SystemConfig.WEB_ROOT + "/index.html", path);
        FileToolHelper.copy(SystemConfig.WEB_ROOT + "/loginCmd.html", path);
        FileToolHelper.copy(SystemConfig.WEB_ROOT + "/main.html", path);

        //  =========
        ProviderStore.moveProviderTo(this.appInfo.getName(), cfgPath);
        ProviderStore.moveSourceTo(this.appInfo.getName(), cfgPath);
    }

    @Override
    protected ProcessResult doExecute() {
        ProcessResult result = new ProcessResult();
        boolean er = false;

        try {
            // genPojo();
            Properties properties = com.sparrow.tools.utils.PropertiesFileUtil
                    .getPropertiesEl("classpath:conf/config4mysql.properties");
            Properties moduleSet = com.sparrow.tools.utils.PropertiesFileUtil
                    .getPropertiesEl("classpath:conf/module.properties");

            FileUtil.clearSub(new File(this.appInfo.getAppHome()));

            String confFile = this.appInfo.getConfigPath() + "/jdbc.properties";

            XmdFreeMarker.getInstance().write("jdbc-properties", this.sourceInfo, confFile);

            Log log = this;

            ConnectionHolder.holdConnection(this.connection);
            InfoHolder holder = new InfoHolder();
            holder.setAppInfo(this.appInfo);
            holder.setSourceInfo(this.sourceInfo);
            holder.setJdbcConfigPath(confFile);

            SpeEnvironment.setInfoHolder(holder);

            GeneratorTool gcd = new GeneratorTool();
            gcd.setBasePath(this.codePath); //SystemConfig.SOURCE_DIR
            gcd.setModule(this.module);
            gcd.setLabel(this.module);
            gcd.setPackPath(this.packPath);
            gcd.setDbConfig(confFile);
            gcd.setLog(log);

            File f = XmdMapperGenerator.getPackDir(gcd.getBasePath(), this.packPath);

            if (f.exists())
                FileUtil.clearSub(f);

            gcd.genPojo();
            // gcd.genServiceCode();

            ConnectionHolder.remove();
            SpeEnvironment.setInfoHolder(null);
            // 编译class

            File classPath = new File(this.bytePath);
            log.msg("-编译java类文件：" +
                    classPath.getPath());
            new ClassCompiler(new File(this.codePath),
                    classPath, new PrintWriter(System.err)).compile();

			 /* if (this.reload) { log.msg("-重新加载web模块：" + classPath.getPath());
             * WebBundle.loadWebBundle(false); }
			 */

            //this.copyPage(this.appInfo.getWebRootPath(), this.appInfo.getConfigPath());
        } catch (Exception e) {
            e.printStackTrace();
            er = true;
            result.setResult("%" + e.getMessage());
        }
        //
        if (er) {
            result.setState(-1);
        } else {
            result.setResult("处理完成");
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