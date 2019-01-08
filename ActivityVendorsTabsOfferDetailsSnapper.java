package com.snapper.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import com.hari.tabs.tabslib.PagerSlidingTabStrip;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.navdrawer.SimpleSideDrawer;
import com.snapper.app.model.VendorOffers;
import com.snapper.app.model.VendorOffersData;
import com.snapper.app.smartImage.MySmartImageView;
import com.snapper.app.util.AppConstants;
import com.snapper.app.util.Utility;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityVendorsTabsOfferDetailsSnapper extends SnapperBaseActivity {
	// Log tag
	//private ListView imageslist;
	RelativeLayout relative;
	ArrayList<HashMap<String, String>> contactList;

	String vendorID = "";

	ArrayList<VendorOffersData> vendorOffersList = new ArrayList<VendorOffersData>();
	private static int SPLASH_TIME_OUT = 1000;

	static int countList = 0;

	static Activity activity;

	public AdapterOffersDetails mAdapter;

	static TextView vendorname;
	static MySmartImageView vendorimage;

	static TextView vendorofferlocation;
	TextView distanceTV;

	//private ProgressWheel progressWheel_CENTER;
	
	String latitudeStr = "";
    String longitudeStr = "";

    Location location = null;
	GPSTracker mGPS = null;
	PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	
	ArrayList<VendorOffers> vendorofferscategorylist;
	String FROM_SCREENStr = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.screen_vendors_offers_list);
		
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
        
		Utility.setDimensions(this);
		
		commonMenuInAllScreens();
		
		 if (mGPS == null) {
			mGPS = new GPSTracker(ActivityVendorsTabsOfferDetailsSnapper.this);
		 }

		// check if mGPS object is created or not
		if (mGPS != null && location == null) {
			location = mGPS.getLocation();
		}
		
		
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setUnderlineHeight(2);
		//tabs.getLayoutParams().height = (int) (Utility.screenHeight / 11.4);
		pager = (ViewPager) findViewById(R.id.pager);
		
		RelativeLayout headerImage = (RelativeLayout) findViewById(R.id.headerRLID);
		headerImage.getLayoutParams().height = (int) (Utility.screenHeight / 10.0);

		activity = this;
		
		Button backBtn = (Button) findViewById(R.id.backBtnID);
		backBtn.setVisibility(View.VISIBLE);
		backBtn.getLayoutParams().width = (int) (Utility.screenHeight / 20.0);
		backBtn.getLayoutParams().height = (int) (Utility.screenHeight / 20.0);

		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				ActivityVendorsTabsOfferDetailsSnapper.this.finish();
				overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
			}
		});
		
		LinearLayout clickedid= (LinearLayout)findViewById(R.id.clickedid);
		clickedid.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Don't touch this on click
			}
		});
		
		if (getIntent() != null) {
			Bundle b = getIntent().getExtras();
			///boolean isNetAvailable = Utility.isOnline(ActivityVendorsTabsOfferDetailsSnapper.this);
			if (b != null) {
				FROM_SCREENStr = b.getString("FROM_SCREEN");
				vendorID = b.getString("vendor_id");
				if (vendorID.trim().length() > 0) {		
					getofferTitles(vendorID);
				}				
			}
		}
		
		distanceTV = (TextView)findViewById(R.id.distanceTVID);
		vendorofferlocation = (TextView) findViewById(R.id.location);
		vendorname = (TextView) findViewById(R.id.vendorname);
		vendorimage = (MySmartImageView) findViewById(R.id.vendorimage);
		vendorimage.getLayoutParams().height = (int) (Utility.screenWidth / 4.8);
		vendorimage.getLayoutParams().width = (int) (Utility.screenWidth / 4.8);
	}

	private void getofferTitles(String vendorId){
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params=new RequestParams();
		params.put("vendor_id", vendorId);
		
		client.post(AppConstants.CATEGORIES_LIST_VENDORS_URL, params,new AsyncHttpResponseHandler() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onSuccess(String content) {
				super.onSuccess(content);
				 try {
					JSONArray jsonObj = new JSONArray(content);
					vendorofferscategorylist=new ArrayList<VendorOffers>();
					for(int i=0;i<jsonObj.length();i++){
						VendorOffers vendoroffer=new VendorOffers();
						vendoroffer.setOffercatergoryid(jsonObj.getJSONObject(i).getString("category"));
						vendoroffer.setOffercatergoryname(jsonObj.getJSONObject(i).getString("category_title"));
						vendorofferscategorylist.add(vendoroffer);
					}
					setAdapter(vendorofferscategorylist);
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@SuppressWarnings("deprecation")
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable error, String content) {
				super.onFailure(statusCode, headers, error, content);
			}			
		});
	}
	
	
	private void setAdapter(ArrayList<VendorOffers> vendorofferscategorylist){
		adapter = new MyPagerAdapter(getSupportFragmentManager(), vendorofferscategorylist);
		pager.setAdapter(adapter);
		tabs.setViewPager(pager);
		tabs.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@SuppressLint("ShowToast")
	public void showCustomToast(String toastmsg) {
		try {
			LayoutInflater inflater = Utility
					.from_HariContext(ActivityVendorsTabsOfferDetailsSnapper.this);
			View layout = null;
			if (inflater != null) {
				layout = inflater.inflate(R.layout.toast_no_netowrk,
						(ViewGroup) ActivityVendorsTabsOfferDetailsSnapper.this
								.findViewById(R.id.custom_toast_layout_id));
				TextView tv = (TextView) layout.findViewById(R.id.text);
				// The actual toast generated here.
				Toast toast = new Toast(ActivityVendorsTabsOfferDetailsSnapper.this);
				toast.setDuration(Toast.LENGTH_LONG);
				tv.setText(toastmsg);
				if (layout != null) {
					toast.setView(layout);
					toast.show();
				}
			} else {
				Toast.makeText(ActivityVendorsTabsOfferDetailsSnapper.this, "" + toastmsg,
						2500).show();
			}
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
		}
	}
	
	private void commonMenuInAllScreens() {
		SELECTED_MENU_ITEM = 10;
		slide_me = new SimpleSideDrawer(this);
		slide_me.setLeftBehindContentView(R.layout.left_menu);
		mDrawerList = (ListView)slide_me.findViewById(R.id.list_slidermenu);
		
		TextView menuTitleTV = (TextView) slide_me.findViewById(R.id.menuTitleTVID);
		menuTitleTV.getLayoutParams().height = (int) (Utility.screenHeight / 10.0);

		menuBtn = (Button) findViewById(R.id.menuBtnID);
		menuBtn.getLayoutParams().width = (int) (Utility.screenHeight / 16.0);
		menuBtn.getLayoutParams().height = (int) (Utility.screenHeight / 16.0);
		menuBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				slide_me.toggleLeftDrawer();
			}
		});
		initializeMenuInAllScreens();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				ActivityVendorsTabsOfferDetailsSnapper.this.finish();
				overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	public class MyPagerAdapter extends FragmentStatePagerAdapter {
		ArrayList<VendorOffers> vendorofferscategorylist;
		
		int countList;

		public MyPagerAdapter(FragmentManager fm,
				ArrayList<VendorOffers> vendorofferscategorylist) {
			super(fm);
			this.vendorofferscategorylist = vendorofferscategorylist;
			
			countList = this.vendorofferscategorylist.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			if (vendorofferscategorylist.size()>0) {
				return vendorofferscategorylist.get(position).getOffercatergoryname();
			}
			
			return null;
		}

		@Override
		public int getCount() {
			return countList;
		}

		@Override
		public Fragment getItem(int position) {
			if (vendorofferscategorylist != null) {
				if (vendorofferscategorylist.size()>0) {
					return InnerListFragment.newInstance(position,countList, vendorofferscategorylist.get(position).getOffercatergoryid(), vendorID, FROM_SCREENStr);
				} 
			} 
			return null;
		}
	}
}