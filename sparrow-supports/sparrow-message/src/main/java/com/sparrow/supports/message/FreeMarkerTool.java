package com.sparrow.supports.message;

import java.io.*;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarkerTool {
    private static FreeMarkerTool instance = null;
    private Configuration configuration;

    private FreeMarkerTool() {
        try {
            configuration = new Configuration();
            configuration.setDefaultEncoding("UTF-8");
            configuration.setLocale(Locale.CHINESE);
            configuration.setDirectoryForTemplateLoading(new File(FreeMarkerTool.class.getClassLoader().getResource("ftl").getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static FreeMarkerTool getInstance() {
        if (null == instance)
            instance = new FreeMarkerTool();
        return instance;
    }

    public String writeString(String ftlname, Map<String, Object> datamap) {
        try {
            StringWriter out = new StringWriter();
            Template template = configuration.getTemplate(ftlname + ".ftl");
            template.process(datamap, out);
            return out.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (TemplateException e) {
            return null;
        }
    }

    public void writeString(String ftlname, Map<String, Object> datamap, Writer out) {
        try {
            Template template = configuration.getTemplate(ftlname + ".ftl");
            template.process(datamap, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
        }
    }

    public void writeFile(String ftlname, Map<String, Object> datamap, File file) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            Writer out = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
            Template template = configuration.getTemplate(ftlname + ".ftl");
            template.process(datamap, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String ftlname, Map<String, Object> datamap, String file) {
        writeFile(ftlname, datamap, new File(file));
    }

    public static void main(String[] args) {
        String temp = FreeMarkerTool.getInstance().writeString("useractivemail.ftl", null);
        System.out.println(temp);
    }
}
