package com.sparrow.tools.utils;

import java.io.*;
import java.util.Locale;
import java.util.Map;

import com.sparrow.tools.template.Templates;

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
		configuration.setClassForTemplateLoading(Templates.class, "ftlx");
		// configuration.setDirectoryForTemplateLoading(new
		// File(FreeMarkerUtils.class.getClassLoader().getResource("ftl").getPath()));
	}

	public synchronized static FreeMarkerUtils getInstance() {
		if (null == instance)
			instance = new FreeMarkerUtils();
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

	public void writeFile(String ftlname, Object object, File file) {
		try {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			Writer out = new OutputStreamWriter(new FileOutputStream(file),
					"utf-8");
			Template template = configuration.getTemplate(ftlname + ".ftl");
			template.process(object, out);
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

	public static void main(String[] args) {
		String temp = FreeMarkerUtils.getInstance().writeString(
				"useractivemail.ftl", null);
		System.out.println(temp);
	}
}
