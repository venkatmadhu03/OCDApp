package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ourcitydeals.ctrl.model.LoginDetails;
import com.ourcitydeals.ctrl.ownLibs.MyTextView;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.AppDataBaseHelper;
import com.ourcitydeals.ctrl.utilities.MySharedPreference;
import com.ourcitydeals.ctrl.utilities.Utility;

/**
 * Created by Jashwikha Chakrapu on 5/29/2016.
 */
public class ShippingAddressScreenActivity extends Activity {
    EditText firstNameET;
    EditText lastNameET;
    EditText emailET;
    EditText mobileET;
    EditText addressLineOneET, addressLineTwoET;
    EditText pinCodeCheckET;
    EditText cityET, countryET, stateET;

    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(ShippingAddressScreenActivity.this);
    MyTextView continueOrderTV;
    ProgressDialog ringProgressDialog;
    LoginDetails loginDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.screen_shipping_address);
        Utility.setDimensions(this);
        setupNavigation();

        if (getIntent()!= null && getIntent().getBooleanExtra("EXIT", false)) {
            Intent bookingDoneIntent = new Intent(ShippingAddressScreenActivity.this, ProductDetailsScreenActivity.class);
            bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            bookingDoneIntent.putExtra("EXIT", true);
            startActivity(bookingDoneIntent);
            onBackPressedAnimationByCHK();
        } else {
            loginDetails = dataBaseHelper.getLoginDetails();

            firstNameET = (EditText)findViewById(R.id.firstnameETID);
            lastNameET = (EditText)findViewById(R.id.lastNameETID);
            emailET = (EditText)findViewById(R.id.emailETID);
            mobileET = (EditText)findViewById(R.id.mobileETID);
            addressLineOneET = (EditText)findViewById(R.id.addressLine1ETID);
            addressLineTwoET = (EditText)findViewById(R.id.addressLine2ETID);
            pinCodeCheckET = (EditText)findViewById(R.id.pinCodeCheckETID);
            cityET = (EditText)findViewById(R.id.cityETID);
            stateET = (EditText)findViewById(R.id.stateETID);
            countryET = (EditText)findViewById(R.id.countryETID);

            if (loginDetails!=null){
                firstNameET.setText(""+loginDetails.getuserName());
                emailET.setText(""+loginDetails.getuserEmail());
                mobileET.setText(""+loginDetails.getUserMobile());
            }

            continueOrderTV = (MyTextView) findViewById(R.id.continueOrderTVID);

            lastNameET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "LAST_NAME"));
            //mobileET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "MOBILE"));
            addressLineOneET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "DELIVERY_ADDRESS_LINE1"));
            addressLineTwoET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "DELIVERY_ADDRESS_LINE2"));
            pinCodeCheckET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "PIN_CODE"));
            cityET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "CITY_NAME"));
            countryET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "COUNTRY"));
            stateET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "STATE"));

        /*boolean isNetAvailable = Utility.isOnline(ShippingAddressScreenActivity.this);
        if (isNetAvailable) {
            try {
                ringProgressDialog = ProgressDialog.show(ShippingAddressScreenActivity.this, "Please wait ...", "Updating details...", true);
                ringProgressDialog.setCancelable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            RequestParams params = new RequestParams();
            if (loginDetails!=null){
                params.put("user_id", loginDetails.getUserID());
            }
            retrieveProfile(AppConstants.RETRIEVE_PROFILE_URL, params);
        } else {
            Utility.showCustomToast("Please connect your internet!", ShippingAddressScreenActivity.this);
        }*/

            String startStr = MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "FROM_SCREEN_USER");
            if (startStr != null && startStr.equalsIgnoreCase("MY_ACCOUNT")){
                continueOrderTV.setVisibility(View.VISIBLE);
                continueOrderTV.setText("Update Profile");
            } else {
                continueOrderTV.setVisibility(View.VISIBLE);
            }

            continueOrderTV.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String firstNameETStr = firstNameET.getText().toString();
                    String lastNameETStr = lastNameET.getText().toString();
                    String emailETStr = emailET.getText().toString();
                    String mobileETStr = mobileET.getText().toString();
                    String addressLineOneETStr = addressLineOneET.getText().toString();
                    String addressLineTwoETStr = addressLineTwoET.getText().toString();
                    String pinCodeCheckETStr = pinCodeCheckET.getText().toString();
                    String cityETStr = cityET.getText().toString();
                    String countryETStr = countryET.getText().toString();
                    String stateETStr = stateET.getText().toString();

                    boolean isValidation = false;
                    if (firstNameETStr!=null && firstNameETStr.equals("")) {
                        Toast.makeText(ShippingAddressScreenActivity.this, "Please enter your first name", Toast.LENGTH_LONG).show();
                        isValidation = true;
                        return;
                    }

                    if (lastNameETStr!=null && lastNameETStr.equals("")) {
                        Toast.makeText(ShippingAddressScreenActivity.this, "Please enter your last name", Toast.LENGTH_LONG).show();
                        isValidation = true;
                        return;
                    }

                    if (emailETStr!=null && emailETStr.equals("")) {
                        Toast.makeText(ShippingAddressScreenActivity.this, "Please enter your email", Toast.LENGTH_LONG).show();
                        isValidation = true;
                        return;
                    }

                    if (mobileETStr!=null && mobileETStr.equals("")) {
                        Toast.makeText(ShippingAddressScreenActivity.this, "Please enter your mobile", Toast.LENGTH_LONG).show();
                        isValidation = true;
                        return;
                    }

                    if (addressLineOneETStr!=null && addressLineOneETStr.equals("")) {
                        Toast.makeText(ShippingAddressScreenActivity.this, "Please enter delivery address line 1", Toast.LENGTH_LONG).show();
                        isValidation = true;
                        return;
                    }

                    if (addressLineTwoETStr!=null && addressLineTwoETStr.equals("")) {
                        Toast.makeText(ShippingAddressScreenActivity.this, "Please enter delivery address line 2", Toast.LENGTH_LONG).show();
                        isValidation = true;
                        return;
                    }

                    if (pinCodeCheckETStr!=null && pinCodeCheckETStr.equals("")) {
                        Toast.makeText(ShippingAddressScreenActivity.this, "Please enter your postal code", Toast.LENGTH_LONG).show();
                        isValidation = true;
                        return;
                    }

                    if (cityETStr!=null && cityETStr.equals("")) {
                        Toast.makeText(ShippingAddressScreenActivity.this, "Please enter your city", Toast.LENGTH_LONG).show();
                        isValidation = true;
                        return;
                    }

                    if (stateETStr!=null && stateETStr.equals("")) {
                        Toast.makeText(ShippingAddressScreenActivity.this, "Please enter your state", Toast.LENGTH_LONG).show();
                        isValidation = true;
                        return;
                    }

                    if (countryETStr!=null && countryETStr.equals("")) {
                        Toast.makeText(ShippingAddressScreenActivity.this, "Please enter your country", Toast.LENGTH_LONG).show();
                        isValidation = true;
                        return;
                    }

                    MySharedPreference.setPreference(ShippingAddressScreenActivity.this, "EMAIL_ID", "" + emailETStr);
                    MySharedPreference.setPreference(ShippingAddressScreenActivity.this, "FIRST_NAME", "" + firstNameETStr);
                    MySharedPreference.setPreference(ShippingAddressScreenActivity.this, "LAST_NAME", "" + lastNameETStr);
                    MySharedPreference.setPreference(ShippingAddressScreenActivity.this, "MOBILE", "" + mobileETStr);
                    MySharedPreference.setPreference(ShippingAddressScreenActivity.this, "DELIVERY_ADDRESS_LINE1", "" + addressLineOneETStr);
                    MySharedPreference.setPreference(ShippingAddressScreenActivity.this, "DELIVERY_ADDRESS_LINE2", "" + addressLineTwoETStr);
                    MySharedPreference.setPreference(ShippingAddressScreenActivity.this, "ZIP_CODE", "" + pinCodeCheckETStr);
                    MySharedPreference.setPreference(ShippingAddressScreenActivity.this, "CITY_NAME", "" + cityETStr);
                    MySharedPreference.setPreference(ShippingAddressScreenActivity.this, "STATE", "" + stateETStr);
                    MySharedPreference.setPreference(ShippingAddressScreenActivity.this, "COUNTRY", "" + countryETStr);

                    boolean isNetAvailable = Utility.isOnline(ShippingAddressScreenActivity.this);
                    if (isNetAvailable) {
                        try {
                            ringProgressDialog = ProgressDialog.show(ShippingAddressScreenActivity.this, "Please wait ...", "Updating details...", true);
                            ringProgressDialog.setCancelable(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        RequestParams params = new RequestParams();
                        if (loginDetails!=null){
                            params.put("user_id", loginDetails.getUserID());
                        }
                        params.put("uname", firstNameETStr);
                        params.put("email", emailETStr);
                        params.put("mobile", mobileETStr);
                        params.put("last_name", lastNameETStr);
                        params.put("address_line1", addressLineOneETStr);
                        params.put("address_line2", addressLineTwoETStr);
                        params.put("pin_code", pinCodeCheckETStr);
                        params.put("city", cityETStr);
                        params.put("state", stateETStr);
                        params.put("country", countryETStr);
                        updateProfileHere(AppConstants.UPDATE_PROFILE_URL, params);
                    } else {
                        Utility.showCustomToast("Please connect your internet!", ShippingAddressScreenActivity.this);
                    }

                /*if (continueOrderTV.getText().toString().equalsIgnoreCase("Update Profile")) {
                    Utility.showCustomToast("Updated successful", ShippingAddressScreenActivity.this);
                } else {
                    startActivity(new Intent(ShippingAddressScreenActivity.this, CreateOrderActivity.class));
                }
                finish();*/
                }
            });
        }
    }

    private void retrieveProfile(String retrieveProfileUrl, RequestParams params) {
        // Make RESTful Web Service call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(retrieveProfileUrl, params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has HTTP response code // '200'
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    if (response.contains("success")){
                        /*lastNameET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "LAST_NAME"));
                        mobileET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "MOBILE"));
                        addressLineOneET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "DELIVERY_ADDRESS_LINE1"));
                        addressLineTwoET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "DELIVERY_ADDRESS_LINE2"));
                        pinCodeCheckET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "PIN_CODE"));
                        cityET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "CITY_NAME"));
                        countryET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "COUNTRY"));
                        stateET.setText(MySharedPreference.getPreferences(ShippingAddressScreenActivity.this, "STATE"));*/
                    }
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }

            @Override
            // When the response returned by REST has HTTP response code other than '200'
            public void onFailure(int statusCode, Throwable error, String content) {
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }
        });
    }

    private void updateProfileHere(String updateProfileDataUrl, RequestParams params) {
        // Make RESTful Web Service call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(updateProfileDataUrl, params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has HTTP response code // '200'
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    if (response.contains("success")){
                       if (continueOrderTV.getText().toString().equalsIgnoreCase("Update Profile")) {
                            Utility.showCustomToast("Updated successful", ShippingAddressScreenActivity.this);
                        } else {
                            startActivity(new Intent(ShippingAddressScreenActivity.this, CreateOrderActivity.class));
                        }
                    }
                }
                finish();
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }

            @Override
            // When the response returned by REST has HTTP response code other than '200'
            public void onFailure(int statusCode, Throwable error, String content) {
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
    }

    public void setupNavigation() {
        RelativeLayout headerImage = (RelativeLayout) findViewById(R.id.headerRLID);
        headerImage.getLayoutParams().height = (int) (Utility.screenHeight / 10.2);

        RelativeLayout backAllRL = (RelativeLayout) findViewById(R.id.backallRLID);
        backAllRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressedAnimationByCHK();
            }
        });

        TextView titleTV = (TextView) findViewById(R.id.titleTVID);
        // titleTV.setTypeface(Utility.font_bold);
        titleTV.setText("My Account");

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.GONE);
        //subTitleTV.setText("Sub Categories");

        Button backBtn = (Button) findViewById(R.id.backBtnID);
        backBtn.getLayoutParams().width = (int) (Utility.screenHeight / 28.0);
        backBtn.getLayoutParams().height = (int) (Utility.screenHeight / 28.0);
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onBackPressedAnimationByCHK();
            }
        });

        Button menuBtn = (Button) findViewById(R.id.moreBtnID);
        menuBtn.getLayoutParams().width = (int) (Utility.screenHeight / 24.0);
        menuBtn.getLayoutParams().height = (int) (Utility.screenHeight / 24.0);
        menuBtn.setVisibility(View.GONE);
        menuBtn.setBackgroundResource(R.drawable.ic_shopping_cart_white_24dp);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressedAnimationByCHK();
    }

    private void onBackPressedAnimationByCHK() {
        finish();
        overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
    }
}