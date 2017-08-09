package org.apache.lucene.dictionary;

import org.apache.lucene.dictionary.ik.IKDict;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yangtao on 2016/2/16.
 */
public class AdjectiveFilterWordsDic extends IKDict {
    private static final AdjectiveFilterWordsDic instance = new AdjectiveFilterWordsDic();

    public static AdjectiveFilterWordsDic getInstance() {
        return instance;
    }

    private AdjectiveFilterWordsDic() {

    }

    public void init() {
        System.out.println("初始化包含特殊形容词词库开始");
        super.init();
        System.out.println("初始化包含特殊形容词词库结束");
    }

    @Override
    public List<String> getLibPath() {
//        Configuration config = new Configuration();
//        config.addResource("dic.xml");
        String path = System.getProperty("dic.adjective.filterWords.path");
        return Arrays.asList(path.split(";", -1));
    }
}
