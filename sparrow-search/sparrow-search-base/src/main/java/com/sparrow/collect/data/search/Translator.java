package com.sparrow.collect.data.search;

import com.dili.dd.searcher.basesearch.search.config.ConfigManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <B>Description</B>翻译器 <br />
 * <B>Copyright</B> Copyright (c) 2014 www.diligrp.com All rights reserved. <br />
 * 本软件源代码版权归地利集团,未经许可不得任意复制与传播.<br />
 * <B>Company</B> 地利集团
 * 
 * @author zhanglin
 * @createTime 2014年6月20日 下午2:45:01
 */
public class Translator {

	private Map<String, String> transLatorDic = new ConcurrentHashMap<>();

	private static Translator instance = new Translator();

	public static Translator getInstance() {
		return instance;
	}

	public void init() {
			buildTransLatorDic();
	}

	private void buildTransLatorDic() {

		String conf =  ConfigManager.getConfig().get("searcher.basesearch.translate.searchId.list");
		if (null != conf) {

			String[] searchIds = conf.split(",");

			for (String searchId : searchIds) {

				String[] fields = ConfigManager.getConfig().get(String.format("searcher.basesearch.translate.%s.tbField.list", searchId)).split(",");

				if (null != fields) {

					for (String fieldName : fields) {

						String keyStr = searchId + fieldName;
						String value = ConfigManager.getConfig().get(String.format("searcher.basesearch.searchId.%s.tbField.%s.translate", searchId, fieldName));
						transLatorDic.put(keyStr, value);

					}
				}
			}
		}
	}

	public String getFieldName(String searchId, String tbField) {

		return transLatorDic.get(searchId + tbField);

	}

}
