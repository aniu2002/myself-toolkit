package com.sparrow.collect.website.domain.search;

import com.sparrow.collect.website.data.search.UserFactor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: zhanghang
 * Date: 2016/1/13
 * Time: 10:34
 * App搜索基础参数
 */
public class AppBaseFactor implements Serializable{
    private static final long serialVersionUID = -2464381953288065187L;

    private UserFactor userFactor;

    /** 目前主要为app定位信息 */

    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 定位的id,市场id或者城市-区id
     */
    private Long gpsLocationId;
    /**
     * 定位的名称，市场名称或者城市-区名称
     */
    private String gpsLocationName;

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Long getGpsLocationId() {
        return gpsLocationId;
    }

    public void setGpsLocationId(Long gpsLocationId) {
        this.gpsLocationId = gpsLocationId;
    }

    public String getGpsLocationName() {
        return gpsLocationName;
    }

    public void setGpsLocationName(String gpsLocationName) {
        this.gpsLocationName = gpsLocationName;
    }

    public UserFactor getUserFactor() {
        return userFactor;
    }

    public void setUserFactor(UserFactor userFactor) {
        this.userFactor = userFactor;
    }
}
