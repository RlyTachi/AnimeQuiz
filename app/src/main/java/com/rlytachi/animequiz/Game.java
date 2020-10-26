package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rlytachi.animequiz.levels.Characters;

public class Game extends AppCompatActivity implements View.OnClickListener {

    final int BUTTON_COUNT = 5;

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

        //View
        final TextView scoreCountView = findViewById(R.id.scoreCountView);
        scoreCountView.setText(Score.getScore() + " " + getString(R.string.scores));

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
        dialog(false, R.layout.activity_characters_levels, R.id.chLevel1, R.id.chLevel2, R.id.chLevel3, R.id.chLevel4, R.id.chLevel5, R.id.chScoreTextView);
        dialog(false, R.layout.activity_locations_levels, R.id.locLevel1, R.id.locLevel2, R.id.locLevel3, R.id.locLevel4, R.id.locLevel5, R.id.locScoreTextView);
        dialog(false, R.layout.activity_titles_levels, R.id.tiLevel1, R.id.tiLevel2, R.id.tiLevel3, R.id.tiLevel4, R.id.tiLevel5, R.id.tiScoreTextView);
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
                dialog(true, R.layout.activity_characters_levels, R.id.chLevel1, R.id.chLevel2, R.id.chLevel3, R.id.chLevel4, R.id.chLevel5, R.id.chScoreTextView);
                break;
            case R.id.locationsLayout:
                dialog(true, R.layout.activity_locations_levels, R.id.locLevel1, R.id.locLevel2, R.id.locLevel3, R.id.locLevel4, R.id.locLevel5, R.id.locScoreTextView);
                break;
            case R.id.titlesLayout:
                dialog(true, R.layout.activity_titles_levels, R.id.tiLevel1, R.id.tiLevel2, R.id.tiLevel3, R.id.tiLevel4, R.id.tiLevel5, R.id.tiScoreTextView);
                break;
            case R.id.itTextLayout:

                break;
            case R.id.eventsLayout:

                break;

            //Characters levels
            case R.id.chLevel1:
                startActivity(new Intent(this, Characters.class));
                break;
            case R.id.chLevel2:
                //startActivity(new Intent(this, Characters.class));
                break;
            case R.id.chLevel3:
                //startActivity(new Intent(this, Characters.class));
                break;
            case R.id.chLevel4:
                //startActivity(new Intent(this, Characters.class));
                break;
            case R.id.chLevel5:
                // startActivity(new Intent(this, Characters.class));
                break;

            //Locations levels
            case R.id.tiLevel1:
                //startActivity(new Intent(this, Characters.class));
                break;
            case R.id.tiLevel2:
                //startActivity(new Intent(this, Characters.class));
                break;
            case R.id.tiLevel3:
                //startActivity(new Intent(this, Characters.class));
                break;
            case R.id.tiLevel4:
                //startActivity(new Intent(this, Characters.class));
                break;
            case R.id.tiLevel5:
                // startActivity(new Intent(this, Characters.class));
                break;

        }
    }

    @SuppressLint("SetTextI18n")
    public void dialog(Boolean show, Integer resDialog, Integer resButton1, Integer resButton2, Integer resButton3, Integer resButton4, Integer resButton5, Integer resTextScore) {
        Dialog dialog = new Dialog(Game.this);
        dialog.setContentView(resDialog);
        if (show) dialog.show();

        int unlockedButton = 0;
        final TextView score = findViewById(resTextScore);

        //Dialog buttons
        final Button level1 = dialog.findViewById(resButton1);
        final Button level2 = dialog.findViewById(resButton2);
        final Button level3 = dialog.findViewById(resButton3);
        final Button level4 = dialog.findViewById(resButton4);
        final Button level5 = dialog.findViewById(resButton5);

        //Set clickListener
        level1.setOnClickListener(this);
        level2.setOnClickListener(this);
        level3.setOnClickListener(this);
        level4.setOnClickListener(this);
        level5.setOnClickListener(this);

        //Lock
        level2.setEnabled(false);
        level3.setEnabled(false);
        level4.setEnabled(false);
        level5.setEnabled(false);

        if (level1.isEnabled()) unlockedButton++;
        if (level2.isEnabled()) unlockedButton++;
        if (level3.isEnabled()) unlockedButton++;
        if (level4.isEnabled()) unlockedButton++;
        if (level5.isEnabled()) unlockedButton++;

        score.setText(getString(R.string.unlocked) + " " + unlockedButton + "/" + BUTTON_COUNT);
    }
}