package com.sparrow.core.config;

import com.sparrow.core.loader.LoadFile;
import com.sparrow.core.log.Logger;
import com.sparrow.core.log.LoggerManager;
import com.sparrow.core.utils.PropertiesFileUtil;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.utils.file.FileUtils;
import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.*;
import java.util.*;

import static java.io.File.separatorChar;

/**
 * 系统配置类
 */
public class SystemConfig {
    private static final Logger log = LoggerManager.getSysLog();
    public static final String fileName = System.getProperty("user.home")
            + "/_system.properties";
    public static final String LINE_SEPARATOR = System.getProperty(
            "line.separator", "\r\n");
    private static final Properties _sysProps;

    public static final String SYS_CLASS_PATH;
    public static final String EXT_CLASS_PATH;
    public static final String SYS_ROOT_PATH;
    public static final String SYS_ENCODING;

    public static final String WEB_ROOT;
    public static final String SOURCE_DIR;
    public static final String TARGET_DIR;

    public static final String HOST_URL;
    public static final String APP_URL;
    public static final String IMG_URL;
    public static final String INDEX_PATH;

    public static final String LOGIN_PATH;
    public static final String LOGIN_PAGE;
    public static final boolean ENABLE_SECURITY;
    public static final boolean IGNORE_GET;

    static {
        boolean useSystemProps = "true".equalsIgnoreCase(System.getProperty("use.system.props"));
        Properties properties;
        if (useSystemProps)
            properties = System.getProperties();
        else {
            properties = PropertiesFileUtil.getProperties(new File(fileName));
            if (properties == null || properties.isEmpty()) {
                properties = PropertiesFileUtil.mergePropertyFiles(new String[]{
                        "classpath:system.properties",
                        "classpath:conf/system.properties",
                        "classpath:eggs/scan.properties",
                        "classpath:conf/default.properties",
                        "classpath:conf/custom.properties"});
                processYml(properties, "classpath:system.yml", "classpath:application.yml", "classpath:config.yml");
            }
        }
        _sysProps = properties;

        WEB_ROOT = _sysProps.getProperty("web.root.path");
        SOURCE_DIR = _sysProps.getProperty("source.generate.path");
        TARGET_DIR = _sysProps.getProperty("target.generate.path");
        HOST_URL = _sysProps.getProperty("web.host");
        APP_URL = _sysProps.getProperty("web.app.prefix", "/rest");
        IMG_URL = _sysProps.getProperty("web.image.prefix", "/img");
        INDEX_PATH = _sysProps.getProperty("index.path");

        LOGIN_PATH = _sysProps.getProperty("web.login.path", "/rest/login");
        LOGIN_PAGE = _sysProps.getProperty("web.login.page", "/app/login.html");

        EXT_CLASS_PATH = _sysProps.getProperty("ext.class.path");
        ENABLE_SECURITY = "true".equals(_sysProps.getProperty("security.enable"));
        IGNORE_GET = "true".equals(_sysProps.getProperty("ignore.get.req"));

        System.out.println(" -------- enable security : " + ENABLE_SECURITY);
        System.out.println(" -------- use system properties : " + useSystemProps);
        System.out.println(" -------- ignore.get.req : " + IGNORE_GET);

        String tmp = System.getProperty("java.class.path");
        if (tmp == null || tmp.length() == 0)
            tmp = ".";
        SYS_CLASS_PATH = tmp;

        tmp = null;
        try {
            tmp = java.net.URLDecoder.decode(LoadFile.class.getResource("")
                    .getFile(), "UTF-8");
            tmp = processPath(tmp);
            tmp = tmp.substring(0, tmp.length() - 16);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SYS_ROOT_PATH = tmp;
        SYS_ENCODING = _sysProps.getProperty("charset.encoding",
                System.getProperty("file.encoding"));
    }

    public static final Properties getProps() {
        return _sysProps;
    }

    public static final String getProps(String key) {
        return _sysProps.getProperty(key);
    }

    public static final String getProps(String key, String defaultValue) {
        return _sysProps.getProperty(key, defaultValue);
    }

    private static String processPath(String webPath) throws IOException {
        webPath = webPath.replace('/', separatorChar);
        webPath = webPath.replace('\\', separatorChar);
        boolean needFirstSeparatorChar = (webPath.charAt(0) == separatorChar);
        StringTokenizer tempTok = new StringTokenizer(webPath, File.separator);
        webPath = "";
        while (tempTok.hasMoreTokens()) {
            webPath += tempTok.nextToken() + separatorChar;
        }
        if (needFirstSeparatorChar) {
            webPath = separatorChar + webPath;
        }
        int idx = webPath.indexOf(".jar!");
        if (idx != -1) {
            webPath = webPath.substring(0, idx);
            idx = webPath.lastIndexOf(separatorChar);
            if (idx != -1)
                webPath = webPath.substring(0, idx);
            if (webPath.startsWith("file:"))
                webPath = webPath.substring(6);
        }
        return new File(webPath).getCanonicalPath();
    }

    public static String getProperty(String key) {
        return _sysProps.getProperty(key, null);
    }

    public static boolean hasProperty(String key) {
        return _sysProps.containsKey(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return _sysProps.getProperty(key, defaultValue);
    }

    public static String getSysProperty(String key) {
        return getSysProperty(key, null);
    }

    public static String getSysProperty(String key, String defaultValue) {
        return System
                .getProperty(key, _sysProps.getProperty(key, defaultValue));
    }

    public static int getInt(String key) {
        String str = getProperty(key);
        return getIntValue(str, 0);
    }

    public static int getInt(String key, int defaultValue) {
        String str = getProperty(key);
        return getIntValue(str, defaultValue);
    }

    public static int getSysInt(String key) {
        return getSysInt(key, 0);
    }

    public static int getSysInt(String key, int defaultValue) {
        String str = getSysProperty(key);
        return getIntValue(str, defaultValue);
    }

    static int getIntValue(String value, int defaultValue) {
        if (StringUtils.isEmpty(value))
            return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static void setProperty(String key, String value) {
        _sysProps.setProperty(key, value);
    }

    public static void store() {
        PropertiesFileUtil.writeProperties(_sysProps, fileName);
    }

    public static Properties processYml(String... files) {
        Properties properties = createStringAdaptingProperties();
        processYml(properties, files);
        return properties;
    }

    static Properties processYml(Properties properties, String... files) {
        Yaml yaml = new Yaml(new StrictMapAppenderConstructor());
        InputStream ins = null;
        int count = 0;
        for (String file : files) {
            try {
                log.debug("Loading from YAML: " + file);
                ins = FileUtils.getInputStream(file);
                Reader reader = new UnicodeReader(ins);
                try {
                    for (Object object : yaml.loadAll(reader)) {
                        if (object != null) {
                            processYml(asMap(object), properties);
                            count++;
                        }
                    }
                    log.debug("Loaded " + count + " document" + (count > 1 ? "s" : "") +
                            " from YAML resource: " + file);
                } finally {
                    reader.close();
                }
            } catch (IOException ex) {
                handleProcessError(file, ex);
            } finally {
                IOUtils.closeQuietly(ins);
            }
        }
        return properties;
    }

    private static Map<String, Object> asMap(Object object) {
        Map<String, Object> result = new LinkedHashMap();
        if (!(object instanceof Map)) {
            // A document can be a text literal
            result.put("document", object);
            return result;
        }

        Map<Object, Object> map = (Map<Object, Object>) object;
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Map) {
                value = asMap(value);
            }
            Object key = entry.getKey();
            if (key instanceof CharSequence) {
                result.put(key.toString(), value);
            } else {
                // It has to be a map key in this case
                result.put("[" + key.toString() + "]", value);
            }
        }
        return result;
    }

    public static Properties createStringAdaptingProperties() {
        return new Properties() {
            @Override
            public String getProperty(String key) {
                Object value = get(key);
                return (value != null ? value.toString() : null);
            }
        };
    }

    private static void buildFlattenedMap(Map<String, Object> result, Map<String, Object> source, String path) {
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            if (StringUtils.hasText(path)) {
                if (key.startsWith("[")) {
                    key = path + key;
                } else {
                    key = path + '.' + key;
                }
            }
            Object value = entry.getValue();
            if (value instanceof String) {
                result.put(key, value);
            } else if (value instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) value;
                buildFlattenedMap(result, map, key);
            } else if (value instanceof Collection) {
                Collection<Object> collection = (Collection<Object>) value;
                int count = 0;
                for (Object object : collection) {
                    buildFlattenedMap(result,
                            Collections.singletonMap("[" + (count++) + "]", object), key);
                }
            } else {
                result.put(key, (value != null ? value : ""));
            }
        }
    }

    protected static final Map<String, Object> getFlattenedMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        buildFlattenedMap(result, source, null);
        return result;
    }

    private static void processYml(Map<String, Object> map, Properties properties) {
        properties.putAll(getFlattenedMap(map));
    }

    private static void handleProcessError(String resource, IOException ex) {
        log.warn("Could not load map from " + resource + ": " + ex.getMessage());
    }
}
