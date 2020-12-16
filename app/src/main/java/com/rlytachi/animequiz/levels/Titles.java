package com.rlytachi.animequiz.levels;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.jaeger.library.StatusBarUtil;
import com.rlytachi.animequiz.Game;
import com.rlytachi.animequiz.Language;
import com.rlytachi.animequiz.LevelChoice;
import com.rlytachi.animequiz.MyContextWrapper;
import com.rlytachi.animequiz.PromoStorage;
import com.rlytachi.animequiz.R;
import com.rlytachi.animequiz.Score;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;

import static com.rlytachi.animequiz.PromoStorage.*;
import static com.rlytachi.animequiz.Settings.*;

public class Titles extends AppCompatActivity {

    //Shared Preferences
    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_LANG = "myLanguage";
    public static final String APP_PREFERENCES_FIRST_SESSION_TITLES_1 = "firstSessionTitles1";
    public static final String APP_PREFERENCES_FIRST_SESSION_TITLES_2 = "firstSessionTitles2";
    public static final String APP_PREFERENCES_FIRST_SESSION_TITLES_3 = "firstSessionTitles3";
    public static final String APP_PREFERENCES_FIRST_SESSION_TITLES_4 = "firstSessionTitles4";
    public static final String APP_PREFERENCES_FIRST_SESSION_TITLES_5 = "firstSessionTitles5";
    public static final int PROMO_VIEW_TARGET = 3;
    public InterstitialAd interstitialAd;

    LinkedList<String> questions = new LinkedList<>();
    LinkedList<Drawable> images = new LinkedList<>();
    Integer[] imageArrCount;
    int image = 0;
    int heartCount = 3;
    int id;
    int seconds = -1;
    int scoreEarnedSession = 0;
    int promoViewCount = 0;
    static boolean firstSessionTiLvl1 = true;
    static boolean firstSessionTiLvl2 = true;
    static boolean firstSessionTiLvl3 = true;
    static boolean firstSessionTiLvl4 = true;
    static boolean firstSessionTiLvl5 = true;

    boolean back = false;
    boolean finished = false;
    boolean finishedByWin = false;
    boolean timesUp = false;

    Dialog promoDialog, helpDialog, winDialog, lossDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titles);
        StatusBarUtil.setTransparent(this);
        promoDialog = new Dialog(Titles.this);
        helpDialog = new Dialog(Titles.this);
        winDialog = new Dialog(Titles.this);
        lossDialog = new Dialog(Titles.this);

        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        interstitialAd = new InterstitialAd(Titles.this);
        interstitialAd.setAdUnitId("ca-app-pub-7217958397153183/4052170352");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                Log.println(Log.INFO, "AD", "interstitialAd is loaded");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                interstitialAd.loadAd(new AdRequest.Builder().build());


                Log.println(Log.ERROR, "AD", "interstitialAd is failed to load");
            }

            @Override
            public void onAdOpened() {
                Log.println(Log.INFO, "AD", "interstitialAd is opened");
            }

            @Override
            public void onAdClosed() {
                playSound(out);
                back = true;
                finish();
                startActivity(new Intent(Titles.this, Game.class));
                super.onAdClosed();

                Log.println(Log.INFO, "AD", "interstitialAd is closed");
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        loadAction();
        ButtonsEvent buttonsEvent = new ButtonsEvent();
        if (buttonsEvent.isInterrupted()) buttonsEvent.interrupt();
        else buttonsEvent.start();

        Animation animHeart = AnimationUtils.loadAnimation(Titles.this, R.anim.anim_btn);
        final ImageView heart1 = findViewById(R.id.heart1);
        final ImageView heart2 = findViewById(R.id.heart2);
        final ImageView heart3 = findViewById(R.id.heart3);
        heart1.setAnimation(animHeart);
        heart2.setAnimation(animHeart);
        heart3.setAnimation(animHeart);

        new CountDownTimer(320000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (heart3.getVisibility() == View.VISIBLE) {
                    heart3.startAnimation(animHeart);
                } else if (heart2.getVisibility() == View.VISIBLE) {
                    heart2.startAnimation(animHeart);
                } else if (heart1.getVisibility() == View.VISIBLE) {
                    heart1.startAnimation(animHeart);
                }
            }

            @Override
            public void onFinish() {
            }
        }.start();

        final TextView mainTextView = findViewById(R.id.mainTextView);
        mainTextView.setOnClickListener(v -> {
            promoViewCount++;
            if (promoViewCount == PROMO_VIEW_TARGET) {
                promoDialog();
                promoViewCount = 0;
            }
        });

        langUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (LevelChoice.getLevel() == 1) level1Load();
        if (LevelChoice.getLevel() == 2) level2Load();
        if (LevelChoice.getLevel() == 3) level3Load();
        if (LevelChoice.getLevel() == 4) level4Load();
        if (LevelChoice.getLevel() == 5) level5Load();

        loadAction();
        imageArrCount = getRandomArray();
        nextQuestion();
    }

    @Override
    protected void onDestroy() {
        langUpdate();
        saveAction();
        image = 0;
        setId(-1);
        heartCount = 3;
        imageArrCount = getRandomArray();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playSound(out);
        back = true;
        saveAction();
        finish();
        startActivity(new Intent(Titles.this, Game.class));
    }


    @Override
    protected void onPause() {
        if (!back) {
            langUpdate();
            finish();
            startActivity(new Intent(Titles.this, Titles.class));
        }

        if (!finishedByWin)
            lossDialog();

        try {
            if (promoDialog.isShowing())
                promoDialog.dismiss();
            if (helpDialog.isShowing())
                helpDialog.dismiss();
            if (winDialog.isShowing())
                winDialog.dismiss();
            if (lossDialog.isShowing())
                lossDialog.dismiss();
        } catch (NullPointerException ignore) {
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

    public void saveAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = mShared.edit();
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_1, isFirstSessionTiLvl1());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_2, isFirstSessionTiLvl2());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_3, isFirstSessionTiLvl3());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_4, isFirstSessionTiLvl4());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_5, isFirstSessionTiLvl5());
        editor.putInt(Game.APP_PREFERENCES_GLOBAL_SCORE, Score.getScore());
        editor.putString(APP_PREFERENCES_LANG, Language.getLang());

        for (int i = 0; i < PromoStorage.getPromoStorage().length; i++) {
            editor.putString(APP_PREFERENCES_PROMO_CODES[i], PromoStorage.getPromoStorage()[i]);
        }

        editor.apply();
    }

    public void loadAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_TITLES_1)) {
            setFirstSessionTiLvl1(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_1, false));
        } else setFirstSessionTiLvl1(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_TITLES_2)) {
            setFirstSessionTiLvl2(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_2, false));
        } else setFirstSessionTiLvl2(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_TITLES_3)) {
            setFirstSessionTiLvl3(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_3, false));
        } else setFirstSessionTiLvl3(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_TITLES_4)) {
            setFirstSessionTiLvl4(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_4, false));
        } else setFirstSessionTiLvl4(true);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_TITLES_5)) {
            setFirstSessionTiLvl5(mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_TITLES_5, false));
        } else setFirstSessionTiLvl5(true);

        String[] temp = new String[PromoStorage.getPromoStorage().length];
        for (int i = 0; i < PromoStorage.getPromoStorage().length; i++) {
            temp[i] = mShared.getString(APP_PREFERENCES_PROMO_CODES[i], null);
        }
        PromoStorage.setPromoStorage(temp);
        Language.setLang(mShared.getString(APP_PREFERENCES_LANG, Locale.getDefault().getLanguage()));
    }


    @SuppressLint("SetTextI18n")
    public void promoDialog() {
        langUpdate();
        promoDialog.setContentView(R.layout.activity_promo);
        promoDialog.show();
        Animation animEnd = AnimationUtils.loadAnimation(this, R.anim.anim_end);
        final EditText input = promoDialog.findViewById(R.id.editTextPromoCode);
        final Button promoUse = promoDialog.findViewById(R.id.promoUse);
        final TextView scoreLeft = promoDialog.findViewById(R.id.promoScoreLeftView);
        final TextView promoWrongView = promoDialog.findViewById(R.id.promoWrongView);
        scoreLeft.setText(getString(R.string.scoreLeft) + " " + Score.getScore());

        promoUse.setOnClickListener(v -> {
            if (promoStorage(input.getText().toString().toLowerCase())) {
                Score.addScore(10);
                saveAction();
            } else {
                promoWrongView.setAnimation(animEnd);
                promoWrongView.startAnimation(animEnd);
                promoWrongView.setVisibility(View.INVISIBLE);
            }
            scoreLeft.setText(getString(R.string.scoreLeft) + " " + Score.getScore());
        });
    }

    //Победа
    @SuppressLint("SetTextI18n")
    public void winDialog() {
        langUpdate();
        try {
            winDialog.setContentView(R.layout.activity_win_action);
            if (!this.isFinishing()) {
                winDialog.show();
            }

            winDialog.setCancelable(false);
            finished = true;
            finishedByWin = true;
            final Button okButton = winDialog.findViewById(R.id.ok);
            final TextView earnedView = winDialog.findViewById(R.id.earned);

            if (LevelChoice.getLevel() == 1) {
                if (!isFirstSessionTiLvl1()) scoreEarnedSession = 2;
                setFirstSessionTiLvl1(false);
            }
            if (LevelChoice.getLevel() == 2) {
                if (!isFirstSessionTiLvl2()) scoreEarnedSession = 2;
                setFirstSessionTiLvl2(false);
            }
            if (LevelChoice.getLevel() == 3) {
                if (!isFirstSessionTiLvl3()) scoreEarnedSession = 2;
                setFirstSessionTiLvl3(false);
            }
            if (LevelChoice.getLevel() == 4) {
                if (!isFirstSessionTiLvl4()) scoreEarnedSession = 2;
                setFirstSessionTiLvl4(false);
            }
            if (LevelChoice.getLevel() == 5) {
                if (!isFirstSessionTiLvl5()) scoreEarnedSession = 2;
                setFirstSessionTiLvl5(false);
            }

            earnedView.setText(getString(R.string.earned) + " " + scoreEarnedSession + " " + getString(R.string.scores));
            Score.addScore(scoreEarnedSession);

            back = true;
            saveAction();
            okButton.setOnClickListener(v -> {
                if (interstitialAd.isLoaded()) {
                    winDialog.dismiss();
                    interstitialAd.show();
                } else {
                    playSound(out);
                    winDialog.dismiss();
                    back = true;
                    finish();
                    startActivity(new Intent(Titles.this, Game.class));
                }
            });
        } catch (RuntimeException ignore) {
        }
    }

    //Проигрыш
    public void lossDialog() {
        langUpdate();
        lossDialog.setContentView(R.layout.activity_loss);
        finished = true;
        if (!this.isFinishing()) {
            lossDialog.show();
        }

        back = true;
        lossDialog.setCancelable(false);
        scoreEarnedSession = 0;
        final Button okButton = lossDialog.findViewById(R.id.ok);
        okButton.setOnClickListener(v -> {
            if (interstitialAd.isLoaded()) {
                lossDialog.dismiss();
                interstitialAd.show();
            } else {
                playSound(out);
                lossDialog.dismiss();
                back = true;
                finish();
                startActivity(new Intent(Titles.this, Game.class));
            }
        });
    }

    //Помощь
    @SuppressLint("SetTextI18n")
    public void helpDialog() {
        langUpdate();

        helpDialog.setContentView(R.layout.activity_help);
        if (!this.isFinishing()) {
            helpDialog.show();
        }


        Animation animButton = AnimationUtils.loadAnimation(Titles.this, R.anim.anim_btn);
        Animation animEnd = AnimationUtils.loadAnimation(this, R.anim.anim_end);
        final Button getHelpBtn = helpDialog.findViewById(R.id.takeHelpBtn);
        final Button getHeartBtn = helpDialog.findViewById(R.id.takeHeartBtn);
        final TextView scoreLeftView = helpDialog.findViewById(R.id.scoreLeftView);
        final TextView notEnoughView = helpDialog.findViewById(R.id.notEnoughView);

        scoreLeftView.setText(getString(R.string.scoreLeft) + " " + Score.getScore());
        getHelpBtn.setOnClickListener(v -> {
            playSound(in);
            getHelpBtn.setAnimation(animButton);
            getHelpBtn.startAnimation(animButton);
            if (Score.minusScore(5)) {
                getHelpAction();
                helpDialog.dismiss();
            } else {
                notEnoughView.setAnimation(animEnd);
                notEnoughView.setText(getString(R.string.notEnough));
                notEnoughView.startAnimation(animEnd);
                notEnoughView.setVisibility(View.INVISIBLE);
            }
            scoreLeftView.setText(getString(R.string.scoreLeft) + " " + Score.getScore());
            saveAction();
        });

        getHeartBtn.setOnClickListener(v -> {
            playSound(in);
            getHeartBtn.setAnimation(animButton);
            getHeartBtn.startAnimation(animButton);
            if (Score.minusScore(5)) {
                getHeartAction();
                helpDialog.dismiss();
            } else {
                notEnoughView.setAnimation(animEnd);
                notEnoughView.setText(getString(R.string.notEnough));
                notEnoughView.startAnimation(animEnd);
                notEnoughView.setVisibility(View.INVISIBLE);
            }
            scoreLeftView.setText(getString(R.string.scoreLeft) + " " + Score.getScore());
            saveAction();
        });
    }

    public void getHelpAction() {
        final Button btn1 = findViewById(R.id.btnCh1);
        final Button btn2 = findViewById(R.id.btnCh2);
        final Button btn3 = findViewById(R.id.btnCh3);
        final Button btn4 = findViewById(R.id.btnCh4);

        String answer = questions.get(getId());

        if (btn1.getText().equals(answer)) {
            btn2.setEnabled(false);
            btn3.setEnabled(false);
            btn4.setEnabled(false);
            btn2.setBackgroundResource(R.drawable.btn_1_locked);
            btn3.setBackgroundResource(R.drawable.btn_1_locked);
            btn4.setBackgroundResource(R.drawable.btn_1_locked);
        } else if (btn2.getText().equals(answer)) {
            btn1.setEnabled(false);
            btn3.setEnabled(false);
            btn4.setEnabled(false);
            btn1.setBackgroundResource(R.drawable.btn_1_locked);
            btn3.setBackgroundResource(R.drawable.btn_1_locked);
            btn4.setBackgroundResource(R.drawable.btn_1_locked);
        } else if (btn3.getText().equals(answer)) {
            btn1.setEnabled(false);
            btn2.setEnabled(false);
            btn4.setEnabled(false);
            btn1.setBackgroundResource(R.drawable.btn_1_locked);
            btn2.setBackgroundResource(R.drawable.btn_1_locked);
            btn4.setBackgroundResource(R.drawable.btn_1_locked);
        } else if (btn4.getText().equals(answer)) {
            btn1.setEnabled(false);
            btn2.setEnabled(false);
            btn3.setEnabled(false);
            btn1.setBackgroundResource(R.drawable.btn_1_locked);
            btn2.setBackgroundResource(R.drawable.btn_1_locked);
            btn3.setBackgroundResource(R.drawable.btn_1_locked);
        }
    }

    public void getHeartAction() {
        final ImageView heart1 = findViewById(R.id.heart1);
        final ImageView heart2 = findViewById(R.id.heart2);
        final ImageView heart3 = findViewById(R.id.heart3);

        heartCount = 3;
        heart1.setVisibility(View.VISIBLE);
        heart2.setVisibility(View.VISIBLE);
        heart3.setVisibility(View.VISIBLE);
    }

    //Возвращает True, если ID текста кнопки совпадает с ID изображения
    public boolean getAnswer(String str) {
        Animation animEnd = AnimationUtils.loadAnimation(this, R.anim.anim_end);
        final TextView info = findViewById(R.id.informationText);
        boolean answer = questions.get(getId()).equals(str);
        info.setText(answer ? R.string.correct : R.string.wrong);
        info.startAnimation(animEnd);
        info.setVisibility(View.INVISIBLE);
        return questions.get(getId()).equals(str);
    }

    //Минус жизни
    public void minusHeart() {
        final ImageView heart1 = findViewById(R.id.heart1);
        final ImageView heart2 = findViewById(R.id.heart2);
        final ImageView heart3 = findViewById(R.id.heart3);
        heartCount--;
        playSound(out);
        if (heart3.getVisibility() == View.VISIBLE) {
            heart3.setVisibility(View.INVISIBLE);
        } else if (heart2.getVisibility() == View.VISIBLE) {
            heart2.setVisibility(View.INVISIBLE);
        } else if (heart1.getVisibility() == View.VISIBLE) {
            lossDialog();
            heart1.setVisibility(View.INVISIBLE);
        }
    }

    //Вовзращает ID изображения, вопроса, ответа в LinkedList.
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static boolean isFirstSessionTiLvl1() {
        return firstSessionTiLvl1;
    }

    public static void setFirstSessionTiLvl1(boolean firstSessionTiLvl1) {
        Titles.firstSessionTiLvl1 = firstSessionTiLvl1;
    }

    public static boolean isFirstSessionTiLvl2() {
        return firstSessionTiLvl2;
    }

    public static void setFirstSessionTiLvl2(boolean firstSessionTiLvl2) {
        Titles.firstSessionTiLvl2 = firstSessionTiLvl2;
    }

    public static boolean isFirstSessionTiLvl3() {
        return firstSessionTiLvl3;
    }

    public static void setFirstSessionTiLvl3(boolean firstSessionTiLvl3) {
        Titles.firstSessionTiLvl3 = firstSessionTiLvl3;
    }

    public static boolean isFirstSessionTiLvl4() {
        return firstSessionTiLvl4;
    }

    public static void setFirstSessionTiLvl4(boolean firstSessionTiLvl4) {
        Titles.firstSessionTiLvl4 = firstSessionTiLvl4;
    }

    public static boolean isFirstSessionTiLvl5() {
        return firstSessionTiLvl5;
    }

    public static void setFirstSessionTiLvl5(boolean firstSessionTiLvl5) {
        Titles.firstSessionTiLvl5 = firstSessionTiLvl5;
    }

    public void nextQuestion() {
        final ImageView characterView = findViewById(R.id.characterView);
        final Button btn1 = findViewById(R.id.btnCh1);
        final Button btn2 = findViewById(R.id.btnCh2);
        final Button btn3 = findViewById(R.id.btnCh3);
        final Button btn4 = findViewById(R.id.btnCh4);

        btn1.setEnabled(true);
        btn2.setEnabled(true);
        btn3.setEnabled(true);
        btn4.setEnabled(true);

        btn1.setBackgroundResource(R.drawable.btn_1);
        btn2.setBackgroundResource(R.drawable.btn_1);
        btn3.setBackgroundResource(R.drawable.btn_1);
        btn4.setBackgroundResource(R.drawable.btn_1);

        if (image == images.size() && heartCount > 0) winDialog();
        if (seconds == 0 && heartCount == 0) lossDialog();

        try {
            //Свайп изображения
            setId(imageArrCount[image]);
            characterView.setImageDrawable(images.get(getId()));
            image++;
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }

        Integer[] buttonArrCount = getRandomArrayButton(getRandomArray());
        //Обновление кнопок

        try {
            btn1.setText(questions.get(buttonArrCount[0]));
            btn2.setText(questions.get(buttonArrCount[1]));
            btn3.setText(questions.get(buttonArrCount[2]));
            btn4.setText(questions.get(buttonArrCount[3]));
        } catch (NullPointerException exception) {
            exception.getStackTrace();
        }
    }

    //Сгенерировать рандомный массив с индексами
    public Integer[] getRandomArray() {
        //если для изображения, то нужно все значения в произвольном порядке
        Integer[] arr = new Integer[questions.size()];

        try {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = i;
            }
            Collections.shuffle(Arrays.asList(arr));
            return arr;
        } catch (ArrayIndexOutOfBoundsException exception) {
            return arr;
        }
    }

    //Сгенерировать рандомный массив с индексами
    public Integer[] getRandomArrayButton(Integer[] arr) {
        //если для кнопок, то нужно 4 значения с 1 верным
        Integer[] arrForButtons = new Integer[4];
        try {

            //Заполнение массива рандомными числами
            for (int i = 0; i < arrForButtons.length; i++) {
                if (arr[i] == getId()) continue;
                arrForButtons[i] = arr[i];
            }

            //Если есть null, то заменить на верный ID
            for (int i = 0; i < arrForButtons.length; i++) {
                if (arrForButtons[i] == null) {
                    arrForButtons[i] = getId();
                    break;
                }
            }

            //Если нет верного ID - добавить
            if (arrForButtons[0] == getId()) return arrForButtons;
            else if (arrForButtons[1] == getId()) return arrForButtons;
            else if (arrForButtons[2] == getId()) return arrForButtons;
            else if (arrForButtons[3] == getId()) return arrForButtons;
            else arrForButtons[0] = getId();

            Collections.shuffle(Arrays.asList(arrForButtons));

            return arrForButtons;
        } catch (ArrayIndexOutOfBoundsException | NullPointerException exception) {
            exception.getStackTrace();
            return arrForButtons;
        }
    }

    //Таймер 16000млс 1000млс
    public class myTimer extends CountDownTimer {
        final TextView timer = findViewById(R.id.timerTextView);
        final TextView info = findViewById(R.id.informationText);
        Animation animEnd = AnimationUtils.loadAnimation(Titles.this, R.anim.anim_end);

        //Конструктор таймера
        public myTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            info.setAnimation(animEnd);
        }

        //Действие каждый тик
        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long millisUntilFinished) {
            if (finished) {
                info.setText("");
            } else {
                seconds = (int) (millisUntilFinished / 1000);
                timer.setText(seconds + getString(R.string.seconds));
            }
            saveAction();
        }

        //Метод после завершения таймера. Действие требуется только в случае истечения времени.
        @Override
        public void onFinish() {
            if (heartCount > 0 && seconds == 0) {
                info.setText(R.string.timesUp);
                info.startAnimation(animEnd);
                info.setVisibility(View.INVISIBLE);

                minusHeart();

                if (heartCount > 0)
                    nextQuestion();

                timesUp = true;
            }
        }
    }

    //Обработчик кнопок
    public class ButtonsEvent extends Thread implements View.OnClickListener {
        //Buttons
        final ImageView backView = findViewById(R.id.backView);
        final ImageView helpView = findViewById(R.id.helpView);
        final Button btn1 = findViewById(R.id.btnCh1);
        final Button btn2 = findViewById(R.id.btnCh2);
        final Button btn3 = findViewById(R.id.btnCh3);
        final Button btn4 = findViewById(R.id.btnCh4);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        //Threads
        myTimer myTimer = new myTimer(11000, 1000);

        @Override
        public void run() {
            super.run();
            //Click listener
            backView.setOnClickListener(this);
            helpView.setOnClickListener(this);
            btn1.setOnClickListener(this);
            btn2.setOnClickListener(this);
            btn3.setOnClickListener(this);
            btn4.setOnClickListener(this);

            //Запуск потока таймера
            myTimer.start();

            //Полоса времени
            progressBar.setMax(10);

            //Проверка статусов
            while (true) {
                progressBar.setProgress(seconds);
                if (finished) {
                    this.interrupt();
                    break;
                }
                if (timesUp) {
                    myTimer.start();
                    timesUp = false;
                }
            }
        }

        public void firstEarn() {
            if (LevelChoice.getLevel() == 1) {
                if (isFirstSessionTiLvl1()) scoreEarnedSession++;
            }
            if (LevelChoice.getLevel() == 2) {
                if (isFirstSessionTiLvl2()) scoreEarnedSession++;
            }
            if (LevelChoice.getLevel() == 3) {
                if (isFirstSessionTiLvl3()) scoreEarnedSession++;
            }
            if (LevelChoice.getLevel() == 4) {
                if (isFirstSessionTiLvl4()) scoreEarnedSession++;
            }
            if (LevelChoice.getLevel() == 5) {
                if (isFirstSessionTiLvl5()) scoreEarnedSession++;
            }
        }

        //Обработчик нажатий
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            Animation animation = AnimationUtils.loadAnimation(Titles.this, R.anim.anim_btn);
            btn1.setAnimation(animation);
            btn2.setAnimation(animation);
            btn3.setAnimation(animation);
            btn4.setAnimation(animation);

            switch (v.getId()) {
                //Назад
                case R.id.backView:
                    playSound(out);
                    back = true;
                    finish();
                    startActivity(new Intent(Titles.this, Game.class));
                    myTimer.onFinish();
                    break;

                //Помощь
                case R.id.helpView:
                    playSound(in);
                    helpDialog();
                    break;

                //Кнопки
                //Если ответ не совпадает, минус сердце
                //Финиш таймера, следующий вопрос и запуск таймера
                case R.id.btnCh1:
                    playSound(in);
                    if (!getAnswer(btn1.getText().toString())) {
                        minusHeart();
                        btn2.startAnimation(animation);
                        btn2.clearAnimation();
                    } else {
                        firstEarn();

                        myTimer.onFinish();
                        nextQuestion();
                        myTimer.start();
                        btn1.startAnimation(animation);
                        btn2.startAnimation(animation);
                        btn3.startAnimation(animation);
                        btn4.startAnimation(animation);
                    }

                    break;

                case R.id.btnCh2:
                    playSound(in);
                    if (!getAnswer(btn2.getText().toString())) {
                        minusHeart();
                        btn1.startAnimation(animation);
                        btn1.clearAnimation();
                    } else {
                        firstEarn();

                        myTimer.onFinish();
                        nextQuestion();
                        myTimer.start();
                        btn1.startAnimation(animation);
                        btn2.startAnimation(animation);
                        btn3.startAnimation(animation);
                        btn4.startAnimation(animation);
                    }

                    break;

                case R.id.btnCh3:
                    playSound(in);
                    if (!getAnswer(btn3.getText().toString())) {
                        minusHeart();
                        btn4.startAnimation(animation);
                        btn4.clearAnimation();
                    } else {
                        firstEarn();

                        myTimer.onFinish();
                        nextQuestion();
                        myTimer.start();
                        btn1.startAnimation(animation);
                        btn2.startAnimation(animation);
                        btn3.startAnimation(animation);
                        btn4.startAnimation(animation);
                    }

                    break;

                case R.id.btnCh4:
                    playSound(in);
                    if (!getAnswer(btn4.getText().toString())) {
                        minusHeart();
                        btn3.startAnimation(animation);
                        btn3.clearAnimation();
                    } else {
                        firstEarn();

                        myTimer.onFinish();
                        nextQuestion();
                        myTimer.start();
                        btn1.startAnimation(animation);
                        btn2.startAnimation(animation);
                        btn3.startAnimation(animation);
                        btn4.startAnimation(animation);
                    }
                    break;
            }
        }
    }

    //Levels
    @SuppressLint("UseCompatLoadingForDrawables")
    public void level1Load() {
        questions.add("bakuman");
        questions.add("boku no hero academia");
        questions.add("charlotte");
        questions.add("children who chase lost voices from deep below");
        questions.add("chuunibyou demo koi ga shitai!");
        questions.add("cross road");
        questions.add("death note");
        questions.add("eden of the east");
        questions.add("hello world");
        questions.add("hyouka");
        questions.add("kiseijuu sei no kakuritsu");
        questions.add("kyoukai no kanata");
        questions.add("land of the lustrous");
        questions.add("let s decorate the promised flowers in the morning of farewells");
        questions.add("naruto");
        questions.add("no game no life");
        questions.add("re zero");
        questions.add("shaman king");
        questions.add("sword art online");
        questions.add("toradora");
        images.add(getDrawable(R.drawable.bakuman));
        images.add(getDrawable(R.drawable.boku_no_hero_academia));
        images.add(getDrawable(R.drawable.charlotte));
        images.add(getDrawable(R.drawable.children_who_chase_lost_voices_from_deep_below));
        images.add(getDrawable(R.drawable.chuunibyou_demo_koi_ga_shitai__));
        images.add(getDrawable(R.drawable.cross_road));
        images.add(getDrawable(R.drawable.death_note));
        images.add(getDrawable(R.drawable.eden_of_the_east));
        images.add(getDrawable(R.drawable.hello_world));
        images.add(getDrawable(R.drawable.hyouka));
        images.add(getDrawable(R.drawable.kiseijuu__sei_no_kakuritsu));
        images.add(getDrawable(R.drawable.kyoukai_no_kanata));
        images.add(getDrawable(R.drawable.land_of_the_lustrous));
        images.add(getDrawable(R.drawable.let_s_decorate_the_promised_flowers_in_the_morning_of_farewells));
        images.add(getDrawable(R.drawable.naruto_anime));
        images.add(getDrawable(R.drawable.no_game_no_life));
        images.add(getDrawable(R.drawable.re_zero));
        images.add(getDrawable(R.drawable.shaman_king));
        images.add(getDrawable(R.drawable.sword_art_online));
        images.add(getDrawable(R.drawable.toradora));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level2Load() {
        questions.add("akira");
        questions.add("boku dake ga inai machi");
        questions.add("dragon ball");
        questions.add("fate stay night");
        questions.add("jojos bizarre adventure");
        questions.add("kami nomi zo shiru sekai");
        questions.add("kamisama hajimemashita");
        questions.add("kill la kill");
        questions.add("kiznaiver");
        questions.add("kono subarashii sekai ni shukufuku wo");
        questions.add("kuroshitsuji");
        questions.add("made in abyss");
        questions.add("mahoutsukai no yome");
        questions.add("mob psycho 100");
        questions.add("noragami");
        questions.add("osomatsu san");
        questions.add("relife");
        questions.add("sakasama no patema");
        questions.add("tsukimonogatari");
        questions.add("zankyou no terror");
        images.add(getDrawable(R.drawable.akira));
        images.add(getDrawable(R.drawable.boku_dake_ga_inai_machi));
        images.add(getDrawable(R.drawable.dragon_ball));
        images.add(getDrawable(R.drawable.fate_stay_night));
        images.add(getDrawable(R.drawable.jojos_bizarre_adventure));
        images.add(getDrawable(R.drawable.kami_nomi_zo_shiru_sekai));
        images.add(getDrawable(R.drawable.kamisama_hajimemashita));
        images.add(getDrawable(R.drawable.kill_la_kill));
        images.add(getDrawable(R.drawable.kiznaiver));
        images.add(getDrawable(R.drawable.kono_subarashii_sekai_ni_shukufuku_wo_));
        images.add(getDrawable(R.drawable.kuroshitsuji));
        images.add(getDrawable(R.drawable.made_in_abyss));
        images.add(getDrawable(R.drawable.mahoutsukai_no_yome));
        images.add(getDrawable(R.drawable.mob_psycho_100));
        images.add(getDrawable(R.drawable.noragami));
        images.add(getDrawable(R.drawable.osomatsu_san));
        images.add(getDrawable(R.drawable.relife));
        images.add(getDrawable(R.drawable.sakasama_no_patema));
        images.add(getDrawable(R.drawable.tsukimonogatari));
        images.add(getDrawable(R.drawable.zankyou_no_terror));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level3Load() {
        questions.add("91 days");
        questions.add("clannad");
        questions.add("durarara");
        questions.add("ergo proxy");
        questions.add("fairy tail");
        questions.add("grisaia no rakuen");
        questions.add("kanon");
        questions.add("karigurashi no arrietty");
        questions.add("kimi ni todoke");
        questions.add("kimi no na wa");
        questions.add("kobayashi san chi no maid dragon");
        questions.add("kokoro connect");
        questions.add("kokoro ga sakebitagatterunda");
        questions.add("one piece");
        questions.add("ore monogatari");
        questions.add("psycho pass");
        questions.add("quanzhi gaoshou");
        questions.add("tamako love story");
        questions.add("tokyo ghoul");
        questions.add("yuri on ice");
        images.add(getDrawable(R.drawable._91_days));
        images.add(getDrawable(R.drawable.clannad));
        images.add(getDrawable(R.drawable.durarara));
        images.add(getDrawable(R.drawable.ergo_proxy));
        images.add(getDrawable(R.drawable.fairy_tail));
        images.add(getDrawable(R.drawable.grisaia_no_rakuen));
        images.add(getDrawable(R.drawable.kanon));
        images.add(getDrawable(R.drawable.karigurashi_no_arrietty));
        images.add(getDrawable(R.drawable.kimi_ni_todoke));
        images.add(getDrawable(R.drawable.kimi_no_na_wa));
        images.add(getDrawable(R.drawable.kobayashi_san_chi_no_maid_dragon));
        images.add(getDrawable(R.drawable.kokoro_connect));
        images.add(getDrawable(R.drawable.kokoro_ga_sakebitagatterunda));
        images.add(getDrawable(R.drawable.one_piece));
        images.add(getDrawable(R.drawable.ore_monogatari__));
        images.add(getDrawable(R.drawable.psycho_pass));
        images.add(getDrawable(R.drawable.quanzhi_gaoshou));
        images.add(getDrawable(R.drawable.tamako_love_story));
        images.add(getDrawable(R.drawable.tokyo_ghoul));
        images.add(getDrawable(R.drawable.yuri_on_ice));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level4Load() {
        questions.add("angel beats!");
        questions.add("ao haru ride");
        questions.add("aoi bungaku series");
        questions.add("ao no exorcist");
        questions.add("binbougami ga");
        questions.add("bleach");
        questions.add("bongo stray dogs");
        questions.add("byousoku 5 centimeter");
        questions.add("detective conan");
        questions.add("d-frag!");
        questions.add("golden time");
        questions.add("inu x boku ss");
        questions.add("kekkai sensen");
        questions.add("new game!");
        questions.add("orange");
        questions.add("overlord");
        questions.add("penguin highway");
        questions.add("shigatsu wa kimi no uso");
        questions.add("to love ru");
        questions.add("yuru yuri");
        images.add(getDrawable(R.drawable.angel_beats_));
        images.add(getDrawable(R.drawable.ao_haru_ride));
        images.add(getDrawable(R.drawable.aoi_bungaku_series));
        images.add(getDrawable(R.drawable.ao_no_exorcist));
        images.add(getDrawable(R.drawable.binbougami_ga_));
        images.add(getDrawable(R.drawable.bleach));
        images.add(getDrawable(R.drawable.bongo_stray_dogs));
        images.add(getDrawable(R.drawable.byousoku_5_centimeter));
        images.add(getDrawable(R.drawable.detective_conan));
        images.add(getDrawable(R.drawable.d_frag_));
        images.add(getDrawable(R.drawable.golden_time));
        images.add(getDrawable(R.drawable.inu_x_boku_ss));
        images.add(getDrawable(R.drawable.kekkai_sensen));
        images.add(getDrawable(R.drawable.new_game_));
        images.add(getDrawable(R.drawable.orange));
        images.add(getDrawable(R.drawable.overlord_));
        images.add(getDrawable(R.drawable.penguin_highway));
        images.add(getDrawable(R.drawable.shigatsu_wa_kimi_no_uso));
        images.add(getDrawable(R.drawable.to_love_ru));
        images.add(getDrawable(R.drawable.yuru_yuri));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level5Load() {
        questions.add("aldnoah zero");
        questions.add("amaama to inazuma");
        questions.add("ano natsu de matteru");
        questions.add("another");
        questions.add("ansatsu kyoushitsu");
        questions.add("boku wa tomodachi ga sukunai");
        questions.add("boruto");
        questions.add("danganronpa");
        questions.add("free!");
        questions.add("fullmetal alchemist");
        questions.add("imouto sae ireba ii");
        questions.add("isshuukan friends");
        questions.add("just because!");
        questions.add("koi wa ameagari no you ni");
        questions.add("no.6");
        questions.add("one punch man");
        questions.add("ore no imouto ga konnani kawaii wake ga nai");
        questions.add("tonari no kaibutsu kun");
        questions.add("totsukuni no shoujo");
        questions.add("trinity seven");
        images.add(getDrawable(R.drawable.aldnoah_zero_));
        images.add(getDrawable(R.drawable.amaama_to_inazuma));
        images.add(getDrawable(R.drawable.ano_natsu_de_matteru));
        images.add(getDrawable(R.drawable.another));
        images.add(getDrawable(R.drawable.ansatsu_kyoushitsu));
        images.add(getDrawable(R.drawable.boku_wa_tomodachi_ga_sukunai));
        images.add(getDrawable(R.drawable.boruto));
        images.add(getDrawable(R.drawable.danganronpa));
        images.add(getDrawable(R.drawable.free_));
        images.add(getDrawable(R.drawable.fullmetal_alchemist));
        images.add(getDrawable(R.drawable.imouto_sae_ireba_ii));
        images.add(getDrawable(R.drawable.isshuukan_friends));
        images.add(getDrawable(R.drawable.just_because_));
        images.add(getDrawable(R.drawable.koi_wa_ameagari_no_you_ni));
        images.add(getDrawable(R.drawable.no_6_));
        images.add(getDrawable(R.drawable.one_punch_man));
        images.add(getDrawable(R.drawable.ore_no_imouto_ga_konnani_kawaii_wake_ga_nai));
        images.add(getDrawable(R.drawable.tonari_no_kaibutsu_kun));
        images.add(getDrawable(R.drawable.totsukuni_no_shoujo));
        images.add(getDrawable(R.drawable.trinity_seven));
    }
}