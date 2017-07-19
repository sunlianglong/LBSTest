package com.example.sunlianglong.lbstest;
import com.baidu.mapapi.model.LatLng;

public class LocationOfPhoto {

    private LatLng location;

    public LocationOfPhoto(double latitude, double longitude){

        location = new LatLng(latitude, longitude);
    }


    /**
     * @return the location
     */
    public LatLng getLocation() {
        return location;
    }
    /**
     * @param location the location to set
     */
    public void setLocation(LatLng location) {
        this.location = location;
    }
}