package com.sparrow.tools.pogen;

import com.sparrow.core.config.FileMnger;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.core.json.JsonMapper;
import com.sparrow.core.utils.PropertiesFileUtil;
import com.sparrow.core.utils.date.TimeUtils;
import com.sparrow.tools.common.DbSetting;
import com.sparrow.tools.holder.ConnectionHolder;
import com.sparrow.tools.mapper.data.STable;
import com.sparrow.tools.mapper.data.Table;
import com.sparrow.tools.mapper.data.TableColumn;
import com.sparrow.tools.pogen.check.*;
import com.sparrow.tools.pogen.generator.DefaultGeneratorWrap;
import com.sparrow.tools.pogen.generator.GeneratorWrap;
import com.sparrow.tools.pogen.generator.IdGeneratorDefine;
import com.sparrow.tools.pogen.meta.GenMngrVo;
import com.sparrow.tools.pogen.meta.MetaDefine;
import com.sparrow.tools.pogen.meta.MetaField;
import com.sparrow.tools.pogen.writer.ModuleWriter;
import com.sparrow.tools.utils.FileUtil;
import com.sparrow.tools.utils.FileUtil.Filter;
import com.sparrow.tools.utils.FreeMarkerUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.Types;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YZC
 * @version 0.1 (2013-11-5)
 * @modify
 */
public class CodeGeneratorEx {
    public static final String defaultDaoName = "com.sparrow.core.orm.dao.BaseDao";

    static final Filter filter = new Filter() {
        @Override
        public boolean check(File file) {
            if (file.isDirectory() && ".svn".equalsIgnoreCase(file.getName()))
                return false;
            return true;
        }
    };

    private ModuleMatcher matcher;
    private IDAliasGenerator generator;
    private Pattern pattern;
    private Pattern excludePattern;
    private Properties property;
    private Properties moduleSet;
    private String jdbcConfig;
    private String moduleName;
    private String moduleLabel;
    private String parentDao = defaultDaoName;
    private String basePath;
    private String packageName;
    private String tableFilter;
    private String excludeFilter;
    private Log log;
    private boolean clearBefore;
    private boolean generateApi;

    public Properties getModuleSet() {
        return moduleSet;
    }

    public void setModuleSet(Properties moduleSet) {
        this.moduleSet = moduleSet;
    }

    public String getParentDao() {
        return parentDao;
    }

    public void setParentDao(String parentDao) {
        this.parentDao = parentDao;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModuleLabel() {
        return moduleLabel;
    }

    public void setModuleLabel(String moduleLabel) {
        this.moduleLabel = moduleLabel;
    }

    public boolean isGenerateApi() {
        return generateApi;
    }

    public void setGenerateApi(boolean generateApi) {
        this.generateApi = generateApi;
    }

    public ModuleMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(ModuleMatcher matcher) {
        this.matcher = matcher;
    }

    public static void main(String args[]) {
        Properties properties = PropertiesFileUtil
                .getPropertiesEl("classpath:conf/config4mysql.properties");
        Properties moduleSet = PropertiesFileUtil
                .getPropertiesEl("classpath:conf/module.properties");
        Log log = new DefaultLog();

        String module = "test";
        String moduleLabel = "报表后台管理";

        // String bytePath = userHome.concat("/_tmp/target");

        CodeGeneratorEx codeGenerator = new CodeGeneratorEx();
        codeGenerator.setBasePath("D:\\workspace\\_code\\sparrow-egg\\sparrow-web\\sparrow-myweb\\src\\main\\java");
        codeGenerator.setPackageName("com.sparrow.app.information");
        codeGenerator.setProperty(properties);
        codeGenerator.setModuleName(module);
        codeGenerator.setModuleLabel(moduleLabel);
        // codeGenerator.setTableFilter("test_*");
        // codeGenerator.setExcludeFilter("bt_*");
        codeGenerator.setLog(log);
        codeGenerator.setClearBefore(true);
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
                matcher.addModule(key, value);
            }
            codeGenerator.setMatcher(matcher);
        }
        try {
            // codeGenerator.execute();
            codeGenerator.executex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void doGenerate(Properties properties, Properties moduleSet,
                                  String basePath, String packageName, String tableFilter,
                                  boolean clearBefore, Log log) throws Exception {

        CodeGeneratorEx poGenerateUtil = new CodeGeneratorEx();
        poGenerateUtil.setBasePath(basePath);
        poGenerateUtil.property = properties;
        poGenerateUtil.setPackageName(packageName);
        poGenerateUtil.setTableFilter(tableFilter);
        poGenerateUtil.setLog(log);
        poGenerateUtil.setClearBefore(clearBefore);
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
            poGenerateUtil.matcher = matcher;
        }

        poGenerateUtil.execute();
    }

    public static Pattern createRegexPattern(String express) {
        return Pattern.compile(express);
    }

    public static String createRegexString(String filter) {
        if (StringUtils.isNotBlank(filter) && !"*".equals(filter)) {
            StringBuilder sb = new StringBuilder();
            String regex;
            boolean notFirst = false;
            for (StringTokenizer tokenizer = new StringTokenizer(filter, ","); tokenizer
                    .hasMoreElements(); ) {
                if (!notFirst)
                    notFirst = true;
                else
                    sb.append('|');
                regex = tokenizer.nextToken().toLowerCase().replace(".", "\\.")
                        .replace("?", ".?").replace("*", ".*");
                sb.append("(").append(regex).append(")");
            }
            return sb.toString();
        }
        return null;
    }

    public void setProperty(Properties property) {
        this.property = property;
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public boolean isClearBefore() {
        return clearBefore;
    }

    public void setClearBefore(boolean clearBefore) {
        this.clearBefore = clearBefore;
    }

    public void initialize() throws Exception {
        if (this.log == null)
            this.log = new DefaultLog();
        this.log.info("### JDBC conf path : " + this.jdbcConfig);
        if (this.property == null) {
            this.testIfEmpty(this.jdbcConfig, "jdbc配置文件");
            this.property = PropertiesFileUtil.getPropertiesEl(this.jdbcConfig);
        }
        this.testIfNull(this.property, "jdbc配置文件-" + this.jdbcConfig);
        this.generator = new AliasGenerator(this.property, this.log);
        if (this.tableFilter == null && this.property != null)
            this.tableFilter = this.property.getProperty("map.table.filter");
        if (this.excludeFilter == null && this.property != null)
            this.excludeFilter = this.property.getProperty("exclude.filter");
        String regex = createRegexString(this.tableFilter);
        this.log.info(" -- Table filter : " + regex);
        this.pattern = StringUtils.isEmpty(regex) ? null : Pattern
                .compile(regex);
        regex = createRegexString(this.excludeFilter);
        this.excludePattern = StringUtils.isEmpty(regex) ? null : Pattern
                .compile(regex);
    }

    protected boolean check(String table) {
        if (this.pattern == null)
            return true;
        Matcher m1 = this.pattern.matcher(table.toLowerCase());
        return m1.matches();
    }

    protected boolean exclude(String table) {
        if (this.excludePattern == null)
            return false;
        Matcher m1 = this.excludePattern.matcher(table.toLowerCase());
        return m1.matches();
    }

    public String getJdbcConfig() {
        return jdbcConfig;
    }

    public void setJdbcConfig(String jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getTableFilter() {
        return tableFilter;
    }

    public void setTableFilter(String tableFilter) {
        this.tableFilter = tableFilter;
    }

    public String getExcludeFilter() {
        return excludeFilter;
    }

    public void setExcludeFilter(String excludeFilter) {
        this.excludeFilter = excludeFilter;
    }

    public Properties getProperty() {
        return property;
    }

    void testIfEmpty(String s, String echo) throws Exception {
        if (s == null || "".equals(s.trim()))
            throw new Exception(echo + ": is empty !");
    }

    void testIfNull(Object object, String echo) throws Exception {
        if (object == null)
            throw new Exception(echo + ": is not found !");
    }

    public void execute() throws Exception {
        this.initialize();

        Properties t = this.property;
        if (t == null) {
            throw new Exception(" Config setting was not found : "
                    + this.jdbcConfig);
        }

        String preName = "pool." + t.getProperty("pool.name");
        DbSetting dbs = new DbSetting(t.getProperty(preName + ".driver"),
                t.getProperty(preName + ".url"), t.getProperty(preName
                + ".user"), t.getProperty(preName + ".password")
        );
        this.testIfEmpty(dbs.driver, "jdbc配置[datasource.driver]");
        this.testIfEmpty(dbs.url, "jdbc配置[datasource.url]");
        this.testIfEmpty(dbs.user, "jdbc配置[datasource.user]");
        this.testIfEmpty(dbs.password, "jdbc配置[datasource.password]");

        this.log.info("### Driver : " + dbs.driver);

        MapperGenerator dm = new MapperGenerator(dbs);
        dm.setFilters(new TableFilters() {
            public boolean filter(String table) {
                return CodeGeneratorEx.this.check(table);
            }
        });
        Connection connection = ConnectionHolder.getConnection();
        if (connection != null) {
            dm.setConnection(connection);
        } else
            dm.checkDatabase();

        // 读取配置，是否有针对table的列配置
        File file = new File(FileMnger.STORE_DIR, FileMnger.DEFAULT_DIR
                + File.separatorChar + this.moduleName);
        File[] fs = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FileMnger.JSON_SUFFIX);
            }
        });
        if (fs.length == 0)
            return;
        Map<String, MetaDefine> metas = new HashMap<String, MetaDefine>();
        for (File f : fs) {
            MetaDefine md = JsonMapper.mapper.readValue(f, MetaDefine.class);
            metas.put(md.getTable().toLowerCase(), md);
        }

        List<STable> tables = dm.getTables();

        // 每个table的信息
        String basePath = this.basePath;
        String suffix = this.packageName;
        int idx = suffix.lastIndexOf('.');
        if (idx != -1)
            suffix = suffix.substring(idx + 1);
        String modulePak;
        String basePak = this.packageName;
        if ("dto".equals(suffix) || "modules".equals(suffix)) {
            modulePak = this.packageName;
            basePak = modulePak.substring(0, modulePak.lastIndexOf('.'));
        } else
            modulePak = this.packageName + ".dto";
        boolean gi = this.isGenerateApi();
        if (this.clearBefore) {
            FileUtil.clearSub(new File(basePath, basePak.replace('.',
                    File.separatorChar)));
            if (gi) {
                String daoPack = basePak + ".dao";
                String servPack = basePak + ".api";
                FileUtil.clearFile(
                        new File(basePath, daoPack.replace('.',
                                File.separatorChar)), filter
                );
                FileUtil.clearFile(
                        new File(basePath, servPack.replace('.',
                                File.separatorChar)), filter
                );
            }
        }

        Map<String, String> mp = new HashMap<String, String>();
        mp.put("applytypedictionary", "applyTypeDictionary");

        String mapperPak = this.packageName + ".mapper";
        String subModule = this.getModuleName();
        // boolean isNoModule = StringUtils.isEmpty(subModule);
        int nx = metas.size() * 9;
        this.log.setStep(100.0 / nx);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<String> srvs = new ArrayList<String>();
        String servicePackName = null;
        for (int i = 0; i < tables.size(); i++) {
            STable stable = tables.get(i);
            String tb = stable.getName();
            if (this.exclude(tb) || !this.check(tb))
                continue;
            if (!metas.containsKey(tb.toLowerCase()))
                continue;
            String gx = mp.get(tb);
            if (gx != null)
                tb = gx;
            Table table = dm.getTableDescriptor(tb, this.generator);
            // if (isNoModule)
            subModule = this.getSubModule(table, tb);
            // else
            // this.correctTableInfo(table, tb);

            String subPak = this.mergeName(modulePak, subModule);
            table.setPakage(subPak);
            table.setDesc(stable.getDesc());
            table.setMapperPakage(mapperPak);
            Map<String, Object> c = new HashMap<String, Object>();
            c.put("table", table);
            // if (table.getKeyGenerator() != null)
            // this.log.msg("-创建POJO(" + table.getName() + ") - "
            // + table.getPakage() + "." + table.getObjName() + " - {"
            // + table.getKeyGenerator() + "}");
            // else
            // this.log.msg("-创建POJO(" + table.getName() + ") - "
            // + table.getPakage() + "." + table.getObjName());
            // DTO
            MetaDefine mdx = metas.get(tb.toLowerCase());
            this.correctTableColumn(table, mdx);
            File moduleFile = MapperGenerator.getJavaPackFile(basePath, subPak,
                    table.getObjName() + "Dto");
            FreeMarkerUtils.getInstance().writeFile("au_Pojo", c, moduleFile);

            if (gi) {
                String label = this.getModuleLabel();
                this.log.info(" --- generate interface , module label : "
                        + label);
                Map<String, Object> d = this.generateInterface(basePak,
                        subModule, table, label, mdx);
                if (d != null) {
                    list.add(d);
                    String srv = (String) d.get("servicePackName") + "."
                            + d.get("pojoClassName") + "Service";
                    if (servicePackName == null)
                        servicePackName = (String) d.get("servicePackName");
                    srvs.add(srv);
                }
            }
        }
        String serMngrPak = this.packageName + ".service";
        File serMngrFile = MapperGenerator.getJavaPackFile(basePath,
                serMngrPak, "ServiceManager");
        this.log.msg("-创建ServiceManager:" + serMngrFile.getAbsolutePath());
        GenMngrVo gv = new GenMngrVo();
        gv.setServClasses(list);
        gv.setServices(srvs);
        gv.setModule(subModule);
        gv.setBasePackage(basePak);
        if (servicePackName != null)
            gv.setServicePackName(servicePackName.substring(0,
                    servicePackName.lastIndexOf('.')));
        FreeMarkerUtils.getInstance().writeFile("au_ServiceMngr", gv,
                serMngrFile);
        String regCmdPak = this.packageName + ".command";
        File regCmdFile = MapperGenerator.getJavaPackFile(basePath, regCmdPak,
                "CmdReg");
        gv.setCmdPackage(regCmdPak);
        FreeMarkerUtils.getInstance().writeFile("au_CmdReg", gv, regCmdFile);
        dm.close();
        if (this.mWriter != null)
            this.mWriter.close();
    }

    public void executex() throws Exception {
        this.initialize();

        Properties t = this.property;
        if (t == null) {
            throw new Exception(" Config setting was not found : "
                    + this.jdbcConfig);
        }

        String preName = "pool." + t.getProperty("pool.name");
        DbSetting dbs = new DbSetting(t.getProperty(preName + ".driver"),
                t.getProperty(preName + ".url"), t.getProperty(preName
                + ".user"), t.getProperty(preName + ".password")
        );
        this.testIfEmpty(dbs.driver, "jdbc配置[datasource.driver]");
        this.testIfEmpty(dbs.url, "jdbc配置[datasource.url]");
        this.testIfEmpty(dbs.user, "jdbc配置[datasource.user]");
        this.testIfEmpty(dbs.password, "jdbc配置[datasource.password]");

        this.log.info("### Driver : " + dbs.driver);

        MapperGenerator dm = new MapperGenerator(dbs);
        dm.setFilters(new TableFilters() {
            public boolean filter(String table) {
                return CodeGeneratorEx.this.check(table);
            }
        });
        Connection connection = ConnectionHolder.getConnection();
        if (connection != null) {
            dm.setConnection(connection);
        } else
            dm.checkDatabase();

        // 读取配置，是否有针对table的列配置
        File file = new File(FileMnger.STORE_DIR, FileMnger.DEFAULT_DIR
                + File.separatorChar + this.moduleName);
        File[] fs = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FileMnger.JSON_SUFFIX);
            }
        });
        if (fs.length == 0)
            return;
        Map<String, MetaDefine> metas = new HashMap<String, MetaDefine>();
        Map<String, String> metaFiles = new HashMap<String, String>();
        for (File f : fs) {
            MetaDefine md = JsonMapper.mapper.readValue(f, MetaDefine.class);
            metas.put(md.getTable().toLowerCase(), md);
            metaFiles.put(md.getTable().toLowerCase(), f.getPath());
        }
        List<STable> tables = dm.getTables();
        for (int i = 0; i < tables.size(); i++) {
            STable stable = tables.get(i);
            String tb = stable.getName();
            Table table = dm.getTableDescriptor(tb, this.generator);
            MetaDefine mdx = metas.get(tb.toLowerCase());
            if (mdx != null && table != null) {
                this.genGridItem(table, mdx);
                String f = metaFiles.get(tb.toLowerCase());
                if (f != null) {
                    String content = JsonMapper.mapper.writeValueAsString(mdx);
                    FileUtil.writeFile(new File(f), content, "utf-8");
                }
            }
        }
        if (this.mWriter != null)
            this.mWriter.close();
    }

    void genGridItem(Table table, MetaDefine md) {
        List<TableColumn> items = table.getItems();
        MetaField mf;
        for (TableColumn item : items) {
            mf = md.findMetaField(item.getName());
            if (mf == null) {
                mf = new MetaField();
                mf.setDesc(item.getDesc());
                mf.setField(item.getFieldName());
                mf.setName(item.getName());
                mf.setSearch(false);
                mf.setSimpleType(item.getSampleType());
                // mf.setUi(ui);
                // mf.setVtype(vtype);
                md.getSetting().add(mf);
            }
            mf.setJavaType(item.getJavaType());
            mf.setType(item.getType());
            // mf.setDesc(item.getDesc());
            mf.setNotNull(item.isNotNull());
            mf.setSize(item.getSize());
            mf.setPrimary(item.isPrimary());
            mf.setSimpleType(item.getSampleType());
        }
    }

    void correctTableInfo(Table table, String tb) {
        // int idxx = tb.indexOf('_');
        // if (idxx != -1)
        // table.setObjName(table.getObjName().substring(idxx));
    }

    String getSubModule(Table table, String tb) {
        String subDir = null, firstToken = null;
        // int idxx = tb.indexOf('_');
        // if (idxx != -1) {
        // firstToken = tb.substring(0, idxx).toLowerCase();
        // if (idxx > 2)
        // subDir = firstToken;
        // table.setObjName(table.getObjName().substring(idxx));
        // }

        if (this.matcher != null) {
            if (StringUtils.isEmpty(firstToken))
                firstToken = this.matcher.matchModule(tb);
            else
                firstToken = this.matcher.matchModule(tb, firstToken);
        }

        if (StringUtils.isNotEmpty(firstToken))
            subDir = firstToken;
        else {
            int idx = tb.indexOf('_');
            if (idx != -1)
                subDir = tb.substring(0, idx);
            else
                subDir = tb;
        }
        return subDir;
    }

    String mergeName(String basePackage, String subPackage) {
        if (StringUtils.isNotEmpty(subPackage))
            return basePackage + "." + subPackage;
        else
            return basePackage;
    }

    String firstCharLowcase(String f) {
        if (StringUtils.isNotEmpty(f))
            return StringUtils.lowerCase(CharUtils.toString(f.charAt(0)))
                    + f.substring(1);
        else
            return f;
    }

    Map<String, Object> generateInterface(String basePackage, String subModule,
                                          Table table, String moduleLabel, MetaDefine md) {
        if (StringUtils.isEmpty(table.getKeyJavaType()))
            return null;
        String pojoName = table.getObjName();
        String regularPojoName = StringUtils.lowerCase(CharUtils
                .toString(pojoName.charAt(0))) + pojoName.substring(1);
        String pojoImport = table.getPakage() + "." + pojoName;
        String daoPack = this.mergeName(basePackage + ".dao", subModule);
        String daoPackImpl = daoPack + ".impl";
        String daoName = pojoName + "Dao";
        String parentDaoImport = this.parentDao;
        String parentDaoName = "BaseDao";// "AbstractDao";
        int idx = parentDaoImport != null ? parentDaoImport.lastIndexOf('.')
                : -1;
        if (idx != -1)
            parentDaoName = parentDaoImport.substring(idx + 1);

        File moduleFile = MapperGenerator.getJavaPackFile(this.basePath,
                daoPack, daoName);
        File moduleImplFile = MapperGenerator.getJavaPackFile(this.basePath,
                daoPackImpl, daoName + "Impl");
        Map<String, Object> c = new HashMap<String, Object>();
        c.put("packageName", daoPack);
        c.put("basePackage", basePackage);
        c.put("daoImplPackage", daoPackImpl);
        c.put("table", table.getName());
        c.put("subModule", subModule);
        c.put("tableDesc", table.getDesc());
        c.put("dateTime", TimeUtils.currentTime());
        c.put("version", "2.0");
        c.put("pojoClass", pojoImport);
        c.put("pojoDaoName", daoName);
        c.put("simplePojoDaoName", firstCharLowcase(daoName));
        if (!table.getKeyJavaType().startsWith("java.lang"))
            c.put("pojoIdType", table.getKeyJavaType());
        c.put("pojoIdSimpleType", table.getKeySimpleJavaType());
        c.put("parentDao", parentDaoName);
        c.put("parentDaoImport", parentDaoImport);
        c.put("pojoClassName", pojoName);
        c.put("regularPojoName", regularPojoName);

        String ftlName = parentDaoName;
        if ("BaseDao".equals(parentDaoName))
            ftlName = "BaseDao";

        this.log.msg("-创建dao接口: " + daoName);
        FreeMarkerUtils.getInstance().writeFile("dao_Interface", c, moduleFile);
        this.log.msg("-创建dao实现类 : " + daoName + "Impl");
        FreeMarkerUtils.getInstance().writeFile("dao_" + ftlName, c,
                moduleImplFile);

        this.generateService(basePackage, subModule, pojoName, c);
        this.generateController(basePackage, subModule, pojoName, c);
        if (this.moduleSet != null) {
            moduleLabel = this.moduleSet.getProperty(subModule + "_label",
                    subModule);
            this.log.info(" ---- reset module label : " + moduleLabel);
        }
        this.log.info(" ---- module label : " + moduleLabel);
        this.generateView(table, c, subModule, moduleLabel, md);
        return c;
    }

    void generateService(String basePackage, String subPackage,
                         String pojoName, Map<String, Object> c) {
        String servicePack = this.mergeName(basePackage + ".api", subPackage);
        String serviceImplPack = this.mergeName(basePackage + ".service",
                subPackage);
        String service = pojoName + "Service";
        String daoPack = (String) c.get("packageName");
        String daoName = (String) c.get("pojoDaoName");

        c.put("servicePackName", servicePack);
        c.put("serviceImplPackage", serviceImplPack);
        c.put("serviceName", service);
        c.put("fServiceName", firstCharLowcase(service));
        c.put("pojoDaoImport", daoPack + "." + daoName);
        c.put("subPack", subPackage);

        File serviceFile = MapperGenerator.getJavaPackFile(this.basePath,
                servicePack, service);
        File serviceImplFile = MapperGenerator.getJavaPackFile(this.basePath,
                serviceImplPack, service + "Impl");
        this.log.msg("-创建service接口类 : " + service);
        FreeMarkerUtils.getInstance().writeFile("au_Service", c, serviceFile);
        this.log.msg("-创建service实现类: " + service + "Impl");
        FreeMarkerUtils.getInstance().writeFile("au_ServiceImpl", c,
                serviceImplFile);
        this.log.msg("-创建cmd service 类: " + service);
        // FreeMarkerUtils.getInstance()
        // .writeFile("au_CmdService", c, serviceFile);
    }

    void generateController(String basePackage, String subPackage,
                            String pojoName, Map<String, Object> c) {
        // controllerPackage
        String controllerPackage = this.mergeName(basePackage + ".command",
                subPackage);
        String pojoClazzName = (String) c.get("pojoClassName");
        String pojo4LowerCase = (String) c.get("table");
        pojo4LowerCase = pojo4LowerCase.toLowerCase();
        c.put("controllerPackage", firstCharLowcase(controllerPackage));
        c.put("pojo4LowerCase", pojo4LowerCase);

        File controllerFile = MapperGenerator.getJavaPackFile(this.basePath,
                controllerPackage, pojoClazzName + "Command");
        this.log.msg("-创建 command : /cmd/" + c.get("subModule") + "/"
                + c.get("pojo4LowerCase"));
        FreeMarkerUtils.getInstance()
                .writeFile("au_Command", c, controllerFile);
    }

    void generateView(Table table, Map<String, Object> c, String moduleName,
                      String moduleLabel, MetaDefine md) {
        // String controllerPackage = this.basePath;
        String webRoot = this.basePath + "/webapp";
        String values[] = this.getGridItems(table, md);
        String gridItems = values[0];
        String destSource = (String) c.get("pojo4LowerCase");
        c.put("gridItems", gridItems);
        c.put("appRoot", SystemConfig.APP_URL);
        c.put("imgRoot", SystemConfig.IMG_URL);
        c.put("restRoot", destSource);
        if (StringUtils.isNotEmpty(values[1])
                && !StringUtils.equals("guid", values[1]))
            c.put("idFeild", values[1]);
        if (StringUtils.isEmpty(moduleName))
            moduleName = "db";
        if (StringUtils.isEmpty(table.getDesc()))
            c.put("itemName", table.getName());
        else
            c.put("itemName", table.getDesc());
        c.put("moduleName", moduleName);
        c.put("destSource", destSource);

        File viewFile = new File(webRoot, "views/" + moduleName + "/"
                + destSource);
        if (!viewFile.exists())
            viewFile.mkdirs();
        File fviewFile = new File(viewFile, "list.html");
        this.log.msg("-创建列表页:" + destSource + "/list.html");
        FreeMarkerUtils.getInstance().writeFile("au_View", c, fviewFile);
        fviewFile = new File(viewFile, "add.html");
        this.log.msg("-创建新增页:" + destSource + "/add.html");
        FreeMarkerUtils.getInstance().writeFile("au_View_add", c, fviewFile);
        fviewFile = new File(viewFile, "edit.html");
        this.log.msg("-创建编辑页:" + destSource + "/edit.html");
        FreeMarkerUtils.getInstance().writeFile("au_View_edit", c, fviewFile);
        fviewFile = new File(viewFile, "detail.html");
        this.log.msg("-创建编辑页:" + destSource + "/detail.html");
        FreeMarkerUtils.getInstance().writeFile("au_View_detail", c, fviewFile);
        if (this.mWriter == null)
            this.mWriter = new ModuleWriter();
        this.mWriter.write(moduleName, moduleLabel, c);
    }

    ModuleWriter mWriter;

    void correctTableColumn(Table table, MetaDefine md) {
        List<TableColumn> items = table.getItems();
        MetaField mf;
        for (TableColumn item : items) {
            if (StringUtils.isEmpty(item.getDesc())) {
                mf = md.findMetaField(item.getName());
                if (mf == null)
                    continue;
                item.setDesc(mf.getDesc());
            }
        }
    }

    String[] getGridItems(Table table, MetaDefine md) {
        StringBuilder sb = new StringBuilder();
        List<TableColumn> items = table.getItems();
        MetaField mf;
        String idFeild = null;
        boolean first = true;
        for (TableColumn item : items) {
            if (first)
                first = false;
            else
                sb.append(",\r\n          ");
            mf = md.findMetaField(item.getName());
            if (mf == null) {
                System.out.println(" -- name : " + item.getName());
                continue;
            }
            this.appendGridItem(sb, item, mf);
            if (item.isPrimary() && idFeild == null)
                idFeild = item.getFieldName();
        }
        return new String[]{sb.toString(), idFeild};
    }

    void appendGridItem(StringBuilder sb, TableColumn item, MetaField mf) {
        boolean selfDefine = (mf != null);
        sb.append('{');
        sb.append("name:'").append(item.getFieldName()).append("',");
        // if (StringUtils.isEmpty(item.getDesc()))
        if (StringUtils.isNotEmpty(mf.getDesc()))
            sb.append("label:'").append(mf.getDesc()).append("',");
        else if (StringUtils.isEmpty(item.getDesc()))
            sb.append("label:'").append(item.getName()).append("',");
        else
            sb.append("label:'").append(item.getDesc()).append("',");

        if (item.isPrimary()) {
            sb.append("width:'30',");
            sb.append("render:'_idx',hidden:true,noe:true");
        } else {
            String t = this.caculateRender(item);
            if (t != null)
                sb.append("render:'").append(t).append("',");
            t = selfDefine ? mf.getUi() : this.caculateEditor(item);
            if (t != null)
                sb.append("editor:'").append(t).append("',");
            t = selfDefine ? mf.getVtype() : this.getVtype(item);
            if (t != null)
                sb.append("vtype:'").append(t).append("',");
            boolean b = selfDefine ? mf.isSearch() : this.needSearch(item);
            if (b)
                sb.append("search:true,");
            b = selfDefine ? mf.isColdef() : false;
            if (!b)
                sb.append("hidden:true,");
            sb.append("max:").append(item.getSize());
            if ("noe".equals(mf.getUi()))
                sb.append(",noe:true");
        }

        sb.append('}');
    }

    boolean needSearch(TableColumn item) {
        if (StringUtils.equals(MapperGenerator.TEXT, item.getRender())) {
            String str = item.getFieldName().toLowerCase();
            if (StringUtils.indexOf(str, "guid") != -1)
                return false;
            if (StringUtils.indexOf(str, "time") != -1)
                return true;
            if (StringUtils.indexOf(str, "date") != -1)
                return true;
            if (item.getSize() < 65)
                return true;
            return false;
        } else if (StringUtils.equals(MapperGenerator.NUMBER, item.getRender())) {
            return false;
        } else if (StringUtils.equals(MapperGenerator.DATE, item.getRender())) {
            return true;
        } else if (StringUtils.equals(MapperGenerator.TIME, item.getRender())) {
            return true;
        } else if (StringUtils
                .equals(MapperGenerator.BOOLEAN, item.getRender())) {
            return false;
        } else
            return false;
    }

    String getVtype(TableColumn item) {
        if (StringUtils.equals(MapperGenerator.TEXT, item.getRender())) {
            if (item.isNotNull())
                return MapperGenerator.REQUIRED;
            else
                return this.caculateVtype(item.getFieldName(), item);
        } else if (StringUtils.equals(MapperGenerator.NUMBER, item.getRender())) {
            return item.getNumberType();
        } else if (StringUtils.equals(MapperGenerator.DATE, item.getRender())) {
            return item.getRender();
        } else if (StringUtils.equals(MapperGenerator.TIME, item.getRender())) {
            return item.getRender();
        } else if (StringUtils
                .equals(MapperGenerator.BOOLEAN, item.getRender())) {
            return item.getRender();
        } else
            return null;
    }

    String caculateVtype(String feildName, TableColumn item) {
        String str = feildName.toLowerCase();
        if (StringUtils.indexOf(str, "time") != -1)
            return "time";
        if (StringUtils.indexOf(str, "date") != -1)
            return "date";
        return null;
    }

    String caculateEditor(TableColumn item) {
        if (StringUtils.equals(MapperGenerator.TEXT, item.getRender())) {
            return this.caculateEditor(item.getFieldName(), item);
        } else if (StringUtils.equals(MapperGenerator.DATE, item.getRender())) {
            return item.getRender();
        } else if (StringUtils.equals(MapperGenerator.TIME, item.getRender())) {
            return item.getRender();
        } else if (StringUtils
                .equals(MapperGenerator.BOOLEAN, item.getRender())) {
            return null;
        } else
            return null;
    }

    String caculateEditor(String feildName, TableColumn item) {
        String str = feildName.toLowerCase();
        if (StringUtils.indexOf(str, "time") != -1)
            return "time";
        if (StringUtils.indexOf(str, "date") != -1)
            return "time";
        if (item.getSize() > 38 || item.getSqlType() == Types.CLOB)
            return "textarea";
        return null;
    }

    String caculateRender(TableColumn item) {
        if (StringUtils.equals(MapperGenerator.TEXT, item.getRender())) {
            return this.caculateConfig(item.getFieldName(), item);
        } else if (StringUtils.equals(MapperGenerator.NUMBER, item.getRender())) {
            return item.getRender();
        } else if (StringUtils.equals(MapperGenerator.DATE, item.getRender())) {
            return item.getRender();
        } else if (StringUtils.equals(MapperGenerator.TIME, item.getRender())) {
            return item.getRender();
        } else if (StringUtils
                .equals(MapperGenerator.BOOLEAN, item.getRender())) {
            return item.getRender();
        } else
            return item.getRender();
    }

    String caculateConfig(String feildName, TableColumn item) {
        String str = feildName.toLowerCase();
        if (StringUtils.indexOf(str, "img") != -1)
            return "img";
        if (StringUtils.indexOf(str, "image") != -1)
            return "img";
        if (StringUtils.indexOf(str, "url") != -1)
            return "link";
        if (item.getSize() > 256)
            return "strcut";
        return null;
    }

    String caculateConfig(String feildName, Map<String, String> map) {
        if (map == null || map.isEmpty())
            return null;
        Iterator<Map.Entry<String, String>> iterator = map.entrySet()
                .iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            if (feildName.indexOf(key) != -1)
                return entry.getValue();
        }
        return null;
    }

    private class AliasGenerator implements IDAliasGenerator {
        private final Map<String, IdGeneratorDefine> generatorDefineMap = new HashMap<String, IdGeneratorDefine>();
        private final Map<String, String> idTypes = new HashMap<String, String>();
        private final String defaultAlias;
        private final Properties t;
        private final GeneratorWrap[] generatorWraps;
        private final Log log;

        public AliasGenerator(Properties t, Log log) {
            this.t = t;
            this.log = log;
            this.defaultAlias = t.getProperty("default.id.generator");
            idTypes.put("uuid", "string");
            idTypes.put("guid", "string");
            idTypes.put("increment", "string");
            idTypes.put("sequence", "long");
            idTypes.put("auto", "*");
            idTypes.put("function", "*");
            // *.id - uuid;
            // scm_*.id - sequence.HDX
            this.generatorWraps = this.generateIdStrategy();
        }

        void log(String msg) {
            if (this.log != null)
                this.log.info(msg);
        }

        GeneratorWrap[] generateIdStrategy() {
            if (this.defaultAlias == null)
                return null;
            List<GeneratorWrap> list = new ArrayList<GeneratorWrap>();
            if (this.defaultAlias.indexOf(';') == -1) {
                GeneratorWrap generatorWrap = this.createGeneratorWrap(null,
                        this.defaultAlias);
                this.log(" -- Add IdStrategy : " + this.defaultAlias);
                list.add(generatorWrap);
            } else {
                String[] items = com.sparrow.core.utils.StringUtils
                        .tokenizeToStringArray(this.defaultAlias, ";");
                for (int i = 0; i < items.length; i++) {
                    String regx = items[i];
                    String nx[] = com.sparrow.core.utils.StringUtils
                            .tokenizeToStringArray(regx, "-");
                    if (nx.length == 2) {
                        GeneratorWrap generatorWrap = this.createGeneratorWrap(
                                nx[0], nx[1]);
                        this.log(" -- Add IdStrategy : "
                                + regx.replace('-', '='));
                        list.add(generatorWrap);
                    }
                }
            }
            return list.toArray(new GeneratorWrap[list.size()]);
        }

        GeneratorWrap createGeneratorWrap(String prefixCheck, String idDefine) {
            GeneratorWrap generatorWrap = new DefaultGeneratorWrap(
                    this.createStrCheck(prefixCheck),
                    this.createIdGeneratorDefine(idDefine));
            return generatorWrap;
        }

        StrCheck createStrCheck(String string) {
            StrCheck strCheck = null;
            if (StringUtils.isBlank(string) || "*".equals(string))
                strCheck = new StrDefaultCheck();
            else if (StringUtils.containsAny(string, '?', '*'))
                strCheck = new StrRegexCheck(string);
            else
                strCheck = new StrEqualsCheck(string);
            return strCheck;
        }

        IdGeneratorDefine createIdGeneratorDefine(String alias) {
            int idx = alias.indexOf('.');
            IdGeneratorDefine idGeneratorDefine = null;
            if (idx != -1) {
                String idGen = alias.substring(0, idx);
                String extra = alias.substring(idx + 1);
                idGeneratorDefine = new IdGeneratorDefine(alias, extra, idGen,
                        idTypes.get(idGen));
            } else
                idGeneratorDefine = new IdGeneratorDefine(alias, alias,
                        idTypes.get(alias));
            return idGeneratorDefine;
        }

        IdGeneratorDefine find(String table, String column) {
            GeneratorWrap[] genWraps = this.generatorWraps;
            if (genWraps == null)
                return null;
            for (int i = 0; i < genWraps.length; i++) {
                GeneratorWrap genWrap = genWraps[i];
                if (genWrap.check(table, column)) {
                    // this.log.info(" #### select ID generator(" +
                    // genWrap.getGenerateDefine() + "), express : " +
                    // genWrap.getStrCheck().getExpress() + " , value : " +
                    // table + "." + column);
                    return genWrap.getGenerateDefine();
                }
            }
            return null;
        }

        @Override
        public IdGeneratorDefine getAlias(String table, String column, int type) {
            String tbx = table.toLowerCase();
            String col = column.toLowerCase();

            String key = tbx + "." + col + ".generator";
            String key2 = table + "." + column + ".generator";
            String alias = this.t.getProperty(key, this.t.getProperty(key2));

            if (StringUtils.isEmpty(alias))
                return this.find(table, column);

            if (generatorDefineMap.containsKey(alias))
                return generatorDefineMap.get(alias);

            IdGeneratorDefine idGeneratorDefine = this
                    .createIdGeneratorDefine(alias);
            generatorDefineMap.put(alias, idGeneratorDefine);

            return idGeneratorDefine;
        }
    }
}
