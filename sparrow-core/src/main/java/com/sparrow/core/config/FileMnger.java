package com.sparrow.core.config;

import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.utils.file.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FileMnger {
    public static final String DEFAULT_DIR = ".cfg";
    public static final String PROVIDER_DIR = ".prd";
    public static final String JSON_SUFFIX = ".json";
    public static final String STORE_DIR = SystemConfig.getProperty("config.store.dir", System.getProperty("user.home"));

    /**
     * 根据文件名将json数据写入文件
     *
     * @param fileName
     * @param text
     * @throws java.io.IOException
     */
    public static void writeStringToFile(String fileName, String text) {
        if (StringUtils.isEmpty(text))
            return;
        File file = new File(STORE_DIR, DEFAULT_DIR);
        if (!file.exists())
            file.mkdir();
        file = new File(file, fileName + JSON_SUFFIX);
        FileUtils.writeFile(file, text, FileUtils.DEFAULT_ENCODING);
    }

    public static void writeText(String module, String fileName, String text) {
        if (StringUtils.isEmpty(text))
            return;
        File file = new File(STORE_DIR, DEFAULT_DIR + File.separatorChar
                + module);
        if (!file.exists())
            file.mkdir();
        file = new File(file, fileName + JSON_SUFFIX);
        FileUtils.writeFile(file, text, FileUtils.DEFAULT_ENCODING);
    }

    public static void writeProviderText(String module, String fileName, String text) {
        if (StringUtils.isEmpty(text))
            return;
        File file = new File(STORE_DIR, PROVIDER_DIR + File.separatorChar
                + module);
        if (!file.exists())
            file.mkdir();
        file = new File(file, fileName + JSON_SUFFIX);
        FileUtils.writeFile(file, text, FileUtils.DEFAULT_ENCODING);
    }

    public static void clearModule(String module) {
        clearModule(module, null);
    }

    public static void clearModule(String module, String fileName) {
        File file = new File(STORE_DIR, DEFAULT_DIR + File.separatorChar
                + module);
        if (!StringUtils.isEmpty(fileName))
            file = new File(file, fileName + JSON_SUFFIX);
        FileUtils.clearFile(file);
    }

    public static void clearProviderModule(String module, String fileName) {
        File file = new File(STORE_DIR, PROVIDER_DIR + File.separatorChar
                + module);
        if (!StringUtils.isEmpty(fileName))
            file = new File(file, fileName + JSON_SUFFIX);
        FileUtils.clearFile(file);
    }

    public static void writeMap(String fileName, Map<String, String> map) {
        if (map == null || map.isEmpty())
            return;
        File file = new File(STORE_DIR, DEFAULT_DIR);
        if (!file.exists())
            file.mkdir();
        file = new File(file, fileName);
        BufferedWriter writer = null;
        try {
            writer = FileUtils.getBufferedWriter(file);
            Iterator<Map.Entry<String, String>> iterator = map.entrySet()
                    .iterator();
            Map.Entry<String, String> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                writer.write(entry.getKey());
                writer.write(FileUtils.EQUAL);
                writer.write(entry.getValue());
                writer.write(FileUtils.LINE_SEPARATOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeProviderMap(String fileName, Map<String, String> map) {
        if (map == null || map.isEmpty())
            return;
        File file = new File(STORE_DIR, PROVIDER_DIR);
        if (!file.exists())
            file.mkdir();
        file = new File(file, fileName);
        BufferedWriter writer = null;
        try {
            writer = FileUtils.getBufferedWriter(file);
            Iterator<Map.Entry<String, String>> iterator = map.entrySet()
                    .iterator();
            Map.Entry<String, String> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                writer.write(entry.getKey());
                writer.write(FileUtils.EQUAL);
                writer.write(entry.getValue());
                writer.write(FileUtils.LINE_SEPARATOR);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readStringFromFile(String fileName) {
        File file = new File(STORE_DIR, DEFAULT_DIR);
        File f = new File(file, fileName + JSON_SUFFIX);
        if (!f.exists() || f.length() == 0)
            return null;
        return FileUtils.readFileString(f);
    }

    public static String readText(String module, String fileName) {
        File file = new File(STORE_DIR, DEFAULT_DIR + File.separatorChar
                + module);
        File f = new File(file, fileName + JSON_SUFFIX);
        if (!f.exists() || f.length() == 0)
            return null;
        return FileUtils.readFileString(f);
    }

    public static String readProviderText(String module, String fileName) {
        File file = new File(STORE_DIR, PROVIDER_DIR + File.separatorChar
                + module);
        File f = new File(file, fileName + JSON_SUFFIX);
        if (!f.exists() || f.length() == 0)
            return null;
        return FileUtils.readFileString(f);
    }

    public static Map<String, String> readProviderMap(String fileName) {
        File file = new File(STORE_DIR, PROVIDER_DIR);
        File f = new File(file, fileName);
        if (!f.exists() || f.length() == 0)
            return null;
        BufferedReader reader = FileUtils.getBufferedReader(f);
        if (reader != null) {
            String line;
            try {
                int idx;
                Map<String, String> map = new HashMap<String, String>();
                while ((line = reader.readLine()) != null) {
                    idx = line.indexOf(FileUtils.EQUAL_CHAR);
                    if (idx != -1)
                        map.put(line.substring(0, idx), line.substring(idx + 1));
                    else
                        map.put(line, line);
                }
                return map;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static Map<String, String> readMap(String fileName) {
        File file = new File(STORE_DIR, DEFAULT_DIR);
        File f = new File(file, fileName);
        if (!f.exists() || f.length() == 0)
            return null;
        BufferedReader reader = FileUtils.getBufferedReader(f);
        if (reader != null) {
            String line;
            try {
                int idx;
                Map<String, String> map = new HashMap<String, String>();
                while ((line = reader.readLine()) != null) {
                    idx = line.indexOf(FileUtils.EQUAL_CHAR);
                    if (idx != -1)
                        map.put(line.substring(0, idx), line.substring(idx + 1));
                    else
                        map.put(line, line);
                }
                return map;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
