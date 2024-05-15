package com.robnetwings.mypos;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.robnetwings.mypos.api.DeviceInfo;
import com.robnetwings.mypos.api.RetrofitClient;
import com.robnetwings.mypos.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class CheckoutActivity extends AppCompatActivity {
    private String email = "info@kobirosafaris.com";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Check if the Intent contains the email extra
        if (getIntent().hasExtra("email")) {
            // Email extra exists, retrieve it
            email = getIntent().getStringExtra("email");
            // Use the email as needed
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        initializeWebviewPayment();

    }

    private void initializeWebviewPayment() {
        DeviceInfo deviceInfo = Tools.getDeviceInfo(CheckoutActivity.this);
        String device = deviceInfo.serial;

        progressDialog.show();

        RetrofitClient.getInstance().getApi().initializeWebViewPayment(email, device).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        // Parsing the response and extracting the access code
                        JSONObject jsonObject = new JSONObject(responseBody);
                        boolean status = jsonObject.getBoolean("status");
                        String message = jsonObject.getString("message");
                        JSONObject data = jsonObject.getJSONObject("data");
                        String authorizationUrl = data.getString("authorization_url");
                        String reference = data.getString("reference");
                        Log.i("reference", reference);
                        Log.i("authorizationUrl", authorizationUrl);
                        Toasty.success(CheckoutActivity.this, message, Toasty.LENGTH_LONG).show();
                        loadCheckout(authorizationUrl);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    // Handle error responses here
                    switch (response.code()) {
                        case 400:
                            Toasty.error(CheckoutActivity.this, "Invalid input data", Toast.LENGTH_LONG).show();
                            break;
                        case 500:
                            Toasty.error(CheckoutActivity.this, "Internal Server Error", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toasty.error(CheckoutActivity.this, "Unknown error occurred", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                // Handle network errors or unexpected failures
                Toasty.error(CheckoutActivity.this, "Failed to initialize payment", Toast.LENGTH_LONG).show();
            }
        });
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressDialog.dismiss();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadCheckout(String authorizationUrl) {
        WebView webView = findViewById(R.id.webview);
        webView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);

        progressDialog.show();
        webView.loadUrl(authorizationUrl);
    }

}