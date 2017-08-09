package org.apache.lucene.dictionary;

import org.apache.lucene.dictionary.ik.IKDict;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yangtao on 2016/1/28.
 */
public class CategoryDic extends IKDict {
    private static final CategoryDic instance = new CategoryDic();

    public static CategoryDic getInstance() {
        return instance;
    }

    private CategoryDic() {

    }

    public void init() {
        System.out.println("初始化分类词库开始");
        super.init();
        System.out.println("初始化分类词库结束");
    }

    @Override
    public List<String> getLibPath() {
//        Configuration config = new Configuration();
//        config.addResource("dic.xml");
        String path = System.getProperty("dic.category.path");
        return Arrays.asList(path.split(";", -1));
    }
}
