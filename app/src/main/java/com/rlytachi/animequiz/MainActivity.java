package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.jaeger.library.StatusBarUtil;

import java.util.Locale;

import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static com.rlytachi.animequiz.Settings.APP_PREFERENCES_MUSIC;
import static com.rlytachi.animequiz.Settings.APP_PREFERENCES_SOUNDS;
import static com.rlytachi.animequiz.Settings.getMusic;
import static com.rlytachi.animequiz.Settings.getSounds;
import static com.rlytachi.animequiz.Settings.in;
import static com.rlytachi.animequiz.Settings.out;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_LANG = "myLanguage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStatusBarTransparent();

        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);

        final ImageView settings = findViewById(R.id.settingsView);
        final ImageView start = findViewById(R.id.startView);
        final ImageView share = findViewById(R.id.shareView);
        settings.setOnClickListener(this);
        start.setOnClickListener(this);
        share.setOnClickListener(this);

        loadAction();
        langUpdate();
    }

    @Override
    protected void onDestroy() {
        langUpdate();
        saveAction();
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settingsView:
                Settings.playSound(in);
                finish();
                startActivity(new Intent(this, Settings.class));
                break;
            case R.id.startView:
                Settings.playSound(in);
                finish();
                startActivity(new Intent(this, Game.class));
                break;
            case R.id.shareView:
                Settings.playSound(in);
                finish();
                startActivity(new Intent(this, Share.class));
                break;

        }
    }

    @SuppressLint("ObsoleteSdkInt")
    public void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
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
        editor.putString(APP_PREFERENCES_LANG, Language.getLang());
        editor.putBoolean(APP_PREFERENCES_SOUNDS, getSounds());
        editor.putBoolean(APP_PREFERENCES_MUSIC, getMusic());

        editor.apply();
    }

    public void loadAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        Language.setLang(mShared.getString(APP_PREFERENCES_LANG, Locale.getDefault().getLanguage()));
        Settings.setSounds(mShared.getBoolean(APP_PREFERENCES_SOUNDS, Settings.getSounds()));
        Settings.setMusic(mShared.getBoolean(APP_PREFERENCES_MUSIC, Settings.getMusic()));
        Unlock.setUnlocked(mShared.getBoolean(Unlock.APP_PREFERENCES_UNLOCK_STATUS, Unlock.getUnlocked()));

    }

}