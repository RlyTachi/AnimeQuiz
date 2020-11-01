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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);

        MobileAds.initialize(this, "ca-app-pub-7217958397153183~4133073169");
        AdView adView = findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        final ImageView backButton = findViewById(R.id.backViewShare);
        backButton.setOnClickListener(v -> {
            playSound(out);
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

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playSound(out);
        finish();
        startActivity(new Intent(Share.this, MainActivity.class));
    }
}