package com.sparrow.collect.orm.mapping;

import com.sparrow.collect.utils.BeanUtils;
import com.sparrow.collect.utils.FileIOUtil;

import org.apache.commons.digester.Digester;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapConfig {
    static final Logger logger = LoggerFactory.getLogger(MapConfig.class);
    private final Map<Class<?>, MapItem> CACHE = new ConcurrentHashMap<Class<?>, MapItem>();

    public MapConfig() {
    }

    public MapConfig(String mapXml) {
        logger.info(" #### sql map : {} ", mapXml);
        if (StringUtils.isNotEmpty(mapXml)) {
            configure(FileIOUtil.getFileInputStream(mapXml), this);
        }
    }

    MapConfig configure(String xmlFile) {
        return configure(FileIOUtil.getFileInputStream(xmlFile));
    }

    MapConfig configure(InputStream ins) {
        return configure(ins, null);
    }

    MapConfig configure(InputStream ins, MapConfig mapConfig) {
        Digester cd = new Digester();
        cd.setNamespaceAware(false);
        cd.setValidating(false);
        cd.setUseContextClassLoader(true);
        cd.addRuleSet(new MapConfigRuleSet());
        if (mapConfig == null)
            mapConfig = new MapConfig();
        cd.push(mapConfig);
        try {
            mapConfig = (MapConfig) cd.parse(ins);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return mapConfig;
    }

    public final String getInsertSql(Class<?> clazz) {
        MapItem item = CACHE.get(clazz);
        if (item != null)
            return item.insert;
        return null;
    }

    public final String getSelectSql(Class<?> clazz) {
        MapItem item = CACHE.get(clazz);
        if (item != null)
            return item.select;
        return null;
    }

    public final String getDeleteSql(Class<?> clazz) {
        MapItem item = CACHE.get(clazz);
        if (item != null)
            return item.delete;
        return null;
    }

    public final String getUpdateSql(Class<?> clazz) {
        MapItem item = CACHE.get(clazz);
        if (item != null)
            return item.update;
        return null;
    }

    public final String getTable(Class<?> clazz) {
        MapItem item = CACHE.get(clazz);
        if (item != null)
            return item.table;
        return null;
    }

    public final String getQuerySql(Class<?> clazz) {
        MapItem item = CACHE.get(clazz);
        if (item != null)
            return item.query;
        return null;
    }

    public final void addMap(Class<?> clazz, MapItem item) {
        CACHE.put(clazz, item);
    }

    public void addMap(String clazzName, MapItem item) {
        try {
            CACHE.put(Class.forName(clazzName), item);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addMapItem(MapRow mapRow) {
        try {
            MapItem item = new MapItem();
            BeanUtils.copy(mapRow, item);
            CACHE.put(Class.forName(mapRow.getClazz()), item);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}