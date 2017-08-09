package com.sparrow.common.statistic.handler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.sparrow.common.statistic.data.ParaItem;
import com.sparrow.common.statistic.data.StatisticAdapter;
import com.sparrow.common.statistic.data.StatisticData;
import com.sparrow.common.statistic.data.StatisticItemMulti;
import com.sparrow.common.statistic.data.StatisticParaVo;
import com.sparrow.core.utils.BeanForceUtil;
import com.sparrow.core.utils.StringUtils;
import com.sparrow.core.utils.date.TimeUtils;


public class StatisticHandler {

	public StatisticData getStatisticNormal(String ids, String dates,
			String dataType) {
		//StatisticParaVo vr = getStatisticParaVoDay(dates);

		List<?> ls = null;
		// List<?> ls = this.crawlSiteFieldMapper.selectByExample(ex);
		StatisticData vo = new StatisticData();
		vo.setCaption(SiteDictionary.getDic(ids) + "--字段更新统计");
		if ("flag".equals(dataType))
			vo.setXdLabel("标志更新");
		else if ("null".equals(dataType))
			vo.setXdLabel("字段更新为空");
		else
			vo.setXdLabel("字段更新");
		vo.setYdLabel("出现次数");
		// vo.setCategories(TimeUtils.getBeforeDates(Calendar.DAY_OF_MONTH,
		// 10));
		vo.setDataSet(wrapColumns(ls, "dataName", "dataValue"));
		return vo;
	}

	public static Map<String, String> wrapColumnsByField(List<?> list,
			String keyField, String valField) {
		if (list == null || list.isEmpty())
			return null;
		Map<String, String> map = new HashMap<String, String>();
		for (Object obj : list) {
			String key = getValue(obj, keyField);
			if ("siteId".equals(keyField))
				key = SiteDictionary.getDic(key);
			String val = getValue(obj, valField);
			if (key == null || val == null)
				continue;
			map.put(key, val);
		}
		return map;
	}

	public static Map<String, String> wrapColumns(List<?> list,
			String keyField, String valField) {
		if (list == null || list.isEmpty())
			return null;
		Map<String, String> map = new HashMap<String, String>();
		for (Object obj : list) {
			String key = getValue(obj, keyField);
			String val = getValue(obj, valField);
			if (key == null || val == null)
				continue;
			map.put(key, val);
		}
		return map;
	}

	public static List<String> generateIdsQueryStr(String taskId) {
		if (StringUtils.isEmpty(taskId))
			return null;
		boolean flag = taskId.indexOf(',') == -1;
		List<String> list = new ArrayList<String>();
		if (flag) {
			list.add(taskId);
			return list;
		}
		String ids[] = taskId.split(",");
		for (int i = 0; i < ids.length; i++)
			list.add(ids[i]);
		return list;
	}

	public static List<Long> generateIdsQuery(String taskId) {
		if (StringUtils.isEmpty(taskId))
			return null;
		boolean flag = taskId.indexOf(',') == -1;
		List<Long> list = new ArrayList<Long>();
		if (flag) {
			list.add(Long.valueOf(taskId));
			return list;
		}
		String ids[] = taskId.split(",");
		for (int i = 0; i < ids.length; i++)
			list.add(Long.valueOf(ids[i]));
		return list;
	}

	public static ParaItem[] genParaItemArray(String taskId) {
		if (StringUtils.isEmpty(taskId))
			return null;
		String ids[] = taskId.split(",");
		ParaItem[] items = new ParaItem[ids.length];
		for (int i = 0; i < ids.length; i++)
			items[i] = new ParaItem(ids[i], SiteDictionary.getDic(ids[i]));
		return items;
	}

	public static ParaItem[] genFieldParaItemArray(String taskId) {
		if (StringUtils.isEmpty(taskId))
			return null;
		String ids[] = taskId.split(",");
		ParaItem[] items = new ParaItem[ids.length];
		for (int i = 0; i < ids.length; i++)
			items[i] = new ParaItem(ids[i], SiteDictionary.getLabel(ids[i]));
		return items;
	}

	public static String[] generateArray(String taskId, String sp) {
		if (StringUtils.isEmpty(taskId))
			return null;
		String ids[] = taskId.split(sp);
		return ids;
	}

	static Object getPropertyVal(Object obj, String field) {
		try {
			Object result = BeanForceUtil.forceGetProperty(obj, field);
			return result;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String args[]) {
		getStatisticParaVo("2012-07");
	}

	static String getValue(Object obj, String field) {
		try {
			Object result = BeanForceUtil.forceGetProperty(obj, field);
			if (result != null)
				return result.toString();
			else
				return null;
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Map<String, StatisticItemMulti> wrapRecordHistory(
			List<?> list, String valField, StatisticAdapter adapter) {
		String[] ls = generateArray(valField, ",");
		Iterator<?> iterator = list.iterator();
		Object obj;
		String key = null, val = null;
		while (iterator.hasNext()) {
			obj = iterator.next();
			for (int i = 0; i < ls.length; i++) {
				key = ls[i];
				String idxKey = getDataSetIdxKey(getPropertyVal(obj,
						adapter.getDataField()));
				if (idxKey == null)
					continue;
				val = getValue(obj, key);
				if (val == null)
					continue;
				adapter.setValue(key, idxKey, Integer.parseInt(val));
			}
		}
		return adapter.getItemMapping();
	}

	public static Map<String, String> wrapSingleRecord(Object object,
			String keyField) {
		String[] ls = generateArray(keyField, ",");
		if (ls == null)
			return null;
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < ls.length; i++) {
			String key = ls[i];
			String val = getValue(object, key);
			if (val == null)
				continue;
			map.put(SiteDictionary.getLabel(key), val);
		}
		return map;
	}

	static String getDataSetIdxKey(Object obj) {
		if (null == obj)
			return null;
		if (obj instanceof DateTime) {
			DateTime time = (DateTime) obj;
			return time.getMonthOfYear() + "-" + time.getDayOfMonth();
		} else
			return obj.toString();
	}

	public static Map<String, StatisticItemMulti> wrapRecords(List<?> list,
			String keyField, String valField, StatisticAdapter adapter) {
		//Map<String, List<String>> map = new HashMap<String, List<String>>();
		Iterator<?> iterator = list.iterator();
		Object obj;
		while (iterator.hasNext()) {
			obj = iterator.next();
			String idxKey = getDataSetIdxKey(getPropertyVal(obj,
					adapter.getDataField()));
			if (idxKey == null)
				continue;
			String key = getValue(obj, keyField);
			String val = getValue(obj, valField);
			adapter.setValue(key, idxKey, Integer.parseInt(val));
		}
		return adapter.getItemMapping();
	}

	static StatisticParaVo getStatisticParaVo(String dates) {
		Calendar calendar;
		boolean isCurrenMonth = false;
		if (StringUtils.isEmpty(dates)) {
			calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			isCurrenMonth = true;
		} else {
			isCurrenMonth = dates.startsWith(TimeUtils
					.getCurrentTime("yyyy-MM"));
			calendar = TimeUtils.string2Calendar(dates, "yyyy-MM");
		}
		int month = calendar.get(Calendar.MONTH) + 1;
		StatisticParaVo v = new StatisticParaVo();
		v.setStart(new DateTime(calendar.getTimeInMillis()));
		calendar.add(Calendar.MONTH, 1);
		v.setEnd(new DateTime(calendar.getTimeInMillis()));

		int days = calendar.getActualMaximum(Calendar.DATE);
		if (isCurrenMonth)
			v.setMaxSetLen(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		else
			v.setMaxSetLen(days);
		String[] cat = new String[days];
		for (int i = 0; i < days; i++) {
			int n = i + 1;
			cat[i] = month + "-" + n;
		}
		v.setCategories(cat);
		return v;
	}

	static StatisticParaVo getStatisticParaVoDay(String dates) {
		Calendar calendar;
		if (StringUtils.isEmpty(dates)) {
			calendar = Calendar.getInstance();
		} else
			calendar = TimeUtils.string2Calendar(dates, "yyyy-MM-dd");
		StatisticParaVo v = new StatisticParaVo();

		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);

		v.setStart(new DateTime(calendar.getTimeInMillis()));

		calendar.add(Calendar.DAY_OF_MONTH, 2);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		v.setEnd(new DateTime(calendar.getTimeInMillis()));
		return v;
	}
}
