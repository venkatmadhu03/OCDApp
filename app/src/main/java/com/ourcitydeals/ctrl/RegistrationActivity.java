package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.Utility;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by OFFICE on 9/29/2015.
 */
public class RegistrationActivity extends Activity {
    private ImageView logoIV;
    private EditText unameET;
    private EditText eemailET;
    private EditText phoneNumET;
    private EditText passwordsET;
    private EditText reEnterPasswordsET;

    public String nameStr = "";
    public String phoneStr = "";
    public String emailStr = "";
    public String passwordStr = "";
    public String conFirmPasswordStr = "";

    ProgressDialog ringProgressDialog;

   // private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPassword, input_layout_refferal_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.register_activity);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);

        Utility.setDimensions(this);

        logoIV = (ImageView) findViewById(R.id.logo);
        logoIV.getLayoutParams().height = (int) (Utility.screenHeight / 5.2);

        unameET = (EditText) findViewById(R.id.unameETID);
        unameET.getLayoutParams().height = (int) (Utility.screenWidth / 9.0);
        unameET.setHintTextColor(getResources().getColor(R.color.white));

        eemailET = (EditText) findViewById(R.id.eemailETID);
        eemailET.getLayoutParams().height = (int) (Utility.screenWidth / 9.0);
        eemailET.setHintTextColor(getResources().getColor(R.color.white));

        phoneNumET = (EditText) findViewById(R.id.mobileETID);
        phoneNumET.getLayoutParams().height = (int) (Utility.screenWidth / 9.0);
        phoneNumET.setHintTextColor(getResources().getColor(R.color.white));

        passwordsET = (EditText) findViewById(R.id.passwordsETID);
        passwordsET.getLayoutParams().height = (int) (Utility.screenWidth / 9.0);
        passwordsET.setHintTextColor(getResources().getColor(R.color.white));

        reEnterPasswordsET = (EditText) findViewById(R.id.reEnterPasswordsETID);
        reEnterPasswordsET.getLayoutParams().height = (int) (Utility.screenWidth / 9.0);
        reEnterPasswordsET.setHintTextColor(getResources().getColor(R.color.white));

        Button registerBtn = (Button) findViewById(R.id.registerBtnID);
        registerBtn.getLayoutParams().height = (int) (Utility.screenWidth / 7.5);

        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                nameStr = unameET.getText().toString();
                emailStr = eemailET.getText().toString();
                phoneStr = phoneNumET.getText().toString();
                passwordStr = passwordsET.getText().toString();
                conFirmPasswordStr = reEnterPasswordsET.getText().toString();

                boolean isNetAvailable = Utility.isOnline(RegistrationActivity.this);
                if (isNetAvailable) {
                    if (nameStr.trim().length() > 0 && emailStr.trim().length() > 0) {
                        if (Utility.isEmailValid(emailStr)) {
                            if (passwordStr.equalsIgnoreCase(conFirmPasswordStr)) {
                                if (phoneStr.length()==10) {
                                    try {
                                        ringProgressDialog = ProgressDialog.show(RegistrationActivity.this, "Please wait ...", "Creating your account...", true);
                                        ringProgressDialog.setCancelable(true);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    RequestParams params = new RequestParams();
                                    params.put("uname", nameStr);
                                    params.put("email", emailStr);
                                    params.put("mobile", phoneStr);
                                    params.put("password", passwordStr);
                                    sendOtpRequesttoServer(AppConstants.REGISTER_URL, params);
                                } else {
                                    Utility.showCustomToast("Invalid mobile number!", RegistrationActivity.this);
                                }
                            } else {
                                Utility.showCustomToast("Password and confirm password not match!", RegistrationActivity.this);
                            }
                        } else {
                            Utility.showCustomToast("Invalid email!", RegistrationActivity.this);
                        }
                    } else {
                        Utility.showCustomToast("All fields are mandatory!", RegistrationActivity.this);
                    }
                } else {
                    Utility.showCustomToast("Please connect your Internet!", RegistrationActivity.this);
                }
            }
        });

        Button loginBtn = (Button) findViewById(R.id.loginBtnID);
        loginBtn.getLayoutParams().height = (int) (Utility.screenWidth / 7.5);

        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void sendOtpRequesttoServer(String _Url, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(_Url, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    try {
                        /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                        JSONObject jsonResponse = new JSONObject(response);
                        String status = jsonResponse.optString("response");
                        if (status.contains("success")) {
                            finish();
                            Utility.showCustomToast("Thank you for registering!", RegistrationActivity.this);
                        } else {
                            Utility.showCustomToast("Already exits your email!", RegistrationActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utility.showCustomToast("Sorry failed. Please try again!", RegistrationActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utility.showCustomToast("Sorry failed. Please try again!", RegistrationActivity.this);
                    }
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                Log.w("onFailure", "" + content);
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
                if (statusCode == 404) {
                    Utility.showCustomToast("Requested resource not found", RegistrationActivity.this);
                } else if (statusCode == 500) {
                    Utility.showCustomToast("Something went wrong at server end", RegistrationActivity.this);
                } else {
                    Utility.showCustomToast("Unexpected Error occurred! Try again.", RegistrationActivity.this);
                }
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        try {
            if (id == 12077) {
                AlertDialog deleteFavAlert = null;
                LayoutInflater liDelete = LayoutInflater.from(this);
                View deleteFavView = liDelete.inflate(R.layout.alert_exit, null);
                AlertDialog.Builder adbDeleteFav = new AlertDialog.Builder(this);
                adbDeleteFav.setView(deleteFavView);
                deleteFavAlert = adbDeleteFav.create();
                adbDeleteFav.setCancelable(false);
                deleteFavAlert.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationByHari;
                deleteFavAlert.show();
                return deleteFavAlert;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        try {
            if (id == 12077) {
                final AlertDialog alt3 = (AlertDialog) dialog;
                Button deleteFavYesBtn = (Button) alt3.findViewById(R.id.yesBtnID);
                Button deleteNoFavBtn = (Button) alt3.findViewById(R.id.noBtnID);

                deleteFavYesBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        RegistrationActivity.this.finish();
                        alt3.dismiss();
                    }
                });

                deleteNoFavBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alt3.dismiss();
                    }
                });
            }

        } catch (Exception e) {
            if (e != null) {
                Log.w("Hari-->", e);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //showDialog(12077);
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