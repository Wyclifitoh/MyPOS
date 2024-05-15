package com.robnetwings.mypos;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.robnetwings.mypos.api.DeviceInfo;
import com.robnetwings.mypos.api.RetrofitClient;
import com.robnetwings.mypos.api.SessionManager;
import com.robnetwings.mypos.utils.Tools;
import com.robnetwings.mypos.utils.Utils;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    public static int splashTimeOut = 2000;

    private Context mContext = SplashActivity.this;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sessionManager = SessionManager.getInstance(mContext);
        // Log activation event
        AppEventsLogger logger = AppEventsLogger.newLogger(this);
        logger.logEvent(AppEventsConstants.EVENT_NAME_ACTIVATED_APP);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sessionManager.isFirstTimeLaunch()) {
                    Intent intent = new Intent(mContext, PaymentActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    if (sessionManager.getPaymentStatus() == null) {
                        startActivity(new Intent(mContext, PaymentActivity.class));
                        finish();
                    } else {
                        if (sessionManager.getPaymentStatus().equals("paid")){
                            startActivity(new Intent(mContext, HomeActivity.class));
                            finish();
                        } else if (sessionManager.getPaymentStatus().equals("unpaid")){
                            if (Utils.isNetworkAvailable(mContext)) {
                                confirmPayment();
                            } else {
                                startActivity(new Intent(mContext, PaymentActivity.class));
                                finish();
                            }
                        }
                    }
                }
            }
        }, splashTimeOut);
    }

    private void confirmPayment() {
        DeviceInfo deviceInfo = Tools.getDeviceInfo(mContext);
        String device_id = deviceInfo.serial;
        RetrofitClient.getInstance().getApi().confirmPayment("Fee", device_id, "").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String str = response.body().string();
                    JSONObject jSONObject = new JSONObject(str);
                    boolean error = jSONObject.getBoolean("error");
                    String message = jSONObject.getString("message");

                    if (error) {
                        sessionManager.savePaymentStatus("unpaid");
                        startActivity(new Intent(mContext, PaymentActivity.class));
                        finish();
                    } else {
                        sessionManager.setFirstTimeLaunch(false);
                        sessionManager.savePaymentStatus("paid");
                        startActivity(new Intent(mContext, HomeActivity.class));

                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }
}

