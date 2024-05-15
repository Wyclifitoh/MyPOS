package com.robnetwings.mypos.api;

import android.util.Base64;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitClient {
    private static final String AUTH = ("Basic " + Base64.encodeToString("PylaApp:pylamobileapp".getBytes(), 2));
    private static final String BASE_URL = "https://wikiteq.robnetwings.com/api/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

    private RetrofitClient() {
    }

    public static synchronized RetrofitClient getInstance() {
        RetrofitClient retrofitClient;
        synchronized (RetrofitClient.class) {
            if (mInstance == null) {
                mInstance = new RetrofitClient();
            }
            retrofitClient = mInstance;
        }
        return retrofitClient;
    }

    public Api getApi() {
        return (Api) this.retrofit.create(Api.class);
    }
}
