package com.sparrow.tools.cmd.eggs;

import com.sparrow.core.config.FileMnger;
import com.sparrow.core.json.JsonMapper;
import com.sparrow.tools.cmd.MetaItem;
import com.sparrow.tools.cmd.XmdMapperGenerator;
import com.sparrow.tools.cmd.freemark.XmdFreeMarker;
import com.sparrow.tools.cmd.meta.PojoTable;
import com.sparrow.tools.mapper.NameRule;
import com.sparrow.tools.mapper.data.Table;
import com.sparrow.tools.mapper.data.TableColumn;
import com.sparrow.tools.pogen.DefaultLog;
import com.sparrow.tools.pogen.IDAliasGenerator;
import com.sparrow.tools.pogen.Log;
import com.sparrow.tools.pogen.check.StrCheck;
import com.sparrow.tools.pogen.check.StrDefaultCheck;
import com.sparrow.tools.pogen.check.StrEqualsCheck;
import com.sparrow.tools.pogen.check.StrRegexCheck;
import com.sparrow.tools.pogen.generator.DefaultGeneratorWrap;
import com.sparrow.tools.pogen.generator.GeneratorWrap;
import com.sparrow.tools.pogen.generator.IdGeneratorDefine;
import com.sparrow.tools.pogen.meta.MetaDefine;
import com.sparrow.tools.pogen.meta.MetaField;
import com.sparrow.tools.utils.FileUtil;
import com.sparrow.tools.utils.FileUtil.Filter;
import com.sparrow.tools.utils.JdbcHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * @author YZC
 * @version 0.1 (2013-11-5)
 * @modify
 */
public class ProviderGenerator {
    protected static final Filter filter = new Filter() {
        @Override
        public boolean check(File file) {
            if (file.isDirectory() && ".svn".equalsIgnoreCase(file.getName()))
                return false;
            return true;
        }
    };

    protected String moduleName;
    protected String moduleLabel;
    protected String basePath;
    protected String packageName;
    protected boolean clearBefore;
    protected boolean generateApi;

    protected final Log log;

    public ProviderGenerator() {
        this(new DefaultLog());
    }

    public ProviderGenerator(Log log) {
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


    public boolean isClearBefore() {
        return clearBefore;
    }

    public void setClearBefore(boolean clearBefore) {
        this.clearBefore = clearBefore;
    }

    public void initialize() throws Exception {
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

    protected File getConfigPath(String basePath) {
        return XmdMapperGenerator.getNormalFile(basePath,
                "eggs", "providerConfig.xml");
    }

    String getJavaSimpleType(String type) {
        int idx = type.lastIndexOf('.');
        if (idx != -1)
            return type.substring(idx + 1);
        return type;
    }

    protected Table genTable(MetaDefine md, String mapperPackage) throws ClassNotFoundException {
        Table table = new Table();
        table.setName(md.getTable());
        table.setDesc(md.getApp());
        table.setMapperPakage(mapperPackage);
        table.setPakage(this.packageName);
        table.setObjName(NameRule.toBeanName(md.getTable()));

        List<MetaField> fields = md.getSetting();
        List<TableColumn> items = new ArrayList<TableColumn>();
        Set<String> imports = new HashSet<String>();
        TableColumn column;
        for (MetaField field : fields) {
            column = new TableColumn();
            column.setDesc(field.getDesc());
            column.setName(field.getName());
            column.setClassType(Class.forName(field.getJavaType()));
            column.setJavaType(field.getJavaType());
            column.setFieldName(field.getField());
            column.setFieldNameX(NameRule.toBeanName(field.getField()));
            column.setSampleType(this.getJavaSimpleType(field.getJavaType()));
            //field.getSimpleType()
            column.setSize(field.getSize());
            column.setType(field.getType());
            column.setSqlType(JdbcHelper.getSqlType(column.getClassType()));
            if (!StringUtils.startsWith(field.getJavaType(), "java.lang"))
                imports.add(field.getJavaType());
            items.add(column);
        }
        table.setItems(items);
        table.setMapperPakage(this.packageName);
        table.setImports(imports);
        return table;
    }

    public void execute() throws Exception {
        this.initialize();
        // 读取配置，是否有针对table的列配置
        File file = new File(FileMnger.STORE_DIR, FileMnger.PROVIDER_DIR
                + File.separatorChar + this.moduleName);
        File[] fs = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(FileMnger.JSON_SUFFIX);
            }
        });

        List<MetaDefine> metaDefines = new ArrayList<MetaDefine>();
        if (fs != null && fs.length > 0) {
            for (File f : fs) {
                MetaDefine md = JsonMapper.mapper.readValue(f, MetaDefine.class);
                metaDefines.add(md);
            }
        }

        Map<String, Object> c = new HashMap<String, Object>();
        String basePath = this.basePath;
        String suffix = this.packageName;
        int idx = suffix.lastIndexOf('.');
        if (idx != -1)
            suffix = suffix.substring(idx + 1);
        String modulePak;
        String basePak = this.packageName;

        if ("provider".equals(suffix) || "providers".equals(suffix)) {
            modulePak = this.packageName;
            basePak = modulePak.substring(0, modulePak.lastIndexOf('.'));
        } else
            modulePak = this.packageName + ".provider";

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
        List<MetaItem> list = new ArrayList<MetaItem>();

        String subPak = this.mergeName(modulePak, subModule);
        Set<String> set = new HashSet<String>();
        for (MetaDefine mdx : metaDefines) {
            Table table = this.genTable(mdx, mapperPak);
            table.setPakage(subPak);
            c.put("table", table);
            MetaItem itm = new MetaItem();
            itm.setClazzName(table.getObjName());
            itm.setSelectSql((String) c.get("_select"));
            itm.setQuerySql(itm.getSelectSql());
            itm.setPrimaryKey(table.getPrimaryKeys());
            itm.setDesc(table.getDesc());
            itm.setTable(table.getName());
            itm.setPack(subPak);

            itm.setApp(mdx.getApp());
            list.add(itm);

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
        //cc.put("sources", sourceConfigs);
        cc.put("cName", "MetaInfo");
        File moduleFile = this.getConfigPath(basePath);
        XmdFreeMarker.getInstance().writeFile("provider", cc, moduleFile);

//        FreeMarkerUtils.getInstance().writeFile("au_ServiceMngr", gv,
//                serMngrFile);
//        String regCmdPak = this.packageName + ".command";
//        File regCmdFile = MapperGenerator.getJavaPackFile(basePath, regCmdPak,
//                "CmdReg");
//        gv.setCmdPackage(regCmdPak);
//        FreeMarkerUtils.getInstance().writeFile("au_CmdReg", gv, regCmdFile);

        //   dm.close();
    }

    protected String mergeName(String basePackage, String subPackage) {
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
