package com.dt.maplib;

import com.baidu.mapapi.model.LatLng;

/**
 * 作者：zzz on 2018/12/20 0020 10:13
 * 邮箱：1038883524@qq.com
 * 功能：
 */

public class LatInfoBean {
    String name;
    String details;
    LatLng latLng;

    public LatInfoBean(String name, String details, LatLng latLng) {
        this.name = name;
        this.details = details;
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
