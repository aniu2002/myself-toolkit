package com.sparrow.collect.website.domain;

/**
 * Created by yangtao on 2015/7/31.
 */
public class City {
    //id
    private Integer id;
    //名称
    private String name;
    //父级id城市
    private Integer parentId;
    //城市级别
    private Integer level;
    //顺序
    private Integer sort;
    //是否是国家
    private Boolean isCountry;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Boolean getIsCountry() {
        return isCountry;
    }

    public void setIsCountry(Boolean isCountry) {
        this.isCountry = isCountry;
    }
}
