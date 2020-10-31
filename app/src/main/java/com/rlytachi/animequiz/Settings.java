package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.rlytachi.animequiz.levels.Characters;

import java.util.Locale;

import static com.rlytachi.animequiz.PromoStorage.APP_PREFERENCES_PROMO_CODES;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_LANG = "myLanguage";
    public static final String APP_PREFERENCES_SOUNDS = "Sounds";
    public static final String APP_PREFERENCES_MUSIC = "Music";
    public InterstitialAd interstitialAd;
    private static boolean sounds = true;
    private static boolean music = true;
    public static MediaPlayer in, out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MobileAds.initialize(this, "ca-app-pub-7217958397153183~4133073169");
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-7217958397153183/4052170352");
        interstitialAd.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                startActivity(new Intent(Settings.this, MainActivity.class));
            }
        });

        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);

        final ImageView back = findViewById(R.id.settingsBackView);
        final RadioGroup langGroup = findViewById(R.id.langGroup);
        final RadioButton langBtn1 = findViewById(R.id.langRadio1);
        final RadioButton langBtn2 = findViewById(R.id.langRadio2);

        final RadioGroup soundsGroup = findViewById(R.id.soundsGroup);
        final RadioButton soundsBtn1 = findViewById(R.id.soundsRadio1);
        final RadioButton soundsBtn2 = findViewById(R.id.soundsRadio2);

        final RadioGroup musicGroup = findViewById(R.id.musicGroup);
        final RadioButton musicBtn1 = findViewById(R.id.musicRadio1);
        final RadioButton musicBtn2 = findViewById(R.id.musicRadio2);

        final Button clearSaveBtn = findViewById(R.id.clearSaveBtn);

        langBtn1.setOnClickListener(this);
        langBtn2.setOnClickListener(this);
        soundsBtn1.setOnClickListener(this);
        soundsBtn2.setOnClickListener(this);
        musicBtn1.setOnClickListener(this);
        musicBtn2.setOnClickListener(this);
        back.setOnClickListener(this);
        clearSaveBtn.setOnClickListener(this);

        loadAction();
        if (Language.getLang().equals("ru")) {
            langBtn1.setChecked(true);
            getBaseContext().getResources().updateConfiguration(Language.setLocationRu(), null);
        } else {
            langBtn2.setChecked(true);
            getBaseContext().getResources().updateConfiguration(Language.setLocationEn(), null);
        }
        if (Settings.getSounds()) {
            soundsBtn1.setChecked(true);
        } else {
            soundsBtn2.setChecked(true);
        }
        if (Settings.getMusic()) {
            musicBtn1.setChecked(true);
        } else {
            musicBtn2.setChecked(true);
        }
        saveAction();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        try {
            System.out.println(Language.getLang());
            if (Language.getLang().equals("ru")) {
                getBaseContext().getResources().updateConfiguration(Language.setLocationRu(), null);
            } else {
                getBaseContext().getResources().updateConfiguration(Language.setLocationEn(), null);
            }
        } catch (Exception ignore) {
        }
        saveAction();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playSound(out);
        finish();
        startActivity(new Intent(Settings.this, MainActivity.class));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.settingsBackView:
                playSound(out);
                finish();
                startActivity(new Intent(Settings.this, MainActivity.class));

                break;

            case R.id.langRadio1:
                playSound(in);
                getBaseContext().getResources().updateConfiguration(Language.setLocationRu(), null);
                saveAction();
                finish();
                startActivity(new Intent(Settings.this, Settings.class));
                break;
            case R.id.langRadio2:
                playSound(out);
                getBaseContext().getResources().updateConfiguration(Language.setLocationEn(), null);
                saveAction();
                finish();
                startActivity(new Intent(Settings.this, Settings.class));
                break;
            case R.id.soundsRadio1:
                playSound(in);
                Settings.setSounds(true);
                Score.addScore(20);
                saveAction();
                break;
            case R.id.soundsRadio2:
                playSound(out);
                Settings.setSounds(false);
                saveAction();
                break;
            case R.id.musicRadio1:
                playSound(in);
                Settings.setMusic(true);
                saveAction();
                break;
            case R.id.musicRadio2:
                playSound(out);
                Settings.setMusic(false);
                saveAction();
                break;

            case R.id.clearSaveBtn:
                playSound(out);
                dialogConfirm();
                break;

        }
    }

    public void saveAction() {
        Animation animEnd = AnimationUtils.loadAnimation(this, R.anim.anim_end);
        final TextView settingsStatusView = findViewById(R.id.settingsStatusView);
        settingsStatusView.setAnimation(animEnd);
        settingsStatusView.startAnimation(animEnd);
        settingsStatusView.setVisibility(View.INVISIBLE);

        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = mShared.edit();
        editor.putString(APP_PREFERENCES_LANG, Language.getLang());
        editor.putInt(Game.APP_PREFERENCES_GLOBAL_SCORE, Score.getScore());
        editor.putBoolean(APP_PREFERENCES_SOUNDS, getSounds());
        editor.putBoolean(APP_PREFERENCES_MUSIC, getMusic());

        for (int i = 0; i < PromoStorage.getPromoStorage().length; i++) {
            editor.putString(APP_PREFERENCES_PROMO_CODES[i], PromoStorage.getPromoStorage()[i]);
        }


        editor.apply();
    }

    public void loadAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Language.setLang(mShared.getString(APP_PREFERENCES_LANG, Locale.getDefault().getLanguage()));
        Settings.setSounds(mShared.getBoolean(APP_PREFERENCES_SOUNDS, Settings.getSounds()));
        Settings.setMusic(mShared.getBoolean(APP_PREFERENCES_MUSIC, Settings.getMusic()));

        for (int i = 0; i < PromoStorage.getPromoStorage().length; i++) {
            if (mShared.contains(APP_PREFERENCES_PROMO_CODES[i])) {
                String[] temp = new String[PromoStorage.getPromoStorage().length];
                temp[i] = mShared.getString(APP_PREFERENCES_PROMO_CODES[i], null);

                PromoStorage.setPromoStorage(temp);
            }
        }
    }

    public void clearSave() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = mShared.edit();
        editor.clear();
        editor.remove(APP_PREFERENCES);
        editor.apply();
        Score.reset();
        PromoStorage.reset();
        saveAction();
    }

    public void dialogConfirm() {
        Dialog confirmDialog = new Dialog(Settings.this);
        confirmDialog.setContentView(R.layout.activity_confirm);
        if (!this.isFinishing()) {
            confirmDialog.show();
        }
        confirmDialog.setCancelable(false);

        final Button confirmButton = confirmDialog.findViewById(R.id.confirmResetBtn);
        final Button cancelButton = confirmDialog.findViewById(R.id.cancelResetBtn);
        confirmButton.setOnClickListener(v -> {
            if (interstitialAd.isLoaded()) {
                clearSave();
                interstitialAd.show();
            } else {
                playSound(out);
                clearSave();
                confirmDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> {
            playSound(out);
            confirmDialog.dismiss();
        });

    }

    public static boolean getSounds() {
        return sounds;
    }

    public static void setSounds(boolean sounds) {
        Settings.sounds = sounds;
    }

    public static boolean getMusic() {
        return music;
    }

    public static void setMusic(boolean music) {
        Settings.music = music;
    }

    public static void playSound(MediaPlayer player) {
        if (Settings.getSounds()) {
            player.start();
            if (!player.isPlaying()) player.stop();
        }
    }
}
