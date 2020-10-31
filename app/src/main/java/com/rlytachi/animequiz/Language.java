package com.rlytachi.animequiz;

import android.content.res.Configuration;

import java.util.Locale;

public class Language {

    private static String lang;

    public static void setLang(String lang) {
        Language.lang = lang;
    }

    public static String getLang() {
        return lang;
    }

    public static Configuration setLocationRu() {
        Locale localeRu = new Locale("ru");
        Locale.setDefault(localeRu);
        Configuration configuration = new Configuration();
        configuration.locale = localeRu;
        lang = "ru";
        return configuration;
    }

    public static Configuration setLocationEn() {
        Locale localeEn = new Locale("en");
        Locale.setDefault(localeEn);
        Configuration configuration = new Configuration();
        configuration.locale = localeEn;
        lang = "en";
        return configuration;
    }

    //getBaseContext().getResources().updateConfiguration(configuration, null);
}
