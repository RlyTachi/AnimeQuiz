package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_LANG = "myLanguage";
    public String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView settings = findViewById(R.id.settingsView);
        final ImageView start = findViewById(R.id.startView);
        final ImageView share = findViewById(R.id.shareView);
        settings.setOnClickListener(this);
        start.setOnClickListener(this);
        share.setOnClickListener(this);

        loadAction();
        if (lang == null) {
            if (Locale.getDefault().getLanguage().equals("ru")) {
                setLocationRu();
            } else {
                setLocationEn();
            }
        } else if (lang.equals("ru")) {
            setLocationRu();
        } else {
            setLocationEn();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveAction();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.settingsView:
                startActivity(new Intent(this, Settings.class));
                break;
            case R.id.startView:
                startActivity(new Intent(this, Game.class));
                break;
            case R.id.shareView:
                startActivity(new Intent(this, Share.class));
                break;

        }
    }

    public void saveAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = mShared.edit();
        editor.putString(APP_PREFERENCES_LANG, lang);

        editor.apply();
    }

    public void loadAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        lang = mShared.getString(APP_PREFERENCES_LANG, Locale.getDefault().getLanguage());
    }

    public void setLocationRu() {
        Locale localeRu = new Locale("ru");
        Locale.setDefault(localeRu);
        Configuration configuration = new Configuration();
        configuration.locale = localeRu;
        getBaseContext().getResources().updateConfiguration(configuration, null);
        lang = "ru";
    }

    public void setLocationEn() {
        Locale localeEn = new Locale("en");
        Locale.setDefault(localeEn);
        Configuration configuration = new Configuration();
        configuration.locale = localeEn;
        getBaseContext().getResources().updateConfiguration(configuration, null);
        lang = "en";
    }
}