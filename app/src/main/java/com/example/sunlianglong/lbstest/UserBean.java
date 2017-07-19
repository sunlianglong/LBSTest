package com.example.sunlianglong.lbstest;

/**
 * Created by sun liang long on 2017/5/30.
 */

public class UserBean {
    private String lat;
    private String lng;
    public String getLat(){
        return lat;
    }
    public String getLng(){
        return lng;
    }
    public void setLat(String lat){
        this.lat = lat;
    }
    public void setLng(String lng){
        this.lng = lng;
    }
    public String toStringlat(){
        return lat;
    }
    public String toStringlng(){
        return lng;
    }
}
