package com.sparrow.tools.cmd.freemark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class XmdFreeMarker {
    private static XmdFreeMarker instance = null;
    private Configuration configuration;

    private XmdFreeMarker() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLocale(Locale.CHINESE);
        configuration.setClassForTemplateLoading(XmdTemplates.class, "ftls");
        // configuration.setDirectoryForTemplateLoading(new
        // File(FreeMarkerUtils.class.getClassLoader().getResource("ftl").getPath()));
    }

    public synchronized static XmdFreeMarker getInstance() {
        if (null == instance)
            instance = new XmdFreeMarker();
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

    public void writeString(String ftlname, Map<String, Object> datamap,
                            Writer out) {
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
            Writer out = new OutputStreamWriter(new FileOutputStream(file),
                    "utf-8");
            Template template = configuration.getTemplate(ftlname + ".ftl");
            template.process(datamap, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(String ftlname, Map<String, Object> datamap,
                          String file) {
        writeFile(ftlname, datamap, new File(file));
    }

    public void write(String ftl, Object object, String file) {
        write(ftl, object, new File(file));
    }

    public void write(String ftl, Object obj, File file) {
        try {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            Writer out = new OutputStreamWriter(new FileOutputStream(file),
                    "utf-8");
            Template template = configuration.getTemplate(ftl + ".ftl");
            template.process(obj, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String temp = XmdFreeMarker.getInstance().writeString(
                "useractivemail.ftl", null);
        System.out.println(temp);
    }
}
