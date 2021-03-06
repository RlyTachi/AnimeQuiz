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

public class Locations extends AppCompatActivity {

    //Shared Preferences
    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_LANG = "myLanguage";
    public static final String APP_PREFERENCES_FIRST_SESSION_LOCATIONS_1 = "firstSessionLocations1";
    public static final String APP_PREFERENCES_FIRST_SESSION_LOCATIONS_2 = "firstSessionLocations2";
    public static final String APP_PREFERENCES_FIRST_SESSION_LOCATIONS_3 = "firstSessionLocations3";
    public static final String APP_PREFERENCES_FIRST_SESSION_LOCATIONS_4 = "firstSessionLocations4";
    public static final String APP_PREFERENCES_FIRST_SESSION_LOCATIONS_5 = "firstSessionLocations5";
    public static final int PROMO_VIEW_TARGET = 3;
    public InterstitialAd interstitialAd;

    LinkedList<String> questions = new LinkedList<>();
    LinkedList<Drawable> images = new LinkedList<>();
    Integer[] imageArrCount;
    int image = 0;
    int heartCount = 1;
    int id;
    int seconds = -1;
    int scoreEarnedSession = 0;
    int promoViewCount = 0;
    static boolean firstSessionLocLvl1 = true;
    static boolean firstSessionLocLvl2 = true;
    static boolean firstSessionLocLvl3 = true;
    static boolean firstSessionLocLvl4 = true;
    static boolean firstSessionLocLvl5 = true;

    boolean back = false;
    boolean finished = false;
    boolean finishedByWin = false;
    boolean timesUp = false;

    Dialog promoDialog, helpDialog, winDialog, lossDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locations);
        StatusBarUtil.setTransparent(this);
        promoDialog = new Dialog(Locations.this);
        helpDialog = new Dialog(Locations.this);
        winDialog = new Dialog(Locations.this);
        lossDialog = new Dialog(Locations.this);

        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        interstitialAd = new InterstitialAd(Locations.this);
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
                startActivity(new Intent(Locations.this, Game.class));
                super.onAdClosed();

                Log.println(Log.INFO, "AD", "interstitialAd is closed");
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        loadAction();
        ButtonsEvent buttonsEvent = new ButtonsEvent();
        if (buttonsEvent.isInterrupted()) buttonsEvent.interrupt();
        else buttonsEvent.start();

        Animation animHeart = AnimationUtils.loadAnimation(Locations.this, R.anim.anim_btn);
        final ImageView heart1 = findViewById(R.id.heart1);
//        final ImageView heart2 = findViewById(R.id.heart2);
//        final ImageView heart3 = findViewById(R.id.heart3);
        heart1.setAnimation(animHeart);
//        heart2.setAnimation(animHeart);
//        heart3.setAnimation(animHeart);

        new CountDownTimer(320000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (heart1.getVisibility() == View.VISIBLE) {
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
        startActivity(new Intent(Locations.this, Game.class));
    }

    @Override
    protected void onPause() {
        if (!back) {
            langUpdate();
            finish();
            startActivity(new Intent(Locations.this, Locations.class));
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
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_1, isFirstSessionLocLvl1());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_2, isFirstSessionLocLvl2());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_3, isFirstSessionLocLvl3());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_4, isFirstSessionLocLvl4());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_LOCATIONS_5, isFirstSessionLocLvl5());
        editor.putInt(Game.APP_PREFERENCES_GLOBAL_SCORE, Score.getScore());
        editor.putString(APP_PREFERENCES_LANG, Language.getLang());

        for (int i = 0; i < PromoStorage.getPromoStorage().length; i++) {
            editor.putString(APP_PREFERENCES_PROMO_CODES[i], PromoStorage.getPromoStorage()[i]);
        }

        editor.apply();
    }

    public void loadAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
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
                if (!isFirstSessionLocLvl1()) scoreEarnedSession = 2;
                setFirstSessionLocLvl1(false);
            }
            if (LevelChoice.getLevel() == 2) {
                if (!isFirstSessionLocLvl2()) scoreEarnedSession = 2;
                setFirstSessionLocLvl2(false);
            }
            if (LevelChoice.getLevel() == 3) {
                if (!isFirstSessionLocLvl3()) scoreEarnedSession = 2;
                setFirstSessionLocLvl3(false);
            }
            if (LevelChoice.getLevel() == 4) {
                if (!isFirstSessionLocLvl4()) scoreEarnedSession = 2;
                setFirstSessionLocLvl4(false);
            }
            if (LevelChoice.getLevel() == 5) {
                if (!isFirstSessionLocLvl5()) scoreEarnedSession = 2;
                setFirstSessionLocLvl5(false);
            }

            earnedView.setText(getString(R.string.earned) + " " + scoreEarnedSession + " " + getString(R.string.scores));
            Score.addScore(scoreEarnedSession);
            back = true;

            saveAction();
            okButton.setOnClickListener(v -> {
                if (interstitialAd.isLoaded()) interstitialAd.show();
                else {
                    playSound(out);
                    winDialog.dismiss();
                    back = true;
                    finish();
                    startActivity(new Intent(Locations.this, Game.class));
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
            if (interstitialAd.isLoaded()) interstitialAd.show();
            else {
                playSound(out);
                lossDialog.dismiss();
                back = true;
                finish();
                startActivity(new Intent(Locations.this, Game.class));
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


        Animation animButton = AnimationUtils.loadAnimation(Locations.this, R.anim.anim_btn);
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

        getHeartBtn.setEnabled(false);
        getHeartBtn.setBackgroundResource(R.drawable.btn_1_locked);
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

        heartCount = 1;
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
//        final ImageView heart2 = findViewById(R.id.heart2);
//        final ImageView heart3 = findViewById(R.id.heart3);
        heartCount--;
        playSound(out);
        if (heart1.getVisibility() == View.VISIBLE) {
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

    public static boolean isFirstSessionLocLvl1() {
        return firstSessionLocLvl1;
    }

    public static void setFirstSessionLocLvl1(boolean firstSessionLocLvl1) {
        Locations.firstSessionLocLvl1 = firstSessionLocLvl1;
    }

    public static boolean isFirstSessionLocLvl2() {
        return firstSessionLocLvl2;
    }

    public static void setFirstSessionLocLvl2(boolean firstSessionLocLvl2) {
        Locations.firstSessionLocLvl2 = firstSessionLocLvl2;
    }

    public static boolean isFirstSessionLocLvl3() {
        return firstSessionLocLvl3;
    }

    public static void setFirstSessionLocLvl3(boolean firstSessionLocLvl3) {
        Locations.firstSessionLocLvl3 = firstSessionLocLvl3;
    }

    public static boolean isFirstSessionLocLvl4() {
        return firstSessionLocLvl4;
    }

    public static void setFirstSessionLocLvl4(boolean firstSessionLocLvl4) {
        Locations.firstSessionLocLvl4 = firstSessionLocLvl4;
    }

    public static boolean isFirstSessionLocLvl5() {
        return firstSessionLocLvl5;
    }

    public static void setFirstSessionLocLvl5(boolean firstSessionLocLvl5) {
        Locations.firstSessionLocLvl5 = firstSessionLocLvl5;
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
        Animation animEnd = AnimationUtils.loadAnimation(Locations.this, R.anim.anim_end);

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
                if (isFirstSessionLocLvl1()) scoreEarnedSession += 2;
            }
            if (LevelChoice.getLevel() == 2) {
                if (isFirstSessionLocLvl2()) scoreEarnedSession += 2;
            }
            if (LevelChoice.getLevel() == 3) {
                if (isFirstSessionLocLvl3()) scoreEarnedSession += 2;
            }
            if (LevelChoice.getLevel() == 4) {
                if (isFirstSessionLocLvl4()) scoreEarnedSession += 2;
            }
            if (LevelChoice.getLevel() == 5) {
                if (isFirstSessionLocLvl5()) scoreEarnedSession += 2;
            }
        }

        //Обработчик нажатий
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            Animation animation = AnimationUtils.loadAnimation(Locations.this, R.anim.anim_btn);
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
                    startActivity(new Intent(Locations.this, Game.class));
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
        questions.add("Aindcrad");
        questions.add("Anteiku");
        questions.add("Elkia Kingdom");
        questions.add("Gigas cedar");
        questions.add("Honnooji Academy");
        questions.add("King Chess Piece");
        questions.add("Port Mafia Base");
        questions.add("Seireitei");
        questions.add("Shiganshina District");
        questions.add("Silbern");
        questions.add("Soul King Palace");
        questions.add("True Cross Academy Town");
        questions.add("U.S.J");
        questions.add("Valley of the End");
        questions.add("Yuuei");
        images.add(getDrawable(R.drawable.aindcrad));
        images.add(getDrawable(R.drawable.anteiku));
        images.add(getDrawable(R.drawable.elkia_kingdom));
        images.add(getDrawable(R.drawable.gigas_cedar));
        images.add(getDrawable(R.drawable.honnji_academy));
        images.add(getDrawable(R.drawable.king_chess_piece));
        images.add(getDrawable(R.drawable.port_mafia_base));
        images.add(getDrawable(R.drawable.seireitei));
        images.add(getDrawable(R.drawable.shiganshina_district));
        images.add(getDrawable(R.drawable.silbern));
        images.add(getDrawable(R.drawable.soul_king_palace));
        images.add(getDrawable(R.drawable.true_cross_academy_town));
        images.add(getDrawable(R.drawable.u_s_j));
        images.add(getDrawable(R.drawable.valley_of_the_end));
        images.add(getDrawable(R.drawable.yuuei));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level2Load() {
        questions.add("Armed Detective Agency Office");
        questions.add("Brocken Fortress");
        questions.add("Heaven's Road Palace");
        questions.add("Kamdet");
        questions.add("Konoe Residence");
        questions.add("Mana River");
        questions.add("Misaki Atrium Arch");
        questions.add("Misaki Municipal High School");
        questions.add("New Hell");
        questions.add("Ruby Palace");
        questions.add("Sanguinem");
        questions.add("Star of Darkness Castle");
        questions.add("Sword Mastery Academy");
        questions.add("Trost District");
        questions.add("Uzumaki");
        images.add(getDrawable(R.drawable.armed_detective_agency_office));
        images.add(getDrawable(R.drawable.brocken_fortress));
        images.add(getDrawable(R.drawable.heaven_s_road_palace));
        images.add(getDrawable(R.drawable.kamdet));
        images.add(getDrawable(R.drawable.konoe_residence));
        images.add(getDrawable(R.drawable.mana_river));
        images.add(getDrawable(R.drawable.misaki_atrium_arch));
        images.add(getDrawable(R.drawable.misaki_municipal_high_school));
        images.add(getDrawable(R.drawable.new_hell));
        images.add(getDrawable(R.drawable.ruby_palace));
        images.add(getDrawable(R.drawable.sanguinem));
        images.add(getDrawable(R.drawable.star_of_darkness_castle));
        images.add(getDrawable(R.drawable.sword_mastery_academy));
        images.add(getDrawable(R.drawable.trost_district));
        images.add(getDrawable(R.drawable.uzumaki));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level3Load() {
        questions.add("1st Floor (Aincrad)");
        questions.add("Alne");
        questions.add("Ancient Forest");
        questions.add("Arubi");
        questions.add("Butterfly Valley");
        questions.add("Danac");
        questions.add("Itsuka residence");
        questions.add("Lost Temple");
        questions.add("Malkuth");
        questions.add("Misaki Clock Tower");
        questions.add("Ocean Turtle");
        questions.add("Orario");
        questions.add("Sangiuem's auditorium");
        questions.add("Tolbana");
        questions.add("World Tree");
        images.add(getDrawable(R.drawable.first_floor_aincrad));
        images.add(getDrawable(R.drawable.alne));
        images.add(getDrawable(R.drawable.ancient_forest));
        images.add(getDrawable(R.drawable.arubi));
        images.add(getDrawable(R.drawable.butterfly_valley));
        images.add(getDrawable(R.drawable.danac));
        images.add(getDrawable(R.drawable.itsuka_residence));
        images.add(getDrawable(R.drawable.lost_temple));
        images.add(getDrawable(R.drawable.malkuth));
        images.add(getDrawable(R.drawable.misaki_clock_tower));
        images.add(getDrawable(R.drawable.ocean_turtle));
        images.add(getDrawable(R.drawable.orario));
        images.add(getDrawable(R.drawable.sangiuem_s_auditorium));
        images.add(getDrawable(R.drawable.tolbana));
        images.add(getDrawable(R.drawable.world_tree));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level4Load() {
        questions.add("Arie Family Altar");
        questions.add("Asakusa");
        questions.add("Fairy King's Forest");
        questions.add("Faubrey");
        questions.add("Fujikasane Mountain");
        questions.add("Grace Field House");
        questions.add("Kamina city");
        questions.add("Kingdom of Liones");
        questions.add("Nairn");
        questions.add("Pasloe");
        questions.add("Restaurant Yukihira");
        questions.add("Teppelin");
        questions.add("The Abyss");
        questions.add("The Capital");
        questions.add("Tokyo Station");
        images.add(getDrawable(R.drawable.arie_family_altar));
        images.add(getDrawable(R.drawable.asakusa));
        images.add(getDrawable(R.drawable.fairy_king_s_forest));
        images.add(getDrawable(R.drawable.faubrey));
        images.add(getDrawable(R.drawable.fujikasane_mountain));
        images.add(getDrawable(R.drawable.grace_field_house));
        images.add(getDrawable(R.drawable.kamina_city));
        images.add(getDrawable(R.drawable.kingdom_of_liones));
        images.add(getDrawable(R.drawable.nairn));
        images.add(getDrawable(R.drawable.pasloe));
        images.add(getDrawable(R.drawable.restaurant_yukihira));
        images.add(getDrawable(R.drawable.teppelin));
        images.add(getDrawable(R.drawable.the_abyss));
        images.add(getDrawable(R.drawable.the_capital));
        images.add(getDrawable(R.drawable.tokyo_station));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level5Load() {
        questions.add("Alne");
        questions.add("Amphitheater");
        questions.add("Blue-Pegasus-Building");
        questions.add("E-Rantel");
        questions.add("First-Fairy-Tail-Building");
        questions.add("Kalinsha");
        questions.add("Monument-of-Ruin");
        questions.add("Mount-myoboku");
        questions.add("Nirvana-tree");
        questions.add("Purgatory");
        questions.add("Quindecim");
        questions.add("Second-Fairy-Tail-Building");
        questions.add("Tenrou-Island");
        questions.add("Tower-of-Heaven");
        questions.add("Treasure");
        images.add(getDrawable(R.drawable.alne2));
        images.add(getDrawable(R.drawable.amphitheater));
        images.add(getDrawable(R.drawable.blue_pegasus_building));
        images.add(getDrawable(R.drawable.e_rantel));
        images.add(getDrawable(R.drawable.first_fairy_tail_building));
        images.add(getDrawable(R.drawable.kalinsha));
        images.add(getDrawable(R.drawable.monument_of_ruin));
        images.add(getDrawable(R.drawable.mount_myoboku));
        images.add(getDrawable(R.drawable.nirvana_tree));
        images.add(getDrawable(R.drawable.purgatory));
        images.add(getDrawable(R.drawable.quindecim));
        images.add(getDrawable(R.drawable.second_fairy_tail_building));
        images.add(getDrawable(R.drawable.tenrou_island));
        images.add(getDrawable(R.drawable.tower_of_heaven));
        images.add(getDrawable(R.drawable.treasure));
    }
}