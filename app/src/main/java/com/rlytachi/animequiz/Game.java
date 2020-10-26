package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Game extends AppCompatActivity implements View.OnClickListener {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Layouts
        final LinearLayout characters = findViewById(R.id.charactersLayout);
        final TextView chScore = findViewById(R.id.chScoreTextView);

        final LinearLayout locations = findViewById(R.id.locationsLayout);
        final TextView locScore = findViewById(R.id.locScoreTextView);

        final LinearLayout titles = findViewById(R.id.titlesLayout);
        final TextView tiScore = findViewById(R.id.tiScoreTextView);

        final LinearLayout items = findViewById(R.id.itemsLayout);
        final TextView itScore = findViewById(R.id.itScoreTextView);

        final LinearLayout events = findViewById(R.id.eventsLayout);
        final TextView evScore = findViewById(R.id.evScoreTextView);

        //Bottom buttons
        final ImageView back = findViewById(R.id.backView);
        final ImageView unlock = findViewById(R.id.unlockView);

        //Listeners
        characters.setOnClickListener(this);
        locations.setOnClickListener(this);
        titles.setOnClickListener(this);
        items.setOnClickListener(this);
        events.setOnClickListener(this);
        back.setOnClickListener(this);
        unlock.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backView:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.unlockView:
                startActivity(new Intent(this, Unlock.class));
                break;
            case R.id.charactersLayout:
                startActivity(new Intent(this, Characters.class));
                break;

        }
    }
}