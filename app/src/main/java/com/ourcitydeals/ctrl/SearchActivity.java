package com.ourcitydeals.ctrl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.PopupMenu;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ourcitydeals.ctrl.utilities.AppConstants;
import com.ourcitydeals.ctrl.utilities.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Jashwikha Chakrapu on 6/5/2016.
 */
public class SearchActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private MaterialSearchView searchView;
    ProgressDialog ringProgressDialog;
    ArrayList<String> searchSuggetionsList = new ArrayList<String>();

    MenuItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.search_home_list);
        Utility.setDimensions(this);
        // setupNavigation();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(policy);

        boolean isNetAvailable = Utility.isOnline(SearchActivity.this);
        if (isNetAvailable) {
            try {
                ringProgressDialog = ProgressDialog.show(SearchActivity.this, "Please wait ...", "Loading results...", true);
                ringProgressDialog.setCancelable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            getSearchSuggitions(AppConstants.SEARCH_SUGGETIONS_URL);
        } else {
            Utility.showCustomToast("Please connect your Internet!", SearchActivity.this);
        }
    }

    private void getSearchSuggitions(String categoriesUrl) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.get(categoriesUrl, params, new AsyncHttpResponseHandler() {

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

                            if (jsonMainNode.length() != 0) {
                                searchSuggetionsList = new ArrayList<String>();
                                for (int i = 0; i < jsonMainNode.length(); i++) {
                                    /****** Get Object for each JSON node.***********/
                                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);

                                    /******* Fetch node values **********/
                                    String product_name = jsonChildNode.optString("product_name");
                                    searchSuggetionsList.add(product_name);
                                }

                                /************ Show Output on screen/activity **********/
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        // get the defined string-array
                                        String[] search_suggestionsList = new String[searchSuggetionsList.size()];
                                        for (int i = 0; i < searchSuggetionsList.size(); i++) {
                                            String sugg = searchSuggetionsList.get(i).toString();
                                            if (sugg != null) {
                                                sugg = sugg.replace("\"", "");
                                                search_suggestionsList[i] = sugg;
                                            }
                                        }

                                        if (search_suggestionsList != null) {
                                            setUpSearchHere(search_suggestionsList);
                                        }
                                    }
                                }, 300);
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
                Log.w("HARI-->", "" + content);
                if (ringProgressDialog != null) {
                    ringProgressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackPressedAnimationByCHK();
    }

    private void onBackPressedAnimationByCHK() {
        finish();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        searchView.showSearch();

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setUpSearchHere(String[] query_suggestions) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.ic_search_white_24dp);
        searchView.setEllipsize(true);
        searchView.setSuggestions(query_suggestions);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent searchList = new Intent(SearchActivity.this, ProductsListActivity.class);
                searchList.putExtra("SEARCH_KEYWORD", "" + query);
                searchList.putExtra("FROM_HOME", "SEARCH");
                startActivity(searchList);
                finish();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                finish();
            }
        });
    }
}