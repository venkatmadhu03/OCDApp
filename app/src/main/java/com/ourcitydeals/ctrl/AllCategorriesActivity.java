package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.ourcitydeals.ctrl.adapters.AllCategoriesListAdapter;
import com.ourcitydeals.ctrl.model.AllCategoriesList;
import com.ourcitydeals.ctrl.ownLibs.ProgressWheel;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by Jashwikha Chakrapu on 5/29/2016.
 */
public class AllCategorriesActivity extends Activity {
    ArrayList<AllCategoriesList> allCategoriesList = new ArrayList<AllCategoriesList>();

    private ProgressWheel progressWheel_CENTER;
    TextView noDataTV;

    AllCategoriesListAdapter mAdapter;
    RecyclerView bidListLVID;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.screen_all_categories);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        Utility.setDimensions(this);
        setupNavigation();

        progressWheel_CENTER = (ProgressWheel) findViewById(R.id.progress_wheel1);
        progressWheel_CENTER.setBarColor(getResources().getColor(R.color.colorPrimary));
        progressWheel_CENTER.setRimColor(Color.LTGRAY);

        noDataTV = (TextView) findViewById(R.id.noDataTVID);
        bidListLVID = (RecyclerView) findViewById(R.id.recycleListRVID);
        bidListLVID.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        bidListLVID.setLayoutManager(mStaggeredGridLayoutManager);

        boolean isNetAvailable = Utility.isOnline(AllCategorriesActivity.this);
        if (isNetAvailable) {
            getAllCategoriesList(AppConstants.ALL_CATEGORIES_URL);
        } else {
            Utility.showCustomToast("Please connect your Internet!", AllCategorriesActivity.this);
        }
    }

    private void getAllCategoriesList(String allCategoriesUrl) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(allCategoriesUrl, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    try {
                        System.out.println(response);

                        try {
                            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                            /*******  Returns null otherwise.  *******/
                            JSONArray jsonMainNode = new JSONArray(response);

                            /*********** Process each JSON Node ************/
                            allCategoriesList = new ArrayList<AllCategoriesList>();
                            for (int i = 0; i < jsonMainNode.length(); i++) {
                                /****** Get Object for each JSON node.***********/
                                JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                /******* Fetch node values **********/
                                String cat_id = jsonChildNode.optString("cat_id");
                                String category_name = jsonChildNode.optString("name");

                                if (category_name!=null && !category_name.isEmpty()){
                                    if (!category_name.equalsIgnoreCase("null")){
                                        allCategoriesList.add(new AllCategoriesList(cat_id,category_name));
                                    }
                                }
                            }

                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    // This method will be executed once the timer is over
                                    if (allCategoriesList.size() > 0) {
                                        mAdapter = new AllCategoriesListAdapter(AllCategorriesActivity.this, allCategoriesList);
                                        bidListLVID.setAdapter(mAdapter);
                                        bidListLVID.setVisibility(View.VISIBLE);
                                        progressWheel_CENTER.setVisibility(View.GONE);
                                        mAdapter.setOnItemClickListener(onItemClickListener);
                                    } else {
                                        bidListLVID.setVisibility(View.GONE);
                                        noDataTV.setVisibility(View.VISIBLE);
                                    }
                                }
                            }, 500);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error, String content) {
                try {
                    Log.w("HARI-->", "onFailure" + content);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
    }

    AllCategoriesListAdapter.OnItemClickListener onItemClickListener = new AllCategoriesListAdapter.OnItemClickListener() {

        @Override
        public void onItemClick(View v, int position) {
            Intent sportIntent = new Intent(AllCategorriesActivity.this, AllSubCategorriesActivity.class);
            sportIntent.putExtra("CAT_ID", allCategoriesList.get(position).getCat_id());
            sportIntent.putExtra("CAT_NAME", allCategoriesList.get(position).getCat_name());
            startActivity(sportIntent);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        titleTV.setText("All Categories");

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
        menuBtn.setVisibility(View.VISIBLE);
        menuBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllCategorriesActivity.this, CartScreenActivity.class));
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