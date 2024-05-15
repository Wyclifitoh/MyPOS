package com.robnetwings.mypos.api;



import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {
    @FormUrlEncoded
    @POST("dictionary_stk_push.php")
    Call<DefaultResponse> stkPush(
            @Field("user_id") String user_id,
            @Field("amount") int amount,
            @Field("callback") String callback,
            @Field("phone") String phone,
            @Field("account_ref") String account_ref
    );

    @FormUrlEncoded
    @POST("dictionary_stripe.php")
    Call<DefaultResponse> stripePayment(
            @Field("transaction_id") String transaction_id,
            @Field("amount") String amount,
            @Field("package_type") String package_type,
            @Field("user_id") String user_id,
            @Field("payment_item") String payment_item
    );

    @FormUrlEncoded
    @POST("submit_feedback.php")
    Call<DefaultResponse> submitFeedback(
            @Field("device_id") String device_id,
            @Field("device_name") String device_name,
            @Field("device_version") String device_version,
            @Field("phone") String phone,
            @Field("email") String email,
            @Field("feedback") String feedback
    );

    @FormUrlEncoded
    @POST("confirm_ad_payment.php")
    Call<ResponseBody> confirmPayment(
            @Field("payment_item") String payment_item,
            @Field("account") String account,
            @Field("codeEntered") String codeEntered
    );

    @FormUrlEncoded
    @POST("mypos_payment.php")
    Call<ResponseBody> initializeWebViewPayment(
            @Field("email") String email,
            @Field("device") String device
    );

}
