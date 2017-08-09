package com.sparrow.app.common.source;

import java.util.List;

/**
 * Created by yuanzc on 2015/12/31.
 */
public interface Source {
    /**
     * 分页查询数据记录
     *
     * @param script       脚本
     * @param data         查询条件
     * @param wrappedClass 结果包装
     * @param page         页码,默认是第一页
     * @param limit        每页数量
     * @param <T>          查询结果包装对象
     * @return 返回对象列表
     */
    <T> List<T> query(String script, Object data, Class<T> wrappedClass, int page, int limit);

    /**
     * 分页查询数据记录
     *
     * @param script       脚本
     * @param data         查询条件
     * @param wrappedClass 结果包装
     * @param <T>          查询结果包装对象
     * @return 返回对象列表
     */
    <T> List<T> query(String script, Object data, Class<T> wrappedClass);

    <T> T getData(String script, Object data, Class<T> wrappedClass);

    boolean initialize();

    boolean destroy();
}
