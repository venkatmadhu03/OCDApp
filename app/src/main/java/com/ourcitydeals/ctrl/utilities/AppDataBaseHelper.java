package com.ourcitydeals.ctrl.utilities;

/**
 * Created by HARIKRISHNA on 8/20/2015.
 * at CaretTech
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ourcitydeals.ctrl.model.LoginDetails;

public class AppDataBaseHelper extends SQLiteOpenHelper {
    public static final String dbName ="OurCityDealsDB";
    public static final int dbVersion = 2;

    public static final String loginTable = "loginNames";
    public static final String userID ="userID";
    public static final String emailID = "emailID";
    public static final String user_login = "user_login";
    public static final String user_mobile = "user_mobile";
    public static final String date = "loginDate";

    public AppDataBaseHelper(Context _ctx) {
        super(_ctx, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + loginTable + " (" + userID + " TEXT, " + emailID + " TEXT, "
                + user_login + " TEXT, " + user_mobile + " TEXT, " + date + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + loginTable);
        onCreate(db);
    }

    public void addLoginDetails(LoginDetails _loginData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + loginTable);
        ContentValues cv = new ContentValues();

        cv.put(userID, _loginData.getUserID());
        cv.put(emailID, _loginData.getuserEmail());
        cv.put(user_mobile, _loginData.getUserMobile());
        cv.put(user_login, _loginData.getuserName());
        cv.put(date, _loginData.getLoginDate());

        db.insert(loginTable, emailID, cv);
        db.close();
    }

    public void deleteLoginDetails() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + loginTable);
    }

    public LoginDetails getLoginDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + loginTable, new String[] {});
        LoginDetails loginDetails = null;
        if (cur.moveToFirst()) {
            do {
                loginDetails = new LoginDetails();
                loginDetails.setUserID(cur.getString(cur.getColumnIndex(userID)));
                loginDetails.setuserEmail(cur.getString(cur.getColumnIndex(emailID)));
                loginDetails.setuserName(cur.getString(cur.getColumnIndex(user_login)));
                loginDetails.setUserMobile(cur.getString(cur.getColumnIndex(user_mobile)));
                loginDetails.setLoginDate(cur.getString(cur.getColumnIndex(date)));
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
        if (loginDetails != null) {
            return loginDetails;
        } else {
            return null;
        }
    }
}