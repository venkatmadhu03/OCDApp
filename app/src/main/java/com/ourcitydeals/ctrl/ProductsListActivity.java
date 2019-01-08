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
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.widget.AdapterView;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ourcitydeals.ctrl.adapters.GridViewAdapter;
import com.ourcitydeals.ctrl.adapters.GridViewHomeAdapter;
import com.ourcitydeals.ctrl.adapters.HomeAllProductsListAdapter;
import com.ourcitydeals.ctrl.adapters.ProductsListAdapter;
import com.ourcitydeals.ctrl.model.HomeProductsData;
import com.ourcitydeals.ctrl.model.ProductsListData;
import com.ourcitydeals.ctrl.ownLibs.ProgressWheel;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jashwikha Chakrapu on 5/29/2016.
 */
public class ProductsListActivity extends Activity implements OnScrollListener {
    //ArrayList<ProductsListData> categoriesWiseProductsList = new ArrayList<ProductsListData>();
    ArrayList<HomeProductsData> categoriesWiseProductsList_Home = new ArrayList<HomeProductsData>();
    ArrayList<HomeProductsData> categoriesWiseProductsList_HomeTemp = new ArrayList<HomeProductsData>();

    private ProgressWheel progressWheel_CENTER;
    TextView noDataTV;

    GridViewHomeAdapter mHomeAdapter;
    GridViewAdapter mListAdapter;
    GridView categoriesListRV;
    //private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    static String cat_id = "";
    static String sub_cat_id = "";
    static String cat_name = "";
    static String sub_cat_name = "";
    static String fromScreen = "";

    static String HOME_KEY = "";
    static String SEARCH_KEYWORD = "";

    //Pagination here
    private int mvisibleItemCount = -1;
    private String fetchDirectionUP = Utility.FETCH_DIRECTION_UP;
    private String fetchDirection = "";
    private int visibleThreshold = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private int mfirstVisibleItem;
    int productsCount = 0;

    int limit = 20;

    RelativeLayout loadingMoreRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null && getIntent().getBooleanExtra("EXIT", false)) {
            Intent bookingDoneIntent = new Intent(ProductsListActivity.this, HomeActivity.class);
            bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            bookingDoneIntent.putExtra("EXIT", true);
            startActivity(bookingDoneIntent);
            onBackPressedAnimationByCHK();
        } else {
            HOME_KEY = "";
            SEARCH_KEYWORD = "";
            cat_id = "";
            sub_cat_id = "";
            cat_name = "";
            sub_cat_name = "";
            fromScreen = "";
            categoriesWiseProductsList_Home = new ArrayList<HomeProductsData>();
            categoriesWiseProductsList_HomeTemp = new ArrayList<HomeProductsData>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            setContentView(R.layout.screen_all_products_list);

            Utility.setDimensions(this);
            overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
            //RelativeLayout relative = (RelativeLayout) findViewById(R.id.listsRLID);
            // relative.setBackgroundColor(getResources().getColor(R.color.white));

            progressWheel_CENTER = (ProgressWheel) findViewById(R.id.progress_wheel1);
            progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
            progressWheel_CENTER.setRimColor(Color.LTGRAY);

            loadingMoreRL = (RelativeLayout) findViewById(R.id.loadingMoreRLID);
            loadingMoreRL.setVisibility(View.GONE);

            noDataTV = (TextView) findViewById(R.id.noDataFoundTVID);
            categoriesListRV = (GridView) findViewById(R.id.gridViewID);
            categoriesListRV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent sportIntent = new Intent(ProductsListActivity.this, ProductDetailsScreenActivity.class);
                    sportIntent.putExtra("PRODUCT_FROM_SCREEN", "GRID_PRODUCTS");
                    sportIntent.putExtra("PRODUCT_ID", categoriesWiseProductsList_Home.get(position).getproduct_id());
                    sportIntent.putExtra("PRODUCT_NAME", categoriesWiseProductsList_Home.get(position).getproduct_name());
                    sportIntent.putExtra("PRODUCT_PRICE", categoriesWiseProductsList_Home.get(position).getprice());
                    sportIntent.putExtra("PRODUCT_REGULAR_PRICE", categoriesWiseProductsList_Home.get(position).getregular_price());
                    sportIntent.putExtra("PRODUCT_IMAGE_URL", categoriesWiseProductsList_Home.get(position).getimageUrl());
                    sportIntent.putExtra("PRODUCT_VENDOR_NAME", categoriesWiseProductsList_Home.get(position).getvendor_name());
                    sportIntent.putExtra("PRODUCT_DESCRIPTION", categoriesWiseProductsList_Home.get(position).getvendor_description());
                    sportIntent.putExtra("VENDOR_DESCRIPTION", categoriesWiseProductsList_Home.get(position).getvendor_description());
                    startActivity(sportIntent);
                }
            });

            categoriesListRV.setOnScrollListener(this);

            boolean isNetAvailable = Utility.isOnline(ProductsListActivity.this);
            if (isNetAvailable) {
                if (getIntent() != null) {
                    Bundle b = getIntent().getExtras();
                    if (b != null) {
                        String productsURL = null;
                        fromScreen = b.getString("FROM_HOME");
                        if (fromScreen != null && fromScreen.equalsIgnoreCase("HOME")) {
                            HOME_KEY = b.getString("HOME_KEY");
                            cat_name = b.getString("CAT_NAME");
                            setupNavigation("" + cat_name);
                            productsURL = AppConstants.HOME_PRODUCTS_VIEW_ALL_URL + "" + HOME_KEY;
                            loadProductsListData(productsURL, productsCount, limit);
                        } else if (fromScreen != null && fromScreen.equalsIgnoreCase("SEARCH")) {
                            SEARCH_KEYWORD= b.getString("SEARCH_KEYWORD");
                            setupNavigation("Search Results");
                            productsURL = AppConstants.SEARCH_RESULTS_URL + "" + SEARCH_KEYWORD;
                            loadProductsListData(productsURL, productsCount, limit);
                        } else {
                            cat_id = b.getString("CAT_ID");
                            sub_cat_id = b.getString("SUB_CAT_ID");
                            cat_name = b.getString("CAT_NAME");
                            sub_cat_name = b.getString("SUB_CAT_NAME");
                            setupNavigation("" + cat_name);
                            if (cat_id != null && !cat_id.isEmpty()) {
                                if (sub_cat_id != null && !sub_cat_id.isEmpty() && sub_cat_id.trim().length() > 0) {
                                    productsURL = AppConstants.CATEGORIES_WISE_PRODUCTS_LIST_URL + "sub_category_id=" + sub_cat_id;
                                } else {
                                    productsURL = AppConstants.CATEGORIES_WISE_PRODUCTS_LIST_URL + "cat_id=" + cat_id;
                                }
                            }
                            loadProductsListData(productsURL, productsCount, limit);
                        }
                    }
                }
            } else {
                Utility.showCustomToast("Please connect your Internet!", ProductsListActivity.this);
                finish();
            }
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
                if (response != null) {
                    try {
                        System.out.println(response);
                        JSONArray jsonResponseArray = new JSONArray();
                        try {

                            if (fromScreen != null && fromScreen.equalsIgnoreCase("HOME")) {
                                if (response.contains("" + HOME_KEY) && response.contains("product_id")) {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    jsonResponseArray = jsonResponse.getJSONArray("" + HOME_KEY);
                                }
                            } else {
                                if (response.contains("product_id")){
                                    jsonResponseArray = new JSONArray(response);
                                }
                            }

                            /****** PRODUCTS LIST ******* Process each JSON node ************/
                            for (int i = 0; i < jsonResponseArray.length(); i++) {
                                /****** Get Object for each JSON node.***********/
                                JSONObject jsonChildNode = jsonResponseArray.getJSONObject(i);

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
                                categoriesWiseProductsList_Home.add(new HomeProductsData(product_id, product_name, price, regular_price, imageUrl, vendor_name, vendor_description, rating, product_desc));
                            }

                            productsCount = productsCount + categoriesWiseProductsList_Home.size();
                            String OutputData = "" + categoriesWiseProductsList_Home.size();
                            Log.i("HARI--->", "JSON parse Item Count = " + OutputData);

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    if (categoriesWiseProductsList_Home.size() > 0){
                                        if (fetchDirection.equalsIgnoreCase(fetchDirectionUP)) {
                                            int position = categoriesWiseProductsList_HomeTemp.size();
                                            categoriesWiseProductsList_HomeTemp.addAll(categoriesWiseProductsList_Home);
                                            categoriesWiseProductsList_Home = categoriesWiseProductsList_HomeTemp;

                                            Set<HomeProductsData> hs = new HashSet<>();
                                            hs.addAll(categoriesWiseProductsList_Home);
                                            categoriesWiseProductsList_Home.clear();
                                            categoriesWiseProductsList_Home.addAll(hs);

                                            mHomeAdapter = new GridViewHomeAdapter(ProductsListActivity.this, categoriesWiseProductsList_Home);
                                            categoriesListRV.setAdapter(mHomeAdapter);
                                            categoriesListRV.setScrollingCacheEnabled(false);
                                            categoriesListRV.setVisibility(View.VISIBLE);
                                            if (categoriesWiseProductsList_Home.size() > 0 && mvisibleItemCount != -1) {
                                                categoriesListRV.setSelection(position - mvisibleItemCount + 2);
                                            } else {
                                                categoriesListRV.setSelection(position);
                                            }
                                        }

                                        if (mHomeAdapter == null) {
                                            Set<HomeProductsData> hs = new HashSet<>();
                                            hs.addAll(categoriesWiseProductsList_Home);
                                            categoriesWiseProductsList_Home.clear();
                                            categoriesWiseProductsList_Home.addAll(hs);
                                            mHomeAdapter = new GridViewHomeAdapter(ProductsListActivity.this, categoriesWiseProductsList_Home);
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
                            }, 300);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            if (categoriesWiseProductsList_Home.size() == 0){
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
                        if (categoriesWiseProductsList_Home.size() == 0){
                            categoriesListRV.setVisibility(View.GONE);
                            noDataTV.setVisibility(View.VISIBLE);
                            loadingMoreRL.setVisibility(View.GONE);
                            progressWheel_CENTER.setVisibility(View.GONE);
                        }
                        categoriesListRV.setVisibility(View.GONE);
                        noDataTV.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (categoriesWiseProductsList_Home.size() == 0){
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
        titleTV.setText("" + Html.fromHtml(_title));

        TextView subTitleTV = (TextView) findViewById(R.id.titleSubTVID);
        subTitleTV.setVisibility(View.GONE);
        if (sub_cat_name != null && !sub_cat_name.isEmpty() && sub_cat_name.trim().length() > 2) {
            subTitleTV.setVisibility(View.VISIBLE);
            subTitleTV.setText("" + Html.fromHtml(sub_cat_name));
        }

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
                startActivity(new Intent(ProductsListActivity.this, CartScreenActivity.class));
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
        if (categoriesWiseProductsList_Home.size() == totalItemCount) {
            loadingMoreRL.setVisibility(View.VISIBLE);
            fetchDirection = DirectiontoFetch;
            categoriesWiseProductsList_HomeTemp = categoriesWiseProductsList_Home;
            if ((productsCount - 20) >= 0) {
                if (Utility.isOnline(ProductsListActivity.this)) {
                    //GrabItNowService.getGrabItNowService().sendSearchProductsRequest(this, catID, location,keyword, productsCount, 10);
                    if (fromScreen != null && fromScreen.equalsIgnoreCase("HOME")) {
                        String productsURL = AppConstants.HOME_PRODUCTS_VIEW_ALL_URL + "" + HOME_KEY;
                        loadProductsListData(productsURL, productsCount, 10);
                    } else if (fromScreen != null && fromScreen.equalsIgnoreCase("SEARCH")) {
                        String productsURL = AppConstants.SEARCH_RESULTS_URL + "" + SEARCH_KEYWORD;
                        loadProductsListData(productsURL, productsCount, 10);
                    } else {
                        String productsURL = "";
                        if (cat_id != null && !cat_id.isEmpty()) {
                            if (sub_cat_id != null && !sub_cat_id.isEmpty() && sub_cat_id.trim().length() > 0) {
                                productsURL = AppConstants.CATEGORIES_WISE_PRODUCTS_LIST_URL + "sub_category_id=" + sub_cat_id;
                            } else {
                                productsURL = AppConstants.CATEGORIES_WISE_PRODUCTS_LIST_URL + "cat_id=" + cat_id;
                            }
                        }
                        loadProductsListData(productsURL, productsCount, 10);
                    }
                } else {
                    Utility.showCustomToast("No Internet available!", ProductsListActivity.this);
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