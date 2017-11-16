package com.szl.icu.miner.tools.template;

import java.io.*;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerUtils {
    private static FreeMarkerUtils instance = null;
    private Configuration configuration;

    private FreeMarkerUtils() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocale(Locale.CHINESE);
        // ftlsx
        configuration.setClassForTemplateLoading(FreeMarkerUtils.class, "fm");
        // configuration.setDirectoryForTemplateLoading(new
        // File(FreeMarkerUtils.class.getClassLoader().getResource("ftl").getPath()));
    }

    public synchronized static FreeMarkerUtils getInstance() {
        if (null == instance)
            instance = new FreeMarkerUtils();
        return instance;
    }

    public String writeString(String ftl, Object data) {
        StringWriter out = new StringWriter();
        writeString(ftl, data, out);
        return out.toString();

    }

    public void writeString(String ftl, Object data, Writer out) {
        try {
            Template template = configuration.getTemplate(ftl + ".ftl");
            template.process(data, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String ftl, Object object, File file) {
        try {
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            Writer out = new OutputStreamWriter(new FileOutputStream(file),
                    "utf-8");
            Template template = configuration.getTemplate(ftl + ".ftl");
            template.process(object, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String ftl, Map<String, Object> datamap,
                          String file) {
        writeFile(ftl, datamap, new File(file));
    }
}
