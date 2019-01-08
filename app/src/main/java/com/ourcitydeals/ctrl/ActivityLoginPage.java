package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ourcitydeals.ctrl.model.LoginDetails;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.AppDataBaseHelper;
import com.ourcitydeals.ctrl.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@SuppressWarnings("deprecation")
public class ActivityLoginPage extends Activity {
    EditText user, passwordET;
    Button lgn, singup;
    public ProgressDialog mPDialog;

    AppDataBaseHelper dbHelper = new AppDataBaseHelper(this);

    InputMethodManager keyboard;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.login_activity);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        loginSessionExpiredHere();

       /* try {
            loginDetails = dbHelper.getLoginDetails();
        } catch (Exception e) {
            if (e != null) {
                Log.w("Hari-->", e);
            }
        }
        Date loginDate = null;
        Date currentDate = null;
        // Date Format: MMM DD, YYYY hh:mm:ss ex: Mar 11, 2013 10:03:08 PM
        if (loginDetails != null) {
            //try {
            formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            try {
                loginDate = formatter.parse(loginDetails.getLoginDate());
            } catch (Exception e) {
                if (e != null) {
                    Log.w("Hari-->", e);
                }
            }

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Calendar cal = Calendar.getInstance();
            String currentDateString = dateFormat.format(cal.getTime());
            try {
                currentDate = formatter.parse(currentDateString);
            } catch (ParseException e) {
                if (e != null) {
                    Log.w("Hari-->", e);
                }
            }

            long diff = currentDate.getTime() - loginDate.getTime();
            long days = diff / (1000 * 60 * 60 * 24);
            if (days < 15) {
                if (Utility.isOnline(this)) {
                    Intent accountIntent = new Intent(ActivityLoginPage.this, ShippingAddressScreenActivity.class);
                    startActivity(accountIntent);
                    ActivityLoginPage.this.finish();
                } else {
                    Snackbar.make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG).show();
                }
            } else {
                loginSessionExpiredHere();
            }
        } else {
            loginSessionExpiredHere();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
    }

    private void loginSessionExpiredHere() {
        keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Utility.setDimensions(ActivityLoginPage.this);

        user = (EditText) findViewById(R.id.eemailETID);
        user.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);

        passwordET = (EditText) findViewById(R.id.passwordsETID);
        passwordET.getLayoutParams().height = (int) (Utility.screenWidth / 7.3);

        lgn = (Button) findViewById(R.id.loginBtnID);
        lgn.getLayoutParams().height = (int) (Utility.screenWidth / 7.5);
        //lgn.getLayoutParams().width = (int) (Utility.screenWidth / 2.8);

        singup = (Button) findViewById(R.id.registerBtnID);
        singup.getLayoutParams().height = (int) (Utility.screenWidth / 7.5);
        //singup.getLayoutParams().width = (int) (Utility.screenWidth / 2.8);

        TextView forgetPasswordBtn = (TextView) findViewById(R.id.forgetPasswordBtnID);
        forgetPasswordBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //startActivity(new Intent(ActivityLoginPage.this, ForgotPageActivity.class));
            }
        });

        lgn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String userID = user.getText().toString();
                String passwordStr = passwordET.getText().toString();

                boolean isNetAvailable = Utility.isOnline(ActivityLoginPage.this);
                if (isNetAvailable) {
                    if (userID.trim().length() > 0 && passwordStr.trim().length() > 0) {
                        if (isValidEmail(userID)) {
                            String udid = Settings.Secure.getString(ActivityLoginPage.this.getContentResolver(), Settings.Secure.ANDROID_ID);
                            showPrgressDialogs("Verifying Please Wait... ");
                            RequestParams params = new RequestParams();
                            params.put("email", userID);
                            params.put("password", passwordStr);
                            params.put("deviceid", "" + udid);
                            registrationSendRequest(AppConstants.LOGIN_URL, params, userID, passwordStr);
                        } else {
                            Snackbar.make(coordinatorLayout, "Invalid email id!", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(coordinatorLayout, "All fields are mandatory!", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_LONG).show();
                }
                keyboard.hideSoftInputFromWindow(user.getWindowToken(), 0);
            }
        });

        singup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ActivityLoginPage.this, RegistrationActivity.class));
                //ActivityLoginPage.this.finish();
            }
        });
    }

    public void showPrgressDialogs(String _loading) {
        mPDialog = new ProgressDialog(ActivityLoginPage.this);
        mPDialog.setMessage(_loading);
        mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mPDialog.setIndeterminate(true);
        mPDialog.setCancelable(false);
        mPDialog.show();
    }

    public final static boolean isValidEmail(CharSequence target) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    protected void registrationSendRequest(String registrationUrl, RequestParams params, final String userID, final String passwordStr) {
        // Make RESTful Web Service call using AsyncHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(registrationUrl, params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has HTTP response code // '200'
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    try {
                        /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        JSONArray jsonArray = new JSONArray(response);
                        if (jsonArray.length()>0) {
                            JSONObject jsonResponse = jsonArray.getJSONObject(0);
                            String status = jsonResponse.optString("response");
                            if (status != null && status.equalsIgnoreCase("no user found")) {
                                Utility.showCustomToast("Email and password incorrect!", ActivityLoginPage.this);
                            } else {
                                String user_id = jsonResponse.optString("ID");
                                String emailStr = jsonResponse.optString("user_email");
                                String username = jsonResponse.optString("user_login");
                                String mobileStr = jsonResponse.optString("mobile");

                                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Calendar cal = Calendar.getInstance();

                                dbHelper.addLoginDetails(new LoginDetails(user_id, username, mobileStr, emailStr, dateFormat.format(cal.getTime())));
                                /*String str = MySharedPreference.getPreferences(ActivityLoginPage.this, "FROM_SCREEN_USER");
                                if (str != null && str.equalsIgnoreCase("MY_WISHLIST")){
                                    //Wish list here
                                } else {
                                    startActivity(new Intent(ActivityLoginPage.this, ShippingAddressScreenActivity.class));
                                }
                                finish();*/
                                onBackPressedAnimationByCHK();
                            }
                        } else {
                            Utility.showCustomToast("User does'nt exit!", ActivityLoginPage.this);
                        }
                    } catch (JSONException e) {
                        Utility.showCustomToast("Sorry failed. Please try again!", ActivityLoginPage.this);
                        e.printStackTrace();
                    } catch (Exception e) {
                        Utility.showCustomToast("Sorry failed. Please try again!", ActivityLoginPage.this);
                        e.printStackTrace();
                    }
                } else {
                    Utility.showCustomToast("Sorry failed. Please try again!", ActivityLoginPage.this);
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
                // When HTTP response code is '404'
                if (statusCode == 404) {
                    Snackbar.make(coordinatorLayout, "Requested resource not found!", Snackbar.LENGTH_LONG).show();
                }
                // When HTTP response code is '500'
                else if (statusCode == 500) {
                    Snackbar.make(coordinatorLayout, "Something went wrong at server end!", Snackbar.LENGTH_LONG).show();
                }
                //When HTTP response code other than 404, 500
                else {
                    Snackbar.make(coordinatorLayout, "Unexpected Error occurred! Please Try Again", Snackbar.LENGTH_LONG).show();
                }
            }
        });
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
        if (dbHelper.getLoginDetails() != null) {
            Intent intent = new Intent();
            intent.putExtra("USER_EMAIL", dbHelper.getLoginDetails().getuserEmail());
            setResult(RESULT_OK, intent);
            finish();//finishing activity
        } else {
            Intent intent = new Intent();
            intent.putExtra("USER_EMAIL", "Not_Login");
            setResult(RESULT_OK, intent);
            finish();//finishing activity
        }
        overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
    }
}