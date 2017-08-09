package com.sparrow.netty.freemark;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreeMarker {
	private final Configuration configuration;
	private final String suffix;

	public FreeMarker(String path, String sf) {
		configuration = new Configuration();
		configuration.setDefaultEncoding("UTF-8");
		configuration.setLocale(Locale.CHINESE);
		configuration.setTemplateLoader(new WebTemplateLoader(path));

		suffix = "." + sf;
	}

	public String renderString(String ftlname, Object data) {
		try {
			if (ftlname.indexOf('.') == -1 || !ftlname.endsWith(suffix))
				ftlname = ftlname + suffix;
			StringWriter out = new StringWriter();
			Template template = configuration.getTemplate(ftlname);
			template.process(data, out);
			return out.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (TemplateException e) {
			return null;
		}
	}

	public static void main(String[] args) {
		String temp = new FreeMarker(
				"E:\\workspace\\schedule\\schedule-app\\schedule-server\\src\\main\\java\\com\\dili\\dd\\schedule\\http\\freemark",
				"ftl").renderString("test", null);
		System.out.println(temp);
	}
}
