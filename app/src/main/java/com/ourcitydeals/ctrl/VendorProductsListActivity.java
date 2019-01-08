package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ourcitydeals.ctrl.adapters.GridViewAdapter;
import com.ourcitydeals.ctrl.adapters.GridViewHomeAdapter;
import com.ourcitydeals.ctrl.adapters.GridViewVendorsAdapter;
import com.ourcitydeals.ctrl.model.HomeProductsData;
import com.ourcitydeals.ctrl.model.VendorsProductsListData;
import com.ourcitydeals.ctrl.ownLibs.ExpandableHeightGridView;
import com.ourcitydeals.ctrl.ownLibs.ProgressWheel;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jashwikha Chakrapu on 5/29/2016.
 */
public class VendorProductsListActivity extends Activity implements OnScrollListener {
    ArrayList<VendorsProductsListData> vendorsProductsListData = new ArrayList<VendorsProductsListData>();
    ArrayList<VendorsProductsListData> vendorsProductsListDataTemp = new ArrayList<VendorsProductsListData>();

    private ProgressWheel progressWheel_CENTER;
    TextView noDataTV;

    GridViewVendorsAdapter mHomeAdapter;
    ExpandableHeightGridView categoriesListRV;

    static String VENDOR_NAME_KEY = "";

    //Pagination here
    private int mvisibleItemCount = -1;
    private String fetchDirectionUP = Utility.FETCH_DIRECTION_UP;
    private String fetchDirection = "";
    private int visibleThreshold = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private int mfirstVisibleItem;
    int productsCount = 0;

    int limit = 100;

    RelativeLayout loadingMoreRL;
    ImageView vendorimage;
    TextView vendornameTV, locationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.screen_all_vendor_products);

        VENDOR_NAME_KEY = "";

        vendorsProductsListData = new ArrayList<VendorsProductsListData>();
        vendorsProductsListDataTemp = new ArrayList<VendorsProductsListData>();

        Utility.setDimensions(this);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);

        progressWheel_CENTER = (ProgressWheel) findViewById(R.id.progress_wheel1);
        progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_CENTER.setRimColor(Color.LTGRAY);

        loadingMoreRL = (RelativeLayout) findViewById(R.id.loadingMoreRLID);
        loadingMoreRL.setVisibility(View.GONE);

        vendorimage = (ImageView) findViewById(R.id.vendorimage);
        vendornameTV = (TextView) findViewById(R.id.vendorname);
        locationTV = (TextView) findViewById(R.id.location);

        noDataTV = (TextView) findViewById(R.id.noDataFoundTVID);
        categoriesListRV = (ExpandableHeightGridView) findViewById(R.id.gridViewID);
        categoriesListRV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent sportIntent = new Intent(VendorProductsListActivity.this, ProductDetailsScreenActivity.class);
                sportIntent.putExtra("PRODUCT_ID", vendorsProductsListData.get(position).getproduct_id());
                startActivity(sportIntent);
            }
        });

        categoriesListRV.setOnScrollListener(this);

        boolean isNetAvailable = Utility.isOnline(VendorProductsListActivity.this);
        if (isNetAvailable) {
            if (getIntent() != null) {
                Bundle b = getIntent().getExtras();
                if (b != null) {
                    String productsURL = null;
                    VENDOR_NAME_KEY = b.getString("VENDOR_NAME");
                    setupNavigation("" + VENDOR_NAME_KEY);
                    productsURL = AppConstants.VENDOR_WISE_PRODUCTS_URL + "" + VENDOR_NAME_KEY;
                    loadProductsListData(productsURL, productsCount, limit);
                }
            }
        } else {
            Utility.showCustomToast("Please connect your Internet!", VendorProductsListActivity.this);
            finish();
        }
    }

    private void loadProductsListData(String allCategoriesUrl, int start, int limit) {
        String productsURL = allCategoriesUrl + "&start=" + start + "&limit=" + limit;
        getAllCategoriesList(productsURL);
        Log.w("HARI--> Products URL", "" + productsURL);
    }

    private void getAllCategoriesList(String allCategoriesUrl) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(allCategoriesUrl, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null && response.contains("product_id")) {
                    try {
                        System.out.println(response);
                        JSONArray jsonResponseArray;
                        try {

                            jsonResponseArray = new JSONArray(response);

                            /****** PRODUCTS LIST ******* Process each JSON node ************/
                            if (jsonResponseArray.length()>0) {
                                JSONObject jsonChildNode1= jsonResponseArray.getJSONObject(0);
                                String vendor_image = jsonChildNode1.optString("vendor_image");
                                String vendor_name = jsonChildNode1.optString("vendor_name");
                                String location = jsonChildNode1.optString("location");

                                vendornameTV.setText(Html.fromHtml(vendor_name));
                                locationTV.setText(Html.fromHtml(location));

                                if (vendor_image != null && !vendor_image.isEmpty()) {
                                    Picasso.with(VendorProductsListActivity.this).load(vendor_image).placeholder(R.drawable.placeholder_pro).into(vendorimage);
                                } else {
                                    vendorimage.setBackgroundResource(R.drawable.placeholder_pro);
                                }

                            }
                            for (int i = 0; i < jsonResponseArray.length(); i++) {
                                /****** Get Object for each JSON node.***********/
                                JSONObject jsonChildNode = jsonResponseArray.getJSONObject(i);

                                /******* Fetch node values **********/
                                String product_id = jsonChildNode.optString("product_id");
                                String product_name = jsonChildNode.optString("product_name");
                                String price = jsonChildNode.optString("price");
                                String regular_price = jsonChildNode.optString("regular_price");
                                String imageUrl = jsonChildNode.optString("image");
                                String rating = jsonChildNode.optString("rating");
                                //String vendor_name = jsonChildNode.optString("vendor_name");
                                vendorsProductsListData.add(new VendorsProductsListData(product_id, product_name, price, regular_price, imageUrl, rating));
                            }

                            productsCount = productsCount + vendorsProductsListData.size();
                            String OutputData = "" + vendorsProductsListData.size();
                            Log.i("HARI--->", "JSON parse Item Count = " + OutputData);

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    if (vendorsProductsListData.size()>0) {
                                        if (fetchDirection.equalsIgnoreCase(fetchDirectionUP)) {
                                            int position = vendorsProductsListDataTemp.size();
                                            vendorsProductsListDataTemp.addAll(vendorsProductsListData);
                                            vendorsProductsListData = vendorsProductsListDataTemp;

                                            Set<VendorsProductsListData> hs = new HashSet<>();
                                            hs.addAll(vendorsProductsListData);
                                            vendorsProductsListData.clear();
                                            vendorsProductsListData.addAll(hs);

                                            mHomeAdapter = new GridViewVendorsAdapter(VendorProductsListActivity.this, vendorsProductsListData);
                                            categoriesListRV.setAdapter(mHomeAdapter);
                                            categoriesListRV.setScrollingCacheEnabled(false);
                                            categoriesListRV.setVisibility(View.VISIBLE);
                                            if (vendorsProductsListData.size() > 0 && mvisibleItemCount != -1) {
                                                categoriesListRV.setSelection(position - mvisibleItemCount + 2);
                                            } else {
                                                categoriesListRV.setSelection(position);
                                            }
                                        }

                                        if (mHomeAdapter == null) {
                                            Set<VendorsProductsListData> hs = new HashSet<>();
                                            hs.addAll(vendorsProductsListData);
                                            vendorsProductsListData.clear();
                                            vendorsProductsListData.addAll(hs);

                                            mHomeAdapter = new GridViewVendorsAdapter(VendorProductsListActivity.this, vendorsProductsListData);
                                            categoriesListRV.setAdapter(mHomeAdapter);
                                        } else {
                                            mHomeAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        categoriesListRV.setVisibility(View.GONE);
                                        noDataTV.setVisibility(View.VISIBLE);
                                        loadingMoreRL.setVisibility(View.GONE);
                                        progressWheel_CENTER.setVisibility(View.GONE);
                                    }

                                    loadingMoreRL.setVisibility(View.GONE);
                                    progressWheel_CENTER.setVisibility(View.GONE);
                                }
                            }, 500);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (vendorsProductsListData.size() == 0){
                                categoriesListRV.setVisibility(View.GONE);
                                noDataTV.setVisibility(View.VISIBLE);
                                loadingMoreRL.setVisibility(View.GONE);
                                progressWheel_CENTER.setVisibility(View.GONE);
                            }
                            categoriesListRV.setVisibility(View.GONE);
                            noDataTV.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (vendorsProductsListData.size() == 0){
                            categoriesListRV.setVisibility(View.GONE);
                            noDataTV.setVisibility(View.VISIBLE);
                            loadingMoreRL.setVisibility(View.GONE);
                            progressWheel_CENTER.setVisibility(View.GONE);
                        }
                        categoriesListRV.setVisibility(View.GONE);
                        noDataTV.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (vendorsProductsListData.size() == 0){
                        categoriesListRV.setVisibility(View.GONE);
                        noDataTV.setVisibility(View.VISIBLE);
                        loadingMoreRL.setVisibility(View.GONE);
                        progressWheel_CENTER.setVisibility(View.GONE);
                    }
                    categoriesListRV.setVisibility(View.GONE);
                    noDataTV.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    categoriesListRV.setVisibility(View.GONE);
                    noDataTV.setVisibility(View.VISIBLE);
                    progressWheel_CENTER.setVisibility(View.GONE);
                    Log.w("HARI-->", "onFailure" + content);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
    }

    public void setupNavigation(String _title) {
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
        titleTV.setText(Html.fromHtml(_title));

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
        menuBtn.getLayoutParams().width = (int) (Utility.screenHeight / 24.0);
        menuBtn.getLayoutParams().height = (int) (Utility.screenHeight / 24.0);
        menuBtn.setVisibility(View.GONE);
        menuBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(VendorProductsListActivity.this, CartScreenActivity.class));
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

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mvisibleItemCount = visibleItemCount;
        if (firstVisibleItem > mfirstVisibleItem && !loading && (firstVisibleItem + visibleItemCount == totalItemCount) && totalItemCount > 0 && totalItemCount != visibleItemCount) {
            fetchDirection = fetchDirectionUP;
            System.out.println("firstVisibleItem" + firstVisibleItem);
            System.out.println("visibleItemCount" + visibleItemCount);
            System.out.println("totalItemCount" + totalItemCount);
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold) && totalItemCount != 0) {
                sendRequestWithScrollDirection(fetchDirectionUP, totalItemCount);
                loading = true;
            }
        }

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        mfirstVisibleItem = firstVisibleItem;
    }

    private void sendRequestWithScrollDirection(String DirectiontoFetch, int totalItemCount) {
        if (vendorsProductsListData.size() == totalItemCount) {
            loadingMoreRL.setVisibility(View.VISIBLE);
            fetchDirection = DirectiontoFetch;
            vendorsProductsListDataTemp = vendorsProductsListData;
            if ((productsCount - 20) >= 0) {
                if (Utility.isOnline(VendorProductsListActivity.this)) {
                    String productsURL = AppConstants.VENDOR_WISE_PRODUCTS_URL + "" + VENDOR_NAME_KEY;
                    loadProductsListData(productsURL, productsCount, 10);
                } else {
                    Utility.showCustomToast("No Internet available!", VendorProductsListActivity.this);
                }
            } else {
                loadingMoreRL.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.v("HARI-->", "onScrollStateChanged()--scrollState" + scrollState);
    }
}