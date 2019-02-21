package com.sparrow.collect.config;

import com.sparrow.collect.analyze.IAnalyze;
import com.sparrow.collect.format.StringFormat;
import com.sparrow.collect.utils.FileIOUtil;
import com.sparrow.collect.utils.GsonKit;
import com.sparrow.collect.utils.bean.BeanUtils;
import com.sparrow.collect.website.utils.PathResolver;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Field;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/2/21 0021.
 */
public class Configuration {
    private static final Configuration INSTANCE = new Configuration();
    private final Map<String, IndexSetting> settings;
    private String indexConfigPath;
    private String baseIndexDataPath;
    private String baseIndexLogsPath;
    private String baseHome;

    private Configuration() {
        settings = new HashMap(5, 0.9f);
        this.initializeIndexConfig();
    }

    private void initializeIndexConfig() {
        String home = System.getProperty("baseHome");
        if (StringUtils.isEmpty(home))
            home = System.getProperty("user.dir");
        this.baseHome = home;
        String path = System.getProperty("index.config.path");
        if (StringUtils.isEmpty(path))
            path = String.format("%s/config", home);
        this.indexConfigPath = path;
        path = System.getProperty("index.data.path");
        if (StringUtils.isEmpty(path))
            path = String.format("%s/data", home);
        this.baseIndexDataPath = path;
        path = System.getProperty("index.logs.path");
        if (StringUtils.isEmpty(path))
            path = String.format("%s/logs", home);
        this.baseIndexLogsPath = path;
        File dir = new File(this.indexConfigPath);
        if (dir.exists()) {
            if (dir.isFile()) {
                this.parseIndexSetting(dir);
            } else {
                File[] files = dir.listFiles();
                for (File file : files) {
                    this.parseIndexSetting(file);
                }
            }
        }
    }

    private void parseIndexSetting(File file) {
        IndexMeta indexMeta = GsonKit.toBean(FileIOUtil.readFile(file), IndexMeta.class);
        String key = indexMeta.getIndex();
        if (StringUtils.isEmpty(key))
            key = PathResolver.trimExtension(file.getName());
        this.settings.put(key, this.indexMetaToIndexSetting(key, file, indexMeta));
    }

    private IndexSetting indexMetaToIndexSetting(String index, File file, IndexMeta indexMeta) {
        IndexSetting indexSetting = new IndexSetting();
        indexSetting.setAlias(index);
        indexSetting.setIndex(index);
        indexSetting.setConfigFile(file.getPath());
        indexSetting.setDataPath(String.format("%s/%s", this.baseIndexDataPath, index));
        indexSetting.setLogsPath(String.format("%s/%s", this.baseIndexLogsPath, index));

        Map<String, String> analyzersMetaMap = indexMeta.getAnalyzers();
        if (MapUtils.isNotEmpty(analyzersMetaMap)) {
            Map<String, IAnalyze> analyzersMap = new HashMap(analyzersMetaMap.size());
            analyzersMetaMap.forEach((k, v) ->
                    analyzersMap.put(k, BeanUtils.instance(v, IAnalyze.class)));
            indexSetting.setAnalyzers(analyzersMap);
        }
        Map<String, String> formatsMap = indexMeta.getFormats();
        if (MapUtils.isNotEmpty(formatsMap)) {
            Map<String, StringFormat> stringFormatMap = new HashMap(formatsMap.size());
            analyzersMetaMap.forEach((k, v) ->
                    stringFormatMap.put(k, BeanUtils.instance(v, StringFormat.class)));
            indexSetting.setFormats(stringFormatMap);
        }
        Map<String, FieldMeta> fieldMetaMap = indexMeta.getFields();
        if (MapUtils.isNotEmpty(fieldMetaMap)) {
            Map<String, FieldSetting> fieldSettingMap = new HashMap(fieldMetaMap.size());
            fieldMetaMap.forEach((k, v) ->
                    fieldSettingMap.put(k, this.createFieldSetting(k, v, indexSetting.getAnalyzers(),
                            indexSetting.getFormats())));
            indexSetting.setFields(fieldSettingMap);
        }
        return indexSetting;
    }

    private FieldSetting createFieldSetting(String name, FieldMeta fieldMeta,
                                            Map<String, IAnalyze> analyzersMap,
                                            Map<String, StringFormat> stringFormatMap) {
        FieldSetting setting = new FieldSetting();
        setting.setName(name);
        setting.setType(FieldType.of(fieldMeta.getType()));
        if (StringUtils.isNotEmpty(fieldMeta.getAnalyzer()) && MapUtils.isNotEmpty(analyzersMap)) {
            setting.setAnalyzer(analyzersMap.get(fieldMeta.getAnalyzer()));
        }
        setting.setBoost(fieldMeta.getBoost());
        if (StringUtils.isNotEmpty(fieldMeta.getFormat()) && MapUtils.isNotEmpty(stringFormatMap)) {
            setting.setFormat(stringFormatMap.get(fieldMeta.getFormat()));
        }
        setting.setDescription(fieldMeta.getDescription());
        setting.setStore(this.getFieldStore(fieldMeta.getStore()));
        return setting;
    }

    private Field.Store getFieldStore(String s) {
        if (StringUtils.isEmpty(s) || StringUtils.equals("yes", s.toLowerCase())
                || StringUtils.equals("true", s.toLowerCase())
                || StringUtils.equals("1", s.toLowerCase()))
            return Field.Store.YES;
        else
            return Field.Store.NO;
    }

    public static Configuration getInstance() {
        return INSTANCE;
    }

    public Collection<String> indexNames() {
        return settings.keySet();
    }

    public Collection<IndexSetting> indexSettings() {
        return settings.values();
    }
}
