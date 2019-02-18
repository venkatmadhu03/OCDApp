package com.ourcitydeals.ctrl;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ourcitydeals.ctrl.adapters.HomeProductsAdapter;
import com.ourcitydeals.ctrl.model.HomeProductsAllData;
import com.ourcitydeals.ctrl.model.HomeProductsData;
import com.ourcitydeals.ctrl.model.LoginDetails;
import com.ourcitydeals.ctrl.ownLibs.ChildAnimationExample;
import com.ourcitydeals.ctrl.ownLibs.SliderLayout;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.AppDataBaseHelper;
import com.ourcitydeals.ctrl.utilities.MySharedPreference;
import com.ourcitydeals.ctrl.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener {
    ArrayList<HomeProductsData> homeProductsDealsList = new ArrayList<HomeProductsData>();
    ArrayList<HomeProductsData> homeProductsMobilesList = new ArrayList<HomeProductsData>();
    ArrayList<HomeProductsData> homeProductsFoodsList = new ArrayList<HomeProductsData>();
    ArrayList<HomeProductsData> homeProductsFurnituresList = new ArrayList<HomeProductsData>();
    ArrayList<HomeProductsData> homeProductsBeautyList = new ArrayList<HomeProductsData>();
    ArrayList<HomeProductsData> homeProductsFashionList = new ArrayList<HomeProductsData>();
    ArrayList<HomeProductsData> homeProductsLatestList = new ArrayList<HomeProductsData>();
    ArrayList<HomeProductsData> homeProductsRecommendList = new ArrayList<HomeProductsData>();
    //ArrayList<HomeProductsData> homeProductsHomesList = new ArrayList<HomeProductsData>();
    ArrayList<HomeProductsAllData> homeProductsHomeAll = new ArrayList<HomeProductsAllData>();

    RecyclerView dealsListRV, latestProductsRV, mobilesRV, recommendedRV, foodProductsRV, furnitureProductsRV, beautyProductsRV, fashionProductsRV; // homeNeedsRV
    LinearLayout progressLL, dealsLL, latestLL, mobilesLL, recommendedLL, foodLL, furnituresLL, beautyLL, fashionLL; //homeNeedsLL
    ScrollView mainScrollView;
    TextView searchBarID;

    int scrollY;

    SliderLayout mDemoSlider;

    TextView viewAllDealsTV, viewAllFurnitureTV, viewAllFashionTV, viewAllLatestsTV;
    TextView viewAllmobilesTV, viewAllFoodTV, viewAllBeautyTV, viewAllrecommendedTV;

    static Button notifCount;
    static int mNotifCount = 5;

    RelativeLayout searchByCategoryRL;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_main);

        if (getIntent() != null && getIntent().getBooleanExtra("EXIT", false)) {
            Intent bookingDoneIntent = new Intent(HomeActivity.this, HomeActivity.class);
            startActivity(bookingDoneIntent);
            finish();
        } else {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//                    .detectAll()
//                    .penaltyLog()
//                    .build();
//            StrictMode.setThreadPolicy(policy);
            StrictMode.ThreadPolicy old = StrictMode.getThreadPolicy();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder(old)
                    .permitDiskWrites()
                    .build());
            StrictMode.setThreadPolicy(old);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            homeProductsLoadingHereByHari();

            createNewSlideByHari();

            viewAllActions();
        }
    }

    private void viewAllActions() {
        viewAllDealsTV = (TextView)findViewById(R.id.viewAllDealsTVID);
        viewAllDealsTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
                sportIntent.putExtra("HOME_KEY", "deals_products");
                sportIntent.putExtra("CAT_NAME", "ALL DEALS");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });

        viewAllmobilesTV = (TextView)findViewById(R.id.viewAllmobilesTVID);
        viewAllmobilesTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
                sportIntent.putExtra("HOME_KEY", "mobiles_products");
                sportIntent.putExtra("FROM_HOME", "HOME");
                sportIntent.putExtra("CAT_NAME", "ALL MOBILES");
                startActivity(sportIntent);
            }
        });

        viewAllFoodTV = (TextView)findViewById(R.id.viewAllFoodTVID);
        viewAllFoodTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
                sportIntent.putExtra("HOME_KEY", "food_products");
                sportIntent.putExtra("CAT_NAME", "ALL FOOD");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });

        viewAllFurnitureTV = (TextView)findViewById(R.id.viewAllFurnitureTVID);
        viewAllFurnitureTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
                sportIntent.putExtra("HOME_KEY", "furniture_products");
                sportIntent.putExtra("CAT_NAME", "ALL FURNITURE");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });

        viewAllBeautyTV = (TextView)findViewById(R.id.viewAllBeautyTVID);
        viewAllBeautyTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
                sportIntent.putExtra("HOME_KEY", "beauty_products");
                sportIntent.putExtra("CAT_NAME", "ALL BEAUTY");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });

        viewAllFashionTV = (TextView)findViewById(R.id.viewAllFashionTVID);
        viewAllFashionTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
                sportIntent.putExtra("HOME_KEY", "fashion_products");
                sportIntent.putExtra("CAT_NAME", "ALL FASHION");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });

        viewAllLatestsTV = (TextView)findViewById(R.id.viewAllLatestsTVID);
        viewAllLatestsTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
                sportIntent.putExtra("HOME_KEY", "latest_products");
                sportIntent.putExtra("CAT_NAME", "ALL LATEST");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });

        viewAllrecommendedTV = (TextView)findViewById(R.id.viewAllrecommendedTVID);
        viewAllrecommendedTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
                sportIntent.putExtra("HOME_KEY", "recommend_products");
                sportIntent.putExtra("CAT_NAME", "ALL RECOMMEDED");
                sportIntent.putExtra("FROM_HOME", "HOME");
                startActivity(sportIntent);
            }
        });
    }

    private void createNewSlideByHari() {
        searchByCategoryRL  = (RelativeLayout) findViewById(R.id.searchByCategoryRLID);
        searchByCategoryRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AllCategorriesActivity.class));
            }
        });

        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        HashMap<String, Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("1", R.drawable.banner1);
        file_maps.put("2", R.drawable.banner2);
        file_maps.put("3", R.drawable.banner3);
        file_maps.put("4", R.drawable.banner4);
       // file_maps.put("5", R.drawable.banner5);
       // file_maps.put("6", R.drawable.banner6);
        //file_maps.put("6", R.drawable.banner7);
        //file_maps.put("7", R.drawable.banner8);

        for (String name : file_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    //  .description(name)
                    .image(file_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new ChildAnimationExample());
        mDemoSlider.setDuration(3000);
        //mDemoSlider.setCustomIndicator(new CircleIndicator(HomeActivity.this));
        mDemoSlider.addOnPageChangeListener(this);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
    }

    private void homeProductsLoadingHereByHari() {
        dealsListRV = (RecyclerView) findViewById(R.id.dealsListRecycleRVID);
        dealsListRV.setHasFixedSize(true);
        LinearLayoutManager dealslm = new LinearLayoutManager(HomeActivity.this);
        dealslm.setOrientation(LinearLayoutManager.HORIZONTAL);
        dealsListRV.setLayoutManager(dealslm);

        latestProductsRV = (RecyclerView) findViewById(R.id.latestProductsRVID);
        latestProductsRV.setHasFixedSize(true);
        LinearLayoutManager latestLM = new LinearLayoutManager(HomeActivity.this);
        latestLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        latestProductsRV.setLayoutManager(latestLM);

        mobilesRV = (RecyclerView) findViewById(R.id.mobilesProductsRVID);
        mobilesRV.setHasFixedSize(true);
        LinearLayoutManager mobilesKM = new LinearLayoutManager(HomeActivity.this);
        mobilesKM.setOrientation(LinearLayoutManager.HORIZONTAL);
        mobilesRV.setLayoutManager(mobilesKM);

        recommendedRV = (RecyclerView) findViewById(R.id.recommendedProductsRVID);
        recommendedRV.setHasFixedSize(true);
        LinearLayoutManager recommendedLM = new LinearLayoutManager(HomeActivity.this);
        recommendedLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        recommendedRV.setLayoutManager(recommendedLM);

        foodProductsRV = (RecyclerView) findViewById(R.id.foodProductsRVID);
        foodProductsRV.setHasFixedSize(true);
        LinearLayoutManager foodProductsLM = new LinearLayoutManager(HomeActivity.this);
        foodProductsLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        foodProductsRV.setLayoutManager(foodProductsLM);

        fashionProductsRV = (RecyclerView) findViewById(R.id.fashionProductsRVID);
        fashionProductsRV.setHasFixedSize(true);
        LinearLayoutManager fashionProductsLM = new LinearLayoutManager(HomeActivity.this);
        fashionProductsLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        fashionProductsRV.setLayoutManager(fashionProductsLM);

        beautyProductsRV = (RecyclerView) findViewById(R.id.beautyProductsRVID);
        beautyProductsRV.setHasFixedSize(true);
        LinearLayoutManager beautyProductsLM = new LinearLayoutManager(HomeActivity.this);
        beautyProductsLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        beautyProductsRV.setLayoutManager(beautyProductsLM);

        furnitureProductsRV = (RecyclerView) findViewById(R.id.furnitureProductsRVID);
        furnitureProductsRV.setHasFixedSize(true);
        LinearLayoutManager furnitureProductsLM = new LinearLayoutManager(HomeActivity.this);
        furnitureProductsLM.setOrientation(LinearLayoutManager.HORIZONTAL);
        furnitureProductsRV.setLayoutManager(furnitureProductsLM);

        progressLL = (LinearLayout) findViewById(R.id.progress);
        dealsLL = (LinearLayout) findViewById(R.id.dealsLLID);
        latestLL = (LinearLayout) findViewById(R.id.latestLLID);
        mobilesLL = (LinearLayout) findViewById(R.id.mobilesLLID);
        //homeNeedsLL = (LinearLayout)findViewById(R.id.homeNeedsLLID);
        recommendedLL = (LinearLayout) findViewById(R.id.recommendedLLID);
        foodLL = (LinearLayout) findViewById(R.id.foodLLID);
        furnituresLL = (LinearLayout) findViewById(R.id.furnitureLLID);
        fashionLL = (LinearLayout) findViewById(R.id.fashionLLID);
        beautyLL = (LinearLayout) findViewById(R.id.beautyLLID);

        searchBarID = (TextView) findViewById(R.id.searchBarID);

        mainScrollView = (ScrollView) findViewById(R.id.mainViewScrollSVID);
        mainScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                scrollY = mainScrollView.getScrollY(); //for verticalScrollView
                invalidateOptionsMenu();
                //DO SOMETHING WITH THE SCROLL COORDINATES
            }
        });

        if (Utility.isOnline(HomeActivity.this)) {
            ifNetisAvilableGetAllHomeProducts(AppConstants.HOME_PRODUCTS_URL);
        } else {
            Utility.showCustomToast("Please connect your internet!", HomeActivity.this);
            finish();
        }

        searchBarID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     * the menu layout has the 'add/new' menu item
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater1 = getMenuInflater();
        menuInflater1.inflate(R.menu.menu_scrolling, menu);
        menu.findItem(R.id.action_search).setVisible(false);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        scrollY = mainScrollView.getScrollY(); //for verticalScrollView
        if (scrollY > 40) {
            menu.findItem(R.id.action_search).setVisible(true);

        } else {
            menu.findItem(R.id.action_search).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1112) {
            if (data != null) {
                String userEmailStr = data.getStringExtra("USER_EMAIL");
                if (userEmailStr != null && !userEmailStr.equalsIgnoreCase("Not_Login")) {
                    MySharedPreference.setPreference(HomeActivity.this, "FROM_SCREEN_USER", "MY_ACCOUNT");
                    startActivity(new Intent(HomeActivity.this, ShippingAddressScreenActivity.class));
                }
            }
        }
    }

    /**
     * react to the user tapping/selecting an options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_my_account:
                AppDataBaseHelper dbHelper = new AppDataBaseHelper(HomeActivity.this);
                LoginDetails loginDetails = dbHelper.getLoginDetails();
                if (loginDetails != null && loginDetails.getuserEmail() != null) {
                    MySharedPreference.setPreference(HomeActivity.this, "FROM_SCREEN_USER", "MY_ACCOUNT");
                    startActivity(new Intent(HomeActivity.this, ShippingAddressScreenActivity.class));
                } else {
                    Intent resultss = new Intent(HomeActivity.this, ActivityLoginPage.class);
                    startActivityForResult(resultss, 1112);
                }
                return true;
            case R.id.action_cart:
                startActivity(new Intent(HomeActivity.this, CartScreenActivity.class));
                return true;
            case R.id.action_my_wishlist:
                startActivity(new Intent(HomeActivity.this, WishListScreenActivity.class));
                return true;
            case R.id.action_search:
                Intent i = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ifNetisAvilableGetAllHomeProducts(String homeProductsURL) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(homeProductsURL, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        System.out.println(response);
                        String OutputData = "";
                        Log.d("HomeProducts", "onSuccess: "+response);
                        JSONObject jsonResponse;

                        try {
                            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                            jsonResponse = new JSONObject(response);

                            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                            /*******  Returns null otherwise.  *******/
                            JSONArray deals_productsJson = jsonResponse.getJSONArray("deals_products");
                          //  JSONArray latest_productsJson = jsonResponse.getJSONArray("latest_products");
                          //  JSONArray recommend_productsJson = jsonResponse.getJSONArray("recommend_products");
                          //  JSONArray mobiles_productsJson = jsonResponse.getJSONArray("mobiles_products");
                          //  JSONArray food_productsJson = jsonResponse.getJSONArray("food_products");
                         //   JSONArray beauty_productsJson = jsonResponse.getJSONArray("beauty_products");
                         //   JSONArray fashion_productsJson = jsonResponse.getJSONArray("fashion_products");
                        //    JSONArray furniture_productsJson = jsonResponse.getJSONArray("furniture_products");

                            /****** FASHION PRODUCTS ******* Process each JSON node ************/
//                            if (fashion_productsJson.length() > 0) {
//                                homeProductsFashionList.clear();
//                                homeProductsFashionList = null;
//                                homeProductsFashionList = new ArrayList<HomeProductsData>();
//
//                                for (int i = 0; i < fashion_productsJson.length(); i++) {
//                                    /****** Get Object for each JSON node.***********/
//                                    JSONObject jsonChildNode = fashion_productsJson.getJSONObject(i);
//
//                                    /******* Fetch node values **********/
//                                    String product_id = jsonChildNode.optString("product_id");
//                                    String product_name = jsonChildNode.optString("product_name");
//                                    String price = jsonChildNode.optString("price");
//                                    String regular_price = jsonChildNode.optString("regular_price");
//                                    String imageUrl = jsonChildNode.optString("image");
//                                    String vendor_name = jsonChildNode.optString("vendor_name");
//                                    //String vendor_description = jsonChildNode.optString("vendor_description");
//                                    String rating = jsonChildNode.optString("rating");
//                                    //String product_desc = jsonChildNode.optString("product_desc");
//                                    homeProductsFashionList.add(new HomeProductsData(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                                }
//
//                                OutputData = "" + homeProductsFashionList.size();
//                                Log.i("JSON parse Item Count", OutputData);
//                            }
//
//                            /****** BEAUTY PRODUCTS ******* Process each JSON node ************/
//                            if (beauty_productsJson.length() > 0) {
//                                homeProductsBeautyList.clear();
//                                homeProductsBeautyList = null;
//                                homeProductsBeautyList = new ArrayList<HomeProductsData>();
//
//                                for (int i = 0; i < beauty_productsJson.length(); i++) {
//                                    /****** Get Object for each JSON node.***********/
//                                    JSONObject jsonChildNode = beauty_productsJson.getJSONObject(i);
//
//                                    /******* Fetch node values **********/
//                                    String product_id = jsonChildNode.optString("product_id");
//                                    String product_name = jsonChildNode.optString("product_name");
//                                    String price = jsonChildNode.optString("price");
//                                    String regular_price = jsonChildNode.optString("regular_price");
//                                    String imageUrl = jsonChildNode.optString("image");
//                                    String vendor_name = jsonChildNode.optString("vendor_name");
//                                    //String vendor_description = jsonChildNode.optString("vendor_description");
//                                    String rating = jsonChildNode.optString("rating");
//                                    //String product_desc = jsonChildNode.optString("product_desc");
//                                    homeProductsBeautyList.add(new HomeProductsData(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                                }
//
//                                OutputData = "" + homeProductsBeautyList.size();
//                                Log.i("JSON parse Item Count", OutputData);
//                            }
//
//                            /****** FURNITURE PRODUCTS ******* Process each JSON node ************/
//                            if (furniture_productsJson.length() > 0) {
//                                homeProductsFurnituresList.clear();
//                                homeProductsFurnituresList = null;
//                                homeProductsFurnituresList = new ArrayList<HomeProductsData>();
//
//                                for (int i = 0; i < furniture_productsJson.length(); i++) {
//                                    /****** Get Object for each JSON node.***********/
//                                    JSONObject jsonChildNode = furniture_productsJson.getJSONObject(i);
//
//                                    /******* Fetch node values **********/
//                                    String product_id = jsonChildNode.optString("product_id");
//                                    String product_name = jsonChildNode.optString("product_name");
//                                    String price = jsonChildNode.optString("price");
//                                    String regular_price = jsonChildNode.optString("regular_price");
//                                    String imageUrl = jsonChildNode.optString("image");
//                                    String vendor_name = jsonChildNode.optString("vendor_name");
//                                    //String vendor_description = jsonChildNode.optString("vendor_description");
//                                    String rating = jsonChildNode.optString("rating");
//                                    //String product_desc = jsonChildNode.optString("product_desc");
//                                    homeProductsFurnituresList.add(new HomeProductsData(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                                }
//
//                                OutputData = "" + homeProductsFurnituresList.size();
//                                Log.i("JSON parse Item Count", OutputData);
//                            }

                            /****** DEALS PRODUCTS ******* Process each JSON node ************/
                            if (deals_productsJson.length() > 0) {
                                homeProductsDealsList.clear();
                                homeProductsDealsList = null;
                                homeProductsDealsList = new ArrayList<HomeProductsData>();

                                for (int i = 0; i < deals_productsJson.length(); i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = deals_productsJson.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    String product_id = jsonChildNode.optString("product_id");
                                    String product_name = jsonChildNode.optString("product_name");
                                    String price = jsonChildNode.optString("price");
                                    String regular_price = jsonChildNode.optString("regular_price");
                                    String imageUrl = jsonChildNode.optString("image");
                                    String vendor_name = jsonChildNode.optString("vendor_name");
                                    //String vendor_description = jsonChildNode.optString("vendor_description");
                                    String rating = jsonChildNode.optString("rating");
                                    //String product_desc = jsonChildNode.optString("product_desc");
                                    homeProductsDealsList.add(new HomeProductsData(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
                                }

                                OutputData = "" + homeProductsDealsList.size();
                                Log.i("JSON parse Item Count", OutputData);
                            }

                            /****** LATEST PRODUCTS ******* Process each JSON node ************/
//                            for (int i = 0; i < latest_productsJson.length(); i++) {
//                                /****** Get Object for each JSON node.***********/
//                                JSONObject jsonChildNode = latest_productsJson.getJSONObject(i);
//
//                                /******* Fetch node values **********/
//                                String product_id = jsonChildNode.optString("product_id");
//                                String product_name = jsonChildNode.optString("product_name");
//                                String price = jsonChildNode.optString("price");
//                                String regular_price = jsonChildNode.optString("regular_price");
//                                String imageUrl = jsonChildNode.optString("image");
//                                String vendor_name = jsonChildNode.optString("vendor_name");
//                                //String vendor_description = jsonChildNode.optString("vendor_description");
//                                String rating = jsonChildNode.optString("rating");
//                                //String product_desc = jsonChildNode.optString("product_desc");
//                                homeProductsLatestList.add(new HomeProductsData(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                            }
//
//                            OutputData = "" + homeProductsLatestList.size();
//                            Log.i("JSON parse Item Count", OutputData);
//
//                            /****** RECOMMENDED PRODUCTS ******* Process each JSON node ************/
//                            for (int i = 0; i < recommend_productsJson.length(); i++) {
//                                /****** Get Object for each JSON node.***********/
//                                JSONObject jsonChildNode = recommend_productsJson.getJSONObject(i);
//
//                                /******* Fetch node values **********/
//                                String product_id = jsonChildNode.optString("product_id");
//                                String product_name = jsonChildNode.optString("product_name");
//                                String price = jsonChildNode.optString("price");
//                                String regular_price = jsonChildNode.optString("regular_price");
//                                String imageUrl = jsonChildNode.optString("image");
//                                String vendor_name = jsonChildNode.optString("vendor_name");
//                                //String vendor_description = jsonChildNode.optString("vendor_description");
//                                String rating = jsonChildNode.optString("rating");
//                                //String product_desc = jsonChildNode.optString("product_desc");
//                                homeProductsRecommendList.add(new HomeProductsData(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                            }
//
//                            OutputData = "" + homeProductsRecommendList.size();
//                            Log.i("JSON parse Item Count", OutputData);
//
//                            /****** MOBILES PRODUCTS ******* Process each JSON node ************/
//                            for (int i = 0; i < mobiles_productsJson.length(); i++) {
//                                /****** Get Object for each JSON node.***********/
//                                JSONObject jsonChildNode = mobiles_productsJson.getJSONObject(i);
//
//                                /******* Fetch node values **********/
//                                String product_id = jsonChildNode.optString("product_id");
//                                String product_name = jsonChildNode.optString("product_name");
//                                String price = jsonChildNode.optString("price");
//                                String regular_price = jsonChildNode.optString("regular_price");
//                                String imageUrl = jsonChildNode.optString("image");
//                                String vendor_name = jsonChildNode.optString("vendor_name");
//                                //String vendor_description = jsonChildNode.optString("vendor_description");
//                                String rating = jsonChildNode.optString("rating");
//                                //String product_desc = jsonChildNode.optString("product_desc");
//                                homeProductsMobilesList.add(new HomeProductsData(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                            }
//
//                            OutputData = "" + homeProductsMobilesList.size();
//                            Log.i("JSON parse Item Count", OutputData);
//
//                            /****** FOOD PRODUCTS ******* Process each JSON node ************/
//                            for (int i = 0; i < food_productsJson.length(); i++) {
//                                //****** Get Object for each JSON node.***********//*
//                                JSONObject jsonChildNode = food_productsJson.getJSONObject(i);
//
//                                //******* Fetch node values **********//*
//                                String product_id = jsonChildNode.optString("product_id");
//                                String product_name = jsonChildNode.optString("product_name");
//                                String price = jsonChildNode.optString("price");
//                                String regular_price = jsonChildNode.optString("regular_price");
//                                String imageUrl = jsonChildNode.optString("image");
//                                String vendor_name = jsonChildNode.optString("vendor_name");
//                                //String vendor_description = jsonChildNode.optString("vendor_description");
//                                String rating = jsonChildNode.optString("rating");
//                                //String product_desc = jsonChildNode.optString("product_desc");
//                                homeProductsFoodsList.add(new HomeProductsData(product_id, product_name, price, regular_price, imageUrl, vendor_name, "", rating, ""));
//                            }
//
//                            OutputData = "" + homeProductsFoodsList.size();
//                            Log.i("JSON parse Item Count", OutputData);

                           homeProductsHomeAll.add(new HomeProductsAllData(homeProductsDealsList, homeProductsLatestList, homeProductsRecommendList, homeProductsMobilesList, homeProductsFoodsList));

                            /************ Show Output on screen/activity **********/
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    if (homeProductsDealsList.size() > 0) {
                                        HomeProductsAdapter adapter1 = new HomeProductsAdapter(HomeActivity.this, homeProductsDealsList);
                                        dealsListRV.setAdapter(adapter1);
                                        dealsLL.setVisibility(View.VISIBLE);
                                        adapter1.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsScreenActivity.class);
                                                sportIntent.putExtra("PRODUCT_ID", homeProductsDealsList.get(position).getproduct_id());
                                                sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                sportIntent.putExtra("PRODUCT_NAME", homeProductsDealsList.get(position).getproduct_name());
                                                sportIntent.putExtra("PRODUCT_PRICE", homeProductsDealsList.get(position).getprice());
                                                sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsDealsList.get(position).getregular_price());
                                                sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsDealsList.get(position).getimageUrl());
                                                sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsDealsList.get(position).getvendor_name());
                                                sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsDealsList.get(position).get_product_desc());
                                                sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsDealsList.get(position).getvendor_description());
                                                startActivity(sportIntent);
                                            }
                                        });
                                    }

                                    if (homeProductsFashionList.size() > 0) {
                                        HomeProductsAdapter adapter1 = new HomeProductsAdapter(HomeActivity.this, homeProductsFashionList);
                                        fashionProductsRV.setAdapter(adapter1);
                                        fashionLL.setVisibility(View.VISIBLE);
                                        adapter1.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsScreenActivity.class);
                                                sportIntent.putExtra("PRODUCT_ID", homeProductsFashionList.get(position).getproduct_id());
                                                sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                sportIntent.putExtra("PRODUCT_NAME", homeProductsFashionList.get(position).getproduct_name());
                                                sportIntent.putExtra("PRODUCT_PRICE", homeProductsFashionList.get(position).getprice());
                                                sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsFashionList.get(position).getregular_price());
                                                sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsFashionList.get(position).getimageUrl());
                                                sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsFashionList.get(position).getvendor_name());
                                                sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsFashionList.get(position).get_product_desc());
                                                sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsFashionList.get(position).getvendor_description());
                                                startActivity(sportIntent);
                                            }
                                        });
                                    }

                                    if (homeProductsBeautyList.size() > 0) {
                                        HomeProductsAdapter adapter1 = new HomeProductsAdapter(HomeActivity.this, homeProductsBeautyList);
                                        beautyProductsRV.setAdapter(adapter1);
                                        beautyLL.setVisibility(View.VISIBLE);
                                        adapter1.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsScreenActivity.class);
                                                sportIntent.putExtra("PRODUCT_ID", homeProductsBeautyList.get(position).getproduct_id());
                                                sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                sportIntent.putExtra("PRODUCT_NAME", homeProductsBeautyList.get(position).getproduct_name());
                                                sportIntent.putExtra("PRODUCT_PRICE", homeProductsBeautyList.get(position).getprice());
                                                sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsBeautyList.get(position).getregular_price());
                                                sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsBeautyList.get(position).getimageUrl());
                                                sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsBeautyList.get(position).getvendor_name());
                                                sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsBeautyList.get(position).get_product_desc());
                                                sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsBeautyList.get(position).getvendor_description());
                                                startActivity(sportIntent);
                                            }
                                        });
                                    }

                                    if (homeProductsFurnituresList.size() > 0) {
                                        HomeProductsAdapter adapter1 = new HomeProductsAdapter(HomeActivity.this, homeProductsFurnituresList);
                                        furnitureProductsRV.setAdapter(adapter1);
                                        furnituresLL.setVisibility(View.VISIBLE);
                                        adapter1.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsScreenActivity.class);
                                                sportIntent.putExtra("PRODUCT_ID", homeProductsFurnituresList.get(position).getproduct_id());
                                                sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                sportIntent.putExtra("PRODUCT_NAME", homeProductsFurnituresList.get(position).getproduct_name());
                                                sportIntent.putExtra("PRODUCT_PRICE", homeProductsFurnituresList.get(position).getprice());
                                                sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsFurnituresList.get(position).getregular_price());
                                                sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsFurnituresList.get(position).getimageUrl());
                                                sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsFurnituresList.get(position).getvendor_name());
                                                sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsFurnituresList.get(position).get_product_desc());
                                                sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsFurnituresList.get(position).getvendor_description());
                                                startActivity(sportIntent);
                                            }
                                        });
                                    }

                                    if (homeProductsLatestList.size() > 0) {
                                        HomeProductsAdapter adapter2 = new HomeProductsAdapter(HomeActivity.this, homeProductsLatestList);
                                        latestProductsRV.setAdapter(adapter2);
                                        latestLL.setVisibility(View.VISIBLE);
                                        adapter2.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsScreenActivity.class);
                                                sportIntent.putExtra("PRODUCT_ID", homeProductsLatestList.get(position).getproduct_id());
                                                sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                sportIntent.putExtra("PRODUCT_NAME", homeProductsLatestList.get(position).getproduct_name());
                                                sportIntent.putExtra("PRODUCT_PRICE", homeProductsLatestList.get(position).getprice());
                                                sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsLatestList.get(position).getregular_price());
                                                sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsLatestList.get(position).getimageUrl());
                                                sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsLatestList.get(position).getvendor_name());
                                                sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsLatestList.get(position).get_product_desc());
                                                sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsLatestList.get(position).getvendor_description());
                                                startActivity(sportIntent);
                                            }
                                        });
                                    }

                                    if (homeProductsMobilesList.size() > 0) {
                                        HomeProductsAdapter adapter3 = new HomeProductsAdapter(HomeActivity.this, homeProductsMobilesList);
                                        mobilesRV.setAdapter(adapter3);
                                        mobilesLL.setVisibility(View.VISIBLE);

                                        adapter3.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsScreenActivity.class);
                                                sportIntent.putExtra("PRODUCT_ID", homeProductsMobilesList.get(position).getproduct_id());
                                                sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                sportIntent.putExtra("PRODUCT_NAME", homeProductsMobilesList.get(position).getproduct_name());
                                                sportIntent.putExtra("PRODUCT_PRICE", homeProductsMobilesList.get(position).getprice());
                                                sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsMobilesList.get(position).getregular_price());
                                                sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsMobilesList.get(position).getimageUrl());
                                                sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsMobilesList.get(position).getvendor_name());
                                                sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsMobilesList.get(position).get_product_desc());
                                                sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsMobilesList.get(position).getvendor_description());
                                                startActivity(sportIntent);
                                            }
                                        });
                                    }

                                    if (homeProductsRecommendList.size() > 0) {
                                        HomeProductsAdapter adapter4 = new HomeProductsAdapter(HomeActivity.this, homeProductsRecommendList);
                                        recommendedRV.setAdapter(adapter4);
                                        recommendedLL.setVisibility(View.VISIBLE);

                                        adapter4.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsScreenActivity.class);
                                                sportIntent.putExtra("PRODUCT_ID", homeProductsRecommendList.get(position).getproduct_id());
                                                sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                sportIntent.putExtra("PRODUCT_NAME", homeProductsRecommendList.get(position).getproduct_name());
                                                sportIntent.putExtra("PRODUCT_PRICE", homeProductsRecommendList.get(position).getprice());
                                                sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsRecommendList.get(position).getregular_price());
                                                sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsRecommendList.get(position).getimageUrl());
                                                sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsRecommendList.get(position).getvendor_name());
                                                sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsRecommendList.get(position).get_product_desc());
                                                sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsRecommendList.get(position).getvendor_description());
                                                startActivity(sportIntent);
                                            }
                                        });
                                    }

                                    if (homeProductsFoodsList.size() > 0) {
                                        HomeProductsAdapter adapter4 = new HomeProductsAdapter(HomeActivity.this, homeProductsFoodsList);
                                        foodProductsRV.setAdapter(adapter4);
                                        foodLL.setVisibility(View.VISIBLE);

                                        adapter4.setOnItemClickListener(new HomeProductsAdapter.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(View view, int position) {
                                                Intent sportIntent = new Intent(HomeActivity.this, ProductDetailsScreenActivity.class);
                                                sportIntent.putExtra("PRODUCT_ID", homeProductsFoodsList.get(position).getproduct_id());
                                                sportIntent.putExtra("PRODUCT_FROM_SCREEN", "HOME_MAIN");
                                                sportIntent.putExtra("PRODUCT_NAME", homeProductsFoodsList.get(position).getproduct_name());
                                                sportIntent.putExtra("PRODUCT_PRICE", homeProductsFoodsList.get(position).getprice());
                                                sportIntent.putExtra("PRODUCT_REGULAR_PRICE", homeProductsFoodsList.get(position).getregular_price());
                                                sportIntent.putExtra("PRODUCT_IMAGE_URL", homeProductsFoodsList.get(position).getimageUrl());
                                                sportIntent.putExtra("PRODUCT_VENDOR_NAME", homeProductsFoodsList.get(position).getvendor_name());
                                                sportIntent.putExtra("PRODUCT_DESCRIPTION", homeProductsFoodsList.get(position).get_product_desc());
                                                sportIntent.putExtra("VENDOR_DESCRIPTION", homeProductsFoodsList.get(position).getvendor_description());
                                                startActivity(sportIntent);
                                            }
                                        });
                                    }
                                    progressLL.setVisibility(View.GONE);
                                }
                            }, 200);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //progressLL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    progressLL.setVisibility(View.GONE);
                    Log.v("HARI-->", "onFailure() : " + content);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            showDialog(12077);
            //super.onBackPressed();
        }
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
                        HomeActivity.this.finish();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_1) {
            Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
            sportIntent.putExtra("CAT_ID", "166");
            sportIntent.putExtra("CAT_NAME", "ELECTRONICS");
            sportIntent.putExtra("SUB_CAT_ID", "");
            sportIntent.putExtra("SUB_CAT_NAME", "");
            sportIntent.putExtra("FROM_HOME", "HOME_MENU");
            startActivity(sportIntent);
        } else if (id == R.id.nav_3) {
            Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
            sportIntent.putExtra("CAT_ID", "258");
            sportIntent.putExtra("CAT_NAME", "FASHION");
            sportIntent.putExtra("SUB_CAT_ID", "");
            sportIntent.putExtra("SUB_CAT_NAME", "");
            sportIntent.putExtra("FROM_HOME", "HOME_MENU");
            startActivity(sportIntent);
        } else if (id == R.id.nav_4) {
            Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
            sportIntent.putExtra("CAT_ID", "110");
            sportIntent.putExtra("CAT_NAME", "WOMEN");
            sportIntent.putExtra("SUB_CAT_ID", "");
            sportIntent.putExtra("SUB_CAT_NAME", "");
            sportIntent.putExtra("FROM_HOME", "HOME_MENU");
            startActivity(sportIntent);
        } else if (id == R.id.nav_5) {
            Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
            sportIntent.putExtra("CAT_ID", "185");
            sportIntent.putExtra("CAT_NAME", "MEN");
            sportIntent.putExtra("SUB_CAT_ID", "");
            sportIntent.putExtra("SUB_CAT_NAME", "");
            sportIntent.putExtra("FROM_HOME", "HOME_MENU");
            startActivity(sportIntent);
        } else if (id == R.id.nav_6) {
            Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
            sportIntent.putExtra("CAT_ID", "192");
            sportIntent.putExtra("CAT_NAME", "BABY & TOYS");
            sportIntent.putExtra("SUB_CAT_ID", "");
            sportIntent.putExtra("SUB_CAT_NAME", "");
            sportIntent.putExtra("FROM_HOME", "HOME_MENU");
            startActivity(sportIntent);
        } else if (id == R.id.nav_7) {
            Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
            sportIntent.putExtra("CAT_ID", "204");
            sportIntent.putExtra("CAT_NAME", "HEALTH &amp; BEAUTY");
            sportIntent.putExtra("SUB_CAT_ID", "");
            sportIntent.putExtra("SUB_CAT_NAME", "");
            sportIntent.putExtra("FROM_HOME", "HOME_MENU");
            startActivity(sportIntent);
        } else if (id == R.id.nav_8) {
            Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
            sportIntent.putExtra("CAT_ID", "212");
            sportIntent.putExtra("CAT_NAME", "FOOD");
            sportIntent.putExtra("SUB_CAT_ID", "");
            sportIntent.putExtra("SUB_CAT_NAME", "");
            sportIntent.putExtra("FROM_HOME", "HOME_MENU");
            startActivity(sportIntent);
        } else if (id == R.id.nav_9) {
            Intent sportIntent = new Intent(HomeActivity.this, ProductsListActivity.class);
            sportIntent.putExtra("CAT_ID", "219");
            sportIntent.putExtra("CAT_NAME", "HOME &amp; FURNITURE");
            sportIntent.putExtra("SUB_CAT_ID", "");
            sportIntent.putExtra("SUB_CAT_NAME", "");
            sportIntent.putExtra("FROM_HOME", "HOME_MENU");
            startActivity(sportIntent);
        } else if (id == R.id.action_my_account1) {
            MySharedPreference.setPreference(HomeActivity.this, "FROM_SCREEN_USER", "MY_ACCOUNT");
            startActivity(new Intent(HomeActivity.this, ActivityLoginPage.class));
        } else if (id == R.id.action_cart1) {
            startActivity(new Intent(HomeActivity.this, CartScreenActivity.class));
        } else if (id == R.id.action_my_wishlist1) {
            startActivity(new Intent(HomeActivity.this, WishListScreenActivity.class));
        } else if (id == R.id.action_terms) {
            startActivity(new Intent(HomeActivity.this, TermsPolicyActivity.class));
        }

        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}