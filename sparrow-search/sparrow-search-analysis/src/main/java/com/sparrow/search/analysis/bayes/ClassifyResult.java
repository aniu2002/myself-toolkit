package com.sparrow.search.analysis.bayes;

/**
 * Project Name: icloudunion
 * Package Name: com.sparrow.search.analysis.bayes
 * Author : YZC
 * Date: 2017/1/24
 * Time: 14:58
 */
/**
 * 分类结果
 */
public class ClassifyResult
{
    public double probility;//分类的概率
    public String classification;//分类
    public ClassifyResult()
    {
        this.probility = 0;
        this.classification = null;
    }
}
