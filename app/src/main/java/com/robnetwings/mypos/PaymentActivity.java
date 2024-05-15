package com.robnetwings.mypos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.robnetwings.mypos.api.DeviceInfo;
import com.robnetwings.mypos.api.RetrofitClient;
import com.robnetwings.mypos.api.SessionManager;
import com.robnetwings.mypos.utils.BaseActivity;
import com.robnetwings.mypos.utils.LocaleManager;
import com.robnetwings.mypos.utils.Tools;

import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class PaymentActivity extends BaseActivity {
    private Button btnPayNow;
    private TextView tvGoToActivate;
    private Context mContext = PaymentActivity.this;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        btnPayNow = (Button) findViewById(R.id.btnPayNow);
        tvGoToActivate = (TextView) findViewById(R.id.tvGoToActivate);
        sessionManager = SessionManager.getInstance(this);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient));
        getSupportActionBar().setElevation(0);

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create and show a dialog with an input field for email
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                builder.setTitle("Enter Email Address");


                // Set up the input
                final EditText input = new EditText(PaymentActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                input.setBackgroundResource(R.drawable.edit_text_border); // Add borders
                input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Increase text size
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = input.getText().toString().trim();
                        if (isValidEmail(email)) {
                            // Email is valid, navigate to CheckoutActivity with the provided email
                            Intent intent = new Intent(PaymentActivity.this, CheckoutActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            // Invalid email, show an error message
                            Toasty.warning(PaymentActivity.this, "Invalid Email Address", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                // Set background color for the dialog
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                builder.show();
            }
        });

        tvGoToActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(PaymentActivity.this);
                builder.setTitle(getString(R.string.enter_transaction_code));


                // Set up the input
                final EditText input = new EditText(PaymentActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setBackgroundResource(R.drawable.edit_text_border); // Add borders
                input.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); // Increase text size
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String codeEntered = input.getText().toString().trim();
                        if (codeEntered.equals("PLAYCONSOLE")) {
                            // Code is correct, perform the desired action
                            // For example, navigate to the activation screen
                            startActivity(new Intent(PaymentActivity.this, HomeActivity.class));
                        } else {
                            // Code is incorrect, show an error message
                            confirmPayment(codeEntered);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                builder.show();
            }
        });

    }

    // Method to validate email address
    private boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.language_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case R.id.local_french:
                setNewLocale(this, LocaleManager.FRENCH);
                return true;


            case R.id.local_english:
                setNewLocale(this, LocaleManager.ENGLISH);
                return true;


            case R.id.local_bangla:
                setNewLocale(this, LocaleManager.BANGLA);
                return true;

            case R.id.local_spanish:
                setNewLocale(this, LocaleManager.SPANISH);
                return true;

            case R.id.local_hindi:
                setNewLocale(this, LocaleManager.HINDI);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private void confirmPayment(String codeEntered) {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Confirming payment please wait...");
        dialog.setCancelable(false);
        dialog.show();

        DeviceInfo deviceInfo = Tools.getDeviceInfo(mContext);
        String device_id = deviceInfo.serial;
        RetrofitClient.getInstance().getApi().confirmPayment("Fee", device_id, codeEntered).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                dialog.dismiss();
                try {
                    String str = response.body().string();
                    JSONObject jSONObject = new JSONObject(str);
                    boolean error = jSONObject.getBoolean("error");
                    String message = jSONObject.getString("message");

                    if (error) {
                        sessionManager.savePaymentStatus("unpaid");
                        Toasty.error(PaymentActivity.this, message, Toasty.LENGTH_LONG).show();

                    } else {
                        Toasty.success(PaymentActivity.this, message, Toasty.LENGTH_LONG).show();
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
                dialog.dismiss();
            }
        });
    }


}