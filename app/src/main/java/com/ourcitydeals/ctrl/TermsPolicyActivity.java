package com.ourcitydeals.ctrl;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ourcitydeals.ctrl.utilities.Utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by HARI on 5/1/2016.
 */
public class TermsPolicyActivity extends Activity {
    ProgressDialog ringProgressDialog;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.screen_terms_policies);
        Utility.setDimensions(this);
        setupNavigation();

        webView = (WebView) findViewById(R.id.detailsTVID);
        String terms = "It is important to note the following Terms and conditions carefully as your use of\n" +
                "\n" +
                "service is subjected to your acceptance of and compliance with the same.\n" +
                "\n" +
                "a) By subscribing to or using any of our services, you agree that you have\n" +
                "\n" +
                "completely read, understood and are bound by the Terms, regardless of how you\n" +
                "\n" +
                "subscribe to or use the services.\n" +
                "\n" +
                "b) In these Terms, references to &quot;you&quot;, &quot;User&quot; shall mean the end user accessing\n" +
                "\n" +
                "the Website, its contents and using the Services offered through the Website.\n" +
                "\n" +
                "&quot;Service Providers&quot; mean independent third party service providers, and &quot;we&quot;,\n" +
                "\n" +
                "&quot;us&quot; and &quot;our&quot; mean Ourcitydeals.com, its affiliates and partners.\n" +
                "\n" +
                "User Account, Password, and Security:\n" +
                "\n" +
                "You will receive a password and account designation upon completing the Website&#39;s\n" +
                "\n" +
                "registration process. You are responsible for maintaining their confidentiality and all the\n" +
                "\n" +
                "activities that occur under them. You agree to (a) immediately notify Ourcitydeals.com\n" +
                "\n" +
                "any unauthorized use of your password or account or any other breach of security, and\n" +
                "\n" +
                "(b) ensure that you exit from your account at the end of each session. We cannot and\n" +
                "\n" +
                "will not be liable for any loss or damage that arises from your failure.\n" +
                "\n" +
                "We provide such services which enable users to purchase various products of their\n" +
                "\n" +
                "choice. Upon placing an order, we shall ship the product and be entitled to its payment\n" +
                "\n" +
                "Logos, Pictures, Items, or any other property of our firm should not be copied, down\n" +
                "\n" +
                "loaded, used, or claimed by any other person or party, violation of which may lead to\n" +
                "\n" +
                "take appropriate legal action.\n" +
                "\n" +
                "Channel Subscription:\n" +
                "\n" +
                "By providing your data, you agree to be communicated via E-mail and SMS, to facilitate\n" +
                "\n" +
                "your navigation of and shopping from the website. We may communicate promotional\n" +
                "\n" +
                "offers periodically, as and when required.\n" +
                "\n" +
                "No Compensation Policy:\n" +
                "\n" +
                "If the quality of any product or service, purchased or utilized by you through the Website\n" +
                "\n" +
                "doesn’t meet your expectations, no compensation will be given. However, if the vendor\n" +
                "\n" +
                "offers Return or refund or Size Exchange of the product, the same can be applicable.\n" +
                "\n" +
                "User Warranty and Representation:\n" +
                "\n" +
                "The user guarantees, warrantees, and certifies that you are the owner of the content\n" +
                "\n" +
                "which you submit and the content does not infringe upon the property rights, intellectual\n" +
                "\n" +
                "property rights or others’ rights. You further warranty that to the best of your knowledge,\n" +
                "\n" +
                "no action, proceeding, or investigation has been instituted or threatened relating to any\n" +
                "\n" +
                "content, trademark, and copyright formerly or currently, in connection with the Services\n" +
                "\n" +
                "Exactness Not Guaranteed:\n" +
                "\n" +
                "WE disclaim any guarantee of exactness as to the finish and appearance of the final\n" +
                "\n" +
                "Product as ordered by the user. Alterations to certain aspects of your order such as the\n" +
                "\n" +
                "merchandise brand, size, colour etc. may be required due to limitations caused by\n" +
                "\n" +
                "availability of product difference in size charts of respective brands etc.";
        loadInWebView(terms);
    }

    private void loadInWebView(String webContent) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        WebSettings settings = webView.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webView.getSettings().setUserAgentString(Locale.getDefault().getLanguage());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("mailto:")) {
                    MailTo mt = MailTo.parse(url);
                    Intent i = Utility.hariEmailIntent(TermsPolicyActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                    startActivity(i);
                    view.reload();
                    return true;
                } else {
                    view.loadUrl(url);
                }
                return true;
            }
        });

        String summary = "<html> <body align=\"justify\" style=\"font-family:Roboto;line-height:20px\">" + webContent + "</body></html>";
        //String summary = "<html> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <body style=\"font-family:Roboto;line-height:20px\">" + webContent + "</body></html>";

        summary = summary.replaceAll("//", "");
        // create text file
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            Log.d("OurCityDeals", "No SDCARD");
        else {
            File direct = new File(Environment.getExternalStorageDirectory() + "/OurCityDeals");

            if (!direct.exists()) {
                if (direct.mkdir()) {
                    // directory is created;
                }
            }

            try {
                File root = new File(Environment.getExternalStorageDirectory() + "/OurCityDeals");
                if (root.canWrite()) {
                    File file = new File(root, "OurCityDealsSingleDetails.html");
                    FileWriter fileWriter = new FileWriter(file);
                    BufferedWriter out = new BufferedWriter(fileWriter);
                    if (summary.contains("<iframe")) {
                        try {
                            int a = summary.indexOf("<iframe");
                            int b = summary.indexOf("</iframe>");
                            summary = summary.replace(
                                    summary.subSequence(a, b), "");
                        } catch (Exception e) {
                            if (e != null) {
                                e.printStackTrace();

                            }
                        }
                    }
                    out.write(summary);
                    out.close();
                }
            } catch (IOException e) {
                if (e != null) {
                    e.printStackTrace();

                }
            }

            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                Log.d("OurCityDeals", "No SDCARD");
            } else {
                webView.loadUrl("file://" + Environment.getExternalStorageDirectory() + "/OurCityDeals" + "/OurCityDealsSingleDetails.html");
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        if (url.startsWith("tel:")) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                            startActivity(intent);
                        } else if (url.startsWith("http:") || url.startsWith("https:")) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);
                        } else if (url.startsWith("mailto:")) {
                            MailTo mt = MailTo.parse(url);
                            Intent i = Utility.hariEmailIntent(TermsPolicyActivity.this, mt.getTo(), mt.getSubject(), mt.getBody(), mt.getCc());
                            startActivity(i);
                            view.reload();
                            return true;
                        } else {
                            view.loadUrl(url);
                        }
                        return true;
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.act_pull_in_right, R.anim.act_push_out_left);
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
        titleTV.setText("TERMS & CONDITIONS");

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
                startActivity(new Intent(TermsPolicyActivity.this, CartScreenActivity.class));
            }
        });
        menuBtn.setBackgroundResource(R.drawable.ic_shopping_cart_white_24dp);
    }
}