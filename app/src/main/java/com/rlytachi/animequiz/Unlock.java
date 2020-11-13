package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static com.rlytachi.animequiz.Settings.in;
import static com.rlytachi.animequiz.Settings.out;
import static com.rlytachi.animequiz.Settings.playSound;

public class Unlock extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    BillingProcessor billingProcessor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);

        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);

        MobileAds.initialize(this);
        AdView adView = findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        billingProcessor = BillingProcessor.newBillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq9CxUJU3RJo9gYNv8uqj/nZtc+uGIG7mp9pV/YjYYlsfB0ekkkGGhc/8i9LOUJ+Pjt49dGFWZ+xJhVHrvvzV2R1H4bu460zuzCSElf8AdlX/MhKtXfEUvrFwHNedtn3/qqhKUYzHZA5hBqlnIanjxF9Twl1or3e9rw9xgPfs9wjGhKSN9dQMszefXL9WYN1/aXDodlRLL6s2sZDQKNORODMKNUOGrl3jAdL5CD4Ravg6oI0gffJ3PvOTNpZoCFyTT97T2mIfymd6A2YKnGEwoE28rGu4/8tlFQlNoN13crunFDALhkVn/s/ttRhybgcjQboBGI6nwiIy13ITPQ08gwIDAQAB", this);
        billingProcessor.initialize();

        Animation animButton = AnimationUtils.loadAnimation(Unlock.this, R.anim.anim_btn);
        final Button buy = findViewById(R.id.buttonBuy);
        buy.setOnClickListener(v -> {
            Settings.playSound(in);
            buy.setAnimation(animButton);
            buy.startAnimation(animButton);
            //TODO purchase

        });

        final Button get = findViewById(R.id.buttonWatch);
        get.setOnClickListener(v -> {
            Settings.playSound(in);
            get.setAnimation(animButton);
            get.startAnimation(animButton);
            //TODO watch ad

        });

        final ImageView backBtn = findViewById(R.id.unlockBackView);
        backBtn.setOnClickListener(v -> {
            Settings.playSound(out);
            finish();
            startActivity(new Intent(this, Game.class));
        });

        try {
            System.out.println(Language.getLang());
            if (Language.getLang().equals("ru")) {
                MyContextWrapper.wrap(getBaseContext(), "ru");
            } else {
                MyContextWrapper.wrap(getBaseContext(), "en");
            }
        } catch (Exception ignore) {
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();

        try {
            System.out.println(Language.getLang());
            if (Language.getLang().equals("ru")) {
                MyContextWrapper.wrap(getBaseContext(), "ru");
            } else {
                MyContextWrapper.wrap(getBaseContext(), "en");
            }
        } catch (Exception ignore) {
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playSound(out);
        finish();
        startActivity(new Intent(Unlock.this, Game.class));
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }
}