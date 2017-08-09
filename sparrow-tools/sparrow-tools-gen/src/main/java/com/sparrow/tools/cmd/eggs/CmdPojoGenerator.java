package com.sparrow.tools.cmd.eggs;


import com.sparrow.tools.cmd.MetaItem;
import com.sparrow.tools.cmd.XmdMapperGenerator;
import com.sparrow.tools.cmd.freemark.XmdFreeMarker;
import com.sparrow.tools.cmd.meta.PojoTable;
import com.sparrow.tools.cmd.meta.SimpleTable;
import com.sparrow.tools.common.DbSetting;
import com.sparrow.tools.holder.ConnectionHolder;
import com.sparrow.tools.pogen.DefaultLog;
import com.sparrow.tools.pogen.IDAliasGenerator;
import com.sparrow.tools.pogen.Log;
import com.sparrow.tools.pogen.TableFilters;
import com.sparrow.tools.pogen.check.StrCheck;
import com.sparrow.tools.pogen.check.StrDefaultCheck;
import com.sparrow.tools.pogen.check.StrEqualsCheck;
import com.sparrow.tools.pogen.check.StrRegexCheck;
import com.sparrow.tools.pogen.generator.DefaultGeneratorWrap;
import com.sparrow.tools.pogen.generator.GeneratorWrap;
import com.sparrow.tools.pogen.generator.IdGeneratorDefine;
import com.sparrow.tools.utils.FileUtil;
import com.sparrow.tools.utils.FileUtil.Filter;
import com.sparrow.tools.utils.PropertiesFileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YZC
 * @version 0.1 (2013-11-5)
 * @modify
 */
public class CmdPojoGenerator {
    static final Filter filter = new Filter() {
        @Override
        public boolean check(File file) {
            if (file.isDirectory() && ".svn".equalsIgnoreCase(file.getName()))
                return false;
            return true;
        }
    };

    private IDAliasGenerator generator;
    private Pattern pattern;
    private Pattern excludePattern;
    private Properties property;
    private String jdbcConfig;
    private String moduleName;
    private String moduleLabel;
    private String basePath;
    private String packageName;
    private String tableFilter;
    private String excludeFilter;
    private boolean clearBefore;
    private boolean generateApi;

    private final Log log;

    public CmdPojoGenerator() {
        this(new DefaultLog());
    }

    public CmdPojoGenerator(Log log) {
        this.log = (log == null ? new DefaultLog() : log);
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

    public static void doGenerate(Properties properties, Properties moduleSet,
                                  String basePath, String packageName, String tableFilter,
                                  boolean clearBefore, Log log) throws Exception {

        CmdPojoGenerator poGenerateUtil = new CmdPojoGenerator();
        poGenerateUtil.setBasePath(basePath);
        poGenerateUtil.property = properties;
        poGenerateUtil.setPackageName(packageName);
        poGenerateUtil.setTableFilter(tableFilter);
        poGenerateUtil.setClearBefore(clearBefore);

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

    public boolean isClearBefore() {
        return clearBefore;
    }

    public void setClearBefore(boolean clearBefore) {
        this.clearBefore = clearBefore;
    }

    public void initialize() throws Exception {
        this.log.info("### JDBC conf path : " + this.jdbcConfig);
        if (this.property == null) {
            this.testIfEmpty(this.jdbcConfig, "jdbc配置文件");
            this.property = PropertiesFileUtil.getPropertiesEl(this.jdbcConfig);
        }
        this.testIfNull(this.property, "jdbc配置文件-" + this.jdbcConfig);
        this.generator = new AliasGenerator(this.property, this.log);
        if (this.property != null) {
            String tmp = this.property.getProperty("map.table.filter");
            if (StringUtils.isNotEmpty(tmp))
                this.tableFilter = tmp;
            tmp = this.property.getProperty("exclude.filter");
            if (StringUtils.isNotEmpty(tmp))
                this.excludeFilter = tmp;
        }
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

    protected File getConfigPath(String basePath) {
        return XmdMapperGenerator.getNormalFile(basePath,
                "eggs", "mapConfig.xml");
    }

    public void execute() throws Exception {
        this.initialize();

        Properties t = this.property;
        if (t == null) {
            throw new Exception(" Config setting was not found : "
                    + this.jdbcConfig);
        }

        Map<String, String> mp = new HashMap<String, String>();

        mp.put("businesssys", "businessSys");

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

        XmdMapperGenerator dm = new XmdMapperGenerator(dbs);
        dm.setFilters(new TableFilters() {
            public boolean filter(String table) {
                return CmdPojoGenerator.this.check(table);
            }
        });
        Connection connection = ConnectionHolder.getConnection();
        if (connection != null) {
            dm.setConnection(connection);
        } else
            dm.checkDatabase();
        List<SimpleTable> tables = dm.getTables();
        Map<String, Object> c = new HashMap<String, Object>();
        String basePath = this.basePath;
        String suffix = this.packageName;
        int idx = suffix.lastIndexOf('.');
        if (idx != -1)
            suffix = suffix.substring(idx + 1);
        String modulePak;
        String basePak = this.packageName;
        if ("domain".equals(suffix) || "modules".equals(suffix)) {
            modulePak = this.packageName;
            basePak = modulePak.substring(0, modulePak.lastIndexOf('.'));
        } else
            modulePak = this.packageName + ".domain";
        boolean generateInterface = this.isGenerateApi();
        if (this.clearBefore) {
            FileUtil.clearSub(new File(basePath, basePak.replace('.',
                    File.separatorChar)));
            if (generateInterface) {
                String daoPack = basePak + ".dao";
                String servPack = basePak + ".service";
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
        String mapperPak = this.packageName + ".mapper";
        String subModule = this.getModuleName();
        boolean isNoModule = StringUtils.isEmpty(subModule);
        List<MetaItem> list = new ArrayList<MetaItem>();
        for (int i = 0; i < tables.size(); i++) {
            SimpleTable stable = tables.get(i);
            String tb = stable.getName();
            if (this.exclude(tb) || !this.check(tb))
                continue;
            String nx = mp.get(tb);
            if (nx != null)
                tb = nx;
            //System.out.println("------ " + nx);
            PojoTable table = dm.getTableDescriptor(tb, this.generator);
            if (isNoModule)
                subModule = this.getSubModule(table, tb);
            else
                this.correctTableInfo(table, tb);

            String subPak = this.mergeName(modulePak, subModule);
            table.setPakage(subPak);
            table.setDesc(stable.getDesc());
            table.setMapperPakage(mapperPak);
            c.put("table", table);
            c.put("_select", table.getSelectSql());
            c.put("_count", table.getCountSql());
            c.put("_insert", table.getInsertSql());
            c.put("_update", table.getUpdateSql());
            c.put("_delete", table.getDeleteSql());
            MetaItem itm = new MetaItem();
            itm.setInsertSql((String) c.get("_insert"));
            itm.setUpdateSql((String) c.get("_update"));
            itm.setClazzName(table.getObjName());
            itm.setSelectSql((String) c.get("_select"));
            itm.setQuerySql(table.getQuerySql());
            itm.setPrimaryKey(table.getPrimaryKeys());
            itm.setDeleteSql((String) c.get("_delete"));
            itm.setDesc(table.getDesc());
            itm.setTable(table.getName());
            itm.setPack(subPak);
            list.add(itm);
            if (table.getKeyGenerator() != null)
                this.log.info("Create POJO for table (" + table.getName()
                        + ") - " + table.getPakage() + "." + table.getObjName()
                        + " - {" + table.getKeyGenerator() + "}");
            else
                this.log.info("Create POJO for table (" + table.getName()
                        + ") - " + table.getPakage() + "." + table.getObjName());
            File moduleFile = XmdMapperGenerator.getJavaPackFile(basePath,
                    subPak, table.getObjName());
            XmdFreeMarker.getInstance().writeFile("pojo", c, moduleFile);
            //System.out.println(moduleFile.getAbsolutePath());
        }

        Map<String, Object> cc = new HashMap<String, Object>();
        cc.put("data", list);
        cc.put("pName", modulePak);
        cc.put("cName", "MetaInfo");
        File moduleFile = this.getConfigPath(basePath);
        XmdFreeMarker.getInstance().writeFile("meta", cc, moduleFile);

//        FreeMarkerUtils.getInstance().writeFile("au_ServiceMngr", gv,
//                serMngrFile);
//        String regCmdPak = this.packageName + ".command";
//        File regCmdFile = MapperGenerator.getJavaPackFile(basePath, regCmdPak,
//                "CmdReg");
//        gv.setCmdPackage(regCmdPak);
//        FreeMarkerUtils.getInstance().writeFile("au_CmdReg", gv, regCmdFile);

        dm.close();
    }

    String mergeName(String basePackage, String subPackage) {
        if (StringUtils.isNotEmpty(subPackage))
            return basePackage + "." + subPackage;
        else
            return basePackage;
    }

    void correctTableInfo(PojoTable table, String tb) {
        // int idxx = tb.indexOf('_');
        // if (idxx != -1)
        // table.setObjName(table.getObjName().substring(idxx));
    }

    String getSubModule(PojoTable table, String tb) {
        String subDir = null;
        // int idxx = tb.indexOf('_');
        // if (idxx != -1) {
        // firstToken = tb.substring(0, idxx).toLowerCase();
        // if (idxx > 1)
        // subDir = firstToken;
        // System.out.println(table.getObjName());
        // table.setObjName(table.getObjName().substring(idxx));
        // }
        return subDir;
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
            this.defaultAlias = t.getProperty("default.id.generator");
            idTypes.put("uuid", "string");
            idTypes.put("guid", "string");
            idTypes.put("increment", "string");
            idTypes.put("sequence", "long");
            // *.id - uuid;
            // scm_*.id - sequence.HDX
            this.log = log;
            this.generatorWraps = this.generateIdStrategy();
        }

        void log(String msg) {
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
