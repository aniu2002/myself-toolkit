package com.sparrow.collect.store;

import com.sparrow.collect.crawler.conf.CrawlerConfig;
import com.sparrow.collect.crawler.conf.store.StoreConfig;
import com.sparrow.collect.orm.ParsedSql;
import com.sparrow.collect.orm.utils.NamedParameterUtils;
import com.sparrow.collect.utils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * Created by Administrator on 2017/7/29 0029.
 */
public class DataStoreFactory {
    static final DataStore DEFAULT = new DefaultStore();

    private DataStoreFactory() {

    }

    public static final DataStore createDataStore(CrawlerConfig crawlerConfig) {
        if (crawlerConfig == null || crawlerConfig.getStore() == null)
            return DEFAULT;
        StoreConfig config = crawlerConfig.getStore();
        if (StringUtils.isEmpty(config.getClazz()))
            return DEFAULT;
        DataStore dataStore = null;
        Map<String, String> storeProps = config.getProps();
        String tempPath = crawlerConfig.getTempDir(System.getProperty("java.io.tmpdir"));
        if (storeProps != null)
            storeProps.put("temp.data.dir", tempPath);
        if (StringUtils.equals(config.getClazz(), "file")) {
            try {
                String path = getFileSaveDir(config.getPath(), tempPath);
                File file = new File(path);
                if (!file.exists())
                    file.mkdirs();
                file = new File(file, String.format("%s.store", config.getAlias()));
                dataStore = new FileDataStore(file, config.isGzip());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("File not found : " + config.getPath());
            }
        } else if (StringUtils.equals(config.getClazz(), "db")) {
            dataStore = new DatabaseStore(storeProps);
        } else if (StringUtils.equals(config.getClazz(), "batch")) {
            dataStore = new BatchDatabaseStore(storeProps);
        } else if (StringUtils.equals(config.getClazz(), "sql")) {
            String sql = storeProps.get("data.insert.sql");
            if (StringUtils.isEmpty(sql)) {
                throw new RuntimeException("Sql template store has not find property for key 'data.insert.sql'");
            }
            ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);
            if (parsedSql.hasNamedParas() && parsedSql.hasTraditionalParas())
                throw new RuntimeException("不能同时处理named参数和传统的'?'参数");
            //必须是cache，cache check 是 BDB ， 才能处理array的参数，仅仅是sql模板的时候
            //而其他db和batch是可以使用BDB的cache check的，并且还可以使用db主键check
            storeProps.put("data.check.type", "cache");
            dataStore = new SqlTemplateStore(storeProps, parsedSql.getActualSql());
            crawlerConfig.getFormat().setParaNameIndexes(wrapParaNameIndexes(parsedSql));
            crawlerConfig.getFormat().setFormatter("array");
        } else if (StringUtils.isNotEmpty(config.getClazz())) {
            Object object = BeanUtils.newInstance(config.getClazz(), storeProps);
            if (object != null)
                dataStore = BeanUtils.cast(object, DataStore.class);
        }
        if (dataStore == null)
            dataStore = DEFAULT;
        return dataStore;
    }

    static String getFileSaveDir(String dir, String tempDir) {
        if (StringUtils.isEmpty(dir))
            return tempDir;
        else
            return dir;
    }

    public static String[] wrapParaNameIndexes(ParsedSql parsedSql) {
        int parameterIndexes[] = parsedSql.getParaIndexes();
        if (parameterIndexes == null || parameterIndexes.length == 0)
            return null;
        int len = parameterIndexes.length, pos;
        String paraNameIndexes[] = new String[len];
        String paramName;
        for (int i = 0; i < len; i++) {
            pos = parameterIndexes[i];
            if (pos > 0) {
                paramName = parsedSql.getParameter(pos - 1);
                paraNameIndexes[i] = paramName;
            }
        }
        return paraNameIndexes;
    }

}
