package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ourcitydeals.ctrl.model.CartListData;
import com.ourcitydeals.ctrl.model.LoginDetails;
import com.ourcitydeals.ctrl.ownLibs.ProgressWheel;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.AppDataBaseHelper;
import com.ourcitydeals.ctrl.utilities.MySharedPreference;
import com.ourcitydeals.ctrl.utilities.Utility;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jashwikha Chakrapu on 5/29/2016.
 */
public class CreateOrderActivity extends Activity {
    ArrayList<CartListData> cartListDataList = new ArrayList<CartListData>();
    AppDataBaseHelper dataBaseHelper = new AppDataBaseHelper(CreateOrderActivity.this);
    public CartPlaceOrdersListAdapter mAdapter;
    RelativeLayout cartIsEmptyRL;
    ListView cartListviewLV;
    private ProgressWheel progressWheel_CENTER;
    TextView totalAmountTV;

    LoginDetails loginDetails;
    ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.screen_order_confirmation);

        if (getIntent()!= null && getIntent().getBooleanExtra("EXIT", false)) {
            Intent bookingDoneIntent = new Intent(CreateOrderActivity.this, ShippingAddressScreenActivity.class);
            bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            bookingDoneIntent.putExtra("EXIT", true);
            startActivity(bookingDoneIntent);
            onBackPressedAnimationByCHK();
        } else {
            StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                    .permitDiskWrites()
                    .detectAll()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
            StrictMode.setThreadPolicy(old);

            loginDetails= dataBaseHelper.getLoginDetails();

            Utility.setDimensions(this);
            setupNavigation();

            TextView nameTV = (TextView)findViewById(R.id.nameTVID);
            nameTV.setText(""+ MySharedPreference.getPreferences(CreateOrderActivity.this, "FIRST_NAME"));

            TextView addressTV = (TextView)findViewById(R.id.addressTVID);
            addressTV.setText(""+MySharedPreference.getPreferences(CreateOrderActivity.this, "DELIVERY_ADDRESS_LINE1")+"\n"+MySharedPreference.getPreferences(CreateOrderActivity.this, "DELIVERY_ADDRESS_LINE2"));

            TextView editTV = (TextView)findViewById(R.id.editTVID);
            editTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MySharedPreference.setPreference(CreateOrderActivity.this, "FROM_SCREEN_USER", "CART");
                    startActivity(new Intent(CreateOrderActivity.this, ShippingAddressScreenActivity.class));
                }
            });

            progressWheel_CENTER = (ProgressWheel) findViewById(R.id.progress_wheel1);
            progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
            progressWheel_CENTER.setRimColor(Color.LTGRAY);

            Button confirmationOrderBtn = (Button)findViewById(R.id.confirmationOrderBtnID);
            confirmationOrderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Utility.isOnline(CreateOrderActivity.this)){
                        try {
                            ringProgressDialog = ProgressDialog.show(CreateOrderActivity.this, "Please wait ...", "Creating order...", true);
                            ringProgressDialog.setCancelable(false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        RequestParams params = new RequestParams();
                        params.put("order_total",""+MySharedPreference.getPreferences(CreateOrderActivity.this, "CART_TOTAL_AMOUNT"));
                        params.put("payment_method","COD");
                        if (loginDetails!=null){
                            params.put("username", ""+loginDetails.getuserEmail());
                            params.put("userid", ""+loginDetails.getUserID());
                        }

                        try {
                            String cartList = createOrdersGroupInServer(cartListDataList).toString();
                            params.put("cart",""+cartList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception eee){
                            eee.printStackTrace();
                        }

                        params.put("billing_phone", ""+MySharedPreference.getPreferences(CreateOrderActivity.this, "MOBILE"));
                        params.put("billing_email", ""+MySharedPreference.getPreferences(CreateOrderActivity.this, "EMAIL_ID"));
                        params.put("billing_postcode", ""+MySharedPreference.getPreferences(CreateOrderActivity.this, "ZIP_CODE"));
                        params.put("billing_state", ""+MySharedPreference.getPreferences(CreateOrderActivity.this, "STATE"));
                        params.put("billing_city", ""+MySharedPreference.getPreferences(CreateOrderActivity.this, "CITY_NAME"));
                        params.put("billing_address_1", ""+MySharedPreference.getPreferences(CreateOrderActivity.this, "DELIVERY_ADDRESS_LINE1"));
                        params.put("billing_address_2", ""+""+MySharedPreference.getPreferences(CreateOrderActivity.this, "DELIVERY_ADDRESS_LINE2"));
                        params.put("billing_company", "");
                        params.put("billing_last_name", ""+MySharedPreference.getPreferences(CreateOrderActivity.this, "LAST_NAME"));
                        params.put("billing_first_name", ""+MySharedPreference.getPreferences(CreateOrderActivity.this, "FIRST_NAME"));
                        params.put("billing_country", "IND");


                        createOrderUrl(AppConstants.ORDER_CREATE_URL, params);
                    }
                }
            });

            Button shopNow = (Button)findViewById(R.id.shopNowBtnID);
            shopNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(CreateOrderActivity.this, HomeActivity.class));
                    finish();
                }
            });

            //cartItemsListRL = (RelativeLayout) findViewById(R.id.cartItemsListRLID);
            ///cartItemsListRL.setVisibility(View.GONE);
            cartIsEmptyRL = (RelativeLayout) findViewById(R.id.cartIsEmptyRLID);
            //cartIsEmptyRL.setVisibility(View.VISIBLE);
            cartListviewLV = (ListView) findViewById(R.id.cartListViewLVID);

            totalAmountTV = (TextView) findViewById(R.id.totalAmountTVID);

            loadCartList();
        }
    }

    private void createOrderUrl(String orderCreateUrl, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(orderCreateUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    System.out.println(response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String ress = jsonObject.getString("response");
                        if (ress!= null && ress.contains("success")){
                            String order_id = jsonObject.getString("order_id");
                            Intent success = new Intent(CreateOrderActivity.this, OrderConfirmedActivity.class);
                            success.putExtra("ORDER_ID", ""+order_id);
                            success.putExtra("TOTAL_AMOUNT", ""+MySharedPreference.getPreferences(CreateOrderActivity.this, "CART_TOTAL_AMOUNT"));
                            startActivity(success);
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
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        loadCartList();
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
        titleTV.setText("CHECKOUT");

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

    private void loadCartList() {
        boolean isNetAvailable = Utility.isOnline(CreateOrderActivity.this);
        if (isNetAvailable) {
            RequestParams params = new RequestParams();
            LoginDetails loginDetails = dataBaseHelper.getLoginDetails();
            String udid = Settings.Secure.getString(CreateOrderActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

            params.put("deviceid", "" + udid);
            if (loginDetails != null) {
                params.put("userid", "" + loginDetails.getUserID());
            }

            getCartList(AppConstants.SHOW_CART_LIST_URL, params);
        } else {
            Utility.showCustomToast("Please connect your Internet!", CreateOrderActivity.this);
            finish();
        }
    }

    private JSONObject createOrdersGroupInServer(ArrayList<CartListData> cartList) throws JSONException {
        JSONObject jResult = new JSONObject();
        JSONArray jArray = new JSONArray();

        for (int i = 0; i < cartList.size(); i++) {
            //if (mDBHelper.getSyncingOrNot(cartList.get(i).get_Identifier()).equals("NO")) {
            JSONObject jGroup = new JSONObject();
            jGroup.put("product_id", cartList.get(i).getproduct_id());
            jGroup.put("product_name", cartList.get(i).getproduct_name());
            jGroup.put("quantity", cartList.get(i).getquantity());
            jGroup.put("price", cartList.get(i).getprice());
            jGroup.put("vendor_id", cartList.get(i).getvendor_id());
            // etc

            jArray.put(jGroup);
        }

        jResult.put("cart", jArray);
        return jResult;
    }


    private void getCartList(final String cartListUrl, RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(cartListUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                Log.d("CartList", "onSuccess: "+cartListUrl);
                if (response != null) {
                    System.out.println(response);
                    if (response.contains("no data found")) {
                        cartListviewLV.setVisibility(View.GONE);
                        cartIsEmptyRL.setVisibility(View.VISIBLE);
                    } else {
                        try {
                            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                            /*******  Returns null otherwise.  *******/
                            JSONObject jsonMainNode1 = new JSONObject(response);
                            String cartTotalAmount = jsonMainNode1.getString("cart_total");
                            String cartCount = jsonMainNode1.getString("count");

                            MySharedPreference.setPreference(CreateOrderActivity.this, "CART_TOTAL_AMOUNT", "" + cartTotalAmount);
                            MySharedPreference.setPreference(CreateOrderActivity.this, "CART_ITEMS_COUNT", "" + cartCount);
                            totalAmountTV.setText(CreateOrderActivity.this.getResources().getString(R.string.rs) + "" +cartTotalAmount);

                            JSONArray jsonMainNode = jsonMainNode1.getJSONArray("cart");

                            /*********** Process each JSON Node ************/
                            cartListDataList.clear();
                            cartListDataList = null;
                            cartListDataList = new ArrayList<CartListData>();
                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                /****** Get Object for each JSON node.***********/
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                /******* Fetch node values **********/
                                String product_name = jsonChildNode.optString("product_name");
                                String product_id = jsonChildNode.optString("product_id");
                                String price = jsonChildNode.optString("price");
                                String quantity = jsonChildNode.optString("quantity");
                                String vendor_id = jsonChildNode.optString("vendor_id");
                                String image = jsonChildNode.optString("image");
                                String vendor_name = jsonChildNode.optString("vendor_name");
                                //String total = jsonChildNode.optString("total");

                                cartListDataList.add(new CartListData(product_id, product_name, price, image, vendor_id, quantity, vendor_name, ""));
                            }

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    if (cartListDataList.size() > 0) {
                                        mAdapter = null;
                                        mAdapter = new CartPlaceOrdersListAdapter(CreateOrderActivity.this, cartListDataList);
                                        cartListviewLV.setAdapter(mAdapter);
                                        cartListviewLV.setVisibility(View.VISIBLE);
                                        cartIsEmptyRL.setVisibility(View.GONE);
                                    } else {
                                        cartListviewLV.setVisibility(View.GONE);
                                        cartIsEmptyRL.setVisibility(View.VISIBLE);
                                    }

                                }
                            }, 500);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            cartListviewLV.setVisibility(View.GONE);
                            cartIsEmptyRL.setVisibility(View.VISIBLE);
                        }
                    }
                }
                progressWheel_CENTER.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    cartListviewLV.setVisibility(View.GONE);
                    cartIsEmptyRL.setVisibility(View.VISIBLE);
                    Log.w("HARI-->", "onFailure" + content);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
    }

    public class CartPlaceOrdersListAdapter extends BaseAdapter {
        ArrayList<CartListData> mCartListData;
        private LayoutInflater layoutInflater = null;
        Context mContext;

        public CartPlaceOrdersListAdapter(Context context, ArrayList<CartListData> _cartListData) {
            this.mContext = context;
            this.mCartListData = _cartListData;
            layoutInflater = LayoutInflater.from(context);//7331167946
        }

        @Override
        public int getCount() {
            return mCartListData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup paramViewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.list_items_row_cart_place_order_list, null, false);

                holder.cardView = (RelativeLayout) convertView.findViewById(R.id.cartItemsCardRLID);

                holder.productNameTV = (TextView) convertView.findViewById(R.id.productNameTVID);

                holder.vendorNameTV = (TextView) convertView.findViewById(R.id.vendorNameTVID);

                holder.priceTV = (TextView) convertView.findViewById(R.id.priceTVID);

                holder.productImageIV = (ImageView) convertView.findViewById(R.id.productImageIVID);

                holder.quantityTV = (TextView) convertView.findViewById(R.id.quantityTVID);

                holder.deleteCartItemBtn = (Button) convertView.findViewById(R.id.deleteCartItemBtnID);

                // This will now execute only for the first time of each row
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.productNameTV.setText("" + Html.fromHtml(mCartListData.get(position).getproduct_name()));
            holder.vendorNameTV.setText("By " + Html.fromHtml(mCartListData.get(position).getvendroName()));
            holder.priceTV.setText("" + mContext.getResources().getString(R.string.rs) + Html.fromHtml(mCartListData.get(position).getprice()));
            holder.quantityTV.setText("" + Html.fromHtml(mCartListData.get(position).getquantity()));

            if (mCartListData.get(position).getimageUrl() != null && !mCartListData.get(position).getimageUrl().isEmpty()) {
                Picasso.with(mContext).load(mCartListData.get(position).getimageUrl()).placeholder(R.drawable.placeholder_pro).memoryPolicy(MemoryPolicy.NO_CACHE).fit().into(holder.productImageIV);
            } else {
                holder.productImageIV.setBackgroundResource(R.drawable.placeholder_pro);
            }

            switch (position % 2) {
                case 0:
                    //holder.cardView.setBackgroundResource(R.color.row1);
                    break;
                case 1:
                    // holder.cardView.setBackgroundResource(R.color.white);
                    break;
            }

            holder.deleteCartItemBtn.setTag(position);
            holder.deleteCartItemBtn.setFocusable(false);
            holder.deleteCartItemBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final int pos = (Integer) v.getTag();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateOrderActivity.this);
                    alertDialogBuilder.setMessage("Are you sure, You wanted to delete this item from cart?");

                    alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            if (Utility.isOnline(CreateOrderActivity.this)) {
                                RequestParams params = new RequestParams();
                                LoginDetails loginDetails = dataBaseHelper.getLoginDetails();
                                String udid = Settings.Secure.getString(CreateOrderActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);

                                params.put("deviceid", "" + udid);
                                if (loginDetails != null) {
                                    params.put("userid", "" + loginDetails.getUserID());
                                }
                                params.put("product_id", "" + mCartListData.get(pos).getproduct_id());

                                removeCartListItem(AppConstants.REMOVE_CART_LIST_ITEM_URL, params);
                            }
                        }
                    });

                    alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });

            return convertView;
        }


        private void removeCartListItem(String removeCartListItemUrl, RequestParams params) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(removeCartListItemUrl, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(String response) {
                    if (response != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String resp = jsonObject.getString("response");
                            String cartTotalAmount = jsonObject.getString("total");
                            String cartCount = jsonObject.getString("count");
                            MySharedPreference.setPreference(CreateOrderActivity.this, "CART_TOTAL_AMOUNT", "" + cartTotalAmount);
                            MySharedPreference.setPreference(CreateOrderActivity.this, "CART_ITEMS_COUNT", "" + cartCount);
                            totalAmountTV.setText(CreateOrderActivity.this.getResources().getString(R.string.rs) + "" +cartTotalAmount);
                            if (resp != null && resp.contains("success")){
                                loadCartList();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    progressWheel_CENTER.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int statusCode, Throwable error, String content) {
                    try {
                        //cartItemsListRL.setVisibility(View.GONE);
                        //cartIsEmptyRL.setVisibility(View.VISIBLE);
                        Log.w("HARI-->", "onFailure" + content);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            });
        }

        class ViewHolder {
            RelativeLayout cardView;
            TextView productNameTV;
            TextView vendorNameTV;
            TextView priceTV, quantityTV;
            ImageView productImageIV;
            Button deleteCartItemBtn;
        }
    }
}