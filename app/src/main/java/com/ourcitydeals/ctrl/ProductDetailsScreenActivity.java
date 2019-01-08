package com.ourcitydeals.ctrl;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ourcitydeals.ctrl.adapters.HomeProductsAdapter;
import com.ourcitydeals.ctrl.model.HomeProductsData;
import com.ourcitydeals.ctrl.model.LoginDetails;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.AppDataBaseHelper;
import com.ourcitydeals.ctrl.utilities.MySharedPreference;
import com.ourcitydeals.ctrl.utilities.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ProductDetailsScreenActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener {
    //Related Products
    ProgressDialog ringProgressDialog;
    LinearLayout relatedProductsLL;
    RecyclerView relatedProductsRV;
    ArrayList<HomeProductsData> relatedProductsList = new ArrayList<HomeProductsData>();

    String PRODUCT_ID = "";

    //All Members
    LinearLayout productInfoLL; // aboutSellerInfoLL
    TextView vendorNameTV, productNameTV, priceTV, regularPriceTV, stackAvailabilityTV, galleryCountTV;
    Button pinCheckingBtn;
    EditText pinCodeCheckET;
    ImageView mDemoSlider, wishListIV;
    TextView buyNowTV;
    LinearLayout addToCartLL, wishListAddLL;

    String vendor_id = "";
    String product_id = "";
    String product_name = "";
    String price = "";
    static String vendor_name = "";
    String vendor_email = "";

    EditText quantitiesET;

    AppDataBaseHelper dbHelper = new AppDataBaseHelper(ProductDetailsScreenActivity.this);
    LoginDetails loginDetails;
    LinearLayout shareLL;
    Spinner sizesListSP, colorsListSP;
    LinearLayout sizesLL, colorsLL, buyNowLL;

    InputMethodManager keyboard;

    ArrayList<String> sizesLists = new ArrayList<String>();
    ArrayList<String> colorsList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.screen_product_details);

        if (getIntent() != null) {
            if (getIntent().getBooleanExtra("EXIT", false)) {
                String fromScreenStacks = MySharedPreference.getPreferences(ProductDetailsScreenActivity.this, "PRODUCT_FROM_SCREEN");
                Intent bookingDoneIntent = null;
                if (fromScreenStacks != null && fromScreenStacks.equalsIgnoreCase("HOME_MAIN")) {
                    bookingDoneIntent = new Intent(ProductDetailsScreenActivity.this, HomeActivity.class);
                } else {
                    bookingDoneIntent = new Intent(ProductDetailsScreenActivity.this, ProductsListActivity.class);
                    /*else if (fromScreenStacks != null && fromScreenStacks.equalsIgnoreCase("GRID_PRODUCTS")) {
                    bookingDoneIntent = new Intent(ProductDetailsScreenActivity.this, ProductsListActivity.class);
                }*/
                }
                if (bookingDoneIntent != null) {
                    bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    bookingDoneIntent.putExtra("EXIT", true);
                    startActivity(bookingDoneIntent);
                }
                onBackPressedAnimationByCHK();
            } else {

                vendor_name = "";
                sizesLists = new ArrayList<String>();
                colorsList = new ArrayList<String>();

                StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                        .permitDiskWrites()
                        .detectAll()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()
                        .penaltyLog()
                        .permitAll()
                        .penaltyDeath()
                        .build());
                StrictMode.setThreadPolicy(old);

                Utility.setDimensions(this);
                setupNavigation();
                keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mDemoSlider = (ImageView) findViewById(R.id.slider);
                mDemoSlider.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (Utility.galleriesList!=null && Utility.galleriesList.size()>0) {
                            startActivity(new Intent(ProductDetailsScreenActivity.this, FullScreenViewActivity.class));
                        }
                    }
                });

                productNameTV = (TextView) findViewById(R.id.productNameTVID);

                priceTV = (TextView) findViewById(R.id.priceTVID);

                regularPriceTV = (TextView) findViewById(R.id.regularPriceTVID);

                pinCodeCheckET = (EditText) findViewById(R.id.pinCodeCheckETID);
                pinCheckingBtn = (Button) findViewById(R.id.pinCheckingBtnID);

                String pinCode = MySharedPreference.getPreferences(ProductDetailsScreenActivity.this, "PIN_CODE");
                if (pinCode!= null && pinCode.trim().length()>2) {
                    pinCodeCheckET.setText(""+pinCode);
                    pinCheckingBtn.setText("CHANGE");
                }

                pinCheckingBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (pinCheckingBtn.getText().toString().equalsIgnoreCase("CHANGE")) {
                            pinCodeCheckET.setText("");
                            pinCheckingBtn.setText("CHECK");
                            MySharedPreference.setPreference(ProductDetailsScreenActivity.this, "PIN_CODE", "");
                        } else {
                            String pinCodeStr = pinCodeCheckET.getText().toString();
                            if (pinCodeStr.trim().length()>2){
                                boolean isNetAvailable = Utility.isOnline(ProductDetailsScreenActivity.this);
                                if (isNetAvailable) {
                                    try {
                                        ringProgressDialog = ProgressDialog.show(ProductDetailsScreenActivity.this, "Please wait ...", "Checking your pin code...", true);
                                        ringProgressDialog.setCancelable(false);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    RequestParams params = new RequestParams();
                                    params.put("pincode", pinCodeStr);
                                    checkingPinCode(AppConstants.PIN_CODE_CHECKING_URL, params, pinCodeStr);
                                } else {
                                    Utility.showCustomToast("Please connect your internet!", ProductDetailsScreenActivity.this);
                                }
                            } else {
                                Utility.showCustomToast("Please enter pin code!", ProductDetailsScreenActivity.this);
                            }
                            keyboard.hideSoftInputFromWindow(pinCodeCheckET.getWindowToken(), 0);
                        }
                    }
                });

                buyNowTV = (TextView) findViewById(R.id.buyNowTVID);

                buyNowLL = (LinearLayout)findViewById(R.id.buyNowLLID);
                sizesLL = (LinearLayout)findViewById(R.id.sizesLLID);
                colorsLL = (LinearLayout)findViewById(R.id.colorsLLID);

                sizesListSP = (Spinner) findViewById(R.id.sizesListSPID);
                colorsListSP = (Spinner) findViewById(R.id.colorsListSPID);

                vendorNameTV = (TextView) findViewById(R.id.vendorNameTVID);
                vendorNameTV.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent sportIntent = new Intent(ProductDetailsScreenActivity.this, VendorProductsListActivity.class);
                        sportIntent.putExtra("VENDOR_NAME", vendor_name);
                        startActivity(sportIntent);
                    }
                });

                regularPriceTV.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (regularPriceTV.getText().toString().equalsIgnoreCase("ASK FOR PRICE/COLLECT AT STORE")){
                            loginDetails = dbHelper.getLoginDetails();
                            if (loginDetails != null && loginDetails.getuserEmail() != null) {
                                Intent sportIntent = new Intent(ProductDetailsScreenActivity.this, ActivityInquiryPage.class);
                                sportIntent.putExtra("PRODUCT_NAME", product_name);
                                sportIntent.putExtra("TO_MAIL", vendor_email);
                                sportIntent.putExtra("FROM_MAIL", loginDetails.getuserEmail());
                                startActivity(sportIntent);
                            } else {
                                Intent resultss = new Intent(ProductDetailsScreenActivity.this, ActivityLoginPage.class);
                                startActivityForResult(resultss, 1111);
                            }

                        }
                    }
                });

                stackAvailabilityTV = (TextView) findViewById(R.id.stackAvailabilityTVID);

                galleryCountTV = (TextView) findViewById(R.id.galleryCountTVID);
                galleryCountTV.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (Utility.galleriesList!=null && Utility.galleriesList.size()>0) {
                            startActivity(new Intent(ProductDetailsScreenActivity.this, FullScreenViewActivity.class));
                        }
                    }
                });

                productInfoLL = (LinearLayout) findViewById(R.id.productInfoLLID);

                quantitiesET = (EditText)findViewById(R.id.quantitiesETID);

                addToCartLL = (LinearLayout) findViewById(R.id.addToCartLLID);
                addToCartLL.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (stackAvailabilityTV.getText().toString().equalsIgnoreCase("In stock")) {
                            String quantitiesETStr = quantitiesET.getText().toString();
                            if (!quantitiesETStr.isEmpty() && quantitiesETStr.trim().length()>0) {
                                if (sizesLists.size()>0){
                                    String pickedSize = sizesListSP.getSelectedItem().toString();
                                    if (pickedSize!=null && !pickedSize.equalsIgnoreCase("Select")) {
                                        loginDetails= dbHelper.getLoginDetails();
                                        addToCartData(loginDetails, pickedSize);
                                    } else {
                                        Utility.showCustomToast("Please select size!", ProductDetailsScreenActivity.this);
                                    }
                                } else {
                                    loginDetails= dbHelper.getLoginDetails();
                                    addToCartData(loginDetails, "");
                                }
                            } else {
                                Utility.showCustomToast("Please enter quantity!", ProductDetailsScreenActivity.this);
                            }
                        } else {
                            Utility.showCustomToast("Out of stock!", ProductDetailsScreenActivity.this);
                        }
                    }
                });

                wishListIV = (ImageView) findViewById(R.id.wishListIVID);
                wishListAddLL = (LinearLayout) findViewById(R.id.wishListAddLLID);
                wishListAddLL.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        loginDetails= dbHelper.getLoginDetails();
                        addToWishListData(loginDetails);
                    }
                });

                shareLL = (LinearLayout) findViewById(R.id.shareLLID);

                shareLL.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        try {
                            String shareBody = "I recommend to View '" + product_name
                                    + "' in OurCityDeals APP. \nI Hope you get at best price in comparison with the market. \n "
                                    + "Download Android App:\n"+"https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();

                            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                            sharingIntent.setType("text/plain");
                            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "OurCityDeals (Open it in Google Play Store to Download the Application)");

                            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                            startActivity(Intent.createChooser(sharingIntent, "Share via"));
                        } catch (ArrayIndexOutOfBoundsException ee) {
                            ee.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                relatedProductsLL = (LinearLayout) findViewById(R.id.relatedProductsLLID);
                relatedProductsLL.setVisibility(View.GONE);
                relatedProductsRV = (RecyclerView) findViewById(R.id.relatedProductsRVID);
                relatedProductsRV.setHasFixedSize(true);
                LinearLayoutManager dealslm = new LinearLayoutManager(ProductDetailsScreenActivity.this);
                dealslm.setOrientation(LinearLayoutManager.HORIZONTAL);
                relatedProductsRV.setLayoutManager(dealslm);

                Bundle b = getIntent().getExtras();
                if (b != null) {
                    String PRODUCT_FROM_SCREEN = b.getString("PRODUCT_FROM_SCREEN");
                    MySharedPreference.setPreference(ProductDetailsScreenActivity.this, "PRODUCT_FROM_SCREEN", ""+PRODUCT_FROM_SCREEN);
                    PRODUCT_ID = b.getString("PRODUCT_ID");
                    if (Utility.isOnline(ProductDetailsScreenActivity.this)) {
                        try {
                            ringProgressDialog = ProgressDialog.show(ProductDetailsScreenActivity.this, "Please wait ...", "Loading data...", true);
                            ringProgressDialog.setCancelable(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        fullDetailsSingleProductData(AppConstants.PRODUCT_FULL_DETAILS_URL + "" + PRODUCT_ID);
                        realtedProductsLoading(AppConstants.RELATED_PRODUCTS_URL + "" + PRODUCT_ID);
                    } else {
                        Utility.showCustomToast("Please connect your internet!", ProductDetailsScreenActivity.this);
                        finish();
                    }
                }

                buyNowTV.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (stackAvailabilityTV.getText().toString().equalsIgnoreCase("In stock")) {
                            String pinCode = MySharedPreference.getPreferences(ProductDetailsScreenActivity.this, "PIN_CODE");
                            if (pinCode!= null && pinCode.trim().length()>2) {
                                String quantitiesETStr = quantitiesET.getText().toString();
                                if (quantitiesETStr != null && !quantitiesETStr.isEmpty() && quantitiesETStr.trim().length()>0) {
                                    if (sizesLists.size()>0){
                                        String pickedSize = sizesListSP.getSelectedItem().toString();
                                        if (pickedSize!=null && !pickedSize.equalsIgnoreCase("Select")) {
                                            loginDetails= dbHelper.getLoginDetails();
                                            addToCartData(loginDetails, pickedSize);
                                            MySharedPreference.setPreference(ProductDetailsScreenActivity.this, "FROM_SCREEN_USER", "CART");
                                            if (loginDetails != null) {
                                                startActivity(new Intent(ProductDetailsScreenActivity.this, ShippingAddressScreenActivity.class));
                                            } else {
                                                Intent resultss = new Intent(ProductDetailsScreenActivity.this, ActivityLoginPage.class);
                                                startActivityForResult(resultss, 1112);
                                            }
                                        } else {
                                            Utility.showCustomToast("Please select size!", ProductDetailsScreenActivity.this);
                                        }
                                    } else {
                                        loginDetails= dbHelper.getLoginDetails();
                                        addToCartData(loginDetails, "");
                                        MySharedPreference.setPreference(ProductDetailsScreenActivity.this, "FROM_SCREEN_USER", "CART");
                                        if (loginDetails != null) {
                                            startActivity(new Intent(ProductDetailsScreenActivity.this, ShippingAddressScreenActivity.class));
                                        } else {
                                            Intent resultss = new Intent(ProductDetailsScreenActivity.this, ActivityLoginPage.class);
                                            startActivityForResult(resultss, 1112);
                                        }
                                    }
                                } else {
                                    Utility.showCustomToast("Please enter quantity!", ProductDetailsScreenActivity.this);
                                }
                            } else {
                                Utility.showCustomToast("Please check pin code availability.", ProductDetailsScreenActivity.this);
                            }
                        } else {
                            Utility.showCustomToast("Out of stock!", ProductDetailsScreenActivity.this);
                        }
                    }
                });
            }
        }
    }

    private void checkingPinCode(String pinCodeCheckUrl, RequestParams params, final String pinCodeStr) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(pinCodeCheckUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    MySharedPreference.setPreference(ProductDetailsScreenActivity.this, "PIN_CODE", "");
                    System.out.println(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray pins = jsonObject.getJSONArray("pincode_status");
                        if (pins.length()>0){
                            JSONObject nodeChild = pins.getJSONObject(0);
                            String result = nodeChild.getString("result");
                            //String delivery_date = jsonObject.getString("delivery_date");

                            if (result!=null && result.equalsIgnoreCase("success")){
                                pinCodeCheckET.setText(""+pinCodeStr);
                                MySharedPreference.setPreference(ProductDetailsScreenActivity.this, "PIN_CODE", ""+pinCodeStr);
                                pinCheckingBtn.setText("CHANGE");
                            } else {
                                Utility.showCustomToast("Oops! We are not currently servicing your area.", ProductDetailsScreenActivity.this);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    Log.v("HARI-->", "" + content);
                    Utility.showCustomToast("Oops! We are not currently servicing your area.", ProductDetailsScreenActivity.this);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }
        });
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1111) {
            if (data != null) {
                String userEmailStr = data.getStringExtra("USER_EMAIL");
                if (userEmailStr != null && !userEmailStr.equalsIgnoreCase("Not_Login")) {
                    Intent sportIntent = new Intent(ProductDetailsScreenActivity.this, ActivityInquiryPage.class);
                    sportIntent.putExtra("PRODUCT_NAME", product_name);
                    sportIntent.putExtra("TO_MAIL", vendor_email);
                    sportIntent.putExtra("FROM_MAIL", userEmailStr);
                    startActivity(sportIntent);
                } else {
                    Utility.showCustomToast("You must login to inquiry!", ProductDetailsScreenActivity.this);
                }
            } else {
                Utility.showCustomToast("You must login to inquiry!", ProductDetailsScreenActivity.this);
            }
        } else if (resultCode == RESULT_OK && requestCode == 1112) {
            if (data != null) {
                String userEmailStr = data.getStringExtra("USER_EMAIL");
                if (userEmailStr != null && !userEmailStr.equalsIgnoreCase("Not_Login")) {
                    startActivity(new Intent(ProductDetailsScreenActivity.this, ShippingAddressScreenActivity.class));
                } else {
                    Utility.showCustomToast("You must login to buy this item!", ProductDetailsScreenActivity.this);
                }
            } else {
                Utility.showCustomToast("You must login to buy this item!", ProductDetailsScreenActivity.this);
            }
        } else if (resultCode == RESULT_OK && requestCode == 1113) {
            if (data != null) {
                String userEmailStr = data.getStringExtra("USER_EMAIL");
                if (userEmailStr != null && !userEmailStr.equalsIgnoreCase("Not_Login")) {
                    try {
                        ringProgressDialog = ProgressDialog.show(ProductDetailsScreenActivity.this, "Please wait ...", "Adding item to wish list...", true);
                        ringProgressDialog.setCancelable(false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    loginDetails = dbHelper.getLoginDetails();
                    RequestParams params = new RequestParams();
                    params.put("vendor_id", ""+vendor_id);
                    params.put("product_id", ""+product_id);
                    params.put("name", ""+product_name);
                    params.put("price", ""+price);
                    params.put("userid", ""+loginDetails.getUserID());
                    addToCartServiceURL(AppConstants.ADD_TO_WISH_LIST_URL, params, "WishList");
                } else {
                    Utility.showCustomToast("You must login to add this item to wish list!", ProductDetailsScreenActivity.this);
                }
            } else {
                Utility.showCustomToast("You must login to add this item to wish list!", ProductDetailsScreenActivity.this);
            }
        }
    }

    private void addToWishListData(LoginDetails _loginDetails) {
        if (Utility.isOnline(ProductDetailsScreenActivity.this)){
            if (_loginDetails != null){
                try {
                    ringProgressDialog = ProgressDialog.show(ProductDetailsScreenActivity.this, "Please wait ...", "Adding item to wish list...", true);
                    ringProgressDialog.setCancelable(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RequestParams params = new RequestParams();
                params.put("vendor_id", ""+vendor_id);
                params.put("product_id", ""+product_id);
                params.put("name", ""+product_name);
                params.put("price", ""+price);
                params.put("userid", ""+_loginDetails.getUserID());
                addToCartServiceURL(AppConstants.ADD_TO_WISH_LIST_URL, params, "WishList");
            } else {
                MySharedPreference.setPreference(ProductDetailsScreenActivity.this, "FROM_SCREEN_USER", "MY_WISHLIST");
                //startActivity(new Intent(ProductDetailsScreenActivity.this, ActivityLoginPage.class));
                Intent resultss = new Intent(ProductDetailsScreenActivity.this, ActivityLoginPage.class);
                startActivityForResult(resultss, 1113);
            }
        }
    }

    private void addToCartData(LoginDetails _loginDetails, String _size) {
        if (Utility.isOnline(ProductDetailsScreenActivity.this)){
            try {
                ringProgressDialog = ProgressDialog.show(ProductDetailsScreenActivity.this, "Please wait ...", "Adding item to cart...", true);
                ringProgressDialog.setCancelable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String quantitiesETStr = quantitiesET.getText().toString();
            String udid = Settings.Secure.getString(ProductDetailsScreenActivity.this.getContentResolver(),Settings.Secure.ANDROID_ID);

            RequestParams params = new RequestParams();
            params.put("deviceid",""+udid);
            params.put("vendor_id", ""+vendor_id);
            params.put("product_id", ""+product_id);
            params.put("product_name", ""+product_name);
            params.put("quantity", ""+quantitiesETStr);
            params.put("price", ""+price);
            if (_size.trim().length()>0){
                params.put("size", ""+_size);
            }
            if (_loginDetails!=null){
                params.put("userid", ""+_loginDetails.getUserID());
            }
            addToCartServiceURL(AppConstants.ADD_TO_CART_URL, params, "Add_To_Cart");
        }
    }

    private void addToCartServiceURL(String addToCartUrl, RequestParams params, final String cartStr) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(addToCartUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null){
                    Log.w("HARI-->",""+response);

                    if (response.contains("already added")){
                        if (cartStr!=null && cartStr.contains("WishList")){
                            wishListIV.setBackgroundResource(R.drawable.wish_list_full);
                        }
                        Utility.showCustomToast("Already added this item", ProductDetailsScreenActivity.this);
                        if (ringProgressDialog != null) {
                            ringProgressDialog.dismiss();
                        }
                        return;
                    }

                    if (response.contains("success")){
                        if (cartStr!=null && cartStr.contains("WishList")){
                            wishListIV.setBackgroundResource(R.drawable.wish_list_full);
                        }
                        Utility.showCustomToast("Item added successful", ProductDetailsScreenActivity.this);
                    } else {
                        Utility.showCustomToast("Failed. Try again!", ProductDetailsScreenActivity.this);
                    }
                }

                if (ringProgressDialog!=null){
                    ringProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    Log.v("HARI-->", "" + content);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                if (ringProgressDialog!=null){
                    ringProgressDialog.dismiss();
                }
            }
        });
    }

    private void fullDetailsSingleProductData(String productDetailsURL) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(productDetailsURL, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        System.out.println(response);
                        try {
                            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                            JSONArray jsonResponse = new JSONArray(response);

                            /****** RELATED PRODUCTS ******* Process each JSON node ************/
                            if (jsonResponse.length() > 0) {

                                /****** Get Object for each JSON node.***********/
                                JSONObject jsonChildNode = jsonResponse.getJSONObject(0);

                                /******* Fetch node values **********/
                                product_id = jsonChildNode.optString("product_id");
                                product_name= jsonChildNode.optString("product_name");
                                String product_desc = jsonChildNode.optString("product_desc");
                                price = jsonChildNode.optString("price");
                                String regular_price = jsonChildNode.optString("regular_price");
                                String imageUrl = jsonChildNode.optString("image");
                                vendor_id = jsonChildNode.optString("vendor_id");
                                vendor_name = jsonChildNode.optString("vendor_name");
                                String vendor_description = jsonChildNode.optString("vendor_description");
                                String rating = jsonChildNode.optString("rating");
                                //String stock = jsonChildNode.optString("stock");
                                String availability = jsonChildNode.optString("availability");
                                vendor_email = jsonChildNode.optString("vendor_email");
                                JSONArray gallary_images = jsonChildNode.getJSONArray("gallary_images");

                                try {
                                    JSONObject product_attributes = jsonChildNode.getJSONObject("product_attributes");
                                    Log.v("HARI--> Gallery", "" + gallary_images.length());

                                if (product_attributes!=null) {
                                    JSONArray sizesJson = new JSONArray();
                                    JSONArray colorsJson = new JSONArray();

                                    if (product_attributes.toString().contains("pa_size")){
                                        sizesJson = product_attributes.getJSONArray("pa_size");
                                    } else {
                                        if (product_attributes.toString().contains("size")){
                                            sizesJson= product_attributes.getJSONArray("size");
                                        }
                                    }
                                    for (int i = 0; i < sizesJson.length(); i++) {
                                        String str_size = sizesJson.getString(i);
                                        if (str_size!=null && !str_size.isEmpty() && !str_size.equalsIgnoreCase("null")){
                                            if (i == 0){
                                                sizesLists.add("Select");
                                            } else {
                                                sizesLists.add(str_size);
                                            }
                                        }
                                    }

                                    Log.v("HARI-->", "Item Sizes Count = " + sizesLists.size());
                                    if (sizesLists!=null && sizesLists.size() >0){
                                        sizesLL.setVisibility(View.VISIBLE);
                                        // Creating adapter for spinner
                                        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(ProductDetailsScreenActivity.this, android.R.layout.simple_spinner_item, sizesLists);
                                        // Drop down layout style - list view with radio button
                                        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        // attaching data adapter to spinner
                                        sizesListSP.setAdapter(monthAdapter);
                                    } else {
                                        sizesLL.setVisibility(View.GONE);
                                    }

                                    if (product_attributes.toString().contains("pa_colour")){
                                        colorsJson = product_attributes.getJSONArray("pa_colour");
                                    } else if (product_attributes.toString().contains("colour")){
                                        colorsJson= product_attributes.getJSONArray("colour");
                                    } else if (product_attributes.toString().contains("color")){
                                        colorsJson= product_attributes.getJSONArray("color");
                                    } else if (product_attributes.toString().contains("pa_color")){
                                        colorsJson= product_attributes.getJSONArray("pa_color");
                                    }

                                    for (int i = 0; i < colorsJson.length(); i++) {
                                        String str_color = colorsJson.getString(i);
                                        if (str_color!=null && !str_color.isEmpty() && !str_color.equalsIgnoreCase("null")){
                                            //if (i == 0){
                                            //    colorsList.add("Select");
                                            //} else {
                                                colorsList.add(str_color);
                                            //}
                                        }
                                    }

                                    Log.v("HARI-->", "Item Colors Count = " + colorsList.size());
                                    if (colorsList!=null && colorsList.size() >0){
                                        colorsLL.setVisibility(View.VISIBLE);
                                        // Creating adapter for spinner
                                        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(ProductDetailsScreenActivity.this, android.R.layout.simple_spinner_item, colorsList);
                                        // Drop down layout style - list view with radio button
                                        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        // attaching data adapter to spinner
                                        colorsListSP.setAdapter(monthAdapter);
                                    } else {
                                        colorsLL.setVisibility(View.GONE);
                                    }
                                }
                                } catch (JSONException ee){
                                    ee.printStackTrace();
                                }

                                Utility.galleriesList.clear();
                                Utility.galleriesList = null;
                                Utility.galleriesList = new ArrayList<String>();

                                for (int i = 0; i < gallary_images.length(); i++) {
                                    String str_image_url = gallary_images.getString(i);
                                    if (str_image_url!=null && !str_image_url.isEmpty() && !str_image_url.equalsIgnoreCase("null")){
                                        Utility.galleriesList.add(str_image_url);
                                    }
                                }

                                if (Utility.galleriesList.size()>0){
                                    galleryCountTV.setVisibility(View.VISIBLE);
                                    galleryCountTV.setText(""+Utility.galleriesList.size());
                                } else {
                                    galleryCountTV.setVisibility(View.GONE);
                                }

                                Log.v("HARI-->", "Gallery Count = " + Utility.galleriesList.size());

                                try {
                                    if (availability!=null && !availability.isEmpty() && !availability.equalsIgnoreCase("null")){
                                        if (availability.equalsIgnoreCase("instock")){
                                            Log.v("HARI-->", "" + availability);
                                            stackAvailabilityTV.setText("In stock");
                                        } else {
                                            Log.v("HARI-->", "" + availability);
                                            stackAvailabilityTV.setText("Out of stock");
                                            stackAvailabilityTV.setTextColor(Color.parseColor("#FF9900"));
                                        }
                                    } else {
                                        Log.v("HARI-->", "" + availability);
                                        stackAvailabilityTV.setText("Out of stock");
                                        stackAvailabilityTV.setTextColor(Color.parseColor("#FF9900"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    stackAvailabilityTV.setText("Out of stock");
                                    stackAvailabilityTV.setTextColor(Color.parseColor("#FF9900"));
                                    Log.v("HARI-->", "" + e);
                                }

                                if (product_name != null && !product_name.isEmpty()) {
                                    productNameTV.setText(Html.fromHtml(product_name));
                                }

                                if (vendor_name != null && !vendor_name.isEmpty()) {
                                    vendorNameTV.setText("By " + Html.fromHtml(vendor_name));
                                }

                                WebView descriptionTV = (WebView) findViewById(R.id.descriptionTVID);
                                //WebView vendorDescriptionWV = (WebView) findViewById(R.id.vendorDescriptionTVID);

                                if (vendor_description != null && !vendor_description.isEmpty() && !vendor_description.equalsIgnoreCase("null")) {
                                   // loadInWebView(vendor_description, vendorDescriptionWV, "Seller");
                                } else {
                                  //  aboutSellerInfoLL.setVisibility(View.GONE);
                                }

                                if (product_desc != null && !product_desc.isEmpty() && !product_desc.equalsIgnoreCase("null")) {
                                    loadInWebView(product_desc, descriptionTV, "Product");
                                } else {
                                    productInfoLL.setVisibility(View.GONE);
                                }

                                if (price != null && !price.isEmpty() && !price.equalsIgnoreCase("null") && !price.equalsIgnoreCase("0")) {
                                    priceTV.setVisibility(View.VISIBLE);
                                    buyNowLL.setVisibility(View.VISIBLE);
                                    priceTV.setText(getResources().getString(R.string.rs) + Html.fromHtml(price));
                                    if (regular_price != null && !regular_price.isEmpty() && !regular_price.equalsIgnoreCase("null")) {
                                        regularPriceTV.setText(getResources().getString(R.string.rs) + Html.fromHtml(regular_price));
                                        regularPriceTV.setPaintFlags(regularPriceTV.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    } else {
                                        regularPriceTV.setVisibility(View.GONE);
                                    }
                                } else {
                                    priceTV.setVisibility(View.GONE);
                                    if (vendor_email!=null && !vendor_email.isEmpty() && vendor_email.contains("@")) {
                                        regularPriceTV.setText("ASK FOR PRICE/COLLECT AT STORE");
                                        regularPriceTV.setVisibility(View.VISIBLE);
                                        buyNowLL.setVisibility(View.GONE);
                                    } else {
                                        regularPriceTV.setVisibility(View.GONE);
                                        buyNowLL.setVisibility(View.VISIBLE);
                                    }
                                }

                                if (imageUrl != null && !imageUrl.isEmpty()) {
                                    Picasso.with(ProductDetailsScreenActivity.this).load(imageUrl).placeholder(R.drawable.placeholder_pro).into(mDemoSlider);
                                } else {
                                    mDemoSlider.setBackgroundResource(R.drawable.placeholder_pro);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    if (ringProgressDialog != null) {
                        ringProgressDialog.dismiss();
                    }
                    Log.v("HARI-->", "" + content);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
    }

    private void loadInWebView(String vendor_description, WebView _webview, String fileName) {
        _webview.getSettings().setJavaScriptEnabled(true);
        _webview.getSettings().setAllowFileAccess(true);
        _webview.getSettings().setLoadsImagesAutomatically(true);
        WebSettings settings = _webview.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        _webview.getSettings().setUserAgentString(Locale.getDefault().getLanguage());
        _webview.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = Utility.hariEmailIntent(ProductDetailsScreenActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    startActivity(i);
                    view.reload();
                    return true;
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });

        String summary = "<!DOCTYPE html><head> <meta http-equiv=\"Content-Type\" \" +\n" +
                "\"content=\"text/html; charset=utf-8\"><html> <body align=\"justify\" style=\"font-family:Roboto;line-height:20px\">" + vendor_description + "</body></html>";
        summary = summary.replaceAll("//", "");

        _webview.loadData(summary, "text/html", "UTF-8");
    }

    private void realtedProductsLoading(String homeProductsUrl) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(homeProductsUrl, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        System.out.println(response);
                        String OutputData = "";

                        try {
                            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                            JSONArray jsonResponse = new JSONArray(response);

                            /****** RELATED PRODUCTS ******* Process each JSON node ************/
                            if (jsonResponse.length() > 0) {
                                relatedProductsList.clear();
                                relatedProductsList = null;
                                relatedProductsList = new ArrayList<HomeProductsData>();

                                for (int i = 0; i < jsonResponse.length(); i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = jsonResponse.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    String product_id = jsonChildNode.optString("product_id");
                                    String product_name = jsonChildNode.optString("product_name");
                                    String price = jsonChildNode.optString("price");
                                    String regular_price = jsonChildNode.optString("regular_price");
                                    String imageUrl = jsonChildNode.optString("image");
                                    String vendor_name = jsonChildNode.optString("vendor_name");
                                    String vendor_description = jsonChildNode.optString("vendor_description");
                                    String rating = jsonChildNode.optString("rating");
                                    String product_desc = jsonChildNode.optString("product_desc");
                                    relatedProductsList.add(new HomeProductsData(product_id, product_name, price, regular_price, imageUrl, vendor_name, vendor_description, rating, product_desc));
                                }

                                OutputData = "" + relatedProductsList.size();
                                Log.i("JSON parse Item Count", OutputData);
                            }

                            /************ Show Output on screen/activity **********/
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    if (relatedProductsList.size() > 0) {
                                        HomeProductsAdapter adapter1 = new HomeProductsAdapter(ProductDetailsScreenActivity.this, relatedProductsList);
                                        relatedProductsRV.setAdapter(adapter1);
                                        relatedProductsLL.setVisibility(View.VISIBLE);
                                        adapter1.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent sportIntent = new Intent(ProductDetailsScreenActivity.this, ProductDetailsScreenActivity.class);
                                                sportIntent.putExtra("PRODUCT_ID", relatedProductsList.get(position).getproduct_id());
                                                sportIntent.putExtra("PRODUCT_FROM_SCREEN", "SAME");
                                                sportIntent.putExtra("PRODUCT_NAME", relatedProductsList.get(position).getproduct_name());
                                                sportIntent.putExtra("PRODUCT_PRICE", relatedProductsList.get(position).getprice());
                                                sportIntent.putExtra("PRODUCT_REGULAR_PRICE", relatedProductsList.get(position).getregular_price());
                                                sportIntent.putExtra("PRODUCT_IMAGE_URL", relatedProductsList.get(position).getimageUrl());
                                                sportIntent.putExtra("PRODUCT_VENDOR_NAME", relatedProductsList.get(position).getvendor_name());
                                                sportIntent.putExtra("PRODUCT_DESCRIPTION", relatedProductsList.get(position).get_product_desc());
                                                sportIntent.putExtra("VENDOR_DESCRIPTION", relatedProductsList.get(position).getvendor_description());
                                                startActivity(sportIntent);
                                                finish();
                                            }
                                        });
                                    } else {
                                        relatedProductsLL.setVisibility(View.GONE);
                                    }
                                }
                            }, 400);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    Log.v("HARI-->", "" + content);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
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
        titleTV.setText("Details");

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.GONE);
        // if (sub_cat_name != null && !sub_cat_name.isEmpty() && sub_cat_name.trim().length() > 2) {
        //    subTitleTV.setVisibility(View.VISIBLE);
        //    subTitleTV.setText("" + Html.fromHtml(sub_cat_name));
        // }

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
        menuBtn.setVisibility(View.VISIBLE);
        menuBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProductDetailsScreenActivity.this, CartScreenActivity.class));
            }
        });
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