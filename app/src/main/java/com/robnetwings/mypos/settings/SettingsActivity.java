package com.robnetwings.mypos.settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.robnetwings.mypos.R;
import com.robnetwings.mypos.settings.backup.BackupActivity;
import com.robnetwings.mypos.settings.categories.CategoriesActivity;
import com.robnetwings.mypos.settings.order_type.OrderTypeActivity;
import com.robnetwings.mypos.settings.payment_method.PaymentMethodActivity;
import com.robnetwings.mypos.settings.shop.ShopInformationActivity;
import com.robnetwings.mypos.settings.unit.UnitActivity;
import com.robnetwings.mypos.utils.BaseActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class SettingsActivity extends BaseActivity {

    CardView cardShopInfo, cardBackup,cardCategory,cardPaymentMethod,cardOrderType,cardUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.action_settings);


        cardShopInfo = findViewById(R.id.card_shop_info);
        cardBackup = findViewById(R.id.card_backup);
        cardCategory=findViewById(R.id.card_category);
        cardPaymentMethod=findViewById(R.id.card_payment_method);
        cardOrderType=findViewById(R.id.card_order_type);
        cardUnit=findViewById(R.id.card_unit);





        MobileAds.initialize(this, initializationStatus -> {
        });


        AdRequest adRequest = new AdRequest.Builder().build();

        cardShopInfo.setOnClickListener(v -> {

            Intent intent = new Intent(SettingsActivity.this, ShopInformationActivity.class);
            startActivity(intent);
        });



        cardCategory.setOnClickListener(v -> {
            Intent intent=new Intent(SettingsActivity.this, CategoriesActivity.class);
            startActivity(intent);
        });


        cardOrderType.setOnClickListener(v -> {
            Intent intent=new Intent(SettingsActivity.this, OrderTypeActivity.class);
            startActivity(intent);
        });

        cardUnit.setOnClickListener(v -> {
            Intent intent=new Intent(SettingsActivity.this, UnitActivity.class);
            startActivity(intent);
        });


        cardPaymentMethod.setOnClickListener(v -> {

            Intent intent = new Intent(SettingsActivity.this, PaymentMethodActivity.class);
            startActivity(intent);
        });


        cardBackup.setOnClickListener(v -> {

            Intent intent = new Intent(SettingsActivity.this, BackupActivity.class);
            startActivity(intent);
        });


    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
