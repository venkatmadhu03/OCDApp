package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ourcitydeals.ctrl.model.LoginDetails;
import com.ourcitydeals.ctrl.ownLibs.MyTextView;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.AppDataBaseHelper;
import com.ourcitydeals.ctrl.utilities.Utility;

//this is activity inquirepage

@SuppressWarnings("deprecation")
public class ActivityInquiryPage extends Activity {
    EditText fromEmailET, toEmailET, commentET;
    MyTextView sendBtn;
    public ProgressDialog mPDialog;


    LoginDetails loginDetails;
    AppDataBaseHelper dbHelper = new AppDataBaseHelper(this);
    InputMethodManager keyboard;

    String FROM_MAIL = "";
    String toMail = "";
    String productName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.screen_vendor_inquiry);
        Utility.setDimensions(this);
        setupNavigation();
        loginSessionExpiredHere();
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
        titleTV.setText("Ask for price");

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.GONE);

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
        menuBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
    }

    private void loginSessionExpiredHere() {
        if (getIntent() != null) {
            Bundle b = getIntent().getExtras();
            if (b != null) {
                productName = b.getString("PRODUCT_NAME");
                toMail = b.getString("TO_MAIL");
                FROM_MAIL = b.getString("FROM_MAIL");
            }
        }

        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Utility.setDimensions(ActivityInquiryPage.this);

        fromEmailET = (EditText) findViewById(R.id.fromEmailETID);
        fromEmailET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
        fromEmailET.setText(FROM_MAIL);

        toEmailET = (EditText) findViewById(R.id.toEmailETID);
        toEmailET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);
        toEmailET.setText(toMail);

        commentET = (EditText) findViewById(R.id.commentETID);

        sendBtn = (MyTextView) findViewById(R.id.sendBtnID);
        sendBtn.getLayoutParams().height = (int) (Utility.screenWidth / 7.5);

        sendBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String fromMailStr = fromEmailET.getText().toString();
                String toMailStr = toEmailET.getText().toString();
                String commentStr = commentET.getText().toString();

                boolean isNetAvailable = Utility.isOnline(ActivityInquiryPage.this);
                if (isNetAvailable) {
                    if (fromMailStr.trim().length() > 0 && toMailStr.trim().length() > 0) {
                            showPrgressDialogs("Sending Please Wait... ");
                            RequestParams params = new RequestParams();
                            params.put("comment", commentStr);
                            params.put("product_name", productName);
                            params.put("to_mail", toMailStr);
                            params.put("from_mail", fromMailStr);
                            senMailToVendor(AppConstants.VENDOR_INQUIRY_URL, params);
                    } else {
                        Utility.showCustomToast("All fields are mandatory!", ActivityInquiryPage.this);
                    }
                } else {
                    Utility.showCustomToast("Please connect your internet!", ActivityInquiryPage.this);
                }
                keyboard.hideSoftInputFromWindow(fromEmailET.getWindowToken(), 0);
            }
        });
    }

    private void senMailToVendor(String vendorInquiryURL, RequestParams params) {
        // Make RESTful Web Service call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(vendorInquiryURL, params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has HTTP response code // '200'
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    if (response.contains("success")){
                        Utility.showCustomToast("Thank you for sending...", ActivityInquiryPage.this);
                    } else {
                        Utility.showCustomToast("Sorry! failed to send", ActivityInquiryPage.this);
                    }
                    finish();
                }
                if (mPDialog != null) {
                    mPDialog.dismiss();
                }
            }

            @Override
            // When the response returned by REST has HTTP response code other than '200'
            public void onFailure(int statusCode, Throwable error, String content) {
                if (mPDialog != null) {
                    mPDialog.dismiss();
                }
                finish();
                Utility.showCustomToast("Sorry! failed to send", ActivityInquiryPage.this);
            }
        });
    }

    public void showPrgressDialogs(String _loading) {
        mPDialog = new ProgressDialog(ActivityInquiryPage.this);
        mPDialog.setMessage(_loading);
        mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPDialog.setIndeterminate(true);
        mPDialog.setCancelable(false);
        mPDialog.show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressedAnimationByCHK();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onBackPressedAnimationByCHK() {
        finish();
        overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
    }
}