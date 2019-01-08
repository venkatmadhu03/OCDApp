package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ourcitydeals.ctrl.ownLibs.ProgressWheel;
import com.ourcitydeals.ctrl.utilities.Utility;

import org.jsoup.Jsoup;

/**
 * Created by HARI on 5/22/2016.
 */
public class SplashScreenActivity extends Activity {
    public static final int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.splash_screen);

        if (Utility.isOnline(SplashScreenActivity.this)) {
            boolean newVersion = false;
            newVersion = web_update();
            if (newVersion == true) {
                showDialog(12227);
            } else {
                ProgressWheel progressWheel_CENTER = (ProgressWheel) findViewById(R.id.progress_wheel1);
                progressWheel_CENTER.setBarColor(getResources().getColor(R.color.white));
                progressWheel_CENTER.setRimColor(getResources().getColor(R.color.transparent));

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                        startActivity(intent);
                        SplashScreenActivity.this.finish();
                    }
                }, SPLASH_TIME_OUT);// delay in milliseconds (1500)
            }
        } else {
            Utility.showCustomToast("Please connect your internet!", SplashScreenActivity.this);
            finish();
        }
    }

    private boolean web_update() {
        try {
            String package_name = getPackageName();
            String curVersion = getApplicationContext().getPackageManager()
                    .getPackageInfo(package_name, 0).versionName;
            String newVersion = curVersion;
            newVersion = Jsoup
                    .connect(
                            "https://play.google.com/store/apps/details?id="
                                    + package_name + "&hl=en")
                    .timeout(30000)
                    .userAgent(
                            "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com").get()
                    .select("div[itemprop=softwareVersion]").first()
                    .ownText();
            return (value(curVersion) < value(newVersion)) ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private long value(String string) {
        string = string.trim();
        if (string.contains(".")) {
            final int index = string.lastIndexOf(".");
            return value(string.substring(0, index)) * 100
                    + value(string.substring(index + 1));
        } else {
            return Long.valueOf(string);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 12227) {
            AlertDialog callMobiledialog = null;
            LayoutInflater liYes = LayoutInflater.from(this);
            View callAddressView = liYes.inflate(R.layout.dialog_layout_update_version, null);
            AlertDialog.Builder adbrok = new AlertDialog.Builder(this);
            adbrok.setView(callAddressView);
            adbrok.setCancelable(false);
            callMobiledialog = adbrok.create();
            callMobiledialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationByHari;
            callMobiledialog.show();
            return callMobiledialog;
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case 12227:
                final AlertDialog alt3 = (AlertDialog) dialog;
                TextView alertTitle = (TextView) alt3.findViewById(R.id.favGINTitleTVID);

                // TextView tv22 = (TextView) alt3.findViewById(R.id.addFavTVID);
                //tv22.setText(getResources().getString(R.string.update_version_message));
                Button addFavYesBtn = (Button) alt3.findViewById(R.id.add_fav_yesBtnID);
                Button addNoFavBtn = (Button) alt3.findViewById(R.id.add_fav_noBtnID);
                addFavYesBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                SplashScreenActivity.this.finish();
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                SplashScreenActivity.this.finish();
                            }
                            SplashScreenActivity.this.finish();
                        } catch (Exception e) {
                            if (e != null) {
                                e.printStackTrace();
                                Log.w("Hari-->", e);
                            }
                        }
                        alt3.dismiss();
                    }
                });
                addNoFavBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alt3.dismiss();
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                                //Intent intent = new Intent(SplashScreenActivity.this, ActivityLoginPage.class);
                                startActivity(intent);
                                SplashScreenActivity.this.finish();
                            }
                        }, SPLASH_TIME_OUT);// delay in milliseconds (1500)
                    }
                });
                break;
        }
        super.onPrepareDialog(id, dialog);
    }
}