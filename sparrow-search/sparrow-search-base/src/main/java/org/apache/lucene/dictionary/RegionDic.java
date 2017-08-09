package org.apache.lucene.dictionary;

import org.apache.lucene.dictionary.ik.IKDict;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yangtao on 2016/2/4.
 */
public class RegionDic extends IKDict {

    private static final RegionDic instance = new RegionDic();

    public static RegionDic getInstance() {
        return instance;
    }

    private RegionDic() {

    }

    public void init() {
        System.out.println("初始化地区词库开始");
        super.init();
        System.out.println("初始化地区词库结束");
    }

    @Override
    public List<String> getLibPath() {
//        Configuration config = new Configuration();
//        config.addResource("dic.xml");
        String path = System.getProperty("dic.region.path");
        return Arrays.asList(path.split(";", -1));
    }
}
