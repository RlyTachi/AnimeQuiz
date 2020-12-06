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
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;
import com.rlytachi.animequiz.levels.Characters;
import com.rlytachi.animequiz.levels.Items;
import com.rlytachi.animequiz.levels.Locations;
import com.rlytachi.animequiz.levels.Titles;

import java.util.Locale;

import static com.rlytachi.animequiz.Settings.*;
import static com.rlytachi.animequiz.levels.Characters.*;
import static com.rlytachi.animequiz.levels.Locations.*;
import static com.rlytachi.animequiz.levels.Titles.*;
import static com.rlytachi.animequiz.levels.Items.*;

public class Game extends AppCompatActivity implements View.OnClickListener {

    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_LANG = "myLanguage";
    public static final String APP_PREFERENCES_GLOBAL_SCORE = "GlobalScore";
    public static final String APP_PREFERENCES_CHARACTERS_LEVELS = "CharactersLevels";
    public static final String APP_PREFERENCES_LOCATIONS_LEVELS = "LocationsLevels";
    public static final String APP_PREFERENCES_TITLES_LEVELS = "TitlesLevels";
    public static final String APP_PREFERENCES_ITEMS_LEVELS = "ItemsLevels";
    //    public static final String APP_PREFERENCES_EVENTS_LEVELS = "EventsLevels";
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
    //    Boolean[] evLevels = ev.getLevels();
    Dialog dialog, warning, unlockDialog;
    boolean back = false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        StatusBarUtil.setTransparent(this);
        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);
        dialog = new Dialog(Game.this);

        //Layouts
        final LinearLayout characters = findViewById(R.id.charactersLayout);
//        final TextView chScore = findViewById(R.id.chScoreTextView);

        final LinearLayout locations = findViewById(R.id.locationsLayout);
//        final TextView locScore = findViewById(R.id.locScoreTextView);

        final LinearLayout titles = findViewById(R.id.titlesLayout);
//        final TextView tiScore = findViewById(R.id.tiScoreTextView);

        final LinearLayout items = findViewById(R.id.itemsLayout);
//        final TextView itScore = findViewById(R.id.itScoreTextView);

//        final LinearLayout events = findViewById(R.id.eventsLayout);
//        final TextView evScore = findViewById(R.id.evScoreTextView);

        //Bottom buttons
        final ImageView back = findViewById(R.id.backView);
        final ImageView unlock = findViewById(R.id.unlockView);

        //Listeners
        characters.setOnClickListener(this);
        locations.setOnClickListener(this);
        titles.setOnClickListener(this);
        items.setOnClickListener(this);
//        events.setOnClickListener(this);
        back.setOnClickListener(this);
        unlock.setOnClickListener(this);

        loadAction();
        langUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAction();
        refreshScore();
        dialog(false, R.layout.activity_characters_levels, R.id.chLevel1, R.id.chLevel2, R.id.chLevel3, R.id.chLevel4, R.id.chLevel5, R.id.chScoreTextView, ch);
        dialog(false, R.layout.activity_locations_levels, R.id.locLevel1, R.id.locLevel2, R.id.locLevel3, R.id.locLevel4, R.id.locLevel5, R.id.locScoreTextView, loc);
        dialog(false, R.layout.activity_titles_levels, R.id.tiLevel1, R.id.tiLevel2, R.id.tiLevel3, R.id.tiLevel4, R.id.tiLevel5, R.id.tiScoreTextView, ti);
        dialog(false, R.layout.activity_items_levels, R.id.itLevel1, R.id.itLevel2, R.id.itLevel3, R.id.itLevel4, R.id.itLevel5, R.id.itScoreTextView, it);
        // dialog(false, R.layout.activity_events_levels, R.id.evLevel1, R.id.evLevel2, R.id.evLevel3, R.id.evLevel4, R.id.evLevel5, R.id.evScoreTextView, ev);
    }

    @Override
    protected void onPause() {
        if (!back) {
            langUpdate();
            finish();
            startActivity(new Intent(Game.this, Game.class));
        }

        try {
            if (unlockDialog.isShowing())
                unlockDialog.dismiss();
            if (dialog.isShowing())
                dialog.dismiss();
            if (warning.isShowing())
                warning.dismiss();
        } catch (NullPointerException ignore) {
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        langUpdate();
        saveAction();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playSound(out);
        back = true;
        finish();
        startActivity(new Intent(Game.this, MainActivity.class));
    }

    public void updateAfterPurchase(){
        if(Unlock.isFirstUnlockUpdate()){
            finish();
            startActivity(new Intent(Game.this, Game.class));
            dialog(false, R.layout.activity_characters_levels, R.id.chLevel1, R.id.chLevel2, R.id.chLevel3, R.id.chLevel4, R.id.chLevel5, R.id.chScoreTextView, ch);
            dialog(false, R.layout.activity_locations_levels, R.id.locLevel1, R.id.locLevel2, R.id.locLevel3, R.id.locLevel4, R.id.locLevel5, R.id.locScoreTextView, loc);
            dialog(false, R.layout.activity_titles_levels, R.id.tiLevel1, R.id.tiLevel2, R.id.tiLevel3, R.id.tiLevel4, R.id.tiLevel5, R.id.tiScoreTextView, ti);
            dialog(false, R.layout.activity_items_levels, R.id.itLevel1, R.id.itLevel2, R.id.itLevel3, R.id.itLevel4, R.id.itLevel5, R.id.itScoreTextView, it);
            Unlock.setFirstUnlockUpdate(false);
        }
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
//        for (int i = 0; i < evLevels.length; i++) {
//            editor.putBoolean(APP_PREFERENCES_EVENTS_LEVELS + i, evLevels[i]);
//        }
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
//        for (int i = 0; i < evLevels.length; i++) {
//            evLevels[i] = mShared.getBoolean(APP_PREFERENCES_EVENTS_LEVELS + i, evLevels[i]);
//            ev.setLevels(evLevels);
//        }
        Language.setLang(mShared.getString(APP_PREFERENCES_LANG, Locale.getDefault().getLanguage()));

        //Characters
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_1)) {
            setFirstSessionChLvl1(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_1, false));
        } else setFirstSessionChLvl1(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_2)) {
            setFirstSessionChLvl2(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_2, false));
        } else setFirstSessionChLvl2(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_3)) {
            setFirstSessionChLvl3(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_3, false));
        } else setFirstSessionChLvl3(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_4)) {
            setFirstSessionChLvl4(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_4, false));
        } else setFirstSessionChLvl4(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_5)) {
            setFirstSessionChLvl5(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_5, false));
        } else setFirstSessionChLvl5(true);

        //Locations
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_1)) {
            setFirstSessionLocLvl1(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_1, false));
        } else setFirstSessionLocLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_2)) {
            setFirstSessionLocLvl2(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_2, false));
        } else setFirstSessionLocLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_3)) {
            setFirstSessionLocLvl3(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_3, false));
        } else setFirstSessionLocLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_4)) {
            setFirstSessionLocLvl4(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_4, false));
        } else setFirstSessionLocLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_5)) {
            setFirstSessionLocLvl5(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_5, false));
        } else setFirstSessionLocLvl5(true);

        //Titles
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_TITLES_1)) {
            setFirstSessionTiLvl1(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_1, false));
        } else setFirstSessionTiLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_TITLES_2)) {
            setFirstSessionTiLvl2(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_2, false));
        } else setFirstSessionTiLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_TITLES_3)) {
            setFirstSessionTiLvl3(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_3, false));
        } else setFirstSessionTiLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_TITLES_4)) {
            setFirstSessionTiLvl4(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_4, false));
        } else setFirstSessionTiLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_TITLES_5)) {
            setFirstSessionTiLvl5(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_5, false));
        } else setFirstSessionTiLvl5(true);

        //Items
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_ITEMS_1)) {
            setFirstSessionItLvl1(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_ITEMS_1, false));
        } else setFirstSessionItLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_ITEMS_2)) {
            setFirstSessionItLvl2(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_ITEMS_2, false));
        } else setFirstSessionItLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_ITEMS_3)) {
            setFirstSessionItLvl3(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_ITEMS_3, false));
        } else setFirstSessionItLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_ITEMS_4)) {
            setFirstSessionItLvl4(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_ITEMS_4, false));
        } else setFirstSessionItLvl5(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_ITEMS_5)) {
            setFirstSessionItLvl5(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_ITEMS_5, false));
        } else setFirstSessionItLvl5(true);

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
        Animation animButton = AnimationUtils.loadAnimation(Game.this, R.anim.anim_btn);
        final LinearLayout characters = findViewById(R.id.charactersLayout);
        final LinearLayout locations = findViewById(R.id.locationsLayout);
        final LinearLayout titles = findViewById(R.id.titlesLayout);
        final LinearLayout items = findViewById(R.id.itemsLayout);
//        final LinearLayout events = findViewById(R.id.eventsLayout);

        //Levels dialogs
        switch (v.getId()) {
            case R.id.backView:
                playSound(out);
                back = true;
                finish();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.unlockView:
                playSound(in);
                back = true;
                finish();
                startActivity(new Intent(this, Unlock.class));
                break;
            case R.id.charactersLayout:
                characters.setAnimation(animButton);
                characters.startAnimation(animButton);
                playSound(in);
                if (!this.isFinishing()) {
                    dialog(true, R.layout.activity_characters_levels, R.id.chLevel1, R.id.chLevel2, R.id.chLevel3, R.id.chLevel4, R.id.chLevel5, R.id.chScoreTextView, ch);
                }
                break;
            case R.id.locationsLayout:
                locations.setAnimation(animButton);
                locations.startAnimation(animButton);
                playSound(in);
                if (!this.isFinishing()) {
                    dialog(true, R.layout.activity_locations_levels, R.id.locLevel1, R.id.locLevel2, R.id.locLevel3, R.id.locLevel4, R.id.locLevel5, R.id.locScoreTextView, loc);
                }
                break;
            case R.id.titlesLayout:
                titles.setAnimation(animButton);
                titles.startAnimation(animButton);
                playSound(in);
                if (!this.isFinishing()) {
                    dialog(true, R.layout.activity_titles_levels, R.id.tiLevel1, R.id.tiLevel2, R.id.tiLevel3, R.id.tiLevel4, R.id.tiLevel5, R.id.tiScoreTextView, ti);
                }
                break;
            case R.id.itemsLayout:
                items.setAnimation(animButton);
                items.startAnimation(animButton);
                playSound(in);
                if (!this.isFinishing()) {
                    dialog(true, R.layout.activity_items_levels, R.id.itLevel1, R.id.itLevel2, R.id.itLevel3, R.id.itLevel4, R.id.itLevel5, R.id.itScoreTextView, it);
                }
                break;
//            case R.id.eventsLayout:
//                events.setAnimation(animButton);
//                events.startAnimation(animButton);
//                playSound(in);
//                if (!this.isFinishing()) {
//                    dialog(true, R.layout.activity_events_levels, R.id.evLevel1, R.id.evLevel2, R.id.evLevel3, R.id.evLevel4, R.id.evLevel5, R.id.evScoreTextView, ev);
//                }
//                break;

            //Characters levels
            case R.id.chLevel1:
                playSound(in);
                back = true;
                finish();
                startActivity(new Intent(this, Characters.class));
                LevelChoice.setLevel(1);

                saveAction();
                break;
            case R.id.chLevel2:
                playSound(in);
                if (!chLevels[1]) {
                    unlockDialog(chLevels, 1, 1, 20);
                } else {
                    back = true;
                    finish();
                    startActivity(new Intent(this, Characters.class));
                    LevelChoice.setLevel(2);
                }
                saveAction();
                break;
            case R.id.chLevel3:
                playSound(in);
                if (chLevels[1]) {
                    if (!chLevels[2]) {
                        unlockDialog(chLevels, 2, 1, 20);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Characters.class));
                        LevelChoice.setLevel(3);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.chLevel4:
                playSound(in);
                if (chLevels[2]) {
                    if (!chLevels[3]) {
                        unlockDialog(chLevels, 3, 1, 30);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Characters.class));
                        LevelChoice.setLevel(4);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.chLevel5:
                playSound(in);
                if (chLevels[3]) {
                    if (!chLevels[4]) {
                        unlockDialog(chLevels, 4, 1, 30);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Characters.class));
                        LevelChoice.setLevel(5);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;

            //Locations levels
            case R.id.locLevel1:
                playSound(in);
                warningDialog();
                LevelChoice.setLevel(1);
                break;
            case R.id.locLevel2:
                playSound(in);
                if (!locLevels[1]) {
                    unlockDialog(locLevels, 1, 2, 30);
                } else {
                    back = true;
                    finish();
                    startActivity(new Intent(this, Locations.class));
                    LevelChoice.setLevel(2);
                }
                break;
            case R.id.locLevel3:
                playSound(in);
                if (locLevels[1]) {
                    if (!locLevels[2]) {
                        unlockDialog(locLevels, 2, 2, 30);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Locations.class));
                        LevelChoice.setLevel(3);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.locLevel4:
                playSound(in);
                if (locLevels[2]) {
                    if (!locLevels[3]) {
                        unlockDialog(locLevels, 3, 2, 60);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Locations.class));
                        LevelChoice.setLevel(4);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.locLevel5:
                playSound(in);
                if (locLevels[3]) {
                    if (!locLevels[4]) {
                        unlockDialog(locLevels, 4, 2, 60);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Locations.class));
                        LevelChoice.setLevel(5);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;

            //Titles levels
            case R.id.tiLevel1:
                playSound(in);
                LevelChoice.setLevel(1);
                back = true;
                finish();
                startActivity(new Intent(this, Titles.class));
                break;
            case R.id.tiLevel2:
                playSound(in);
                if (!tiLevels[1]) {
                    unlockDialog(tiLevels, 1, 3, 20);
                } else {
                    back = true;
                    finish();
                    startActivity(new Intent(this, Titles.class));
                    LevelChoice.setLevel(2);
                }
                break;
            case R.id.tiLevel3:
                playSound(in);
                if (tiLevels[1]) {
                    if (!tiLevels[2]) {
                        unlockDialog(tiLevels, 2, 3, 20);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Titles.class));
                        LevelChoice.setLevel(3);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tiLevel4:
                playSound(in);
                if (tiLevels[2]) {
                    if (!tiLevels[3]) {
                        unlockDialog(tiLevels, 3, 3, 30);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Titles.class));
                        LevelChoice.setLevel(4);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tiLevel5:
                playSound(in);
                if (tiLevels[3]) {
                    if (!tiLevels[4]) {
                        unlockDialog(tiLevels, 4, 3, 30);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Titles.class));
                        LevelChoice.setLevel(5);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;

            //Items levels
            case R.id.itLevel1:
                playSound(in);
                LevelChoice.setLevel(1);
                back = true;
                startActivity(new Intent(this, Items.class));
                break;
            case R.id.itLevel2:
                playSound(in);
                if (!itLevels[1]) {
                    unlockDialog(itLevels, 1, 4, 30);
                } else {
                    back = true;
                    finish();
                    startActivity(new Intent(this, Items.class));
                    LevelChoice.setLevel(2);
                }
                break;
            case R.id.itLevel3:
                playSound(in);
                if (itLevels[1]) {
                    if (!itLevels[2]) {
                        unlockDialog(itLevels, 2, 4, 30);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Items.class));
                        LevelChoice.setLevel(3);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.itLevel4:
                playSound(in);
                if (tiLevels[2]) {
                    if (!itLevels[3]) {
                        unlockDialog(itLevels, 3, 4, 40);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Items.class));
                        LevelChoice.setLevel(4);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.itLevel5:
                playSound(in);
                if (tiLevels[3]) {
                    if (!itLevels[4]) {
                        unlockDialog(itLevels, 4, 4, 40);
                    } else {
                        back = true;
                        finish();
                        startActivity(new Intent(this, Items.class));
                        LevelChoice.setLevel(5);
                    }
                } else {
                    Toast.makeText(this, getString(R.string.levelPrevious), Toast.LENGTH_SHORT).show();
                }
                break;

        }
        refreshScore();
        saveAction();
    }

    @SuppressLint("SetTextI18n")
    public boolean unlockDialog(Boolean[] levels, int index, int type, int cost) {
        langUpdate();
        unlockDialog = new Dialog(Game.this);
        unlockDialog.setContentView(R.layout.activity_unlock_action);
        unlockDialog.show();
        Animation animButton = AnimationUtils.loadAnimation(Game.this, R.anim.anim_btn);
        Animation animEnd = AnimationUtils.loadAnimation(this, R.anim.anim_end);
        final Button okButton = unlockDialog.findViewById(R.id.unlockBtn);
        final TextView unlockScoreLeftView = unlockDialog.findViewById(R.id.unlockScoreLeftView);
        final TextView unlockWarning = unlockDialog.findViewById(R.id.unlockNotEnoughView);
        final TextView unlockCost = unlockDialog.findViewById(R.id.unlockTextView);

        unlockCost.setText(getString(R.string.unlockLevel) + " " + cost + " " + getString(R.string.scores));

        unlockScoreLeftView.setText(getString(R.string.scoreLeft) + " " + Score.getScore());
        okButton.setOnClickListener(v -> {
            okButton.setAnimation(animButton);
            okButton.startAnimation(animButton);

            if (Score.minusScore(cost)) {
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
                if (type == 4)
                    dialog(true, R.layout.activity_items_levels, R.id.itLevel1, R.id.itLevel2, R.id.itLevel3, R.id.itLevel4, R.id.itLevel5, R.id.itScoreTextView, it);
//                if (type == 5)
//                    dialog(true, R.layout.activity_events_levels, R.id.evLevel1, R.id.evLevel2, R.id.evLevel3, R.id.evLevel4, R.id.evLevel5, R.id.evScoreTextView, ev);

            } else {
                playSound(in);
                unlockWarning.setAnimation(animEnd);
                unlockWarning.startAnimation(animEnd);
                unlockWarning.setVisibility(View.INVISIBLE);
                back = true;
                finish();
                startActivity(new Intent(this, Unlock.class));
            }
        });
        return true;
    }

    public void warningDialog() {
        langUpdate();
        warning = new Dialog(Game.this);
        warning.setContentView(R.layout.activity_warning_difficult);
        warning.show();

        Animation animation = AnimationUtils.loadAnimation(Game.this, R.anim.anim_btn);

        final Button ok = warning.findViewById(R.id.ok);
        final Button cancel = warning.findViewById(R.id.cancel);

        ok.setOnClickListener(v -> {
            ok.setAnimation(animation);
            playSound(in);
            back = true;
            finish();
            startActivity(new Intent(this, Locations.class));
        });

        cancel.setOnClickListener(v -> {
            playSound(out);
            cancel.setAnimation(animation);
            warning.dismiss();
        });
    }

    @SuppressLint("SetTextI18n")
    public void dialog(Boolean show, Integer resDialog, Integer resButton1, Integer resButton2, Integer resButton3, Integer resButton4, Integer resButton5, Integer resTextScore, LevelChoice obj) {
        langUpdate();
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

        if (Unlock.getUnlocked()) {
            level1.setBackgroundResource(R.drawable.btn_1);
            level2.setBackgroundResource(R.drawable.btn_1);
            level3.setBackgroundResource(R.drawable.btn_1);
            level4.setBackgroundResource(R.drawable.btn_1);
            level5.setBackgroundResource(R.drawable.btn_1);
            obj.setLevels(new Boolean[]{true, true, true, true, true});
        }

        if (obj.getType().equals("ch")) {
            if (!Characters.isFirstSessionChLvl1())
                level1.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Characters.isFirstSessionChLvl2())
                level2.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Characters.isFirstSessionChLvl3())
                level3.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Characters.isFirstSessionChLvl4())
                level4.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Characters.isFirstSessionChLvl5())
                level5.setBackgroundResource(R.drawable.btn_1_passed);
        } else if (obj.getType().equals("loc")) {
            if (!Locations.isFirstSessionLocLvl1())
                level1.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Locations.isFirstSessionLocLvl2())
                level2.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Locations.isFirstSessionLocLvl3())
                level3.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Locations.isFirstSessionLocLvl4())
                level4.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Locations.isFirstSessionLocLvl5())
                level5.setBackgroundResource(R.drawable.btn_1_passed);
        } else if (obj.getType().equals("ti")) {
            if (!Titles.isFirstSessionTiLvl1())
                level1.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Titles.isFirstSessionTiLvl2())
                level2.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Titles.isFirstSessionTiLvl3())
                level3.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Titles.isFirstSessionTiLvl4())
                level4.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Titles.isFirstSessionTiLvl5())
                level5.setBackgroundResource(R.drawable.btn_1_passed);
        } else if (obj.getType().equals("it")) {
            if (!Items.isFirstSessionItLvl1())
                level1.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Items.isFirstSessionItLvl2())
                level2.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Items.isFirstSessionItLvl3())
                level3.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Items.isFirstSessionItLvl4())
                level4.setBackgroundResource(R.drawable.btn_1_passed);
            if (!Items.isFirstSessionItLvl5())
                level5.setBackgroundResource(R.drawable.btn_1_passed);
        }

        score.setText(getString(R.string.unlocked) + " " + unlockedButton + "/" + BUTTON_COUNT);
    }
}