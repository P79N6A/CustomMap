package com.dt.custommap;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * 作者：zzz on 2018/12/19 0019 09:53
 * 邮箱：1038883524@qq.com
 * 功能：
 */

public class Myapp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
