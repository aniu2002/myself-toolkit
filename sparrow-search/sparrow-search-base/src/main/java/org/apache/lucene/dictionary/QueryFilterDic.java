package org.apache.lucene.dictionary;

import org.apache.lucene.dictionary.ik.IKDict;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yangtao on 2016/2/29.
 */
public class QueryFilterDic extends IKDict {
    private static QueryFilterDic instance = new QueryFilterDic();

    private QueryFilterDic() {}

    public static QueryFilterDic getInstance() {
        return instance;
    }

    public void init() {
        System.out.println("初始化市场、城市、分类组合词库开始");
        super.init();
        System.out.println("初始化市场、城市、分类组合词库结束");
    }

    @Override
    public List<String> getLibPath() {
//        Configuration config = new Configuration();
//        config.addResource("dic.xml");
        String path = System.getProperty("dic.queryFilter.path");
        return Arrays.asList(path.split(";", -1));
    }
}
