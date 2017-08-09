package com.sparrow.tools.cmd;

import com.sparrow.tools.pogen.CodeGeneratorExl;
import com.sparrow.tools.pogen.Log;
import com.sparrow.tools.pogen.check.ModuleMatcher;
import com.sparrow.tools.utils.FileUtil;
import com.sparrow.tools.utils.PropertiesFileUtil;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by Administrator on 2015/6/1 0001.
 */
public class GeneratorCmdCode implements Log {
    private String dbConfig="classpath:conf/config4mysql.properties";
    private String mdConfig="classpath:conf/module.properties";
    private String basePath;
    private String module;
    private String label;
    private String packPath = "com.au.data.apps";

    public String getDbConfig() {
        return dbConfig;
    }

    public void setDbConfig(String dbConfig) {
        this.dbConfig = dbConfig;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
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

    public String getPackPath() {
        return packPath;
    }

    public void setPackPath(String packPath) {
        this.packPath = packPath;
    }

    public String getMdConfig() {
        return mdConfig;
    }

    public void setMdConfig(String mdConfig) {
        this.mdConfig = mdConfig;
    }

    public static void main(String args[]) {
        GeneratorCmdCode gcd=new GeneratorCmdCode();
        gcd.setBasePath("D:\\workspace\\_code"); //SystemConfig.SOURCE_DIR
        gcd.setModule("test");
        gcd.setLabel("测试");
        gcd.setPackPath("com.sparrow.collect.website.domain");
        gcd.setDbConfig("classpath:config4mysql.properties");
        gcd.setMdConfig("classpath:module.properties");

        FileUtil.clearSub(new File(gcd.getBasePath()));

        gcd.genPojo();
        gcd.genServiceCode();
    }
     void genServiceCode( ) {
        Properties properties = PropertiesFileUtil
                .getPropertiesEl(this.dbConfig);
        Properties moduleSet = PropertiesFileUtil
                .getPropertiesEl(this.mdConfig);
        Log log = this;

        CodeGeneratorExl codeGenerator = new CodeGeneratorExl();
        codeGenerator.setBasePath(this.basePath);
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
        try {
            codeGenerator.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void genPojo() {
        XmdPojoGenerateTask task = new XmdPojoGenerateTask();
        task.setBasePath(this.basePath);
        task.setPackageName(this.packPath);
        task.setTableFilter("*");
        task.setJdbcConfig(this.dbConfig);
        task.setClearBefore(false);
        task.execute();
    }


    @Override
    public void info(Object msg) {

    }

    @Override
    public void msg(Object msg) {
        System.out.println(msg);
    }

    @Override
    public void setStep(double step) {

    }
}
