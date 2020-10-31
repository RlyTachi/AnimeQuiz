package com.rlytachi.animequiz;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rlytachi.animequiz.levels.Characters;

import java.util.Locale;

import static com.rlytachi.animequiz.Settings.in;
import static com.rlytachi.animequiz.Settings.out;
import static com.rlytachi.animequiz.Settings.playSound;

public class Game extends AppCompatActivity implements View.OnClickListener {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_LANG = "myLanguage";
    public static final String APP_PREFERENCES_GLOBAL_SCORE = "GlobalScore";
    public static final String APP_PREFERENCES_CHARACTERS_LEVELS = "CharactersLevels";
    public static final String APP_PREFERENCES_LOCATIONS_LEVELS = "LocationsLevels";
    public static final String APP_PREFERENCES_TITLES_LEVELS = "TitlesLevels";
    public static final String APP_PREFERENCES_ITEMS_LEVELS = "ItemsLevels";
    public static final String APP_PREFERENCES_EVENTS_LEVELS = "EventsLevels";
    final int BUTTON_COUNT = 5;
    int score = Score.getScore();
    LevelChoice ch = new LevelChoice("ch");
    LevelChoice loc = new LevelChoice("loc");
    LevelChoice ti = new LevelChoice("ti");
    LevelChoice it = new LevelChoice("it");
    LevelChoice ev = new LevelChoice("ev");
    Boolean[] chLevels = ch.getLevels();
    Boolean[] locLevels = loc.getLevels();
    Boolean[] tiLevels = ti.getLevels();
    Boolean[] itLevels = it.getLevels();
    Boolean[] evLevels = ev.getLevels();
    Dialog dialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);
        dialog = new Dialog(Game.this);

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

        loadAction();
        try {
            System.out.println(Language.getLang());
            if (Language.getLang().equals("ru")) {
                getBaseContext().getResources().updateConfiguration(Language.setLocationRu(), null);
            } else {
                getBaseContext().getResources().updateConfiguration(Language.setLocationEn(), null);
            }
        } catch (Exception ignore) {
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAction();
        refreshScore();
        dialog(false, R.layout.activity_characters_levels, R.id.chLevel1, R.id.chLevel2, R.id.chLevel3, R.id.chLevel4, R.id.chLevel5, R.id.chScoreTextView, ch);
        dialog(false, R.layout.activity_locations_levels, R.id.locLevel1, R.id.locLevel2, R.id.locLevel3, R.id.locLevel4, R.id.locLevel5, R.id.locScoreTextView, loc);
        dialog(false, R.layout.activity_titles_levels, R.id.tiLevel1, R.id.tiLevel2, R.id.tiLevel3, R.id.tiLevel4, R.id.tiLevel5, R.id.tiScoreTextView, ti);
    }

    @Override
    protected void onDestroy() {
        try {
            System.out.println(Language.getLang());
            if (Language.getLang().equals("ru")) {
                getBaseContext().getResources().updateConfiguration(Language.setLocationRu(), null);
            } else {
                getBaseContext().getResources().updateConfiguration(Language.setLocationEn(), null);
            }
        } catch (Exception ignore) {
        }
        saveAction();
        dialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playSound(out);
        finish();
        startActivity(new Intent(Game.this, MainActivity.class));
    }

    public void saveAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = mShared.edit();

        for (int i = 0; i < chLevels.length; i++) {
            editor.putBoolean(APP_PREFERENCES_CHARACTERS_LEVELS + i, chLevels[i]);
        }
        for (int i = 0; i < locLevels.length; i++) {
            editor.putBoolean(APP_PREFERENCES_LOCATIONS_LEVELS + i, locLevels[i]);
        }
        for (int i = 0; i < tiLevels.length; i++) {
            editor.putBoolean(APP_PREFERENCES_TITLES_LEVELS + i, tiLevels[i]);
        }
        for (int i = 0; i < itLevels.length; i++) {
            editor.putBoolean(APP_PREFERENCES_ITEMS_LEVELS + i, itLevels[i]);
        }
        for (int i = 0; i < evLevels.length; i++) {
            editor.putBoolean(APP_PREFERENCES_EVENTS_LEVELS + i, evLevels[i]);
        }
        editor.putString(APP_PREFERENCES_LANG, Language.getLang());
        editor.putInt(Game.APP_PREFERENCES_GLOBAL_SCORE, Score.getScore());

        editor.apply();
    }


    public void loadAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mShared.contains(APP_PREFERENCES_GLOBAL_SCORE)) {
            score = mShared.getInt(APP_PREFERENCES_GLOBAL_SCORE, Score.getScore());
            Score.setScore(score);
        }
        for (int i = 0; i < chLevels.length; i++) {
            chLevels[i] = mShared.getBoolean(APP_PREFERENCES_CHARACTERS_LEVELS + i, chLevels[i]);
            ch.setLevels(chLevels);
        }
        for (int i = 0; i < locLevels.length; i++) {
            locLevels[i] = mShared.getBoolean(APP_PREFERENCES_LOCATIONS_LEVELS + i, locLevels[i]);
            loc.setLevels(locLevels);
        }
        for (int i = 0; i < tiLevels.length; i++) {
            tiLevels[i] = mShared.getBoolean(APP_PREFERENCES_TITLES_LEVELS + i, tiLevels[i]);
            ti.setLevels(tiLevels);
        }
        for (int i = 0; i < itLevels.length; i++) {
            itLevels[i] = mShared.getBoolean(APP_PREFERENCES_ITEMS_LEVELS + i, itLevels[i]);
            it.setLevels(itLevels);
        }
        for (int i = 0; i < evLevels.length; i++) {
            evLevels[i] = mShared.getBoolean(APP_PREFERENCES_EVENTS_LEVELS + i, evLevels[i]);
            ev.setLevels(evLevels);
        }
        Language.setLang(mShared.getString(APP_PREFERENCES_LANG, Locale.getDefault().getLanguage()));
        saveAction();
        refreshScore();
    }

    @SuppressLint("SetTextI18n")
    public void refreshScore() {
        final TextView scoreCountView = findViewById(R.id.scoreCountView);
        scoreCountView.setText(score + " " + getString(R.string.scores));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backView:
                playSound(out);
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.unlockView:
                playSound(in);
                finish();
                startActivity(new Intent(this, Unlock.class));
                break;
            case R.id.charactersLayout:
                playSound(in);
                if (!this.isFinishing()) {
                    dialog(true, R.layout.activity_characters_levels, R.id.chLevel1, R.id.chLevel2, R.id.chLevel3, R.id.chLevel4, R.id.chLevel5, R.id.chScoreTextView, ch);
                }
                break;
            case R.id.locationsLayout:
                playSound(in);
                if (!this.isFinishing()) {
                    dialog(true, R.layout.activity_locations_levels, R.id.locLevel1, R.id.locLevel2, R.id.locLevel3, R.id.locLevel4, R.id.locLevel5, R.id.locScoreTextView, loc);
                }
                break;
            case R.id.titlesLayout:
                playSound(in);
                if (!this.isFinishing()) {
                    dialog(true, R.layout.activity_titles_levels, R.id.tiLevel1, R.id.tiLevel2, R.id.tiLevel3, R.id.tiLevel4, R.id.tiLevel5, R.id.tiScoreTextView, it);
                }
                break;
            case R.id.itTextLayout:
                playSound(in);
                if (!this.isFinishing()) {

                }
                break;
            case R.id.eventsLayout:
                playSound(in);
                if (!this.isFinishing()) {

                }

                break;

            //Characters levels
            case R.id.chLevel1:
                playSound(in);
                finish();
                startActivity(new Intent(this, Characters.class));
                LevelChoice.setLevel(1);

                saveAction();
                break;
            case R.id.chLevel2:
                playSound(in);
                if (!chLevels[1]) {
                    unlockDialog(chLevels, 1, 1);
                } else {
                    finish();
                    startActivity(new Intent(this, Characters.class));
                    LevelChoice.setLevel(2);
                }
                saveAction();
                break;
            case R.id.chLevel3:
                playSound(in);
                if (!chLevels[2]) {
                    unlockDialog(chLevels, 2, 1);
                } else {
                    finish();
                    startActivity(new Intent(this, Characters.class));
                    LevelChoice.setLevel(3);
                }
                break;
            case R.id.chLevel4:
                playSound(in);
                if (!chLevels[3]) {
                    unlockDialog(chLevels, 3, 1);
                } else {
                    finish();
                    startActivity(new Intent(this, Characters.class));
                    LevelChoice.setLevel(4);
                }
                break;
            case R.id.chLevel5:
                playSound(in);
                if (!chLevels[4]) {
                    unlockDialog(chLevels, 4, 1);
                } else {
                    finish();
                    startActivity(new Intent(this, Characters.class));
                    LevelChoice.setLevel(5);
                }
                break;

            //Locations levels
            case R.id.locLevel1:
                playSound(in);
                LevelChoice.setLevel(1);
                break;
            case R.id.locLevel2:
                playSound(in);
                if (!locLevels[4]) {
                    unlockDialog(locLevels, 1, 2);
                } else {
                    finish();
                    startActivity(new Intent(this, Characters.class));
                    LevelChoice.setLevel(2);
                }
                break;
            case R.id.locLevel3:
                playSound(in);
                if (!locLevels[4]) {
                    unlockDialog(locLevels, 2, 2);
                } else {
                    finish();
                    startActivity(new Intent(this, Characters.class));
                    LevelChoice.setLevel(3);
                }
                break;
            case R.id.locLevel4:
                playSound(in);
                if (!locLevels[4]) {
                    unlockDialog(locLevels, 3, 2);
                } else {
                    finish();
                    startActivity(new Intent(this, Characters.class));
                    LevelChoice.setLevel(4);
                }
                break;
            case R.id.locLevel5:
                playSound(in);
                if (!locLevels[4]) {
                    unlockDialog(locLevels, 4, 2);
                } else {
                    finish();
                    startActivity(new Intent(this, Characters.class));
                    LevelChoice.setLevel(5);
                }
                break;

        }
        refreshScore();
        saveAction();
    }

    @SuppressLint("SetTextI18n")
    public boolean unlockDialog(Boolean[] levels, int index, int type) {
        Dialog unlockDialog = new Dialog(Game.this);
        unlockDialog.setContentView(R.layout.activity_unlock_action);
        unlockDialog.show();
        Animation animButton = AnimationUtils.loadAnimation(Game.this, R.anim.anim_btn);
        Animation animEnd = AnimationUtils.loadAnimation(this, R.anim.anim_end);
        final Button okButton = unlockDialog.findViewById(R.id.unlockBtn);
        final TextView unlockScoreLeftView = unlockDialog.findViewById(R.id.unlockScoreLeftView);
        final TextView unlockWarning = unlockDialog.findViewById(R.id.unlockNotEnoughView);

        unlockScoreLeftView.setText(getString(R.string.scoreLeft) + " " + Score.getScore());
        okButton.setOnClickListener(v -> {
            okButton.setAnimation(animButton);
            okButton.startAnimation(animButton);
            if (Score.minusScore(20)) {
                playSound(out);
                unlockDialog.dismiss();
                levels[index] = true;
                refreshScore();
                saveAction();

                finish();
                startActivity(new Intent(this, Game.class));

                if (type == 1)
                    dialog(true, R.layout.activity_characters_levels, R.id.chLevel1, R.id.chLevel2, R.id.chLevel3, R.id.chLevel4, R.id.chLevel5, R.id.chScoreTextView, ch);
                if (type == 2)
                    dialog(true, R.layout.activity_locations_levels, R.id.locLevel1, R.id.locLevel2, R.id.locLevel3, R.id.locLevel4, R.id.locLevel5, R.id.locScoreTextView, loc);
                if (type == 3)
                    dialog(true, R.layout.activity_titles_levels, R.id.tiLevel1, R.id.tiLevel2, R.id.tiLevel3, R.id.tiLevel4, R.id.tiLevel5, R.id.tiScoreTextView, ti);

            } else {
                playSound(in);
                unlockWarning.setAnimation(animEnd);
                unlockWarning.startAnimation(animEnd);
                unlockWarning.setVisibility(View.INVISIBLE);
            }
        });
        return true;
    }

    @SuppressLint("SetTextI18n")
    public void dialog(Boolean show, Integer resDialog, Integer resButton1, Integer resButton2, Integer resButton3, Integer resButton4, Integer resButton5, Integer resTextScore, LevelChoice obj) {

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
        if (obj.getLevels()[0]) level1.setBackgroundResource(R.drawable.btn_1);
        if (obj.getLevels()[1]) level2.setBackgroundResource(R.drawable.btn_1);
        if (obj.getLevels()[2]) level3.setBackgroundResource(R.drawable.btn_1);
        if (obj.getLevels()[3]) level4.setBackgroundResource(R.drawable.btn_1);
        if (obj.getLevels()[4]) level5.setBackgroundResource(R.drawable.btn_1);

        if (obj.getLevels()[0]) unlockedButton++;
        if (obj.getLevels()[1]) unlockedButton++;
        if (obj.getLevels()[2]) unlockedButton++;
        if (obj.getLevels()[3]) unlockedButton++;
        if (obj.getLevels()[4]) unlockedButton++;

        score.setText(getString(R.string.unlocked) + " " + unlockedButton + "/" + BUTTON_COUNT);
    }
}