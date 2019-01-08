package com.ourcitydeals.ctrl;

import android.app.Application;

import com.ourcitydeals.ctrl.utilities.TypefaceUtil;

/**
 * Created by OFFICE on 5/27/2016.
 */
public class MyOCDApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "Roboto_Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf
    }
}
