package com.rlytachi.animequiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.jaeger.library.StatusBarUtil;

import static com.rlytachi.animequiz.Settings.*;

public class Unlock extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_UNLOCK_STATUS = "unlockStatus";
    BillingProcessor billingProcessor = null;
    private RewardedAd rewardedAd;
    static Boolean unlocked;
    static boolean firstUnlockUpdate = false;
    boolean back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock);
        StatusBarUtil.setTransparent(this);
        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);
        langUpdate();
        loadAction();

        MobileAds.initialize(this);
        AdView adView = findViewById(R.id.adView3);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        rewardedAd = new RewardedAd(this,
                "ca-app-pub-7217958397153183/1366891139");

        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                // Ad successfully loaded.
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError adError) {
                // Ad failed to load.
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

        billingProcessor = BillingProcessor.newBillingProcessor(this, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq9CxUJU3RJo9gYNv8uqj/nZtc+uGIG7mp9pV/YjYYlsfB0ekkkGGhc/8i9LOUJ+Pjt49dGFWZ+xJhVHrvvzV2R1H4bu460zuzCSElf8AdlX/MhKtXfEUvrFwHNedtn3/qqhKUYzHZA5hBqlnIanjxF9Twl1or3e9rw9xgPfs9wjGhKSN9dQMszefXL9WYN1/aXDodlRLL6s2sZDQKNORODMKNUOGrl3jAdL5CD4Ravg6oI0gffJ3PvOTNpZoCFyTT97T2mIfymd6A2YKnGEwoE28rGu4/8tlFQlNoN13crunFDALhkVn/s/ttRhybgcjQboBGI6nwiIy13ITPQ08gwIDAQAB", this);
        billingProcessor.initialize();

        Animation animButton = AnimationUtils.loadAnimation(Unlock.this, R.anim.anim_btn);
        final Button buy = findViewById(R.id.buttonBuy);
        buy.setOnClickListener(v -> {
            Settings.playSound(in);
            buy.setAnimation(animButton);
            buy.startAnimation(animButton);

            back = true;
            if (billingProcessor.isPurchased("unlock_all"))
                billingProcessor.consumePurchase("unlock_all");
            billingProcessor.purchase(this, "unlock_all");

        });

        final Button get = findViewById(R.id.buttonWatch);
        get.setOnClickListener(v -> {
            Settings.playSound(in);
            get.setAnimation(animButton);
            get.startAnimation(animButton);

            if (rewardedAd.isLoaded()) {
                back = true;

                Activity activityContext = Unlock.this;
                RewardedAdCallback adCallback = new RewardedAdCallback() {
                    @Override
                    public void onRewardedAdOpened() {
                        // Ad opened.
                        Log.d("REWARD", "Opened");
                    }

                    @Override
                    public void onRewardedAdClosed() {
                        // Ad closed.
                        Log.d("REWARD", "Closed");
                    }

                    @Override
                    public void onUserEarnedReward(@NonNull com.google.android.gms.ads.rewarded.RewardItem rewardItem) {
                        Score.addScore(rewardItem.getAmount());
                        Log.d("REWARD", "Earned");
                        saveAction();
                    }

                    @Override
                    public void onRewardedAdFailedToShow(AdError adError) {
                        // Ad failed to display.
                        Log.d("REWARD", "Failed");
                    }

                };
                rewardedAd.show(activityContext, adCallback);
            } else {
                Toast.makeText(this, "The rewarded ad wasn't loaded yet.", Toast.LENGTH_SHORT).show();
                Log.d("REWARD", "The rewarded ad wasn't loaded yet.");
            }
        });

        final ImageView backBtn = findViewById(R.id.unlockBackView);
        backBtn.setOnClickListener(v -> {
            Settings.playSound(out);
            back = true;
            finish();
            startActivity(new Intent(this, Game.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        if (billingProcessor.isPurchased("unlock_all")) {
            purchaseUnlockAll();
        }

        if (!back) {
            langUpdate();
            finish();
            startActivity(new Intent(Unlock.this, Unlock.class));
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (billingProcessor.isPurchased("unlock_all")) {
            purchaseUnlockAll();
        }

        if (billingProcessor != null)
            billingProcessor.release();

        langUpdate();
        saveAction();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (billingProcessor.isPurchased("unlock_all")) {
            purchaseUnlockAll();
        }

        playSound(out);
        back = true;
        finish();
        startActivity(new Intent(Unlock.this, Game.class));
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        purchaseUnlockAll();
        Log.i("BP", "Purchased");
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Toast.makeText(this, "Restored", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        Log.e("BP", "Error");

        billingProcessor.release();
        billingProcessor.initialize();
    }

    @Override
    public void onBillingInitialized() {
        Toast.makeText(this, "Initialized", Toast.LENGTH_SHORT).show();
        Log.i("BP", "Initialized");
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
        editor.putInt(Game.APP_PREFERENCES_GLOBAL_SCORE, Score.getScore());
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

    private void purchaseUnlockAll() {
        setUnlocked(true);
        setFirstUnlockUpdate(true);
        saveAction();
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        Log.i("BP", "Success");
    }
}