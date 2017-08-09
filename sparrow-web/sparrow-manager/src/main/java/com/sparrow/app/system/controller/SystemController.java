package com.sparrow.app.system.controller;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import com.sparrow.core.utils.PropertiesFileUtil;
import com.sparrow.core.config.SystemConfig;
import com.sparrow.server.web.OpResult;
import com.sparrow.server.web.annotation.PathVariable;
import com.sparrow.server.web.annotation.ReqMapping;
import com.sparrow.server.web.annotation.RequestBody;
import com.sparrow.server.web.annotation.ResponseBody;
import com.sparrow.server.web.annotation.WebController;

@WebController(value = "/sys")
public class SystemController {
	@ReqMapping(value = "/setting", method = ReqMapping.GET)
	@ResponseBody
	public Properties getSetting(String keywords) {
		return SystemConfig.getProps();
	}

	@ReqMapping(value = "/properties/{name}", method = ReqMapping.GET)
	@ResponseBody
	public Object getProperties(@PathVariable("name") String name) {
		return PropertiesFileUtil.readPropItems("classpath:conf/" + name
				+ ".properties");
	}

	@ReqMapping(value = "/properties/{name}", method = ReqMapping.POST)
	@ResponseBody
	public OpResult saveProperties(@PathVariable("name") String name,
			@RequestBody Map<String, String> map) {
		File f = PropertiesFileUtil.getPropertyFile("classpath:conf/" + name
				+ ".properties");
		System.out.println(f.getAbsolutePath());
		PropertiesFileUtil.writeProperties(map, f);
		return OpResult.OK;
	}
}
