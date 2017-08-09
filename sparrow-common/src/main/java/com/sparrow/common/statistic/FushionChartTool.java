package com.sparrow.common.statistic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.sparrow.common.statistic.data.StatisticData;
import com.sparrow.common.statistic.data.StatisticExtData;
import com.sparrow.common.statistic.data.StatisticItemMulti;
import com.sparrow.core.utils.BeanForceUtil;


public class FushionChartTool {

	public static StatisticData createStatisticVo(String caption,
			String xLabel, String yLabel) {
		StatisticData vo = new StatisticData();
		vo.setCaption(caption);
		vo.setXdLabel(xLabel);
		vo.setYdLabel(yLabel);
		return vo;
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

	public static String generateCharts(String title, String xName,
			String yName, Map<String, String> dataSets) {
		StringBuilder sb = new StringBuilder();
		genNormalChartHeader(sb, title, xName, yName);
		if (dataSets == null || dataSets.isEmpty()) {
			generateDefaultData(sb, null);
		} else {
			// (time string) 2 (server runtime) pair
			Iterator<Map.Entry<String, String>> iterator = dataSets.entrySet()
					.iterator();
			Map.Entry<String, String> entry;
			while (iterator.hasNext()) {
				entry = iterator.next();
				String label = entry.getKey();
				String v = entry.getValue();
				generateLabelSet(sb, label, v);
			}
		}
		genTrendLines(sb, 500);
		genNormalChartTail(sb);
		return sb.toString();
	}

	private static void genTrendLines(StringBuilder sb, int value) {
		sb.append("<trendLines><line startValue=\"")
				.append(value)
				.append("\" color=\"009933\" displayvalue=\"警戒线\" /></trendLines>");
	}

	public static String generateCharts(StatisticData vo) {
		return generateCharts(vo.getCaption(), vo.getXdLabel(),
				vo.getYdLabel(), vo.getDataSet());
	}

	public static String generateCharts(StatisticExtData vo) {
		return generateCharts(vo.getCaption(), vo.getXdLabel(),
				vo.getYdLabel(), vo.getCategories(), vo.getDataSet());
	}

	public static String generateCharts(String title, String xName,
			String yName, String[] categories,
			Map<String, StatisticItemMulti> dataSets) {
		StringBuilder sb = new StringBuilder();

		generateChartHeader(sb, title, xName, yName);
		// sb.append("numberPrefix=\"$\"");
		generateCategories(sb, categories);
		if (dataSets == null || dataSets.isEmpty()) {
			generateDefaultData(sb, null);
		} else {
			// (time string) 2 (server runtime) pair
			Iterator<Map.Entry<String, StatisticItemMulti>> iterator = dataSets
					.entrySet().iterator();
			Map.Entry<String, StatisticItemMulti> entry;
			StatisticItemMulti itm;
			while (iterator.hasNext()) {
				entry = iterator.next();
				itm = entry.getValue();
				generateDataSet(sb, itm.getValues(), itm.getLabel());
			}
		}
		generateChartTail(sb);
		return sb.toString();
	}

	private static void generateChartTail(StringBuilder sb) {
		sb.append("<styles><definition><style ")
				.append("name=\"Anim1\" type=\"animation\" ")
				.append("param=\"_xscale\" start=\"0\" duration=\"1\"")
				.append("/></definition>");
		sb.append("<application><apply ")
				.append("toObject=\"DIVLINES\" styles=\"Anim1\"")
				.append("/></application></styles>");
		sb.append("</chart>");
	}

	private static void genNormalChartTail(StringBuilder sb) {
		sb.append("<styles><definition><style ")
				.append("name=\"CanvasAnim\" type=\"animation\" ")
				.append("param=\"_xscale\" start=\"0\" duration=\"1\"")
				.append("/></definition>");
		sb.append("<application><apply ")
				.append("toObject=\"Canvas\" styles=\"CanvasAnim\"")
				.append("/></application></styles>");
		sb.append("</chart>");
	}

	private static void genNormalChartHeader(StringBuilder sb, String title,
			String xName, String yName) {
		// <?xml version="1.0" encoding="UTF-8"?>
		sb.append("<chart caption=\"").append(title).append("\" xAxisName=\"")
				.append(xName).append("\" yAxisName=\"").append(yName)
				.append("\" showValues=\"0\" slantLabels=\"1\" ")
				.append("labelStep=\"1\" labelDisplay=\"Rotate\"")
				.append(" baseFontSize=\"12\">");
	}

	private static void generateChartHeader(StringBuilder sb, String title,
			String xName, String yName) {
		// <?xml version="1.0" encoding="UTF-8"?>
		sb.append("<chart caption=\"").append(title).append("\" xAxisName=\"")
				.append(xName).append("\" yAxisName=\"").append(yName)
				.append("\" showValues=\"0\" slantLabels=\"1\" ")
				.append("labelStep=\"2\" labelDisplay=\"Rotate\"")
				.append(" baseFontSize=\"12\">");
	}

	private static void generateCategories(StringBuilder sb, String[] categories) {
		sb.append("<categories>");
		for (int i = 0; i < categories.length; i++) {
			generateCategory(sb, categories[i]);
		}
		sb.append("</categories>");
	}

	private static void generateDataSet(StringBuilder sb, int[] dataSet,
			String title) {
		if (dataSet == null)
			return;
		sb.append("<dataset seriesName=\"").append(title).append("\">");
		for (int i = 0; i < dataSet.length; i++) {
			generateSet(sb, dataSet[i]);
		}
		sb.append("</dataset>");
	}

	private static void generateSet(StringBuilder sb, int value) {
		sb.append("<set value=\"").append(value).append("\"/>");
	}

	private static void generateLabelSet(StringBuilder sb, String label,
			String value) {
		sb.append("<set label=\"").append(label).append("\" value=\"")
				.append(value).append("\" showValue=\"1\" showName=\"1\"/>");
	}

	private static void generateCategory(StringBuilder sb, String label) {
		sb.append("<category label=\"").append(label).append("\"/>");
	}

	private static void generateDefaultData(StringBuilder sb, String serName) {
		if (StringUtils.isBlank(serName))
			sb.append("<dataset/>");
		else
			sb.append("<dataset seriesName=\"").append(serName).append("\"/>");
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
}
