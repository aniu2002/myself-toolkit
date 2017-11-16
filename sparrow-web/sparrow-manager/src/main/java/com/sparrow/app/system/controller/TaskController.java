package com.sparrow.app.system.controller;

import java.io.File;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import com.sparrow.common.backend.ProcessJob;
import com.sparrow.common.backend.ProcessResult;
import com.sparrow.common.concurrency.PoolManger;
import com.sparrow.core.config.FileMnger;
import com.sparrow.server.context.Application;
import com.sparrow.server.context.BeanContextHelper;
import com.sparrow.core.bundle.BundleLoader;
import com.sparrow.core.log.SysLogger;
import com.sparrow.orm.template.HitTemplate;
import com.sparrow.server.context.ServerBundleContext;
import com.sparrow.service.annotation.Autowired;
import com.sparrow.service.bean.BeanInitialize;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.server.web.OpResult;
import com.sparrow.server.web.annotation.PathVariable;
import com.sparrow.server.web.annotation.ReqMapping;
import com.sparrow.server.web.annotation.ReqParameter;
import com.sparrow.server.web.annotation.ResponseBody;
import com.sparrow.server.web.annotation.WebController;

import com.sparrow.tools.cmd.XmdPojoGenerateTask;
import com.sparrow.tools.compile.ClassCompiler;
import com.sparrow.tools.holder.ConnectionHolder;
import com.sparrow.tools.pogen.CodeGeneratorEx;
import com.sparrow.tools.pogen.Log;
import com.sparrow.tools.pogen.PojoGenerateTask;
import com.sparrow.tools.pogen.check.ModuleMatcher;
import com.sparrow.tools.utils.PropertiesFileUtil;

@WebController(value = "/task")
public class TaskController implements BeanInitialize {
    @Autowired("hitTemplate")
    private HitTemplate hitTemplate;
    private boolean first = true;
    static String packPath = "com.dili.dd.cornucopia.bps";

    @ReqMapping(value = "/generate", method = ReqMapping.GET)
    @ResponseBody
    public String generateSource(String keywords) {
        // System.setProperty(PojoGenerator.GENERATE_INTERFACE_KEY, "true");
        // System.setProperty(PojoGenerator.GENERATE_MODULE_NAME, "system");
        // System.setProperty(PojoGenerator.GENERATE_MODULE_LABEL, "BT种子资源");
        ConnectionHolder.holdConnection(this.hitTemplate.getSessionFactory().getConnection());
        PojoGenerateTask task = new PojoGenerateTask();
        task.setBasePath(SystemConfig.SOURCE_DIR);
        task.setPackageName(packPath);
        task.setJdbcConfig("classpath:conf/config4mysql.properties");
        task.setTableFilter("test_*");
        task.setClearBefore(true);
        task.execute();
        ConnectionHolder.remove();

        File classPath = new File(SystemConfig.TARGET_DIR);
        new ClassCompiler(new File(SystemConfig.SOURCE_DIR), classPath,
                new PrintWriter(System.err)).compile();
        // Application.actionController = new AnnotationController();
        // Application.sessionFactory = new AnnotationCfgSessionFactory();
        // Application.serviceContext = new AppServiceContext();
        ServerBundleContext cxt = Application.app().createBundleContext("system");
        BundleLoader loader = new BundleLoader(classPath);

        if (first) {
            BeanContextHelper.loadToAppContext("au/app/**/*.class", cxt,
                    loader.getClassLoader(), false, false);
            first = false;
        } else
            BeanContextHelper.loadToAppContext("au/app/**/*.class", cxt,
                    loader.getClassLoader(), true, false);
        return "{flag:1}";
    }

    @ReqMapping(value = "/install/{module}", method = ReqMapping.POST)
    public Object generateSourceEx(@PathVariable("module") String module,
                                   @ReqParameter("label") String moduleLabel,
                                   @ReqParameter("reload") boolean reload,
                                   @ReqParameter("token") String token,
                                   @ReqParameter("pack") String packPath) {
        ProcessJobUk job = new ProcessJobUk(token, "资源同步");
        String userHome = FileMnger.STORE_DIR;
        String codePath = userHome.concat("/_tmp/src");
        String bytePath = userHome.concat("/_tmp/target");
        job.setLabel(moduleLabel);
        job.setModule(module);
        job.setReload(reload);
        job.setPackPath(packPath);
        job.setBasePath(codePath);
        job.setBytePath(bytePath);
        PoolManger.threadPool.submit(job);
        return OpResult.OK;
    }

    @Override
    public void initialize() {
        SysLogger.info(" --- Task web initialized ...");
    }
}

class ProcessJobUk extends ProcessJob implements Log {
    private String basePath;
    private String bytePath;
    private String module;
    private String label;
    private String packPath = "com.dili.dd.cornucopia.bps";
    private boolean reload;

    public ProcessJobUk(String sid) {
        super(sid);
    }

    public ProcessJobUk(String sid, String label) {
        super(sid, label);
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
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
        task.setBasePath(this.basePath);
        task.setPackageName(this.packPath);
        task.setTableFilter("*");
        task.setJdbcConfig("classpath:conf/config4mysql.properties");
        task.setClearBefore(false);
        task.execute();
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
            Log log = this;

            CodeGeneratorEx codeGenerator = new CodeGeneratorEx();
            codeGenerator.setBasePath(basePath);
            codeGenerator.setProperty(properties);
            codeGenerator.setModuleName(this.module);
            codeGenerator.setModuleLabel(this.label);
            codeGenerator.setPackageName(this.packPath);
            // codeGenerator.setTableFilter("test_*");
            // codeGenerator.setExcludeFilter("bt_*");
            // codeGenerator.setBasePath(basePath)
            codeGenerator.setModuleSet(moduleSet);
            codeGenerator.setLog(log);
            codeGenerator.setClearBefore(false);
            codeGenerator.setGenerateApi(true);
            if (moduleSet != null && moduleSet.size() > 0) {
                ModuleMatcher matcher = new ModuleMatcher();
                Properties prop = moduleSet;
                Enumeration<Object> enumeration = prop.keys();
                while (enumeration.hasMoreElements()) {
                    String key = (String) enumeration.nextElement();
                    String value = prop.getProperty(key);
                    if (log != null)
                        log.info("add module matcher : " + key + " - " + value);
                    matcher.addModule(value, key);
                }
                codeGenerator.setMatcher(matcher);
            }
            codeGenerator.execute();

            // 编译class
            /*
			 * File classPath = new File(bytePath); log.msg("-编译java类文件：" +
			 * classPath.getPath()); new ClassCompiler(new File(codePath),
			 * classPath, new PrintWriter( System.err)).compile();
			 * 
			 * if (this.reload) { log.msg("-重新加载web模块：" + classPath.getPath());
			 * WebBundle.loadWebBundle(false); }
			 */
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
