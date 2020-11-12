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

    @Deprecated
    public static Configuration setLocationRu() {
        Locale localeRu = new Locale("ru");
        Locale.setDefault(localeRu);
        Configuration configuration = new Configuration();
        configuration.locale = localeRu;
        lang = "ru";
        return configuration;
    }

    @Deprecated
    public static Configuration setLocationEn() {
        Locale localeEn = new Locale("en");
        Locale.setDefault(localeEn);
        Configuration configuration = new Configuration();
        configuration.locale = localeEn;
        lang = "en";
        return configuration;
    }

    /*
            try {
            System.out.println(Language.getLang());
            if (Language.getLang().equals("ru")) {
                MyContextWrapper.wrap(getBaseContext(), "ru");
            } else {
                MyContextWrapper.wrap(getBaseContext(), "en");
            }
        } catch (Exception ignore) {
        }
     */
    //getBaseContext().getResources().updateConfiguration(configuration, null);
}
