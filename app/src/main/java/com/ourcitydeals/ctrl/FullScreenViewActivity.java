package com.ourcitydeals.ctrl;

import com.ourcitydeals.ctrl.adapters.FullScreenImageAdapter;
import com.ourcitydeals.ctrl.utilities.Utility;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class FullScreenViewActivity extends Activity{

	//private Utils utils;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
		}
		setContentView(R.layout.activity_fullscreen_view);

		viewPager = (ViewPager) findViewById(R.id.pager);

		//utils = new Utils(getApplicationContext());

		//Intent i = getIntent();
		//int position = i.getIntExtra("position", 0);

		// close button click event
		Button btnClose = (Button) findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, Utility.galleriesList);

		viewPager.setAdapter(adapter);

		// displaying selected image first
		//viewPager.setCurrentItem(position);
	}
}
