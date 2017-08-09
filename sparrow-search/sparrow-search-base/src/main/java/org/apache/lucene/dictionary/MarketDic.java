package org.apache.lucene.dictionary;

import org.apache.lucene.dictionary.ik.IKDict;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yangtao on 2016/2/4.
 */
public class MarketDic extends IKDict {

    private static final MarketDic instance = new MarketDic();

    public static MarketDic getInstance() {
        return instance;
    }

    private MarketDic() {

    }

    public void init() {
        System.out.println("初始化市场词库开始");
        super.init();
        System.out.println("初始化市场词库结束");
    }

    @Override
    public List<String> getLibPath() {
//        Configuration config = new Configuration();
//        config.addResource("dic.xml");
        String path = System.getProperty("dic.market.path");
        return Arrays.asList(path.split(";", -1));
    }
}
