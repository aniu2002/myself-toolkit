package com.sparrow.app.data.meta;

import com.sparrow.common.backend.ProcessJob;
import com.sparrow.common.backend.ProcessResult;
import com.sparrow.common.concurrency.PoolManger;
import com.sparrow.common.source.SourceManager;
import com.sparrow.core.config.FileMnger;
import com.sparrow.http.command.BaseCommand;
import com.sparrow.http.command.Request;
import com.sparrow.http.command.Response;
import com.sparrow.http.command.resp.FreeMarkerResponse;
import com.sparrow.http.command.resp.JsonResponse;
import com.sparrow.http.command.resp.JsonStrResponse;
import com.sparrow.core.utils.JsonFormat;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.utils.file.FileToolHelper;
import com.sparrow.core.json.JsonMapper;
import com.sparrow.server.web.OpResult;
import com.sparrow.app.common.InfoHolder;
import com.sparrow.app.common.SpeEnvironment;
import com.sparrow.app.services.app.AppInfo;
import com.sparrow.app.services.app.AppStore;
import com.sparrow.app.services.meta.DefaultMetaService;
import com.sparrow.app.services.provider.ProviderStore;
import com.sparrow.app.services.source.DataSourceStore;
import com.sparrow.app.services.source.SourceInfo;
import com.sparrow.tools.cmd.XmdPojoGenerateTask;
import com.sparrow.tools.cmd.eggs.GeneratorTool;
import com.sparrow.tools.cmd.freemark.XmdFreeMarker;
import com.sparrow.tools.compile.ClassCompiler;
import com.sparrow.tools.holder.ConnectionHolder;
import com.sparrow.tools.pogen.Log;
import com.sparrow.tools.utils.FileUtil;
import com.sparrow.tools.utils.PropertiesFileUtil;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.*;

public class MetaManageCommand extends BaseCommand {
    private static Object synObject = new Object();
    private static Map<String, DefaultMetaService> metaServiceMap = new HashMap<String, DefaultMetaService>();
    private static DataSourceStore dbStore;

    public static final DefaultMetaService getExistMetaInterface(String key) {
        return metaServiceMap.get(key);
    }

    public static final SourceInfo getSourceInfoByApp(String app) {
        AppStore appStore = (AppStore) SourceManager.getSourceHandler("apps");
        AppInfo info = appStore.getAppInfo(app);
        if (info == null)
            return null;
        if (dbStore == null) {
            synchronized (synObject) {
                if (dbStore == null)
                    dbStore = (DataSourceStore) SourceManager.getSourceHandler("dbs");
            }
        }
        return dbStore.getDataSource(info.getSourceName());
    }


    public static final DefaultMetaService getMetaInterface(String key) {
        DefaultMetaService metaService = metaServiceMap.get(key);
        if (metaService == null) {
            if (dbStore == null) {
                synchronized (synObject) {
                    if (dbStore == null)
                        dbStore = (DataSourceStore) SourceManager.getSourceHandler("dbs");
                }
            }
            SourceInfo sourceInfo = dbStore.getDataSource(key);
            if (sourceInfo != null) {
                metaService = new DefaultMetaService(sourceInfo);
                metaServiceMap.put(key, metaService);
            }
        }
        return metaService;
    }

    @Override
    protected Response doGet(Request request) {
        String _t = request.get("_t");
        Object obj = null;
        if ("gp".equals(_t)) {
            return new FreeMarkerResponse("#project/base-data", request.getParas());
        } else if ("setting".equals(_t)) {
            String str = this.getTableSetting(request.get("module"), request.get("table"));
            return new JsonStrResponse(str);
        } else if ("clear".equals(_t)) {
            obj = this.clearTableSetting(request.get("module"), request.get("table"));
            return new JsonResponse(obj);
        } else if ("conf".equals(_t)) {
            obj = this.getColumnDef(request.get("module"));
            return new JsonResponse(obj);
        } else if ("tables".equals(_t)) {
            obj = this.getTables(request.get("source"));
            return new JsonResponse(obj);
        } else if ("tableInfo".equals(_t)) {
            obj = this.getTableInfo(request.get("table"), request.get("source"));
            return new JsonResponse(obj);
        } else if ("tableData".equals(_t)) {
            obj = this.getTableData(request.get("table"), request.get("source"));
            return new JsonResponse(obj);
        } else if ("tableColumns".equals(_t)) {
            obj = this.getTableColumns(request.get("table"), request.get("source"));
            return new JsonResponse(obj);
        }

        return super.doGet(request);
    }

    @Override
    protected Response doDelete(Request request) {
        return super.doDelete(request);
    }

    @Override
    protected Response doPut(Request request) {
        return super.doPut(request);
    }

    @Override
    protected Response doPost(Request request) {
        String _t = request.get("_t");
        Object obj = null;
        if ("setting".equals(_t)) {
            obj = this.saveTableSetting(request.get("module"), request.get("table"), request.getBody());
        } else if ("clear".equals(_t)) {
            obj = this.clearTableSetting(request.get("module"), request.get("table"));
        } else if ("conf".equals(_t)) {
            try {
                Map<String, String> map = JsonMapper.mapper.readValue(request.getBody(), new TypeReference<Map<String, String>>() {
                });
                obj = this.saveColumnDef(request.get("module"), map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("install".equals(_t)) {
            return this.generateSourceEx(request.get("source"), request.get("module"), request.get("label"), false, request.get("pack"));
        }

        if (obj != null)
            return new JsonResponse(obj);
        return super.doPost(request);
    }

    Object saveTableSetting(String module, String table, String string) {
        // TableColumn column
        FileMnger.writeText(module, table, JsonFormat.format(string));
        return OpResult.OK;
    }

    Object clearTableSetting(String module, String table) {
        // TableColumn column
        FileMnger.clearModule(module, table);
        return OpResult.OK;
    }

    String getTableSetting(String module, String table) {
        return FileMnger.readText(module, table);
    }

    Object saveColumnDef(String module, Map<String, String> map) {
        FileMnger.writeMap(module, map);
        return OpResult.OK;
    }


    Object getColumnDef(String module) {
        return FileMnger.readMap(module);
    }

    Object getTables(String source) {
        DefaultMetaService dbMetaSrv = this.getMetaInterface(source);
        if (dbMetaSrv != null)
            return dbMetaSrv.getTableNames();
        else
            return Collections.EMPTY_LIST;
    }


    public Object getTableInfo(String table, String source) {
        DefaultMetaService dbMetaSrv = this.getMetaInterface(source);
        if (dbMetaSrv != null)
            return dbMetaSrv.getTable(table);
        else
            return Collections.EMPTY_LIST;
    }

    Object getTableData(String table, String source) {
        DefaultMetaService dbMetaSrv = this.getMetaInterface(source);
        if (dbMetaSrv != null)
            return dbMetaSrv.getTableData(table, 1, 100);
        else
            return Collections.EMPTY_LIST;
    }

    Object getTableColumns(String table, String source) {
        DefaultMetaService dbMetaSrv = this.getMetaInterface(source);
        if (dbMetaSrv != null)
            return dbMetaSrv.getColumnMetaData(table);
        else
            return Collections.EMPTY_LIST;
    }

    Response generateSourceEx(String source, String module,
                              String moduleLabel,
                              boolean reload,
                              String packPath) {
        String token = module + UUID.randomUUID().toString();
        AppStore appStore = (AppStore) SourceManager.getSourceHandler("apps");
        AppInfo info = appStore.getAppInfo(module);
        ProcessJobUk job = new ProcessJobUk(token, "资源同步");
        String userHome = FileMnger.STORE_DIR;
        String codePath = (info != null ? info.getAppHome() + "/src" : userHome.concat("/_tmp/src"));
        String bytePath = (info != null ? info.getAppHome() + "/classes" : userHome.concat("/_tmp/target"));
        job.setLabel(moduleLabel);
        job.setAppInfo(info);
        if (this.dbStore == null)
            this.dbStore = (DataSourceStore) SourceManager.getSourceHandler("dbs");
        job.setSourceInfo(this.dbStore.getDataSource(source));
        job.setConnection(this.getMetaInterface(source).getConnection());
        job.setModule(module);
        job.setReload(reload);
        job.setPackPath(packPath);
        job.setCodePath(codePath);
        job.setBytePath(bytePath);
        PoolManger.threadPool.submit(job);
        return new JsonStrResponse("{\"token\":\"" + token + "\"}");
    }

}

class ProcessJobUk extends ProcessJob implements Log {
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

    public ProcessJobUk(String sid) {
        super(sid);
    }

    public ProcessJobUk(String sid, String label) {
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
        FileToolHelper.copyFile(SystemConfig.WEB_ROOT + "/js", path);
        FileToolHelper.copyFile(SystemConfig.WEB_ROOT + "/css", path);
        FileToolHelper.copyFile(SystemConfig.WEB_ROOT + "/icons", path);
        FileToolHelper.copyFile(SystemConfig.WEB_ROOT + "/img", path);
        FileToolHelper.copyFile(SystemConfig.WEB_ROOT + "/index.html", path);
        FileToolHelper.copyFile(SystemConfig.WEB_ROOT + "/loginCmd.html", path);
        FileToolHelper.copyFile(SystemConfig.WEB_ROOT + "/main.html", path);

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
            Properties properties = PropertiesFileUtil
                    .getPropertiesEl("classpath:conf/config4mysql.properties");
            Properties moduleSet = PropertiesFileUtil
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

            FileUtil.clearSub(new File(gcd.getBasePath()));

            gcd.genPojo();
            gcd.genServiceCode();
            gcd.genProviderCode();

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

            this.copyPage(this.appInfo.getWebRootPath(), this.appInfo.getConfigPath());
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
