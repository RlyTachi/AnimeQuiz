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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

public class Settings extends AppCompatActivity implements View.OnClickListener {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_LANG = "myLanguage";
    public String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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

        langBtn1.setOnClickListener(this);
        langBtn2.setOnClickListener(this);
        soundsBtn1.setOnClickListener(this);
        soundsBtn2.setOnClickListener(this);
        musicBtn1.setOnClickListener(this);
        musicBtn2.setOnClickListener(this);

        back.setOnClickListener(this);

        loadAction();
        System.out.println();
        if (lang == null) {
            if (Locale.getDefault().getLanguage().equals("ru")) {
                langBtn1.setChecked(true);
                setLocationRu();
            } else {
                langBtn2.setChecked(true);
                setLocationEn();
            }
        } else if (lang.equals("ru")) {
            langBtn1.setChecked(true);
            setLocationRu();
        } else {
            langBtn2.setChecked(true);
            setLocationEn();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAction();
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
            case R.id.settingsBackView:
                startActivity(new Intent(Settings.this, MainActivity.class));
                break;

            case R.id.langRadio1:
                setLocationRu();
                saveAction();
                finish();
                startActivity(new Intent(Settings.this, Settings.class));
                break;
            case R.id.langRadio2:
                setLocationEn();
                saveAction();
                finish();
                startActivity(new Intent(Settings.this, Settings.class));
                break;
            case R.id.soundsRadio1:

                break;
            case R.id.soundsRadio2:

                break;
            case R.id.musicRadio1:

                break;
            case R.id.musicRadio2:

                break;

        }
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
}
