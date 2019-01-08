package com.ourcitydeals.ctrl.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ourcitydeals.ctrl.R;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by HARIKRISHNA on 8/21/2015.
 * at CaretTech
 */
public class Utility {
    public static int screenHeight;
    public static int screenWidth;
    public static NetworkInfo netInfo;
    public static final String FETCH_DIRECTION_UP = "up";
    public static String CURRENT_SUB_CAT_TITLE = "";
    public static String KEY_PHONE_NUM = "PHONE_NUM";
    public static String KEY_NAME = "NAME";
    public static String KEY_OTP = "OTP";
    public static String KEY_EMAIL_ID = "EMAIL_ID";
    public static String KEY_REFERENCE = "REFERENCE";
    public static String KEY_COMING_FROM = "COMING_FROM";

    //public static Typeface custom_font;

    public static String DASHBOARD_ICON_ID = "DASHBOARD_ICON_ID";

    public static ArrayList<String> galleriesList = new ArrayList<String>();

    public static boolean isOnline(Context _Context) {
        ConnectivityManager cm = (ConnectivityManager) _Context.getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public static void setDimensions(Context _context) {
        try {
            Display display = ((Activity) _context).getWindowManager().getDefaultDisplay();
            int screenWidth = display.getWidth();
            int screenHeight = display.getHeight();
            Utility.screenWidth = screenWidth;
            Utility.screenHeight = screenHeight;

            ///custom_font = Typeface.createFromAsset(_context.getAssets(), "bebas_neue.ttf");
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
                Log.w("HARI-->DEBUG", e);
            }
        }
    }

    /**
     * Obtains the LayoutInflater from the given context.
     */
    public static LayoutInflater from_Context(Context context) {
        LayoutInflater layoutInflater = null;
        try {
            if (context != null) {
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }

            if (layoutInflater == null) {
                throw new AssertionError("LayoutInflater not found.");
            }
        } catch (Exception e) {
            if (e != null) {
                Log.w("HARI-->DEBUG", e);
            }
            layoutInflater = null;
        }
        return layoutInflater;
    }

    public static Intent hariEmailIntent(Context context, String address, String subject, String body, String cc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        intent.putExtra(Intent.EXTRA_TEXT, body);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.setType("message/rfc822");
        return intent;
    }

    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static float distFrom(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (float) (dist);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static void showCustomToast(String toastmsg, Activity _activity) {
        try {
            LayoutInflater inflater = Utility.from_Context(_activity);
            View layout = null;
            if (inflater != null) {
                layout = inflater.inflate(R.layout.toast_no_netowrk, (ViewGroup) _activity.findViewById(R.id.custom_toast_layout_id));
                TextView tv = (TextView) layout.findViewById(R.id.text);
                // The actual toast generated here.
                Toast toast = new Toast(_activity);
                tv.setText(toastmsg);
                toast.setDuration(Toast.LENGTH_SHORT);
                //tv.setTypeface(Utility.font_reg);
                if (layout != null) {
                    toast.setView(layout);
                    toast.show();
                }
            } else {
                Toast.makeText(_activity, "" + toastmsg, Toast.LENGTH_SHORT).show();
            }
        } catch (AssertionError e) {
            if (e != null) {
                Log.w("HARI-->DEBUG", e);
                Toast.makeText(_activity, "" + toastmsg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            if (e != null) {
                Log.w("HARI-->DEBUG", e);
                Toast.makeText(_activity, "" + toastmsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}