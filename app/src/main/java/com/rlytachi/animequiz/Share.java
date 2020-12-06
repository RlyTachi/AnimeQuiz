package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import static com.rlytachi.animequiz.Settings.in;
import static com.rlytachi.animequiz.Settings.out;
import static com.rlytachi.animequiz.Settings.playSound;

public class Share extends AppCompatActivity {

    boolean back = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);

        MobileAds.initialize(this);
        AdView adView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        final ImageView backButton = findViewById(R.id.backViewShare);
        backButton.setOnClickListener(v -> {
            playSound(out);
            back = true;
            finish();
            startActivity(new Intent(Share.this, MainActivity.class));
        });

        final LinearLayout vkView = findViewById(R.id.vkViewClick);
        vkView.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://vk.com/onoderadev")));
        });

        final LinearLayout twitterView = findViewById(R.id.twitterViewClick);
        twitterView.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/emanongn")));
        });

        langUpdate();
    }

    @Override
    protected void onDestroy() {
        langUpdate();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playSound(out);
        back = true;
        finish();
        startActivity(new Intent(Share.this, MainActivity.class));
    }

    @Override
    protected void onPause() {
        if (!back) {
            langUpdate();
            finish();
            startActivity(new Intent(Share.this, Share.class));
        }

        super.onPause();
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
}