package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ourcitydeals.ctrl.utilities.Utility;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

/**
 * Created by Jashwikha Chakrapu on 5/29/2016.
 */
public class OrderConfirmedActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.screen_result);
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
        Utility.setDimensions(this);
        setupNavigation();

        if (getIntent() != null){
            Bundle b =  getIntent().getExtras();
            if (b != null){
                TextView orderID = (TextView)findViewById(R.id.orderID);
                TextView amountTV = (TextView)findViewById(R.id.amountTVID);
                String ORDER_ID = b.getString("ORDER_ID");
                String TOTAL_AMOUNT = b.getString("TOTAL_AMOUNT");
                orderID.setText("#"+ORDER_ID);
                amountTV.setText(getResources().getString(R.string.rs)+""+TOTAL_AMOUNT);
            }
        }
    }

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
        titleTV.setText("Order Placed");

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
        menuBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(OrderConfirmedActivity.this, CartScreenActivity.class));
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
        overridePendingTransition(R.anim.act_pull_in_left, R.anim.act_push_out_right);
        Intent bookingDoneIntent = new Intent(OrderConfirmedActivity.this, CreateOrderActivity.class);
        bookingDoneIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        bookingDoneIntent.putExtra("EXIT", true);
        startActivity(bookingDoneIntent);
        finish();
    }
}