package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Game extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final ImageView back = (ImageView) findViewById(R.id.backView);
        final ImageView unlock = (ImageView) findViewById(R.id.unlockView);
        back.setOnClickListener(this);
        unlock.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backView:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.unlockView:
                startActivity(new Intent(this, Unlock.class));
                break;

        }
    }
}