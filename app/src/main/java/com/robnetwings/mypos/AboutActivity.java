package com.robnetwings.mypos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.robnetwings.mypos.api.DefaultResponse;
import com.robnetwings.mypos.api.DeviceInfo;
import com.robnetwings.mypos.api.RetrofitClient;
import com.robnetwings.mypos.utils.Tools;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AboutActivity extends AppCompatActivity {
    private Button contact_us_button, submit_feedback_button;
    private EditText feedback_input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.about_us);
        Tools.setSystemBarColor(this, R.color.colorPrimary);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient));
        getSupportActionBar().setElevation(0);

        contact_us_button = (Button) findViewById(R.id.contact_us_button);
        submit_feedback_button = (Button) findViewById(R.id.submit_feedback_button);
        feedback_input = (EditText) findViewById(R.id.feedback_input);

        contact_us_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactSupport();
            }
        });

        submit_feedback_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String note = feedback_input.getText().toString().trim();
                if (TextUtils.isEmpty(note)) {
                    Toasty.error(AboutActivity.this, "Feedback input can't be empty", Toasty.LENGTH_LONG).show();
                    feedback_input.setError("Feedback input can't be empty");
                    feedback_input.requestFocus();
                } else {
                    submitFeedback(note);
                }
            }
        });

    }

    private void submitFeedback(String note) {
        ProgressDialog progressDialog = new ProgressDialog(AboutActivity.this);
        progressDialog.setTitle("Please wait!!!");
        progressDialog.show();
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        DeviceInfo deviceInfo = Tools.getDeviceInfo(AboutActivity.this);
        String serial = deviceInfo.serial;
        String device = deviceInfo.device;
        String version = deviceInfo.os_version;

        RetrofitClient.getInstance().getApi().submitFeedback(serial, device, version, "0712345678", "wikiteq@mypos.com", note).enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                progressDialog.dismiss();
                DefaultResponse defaultResponse = response.body();

                try {
                    if (defaultResponse.isErr()) {
                        Toasty.error(AboutActivity.this, "Something went wrong try again later", Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.success(AboutActivity.this, "Feedback Sent...we will contact you shortly", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toasty.error(AboutActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toasty.error(AboutActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void contactSupport() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:wikiteqsolutions@gmail.com")); // Set recipient email address
        intent.putExtra(Intent.EXTRA_SUBJECT, "Support Inquiry"); // Set email subject
        intent.putExtra(Intent.EXTRA_TEXT, "Hello, I need assistance with..."); // Set email body
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle if no email app is available
            Toasty.warning(AboutActivity.this, "No email app found", Toasty.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}