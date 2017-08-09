package com.sparrow.common.statistic.handler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: YZC
 * Date: 12-11-28
 * Time: 下午4:39
 * To change this template use File | Settings | File Templates.
 */
public class SiteDictionary {
    static final Map<String, String> dic = new HashMap<String, String>();
    static final Map<String, String> labels = new HashMap<String, String>();

    static {
        dic.put("1", "大众点评");
        dic.put("2", "饭统网");
        dic.put("3", "QQ美食");
        dic.put("4", "百度");
        dic.put("5", "悦乐");
        dic.put("9", "京探网");
        dic.put("10", "胡椒蓓蓓");
        dic.put("12", "号码百事通");
        dic.put("15", "55bbs");
        dic.put("22", "中信银行");
        dic.put("23", "交通银行");
        dic.put("26", "订餐小秘书");
        dic.put("27", "微博美食");
        dic.put("43", "华夏银行");
        dic.put("45", "光大银行");
        dic.put("46", "宁波银行");
        dic.put("48", "兴业银行");
        dic.put("49", "平安银行");
        dic.put("50", "浦发银行");
        dic.put("51", "农业银行");
        dic.put("52", "民生银行");
        dic.put("53", "广发银行");
        dic.put("54", "建设银行");
        dic.put("55", "招商银行");
        dic.put("57", "工商银行");
        dic.put("61", "中国银行");
        dic.put("66", "丁丁优惠");
        dic.put("75", "杭州银行");
        dic.put("81", "钱库网");
        dic.put("96", "维络城");
        dic.put("97", "东莞银行");
        dic.put("98", "邮政储蓄");
        dic.put("109", "爱乐活");
        dic.put("110", "咕嘟妈咪");
        dic.put("112", "12580");
        dic.put("114", "开饭喇");
        dic.put("115", "一点优惠");
        dic.put("116", "上生活");
        dic.put("133", "易淘食");
        dic.put("134", "哗啦啦");
        dic.put("135", "外卖库");
        dic.put("137", "美餐网");
        dic.put("141", "clubzone");
        dic.put("108", "本地搜");
        dic.put("136", "到家美食会");
        dic.put("140", "豆瓣网");
        dic.put("142", "百度地图");
        dic.put("139", "大麦网");
        dic.put("143", "北京ktv");
        dic.put("144", "熊猫打折");

        labels.put("requests", "请求数");
        labels.put("inst", "新增数");
        labels.put("updat", "更新数");
        labels.put("success", "成功数");
        labels.put("failure", "失败数");
        labels.put("total", "总数量");
        labels.put("bytes", "文本总长");
        labels.put("increament", "新增量");
        labels.put("contentUpd", "内容更新量");
        labels.put("baseContentUpd", "基础更新量");
        labels.put("oldContent", "老数据");
        labels.put("updated", "已更新量");
        labels.put("needUpdate", "未更新量");
        labels.put("transFail", "清理失败");
        labels.put("invalid", "有效数量");
        labels.put("metaUpdated", "Meta更新量");
        labels.put("metaTotal", "Meta总数量");
    }

    public static String getDic(String sid) {
        return dic.get(sid);
    }

    public static String getLabel(String sid) {
        return labels.get(sid);
    }

    public static String getDicEx(String sid) {
        String tmp = dic.get(sid);
        int idx = tmp.indexOf(',');
        if (idx != -1)
            tmp = tmp.substring(0, idx);
        return tmp;
    }
}