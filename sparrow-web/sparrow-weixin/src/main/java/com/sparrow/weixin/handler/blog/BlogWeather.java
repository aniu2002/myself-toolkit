package com.sparrow.weixin.handler.blog;

/**
 * Created by yuanzc on 2015/6/5.
 */
public class BlogWeather {
    private int id;
    private String city;
    private String county;
    private String date;
    //白天的天气状况
    private String day_condition;
    //白天的风况
    private String day_wind;
    //白天最高气温
    private String day_temperature;
    //晚上的天气状况
    private String night_condition;
    //晚上的风况
    private String night_wind;
    //晚上的最低气温
    private String night_temperature;
    private long update_time;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getDay_condition() {
        return day_condition;
    }

    public void setDay_condition(String day_condition) {
        this.day_condition = day_condition;
    }

    public String getDay_wind() {
        return day_wind;
    }

    public void setDay_wind(String day_wind) {
        this.day_wind = day_wind;
    }

    public String getDay_temperature() {
        return day_temperature;
    }

    public void setDay_temperature(String day_temperature) {
        this.day_temperature = day_temperature;
    }

    public String getNight_condition() {
        return night_condition;
    }

    public void setNight_condition(String night_condition) {
        this.night_condition = night_condition;
    }

    public String getNight_wind() {
        return night_wind;
    }

    public void setNight_wind(String night_wind) {
        this.night_wind = night_wind;
    }

    public String getNight_temperature() {
        return night_temperature;
    }

    public void setNight_temperature(String night_temperature) {
        this.night_temperature = night_temperature;
    }

    public long getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(long update_time) {
        this.update_time = update_time;
    }
}
