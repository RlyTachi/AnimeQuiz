package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.jaeger.library.StatusBarUtil;

import static com.rlytachi.animequiz.Settings.*;

public class Unlock extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_UNLOCK_STATUS = "unlockStatus";
    BillingProcessor billingProcessor = null;
    static Boolean unlocked = false;
    static boolean firstUnlockUpdate = false;
    boolean back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        StatusBarUtil.setTransparent(this);
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

            setUnlocked(true);
            setFirstUnlockUpdate(true);
        });

        final Button get = findViewById(R.id.buttonWatch);
        get.setOnClickListener(v -> {
            Settings.playSound(in);
            get.setAnimation(animButton);
            get.startAnimation(animButton);
            //TODO watch ad

            setUnlocked(false);
        });

        final ImageView backBtn = findViewById(R.id.unlockBackView);
        backBtn.setOnClickListener(v -> {
            Settings.playSound(out);
            back = true;
            finish();
            startActivity(new Intent(this, Game.class));
        });

        langUpdate();
        loadAction();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        if (!back) {
            langUpdate();
            finish();
            startActivity(new Intent(Unlock.this, Unlock.class));
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (billingProcessor != null)
            billingProcessor.release();

        langUpdate();
        saveAction();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playSound(out);
        back = true;
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

    public void langUpdate() {
        try {
            System.out.println(Language.getLang());
            if (Language.getLang().equals("ru")) {
                MyContextWrapper.wrap(getBaseContext(), "ru");
            } else if (Language.getLang().equals("ja")) {
                MyContextWrapper.wrap(getBaseContext(), "ja");
            } else {
                MyContextWrapper.wrap(getBaseContext(), "en");
            }
        } catch (Exception ignore) {
        }
    }

    public void saveAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = mShared.edit();
        editor.putBoolean(APP_PREFERENCES_UNLOCK_STATUS, getUnlocked());
        editor.apply();
    }

    public void loadAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        setUnlocked(mShared.getBoolean(APP_PREFERENCES_UNLOCK_STATUS, false));
    }

    public static Boolean getUnlocked() {
        return unlocked;
    }

    public static void setUnlocked(Boolean unlocked) {
        Unlock.unlocked = unlocked;
    }

    public static boolean isFirstUnlockUpdate() {
        return firstUnlockUpdate;
    }

    public static void setFirstUnlockUpdate(boolean firstUnlockUpdate) {
        Unlock.firstUnlockUpdate = firstUnlockUpdate;
    }
}