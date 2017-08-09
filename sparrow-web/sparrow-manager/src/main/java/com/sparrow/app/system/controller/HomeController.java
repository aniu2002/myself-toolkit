package com.sparrow.app.system.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.sparrow.server.web.annotation.PathVariable;
import com.sparrow.server.web.annotation.ReqMapping;
import com.sparrow.server.web.annotation.ReqParameter;
import com.sparrow.server.web.annotation.ResponseBody;
import com.sparrow.server.web.annotation.WebController;


@WebController(value = "/")
public class HomeController {
	@ReqMapping(value = "/login", method = ReqMapping.GET)
	public String login(Map<String, String> in) {
		return "redirect:/app/login";
	}

	@ReqMapping(value = "/index", method = ReqMapping.GET)
	public String index(Map<String, String> in) {
		return "redirect:/app/index";
	}

	@ReqMapping(value = "/xx/{user}/{password}", method = ReqMapping.GET)
	public Object getClassName(Map<String, String> in) {
		Iterator<Entry<String, String>> iter = in.entrySet().iterator();
		Entry<String, String> entry;
		while (iter.hasNext()) {
			entry = iter.next();
			System.out.println(" ---- " + entry.getKey() + " = "
					+ entry.getValue());
		}
		return "{name:'dd'}";
	}

	@ReqMapping(value = "/hello/{user}/{password}", method = ReqMapping.GET)
	public @ResponseBody
	Object getXName(@PathVariable("user") String user,
			@PathVariable("password") String password,
			@ReqParameter("test") String testx, String test, int total,
			@ReqParameter("total") int totalx) {
		System.out.println("user:" + user);
		System.out.println("password:" + password);
		System.out.println("testx:" + testx);
		System.out.println("test:" + test);
		System.out.println("total:" + total);
		System.out.println("totalx:" + totalx);
		return "cc{name:'dd'}";
	}
}
