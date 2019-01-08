package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ourcitydeals.ctrl.ownLibs.MyTextView;
import com.ourcitydeals.ctrl.utilities.MySharedPreference;
import com.ourcitydeals.ctrl.utilities.Utility;

/**
 * Created by Jashwikha Chakrapu on 5/29/2016.
 */
public class PaymentMethodScreenActivity extends Activity {
    TextView totalAmountTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.screen_payment_type);
        Utility.setDimensions(this);
        setupNavigation();

        totalAmountTV = (TextView) findViewById(R.id.totalAmountTVID);

        MyTextView continueOrderTV = (MyTextView) findViewById(R.id.continueOrderTVID);
        continueOrderTV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(PaymentMethodScreenActivity.this, CreateOrderActivity.class));
            }
        });

        LinearLayout linearCredit = (LinearLayout) findViewById(R.id.linearCredit);
        linearCredit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utility.showCustomToast("Coming Soon!", PaymentMethodScreenActivity.this);
            }
        });

        LinearLayout linearDebit = (LinearLayout) findViewById(R.id.linearDebit);
        linearDebit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utility.showCustomToast("Coming Soon!", PaymentMethodScreenActivity.this);
            }
        });

        LinearLayout linearNetBanking = (LinearLayout) findViewById(R.id.linearNetBanking);
        linearNetBanking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Utility.showCustomToast("Coming Soon!", PaymentMethodScreenActivity.this);
            }
        });

        totalAmountTV.setText(PaymentMethodScreenActivity.this.getResources().getString(R.string.rs) + "" +MySharedPreference.getPreferences(PaymentMethodScreenActivity.this, "CART_TOTAL_AMOUNT"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
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
        titleTV.setText("Select Payment");

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
}