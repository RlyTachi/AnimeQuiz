package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageView settings = (ImageView) findViewById(R.id.settingsView);
        final ImageView start = (ImageView) findViewById(R.id.startView);
        final ImageView share = (ImageView) findViewById(R.id.shareView);
        settings.setOnClickListener(this);
        start.setOnClickListener(this);
        share.setOnClickListener(this);
    }

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
}