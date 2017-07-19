package com.example.sunlianglong.lbstest;

/**
 * Created by sun liang long on 2017/5/30.
 */

public class Box {
    private String box_id;
    private String box_size;
    private String box_status;
    private String group_id;
    private String position;

    public String getBox_id() {
        return box_id;
    }

    public void setBox_id(String box_id) {
        this.box_id = box_id;
    }

    public String getBox_size() {
        return box_size;
    }

    public void setBox_size(String box_size) {
        this.box_size = box_size;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getBox_status() {

        return box_status;
    }

    public String getLocation() {
        return position;
    }

    public void setLocation(String location) {
        this.position = location;
    }

    public void setBox_status(String box_status) {
        this.box_status = box_status;
    }
    public String toStringid() {
        return box_id;
    }
    public String toStringsize() {
        return box_size;
    }
    public String toStringroup_id() {
        return group_id;
    }
    public String toStringbox_status() {
        return box_status;
    }
    public String toStringposition(){
        return position;
    }
}
