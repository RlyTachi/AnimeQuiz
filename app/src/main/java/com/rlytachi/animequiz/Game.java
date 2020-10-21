package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rlytachi.animequiz.levels.Characters;

public class Game extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //Layouts
        final LinearLayout characters = (LinearLayout) findViewById(R.id.charactersLayout);
        final LinearLayout locations = (LinearLayout) findViewById(R.id.locationsLayout);
        final LinearLayout titles = (LinearLayout) findViewById(R.id.titlesLayout);
        final LinearLayout items = (LinearLayout) findViewById(R.id.itemsLayout);
        final LinearLayout events = (LinearLayout) findViewById(R.id.eventsLayout);

        //Bottom buttons
        final ImageView back = (ImageView) findViewById(R.id.backView);
        final ImageView unlock = (ImageView) findViewById(R.id.unlockView);

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