package com.sparrow.netty.view;

public class FreemarkerView implements View {
	private String template;
	private Object paras;

	public FreemarkerView(String template, Object paras) {
		this.template = template;
		this.paras = paras;
	}

	@Override
	public String getMimeType() {
		return "text/html";
	}

	@Override
	public String getView() {
		return this.template;
	}

	public Object getPara() {
		return this.paras;
	}
}
