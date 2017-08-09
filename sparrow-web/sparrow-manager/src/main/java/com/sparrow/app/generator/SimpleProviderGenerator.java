package com.sparrow.app.generator;

import com.sparrow.core.config.FileMnger;
import com.sparrow.core.json.JsonMapper;
import com.sparrow.app.data.app.DataProviderCommand;
import com.sparrow.app.common.InfoHolder;
import com.sparrow.app.common.SpeEnvironment;
import com.sparrow.app.services.provider.ProviderItem;
import com.sparrow.app.services.provider.SourceConfig;
import com.sparrow.tools.cmd.MetaItem;
import com.sparrow.tools.cmd.XmdMapperGenerator;
import com.sparrow.tools.cmd.eggs.ProviderGenerator;
import com.sparrow.tools.cmd.freemark.XmdFreeMarker;
import com.sparrow.tools.mapper.data.Table;
import com.sparrow.tools.pogen.meta.MetaDefine;
import com.sparrow.tools.utils.FileUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * @author YZC
 * @version 0.1 (2013-11-5)
 * @modify
 */
public class SimpleProviderGenerator extends ProviderGenerator{

    @Override
    protected File getConfigPath(String basePath) {
        InfoHolder holder = SpeEnvironment.getInfoHolder();
        if (holder == null)
            return XmdMapperGenerator.getNormalFile(basePath,
                    "eggs", "providerConfig.xml");
        else {
            File f = XmdMapperGenerator.getNormalFile(holder.getAppInfo().getConfigPath(),
                    "eggs", "providerConfig.xml");
            holder.setMapConfigPath(f.getPath().replace('\\', '/'));
            return f;
        }
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
        List<SourceConfig> sourceConfigs = new ArrayList<SourceConfig>();
        String subPak = this.mergeName(modulePak, subModule);
        Set<String> set = new HashSet<String>();
        for (MetaDefine mdx : metaDefines) {
            Table table = this.genTable(mdx, mapperPak);
            table.setPakage(subPak);
            c.put("table", table);
            ProviderItem providerItem = DataProviderCommand.providerStore.getProviderItem(mdx.getApp(), mdx.getTable());
            String sourceName = "@";
            if (!"@".equals(providerItem.getSource())) {
                SourceConfig sourceConfig = DataProviderCommand.providerStore.getSourceConfig(mdx.getApp(), providerItem.getSource());
                if (sourceConfig != null) {
                    if (!set.contains(sourceConfig.getName())) {
                        sourceConfigs.add(sourceConfig);
                        set.add(sourceConfig.getName());
                    }
                    sourceName = sourceConfig.getName();
                }
            }
            c.put("_select", providerItem.getScript());

            MetaItem itm = new MetaItem();
            itm.setClazzName(table.getObjName());
            itm.setSelectSql((String) c.get("_select"));
            itm.setQuerySql(itm.getSelectSql());
            itm.setPrimaryKey(table.getPrimaryKeys());
            itm.setDesc(table.getDesc());
            itm.setTable(table.getName());
            itm.setPack(subPak);

            itm.setSource(sourceName);
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
        cc.put("sources", sourceConfigs);
        cc.put("pName", modulePak);
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
}
