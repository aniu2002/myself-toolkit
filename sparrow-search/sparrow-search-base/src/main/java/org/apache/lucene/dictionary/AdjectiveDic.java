package org.apache.lucene.dictionary;

import org.apache.lucene.dictionary.ik.IKDict;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yangtao on 2016/2/3.
 */
public class AdjectiveDic extends IKDict {
    private static final AdjectiveDic instance = new AdjectiveDic();

    public static AdjectiveDic getInstance() {
        return instance;
    }

    private AdjectiveDic() {

    }

    public void init() {
        System.out.println("初始化形容词词库开始");
        super.init();
        System.out.println("初始化形容词词库结束");
    }

    @Override
    public List<String> getLibPath() {
//        Configuration config = new Configuration();
//        config.addResource("dic.xml");
        String path = System.getProperty("dic.adjective.path");
        return Arrays.asList(path.split(";", -1));
    }
}
