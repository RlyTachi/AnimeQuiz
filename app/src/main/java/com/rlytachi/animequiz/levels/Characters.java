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
import com.google.android.gms.ads.MobileAds;
import com.jaeger.library.StatusBarUtil;
import com.rlytachi.animequiz.Game;
import com.rlytachi.animequiz.Language;
import com.rlytachi.animequiz.LevelChoice;
import com.rlytachi.animequiz.MyContextWrapper;
import com.rlytachi.animequiz.PromoStorage;
import com.rlytachi.animequiz.R;
import com.rlytachi.animequiz.Score;
import com.rlytachi.animequiz.Settings;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Locale;

import static com.rlytachi.animequiz.PromoStorage.APP_PREFERENCES_PROMO_CODES;
import static com.rlytachi.animequiz.PromoStorage.promoStorage;
import static com.rlytachi.animequiz.Settings.in;
import static com.rlytachi.animequiz.Settings.out;
import static com.rlytachi.animequiz.Settings.playSound;

public class Characters extends AppCompatActivity {

    //Shared Preferences
    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_LANG = "myLanguage";
    public static final String APP_PREFERENCES_FIRST_SESSION_CHARACTERS_1 = "firstSessionCharacters1";
    public static final String APP_PREFERENCES_FIRST_SESSION_CHARACTERS_2 = "firstSessionCharacters2";
    public static final String APP_PREFERENCES_FIRST_SESSION_CHARACTERS_3 = "firstSessionCharacters3";
    public static final String APP_PREFERENCES_FIRST_SESSION_CHARACTERS_4 = "firstSessionCharacters4";
    public static final String APP_PREFERENCES_FIRST_SESSION_CHARACTERS_5 = "firstSessionCharacters5";
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
    static boolean firstSessionChLvl1 = true;
    static boolean firstSessionChLvl2 = true;
    static boolean firstSessionChLvl3 = true;
    static boolean firstSessionChLvl4 = true;
    static boolean firstSessionChLvl5 = true;

    boolean back = false;
    boolean finished = false;
    boolean finishedByWin = false;
    boolean timesUp = false;

    Dialog promoDialog, helpDialog, winDialog, lossDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters);
        StatusBarUtil.setTransparent(this);
        promoDialog = new Dialog(Characters.this);
        helpDialog = new Dialog(Characters.this);
        winDialog = new Dialog(Characters.this);
        lossDialog = new Dialog(Characters.this);

        MobileAds.initialize(this);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-7217958397153183/4052170352");
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                playSound(out);
                back = true;
                finish();
                startActivity(new Intent(Characters.this, Game.class));
            }
        });

        in = MediaPlayer.create(this, R.raw.in);
        out = MediaPlayer.create(this, R.raw.out);

        loadAction();
        ButtonsEvent buttonsEvent = new ButtonsEvent();
        if (buttonsEvent.isInterrupted()) buttonsEvent.interrupt();
        else buttonsEvent.start();

        Animation animHeart = AnimationUtils.loadAnimation(Characters.this, R.anim.anim_btn);
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
        startActivity(new Intent(Characters.this, Game.class));
    }


    @Override
    protected void onPause() {
        if(!back) {
            langUpdate();
            finish();
            startActivity(new Intent(Characters.this, Characters.class));
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
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_1, isFirstSessionChLvl1());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_2, isFirstSessionChLvl2());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_3, isFirstSessionChLvl3());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_4, isFirstSessionChLvl4());
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS_5, isFirstSessionChLvl5());
        editor.putInt(Game.APP_PREFERENCES_GLOBAL_SCORE, Score.getScore());
        editor.putString(APP_PREFERENCES_LANG, Language.getLang());

        for (int i = 0; i < PromoStorage.getPromoStorage().length; i++) {
            editor.putString(APP_PREFERENCES_PROMO_CODES[i], PromoStorage.getPromoStorage()[i]);
        }

        editor.apply();
    }

    public void loadAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
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
                if (!isFirstSessionChLvl1()) scoreEarnedSession = 2;
                setFirstSessionChLvl1(false);
            }
            if (LevelChoice.getLevel() == 2) {
                if (!isFirstSessionChLvl2()) scoreEarnedSession = 2;
                setFirstSessionChLvl2(false);
            }
            if (LevelChoice.getLevel() == 3) {
                if (!isFirstSessionChLvl3()) scoreEarnedSession = 2;
                setFirstSessionChLvl3(false);
            }
            if (LevelChoice.getLevel() == 4) {
                if (!isFirstSessionChLvl4()) scoreEarnedSession = 2;
                setFirstSessionChLvl4(false);
            }
            if (LevelChoice.getLevel() == 5) {
                if (!isFirstSessionChLvl5()) scoreEarnedSession = 2;
                setFirstSessionChLvl5(false);
            }

            earnedView.setText(getString(R.string.earned) + " " + scoreEarnedSession + " " + getString(R.string.scores));
            Score.addScore(scoreEarnedSession);

            saveAction();
            okButton.setOnClickListener(v -> {
                if (interstitialAd.isLoaded()) {
                    winDialog.dismiss();
                    interstitialAd.show();
                } else {
                    playSound(out);
                    back = true;
                    winDialog.dismiss();
                    finish();
                    startActivity(new Intent(Characters.this, Game.class));
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
                startActivity(new Intent(Characters.this, Game.class));
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


        Animation animButton = AnimationUtils.loadAnimation(Characters.this, R.anim.anim_btn);
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

    public static boolean isFirstSessionChLvl1() {
        return firstSessionChLvl1;
    }

    public static void setFirstSessionChLvl1(boolean firstSessionChLvl1) {
        Characters.firstSessionChLvl1 = firstSessionChLvl1;
    }

    public static boolean isFirstSessionChLvl2() {
        return firstSessionChLvl2;
    }

    public static void setFirstSessionChLvl2(boolean firstSessionChLvl2) {
        Characters.firstSessionChLvl2 = firstSessionChLvl2;
    }

    public static boolean isFirstSessionChLvl3() {
        return firstSessionChLvl3;
    }

    public static void setFirstSessionChLvl3(boolean firstSessionChLvl3) {
        Characters.firstSessionChLvl3 = firstSessionChLvl3;
    }

    public static boolean isFirstSessionChLvl4() {
        return firstSessionChLvl4;
    }

    public static void setFirstSessionChLvl4(boolean firstSessionChLvl4) {
        Characters.firstSessionChLvl4 = firstSessionChLvl4;
    }

    public static boolean isFirstSessionChLvl5() {
        return firstSessionChLvl5;
    }

    public static void setFirstSessionChLvl5(boolean firstSessionChLvl5) {
        Characters.firstSessionChLvl5 = firstSessionChLvl5;
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
        Animation animEnd = AnimationUtils.loadAnimation(Characters.this, R.anim.anim_end);

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
                if (isFirstSessionChLvl1()) scoreEarnedSession++;
            }
            if (LevelChoice.getLevel() == 2) {
                if (isFirstSessionChLvl2()) scoreEarnedSession++;
            }
            if (LevelChoice.getLevel() == 3) {
                if (isFirstSessionChLvl3()) scoreEarnedSession++;
            }
            if (LevelChoice.getLevel() == 4) {
                if (isFirstSessionChLvl4()) scoreEarnedSession++;
            }
            if (LevelChoice.getLevel() == 5) {
                if (isFirstSessionChLvl5()) scoreEarnedSession++;
            }
        }

        //Обработчик нажатий
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            Animation animation = AnimationUtils.loadAnimation(Characters.this, R.anim.anim_btn);
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
                    startActivity(new Intent(Characters.this, Game.class));
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
        questions.add("hinata");
        questions.add("kanna");
        questions.add("kurisu");
        questions.add("mitsuha");
        questions.add("phosphophyllite");
        questions.add("diamond");
        questions.add("naruto");
        questions.add("dio");
        questions.add("kaneki");
        questions.add("kirito");
        questions.add("lelouch");
        questions.add("beatrice");
        questions.add("kamina");
        questions.add("mob");
        questions.add("haruka");
        questions.add("bruno");
        questions.add("kira");
        questions.add("rintaro");
        questions.add("akiko");
        questions.add("midoriya");
        images.add(getDrawable(R.drawable.hinata));
        images.add(getDrawable(R.drawable.kanna));
        images.add(getDrawable(R.drawable.kurisu));
        images.add(getDrawable(R.drawable.mitsuha));
        images.add(getDrawable(R.drawable.phosphophyllite));
        images.add(getDrawable(R.drawable.diamond));
        images.add(getDrawable(R.drawable.naruto));
        images.add(getDrawable(R.drawable.dio));
        images.add(getDrawable(R.drawable.kaneki));
        images.add(getDrawable(R.drawable.kirito));
        images.add(getDrawable(R.drawable.lelouch));
        images.add(getDrawable(R.drawable.beatrice));
        images.add(getDrawable(R.drawable.kamina));
        images.add(getDrawable(R.drawable.mob));
        images.add(getDrawable(R.drawable.haruka));
        images.add(getDrawable(R.drawable.bruno));
        images.add(getDrawable(R.drawable.kira));
        images.add(getDrawable(R.drawable.rintaro));
        images.add(getDrawable(R.drawable.akiko));
        images.add(getDrawable(R.drawable.midoriya));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level2Load() {
        questions.add("asta");
        questions.add("dazai");
        questions.add("decim");
        questions.add("hinata");
        questions.add("isla");
        questions.add("kaguya");
        questions.add("kaori");
        questions.add("kiki");
        questions.add("lain");
        questions.add("luffy");
        questions.add("madoka");
        questions.add("maquia");
        questions.add("mikasa");
        questions.add("natsu");
        questions.add("rika");
        questions.add("rin");
        questions.add("shiro");
        questions.add("violet");
        questions.add("yato");
        questions.add("yukio");
        images.add(getDrawable(R.drawable.asta));
        images.add(getDrawable(R.drawable.dazai));
        images.add(getDrawable(R.drawable.decim));
        images.add(getDrawable(R.drawable.hinata2));
        images.add(getDrawable(R.drawable.isla));
        images.add(getDrawable(R.drawable.kaguya));
        images.add(getDrawable(R.drawable.kaori));
        images.add(getDrawable(R.drawable.kiki));
        images.add(getDrawable(R.drawable.lain));
        images.add(getDrawable(R.drawable.luffy));
        images.add(getDrawable(R.drawable.madoka));
        images.add(getDrawable(R.drawable.maquia));
        images.add(getDrawable(R.drawable.mikasa));
        images.add(getDrawable(R.drawable.natsu));
        images.add(getDrawable(R.drawable.rika));
        images.add(getDrawable(R.drawable.rin));
        images.add(getDrawable(R.drawable.shiro));
        images.add(getDrawable(R.drawable.violet));
        images.add(getDrawable(R.drawable.yato));
        images.add(getDrawable(R.drawable.yukio));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level3Load() {
        questions.add("antarcticite");
        questions.add("eugeo");
        questions.add("felix");
        questions.add("fubuki");
        questions.add("hachiman");
        questions.add("itachi");
        questions.add("izaya");
        questions.add("keima");
        questions.add("maho");
        questions.add("mashiro");
        questions.add("mei");
        questions.add("mikaela");
        questions.add("mirai");
        questions.add("sagiri");
        questions.add("saiko");
        questions.add("shana");
        questions.add("suzaku");
        questions.add("taiga");
        questions.add("tomoko");
        questions.add("zoro");
        images.add(getDrawable(R.drawable.antarcticite));
        images.add(getDrawable(R.drawable.eugeo));
        images.add(getDrawable(R.drawable.felix));
        images.add(getDrawable(R.drawable.fubuki));
        images.add(getDrawable(R.drawable.hachiman));
        images.add(getDrawable(R.drawable.itachi));
        images.add(getDrawable(R.drawable.izaya));
        images.add(getDrawable(R.drawable.keima));
        images.add(getDrawable(R.drawable.maho));
        images.add(getDrawable(R.drawable.mashiro));
        images.add(getDrawable(R.drawable.mei));
        images.add(getDrawable(R.drawable.mikaela));
        images.add(getDrawable(R.drawable.mirai));
        images.add(getDrawable(R.drawable.sagiri));
        images.add(getDrawable(R.drawable.saiko));
        images.add(getDrawable(R.drawable.shana));
        images.add(getDrawable(R.drawable.suzaku));
        images.add(getDrawable(R.drawable.taiga));
        images.add(getDrawable(R.drawable.tomoko));
        images.add(getDrawable(R.drawable.zoro));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level4Load() {
        questions.add("boogiepop");
        questions.add("edward");
        questions.add("guts");
        questions.add("haise");
        questions.add("hitagi");
        questions.add("hyakkimaru");
        questions.add("inori");
        questions.add("kanade");
        questions.add("kayo");
        questions.add("makoto");
        questions.add("moritaka");
        questions.add("mugi");
        questions.add("nine");
        questions.add("sawa");
        questions.add("sekai");
        questions.add("senkuu");
        questions.add("shuu");
        questions.add("twelve");
        questions.add("yuno");
        questions.add("zero two");
        images.add(getDrawable(R.drawable.boogiepop));
        images.add(getDrawable(R.drawable.edward));
        images.add(getDrawable(R.drawable.guts));
        images.add(getDrawable(R.drawable.haise));
        images.add(getDrawable(R.drawable.hitagi));
        images.add(getDrawable(R.drawable.hyakkimaru));
        images.add(getDrawable(R.drawable.inori));
        images.add(getDrawable(R.drawable.kanade));
        images.add(getDrawable(R.drawable.kayo));
        images.add(getDrawable(R.drawable.makoto));
        images.add(getDrawable(R.drawable.moritaka));
        images.add(getDrawable(R.drawable.mugi));
        images.add(getDrawable(R.drawable.nine));
        images.add(getDrawable(R.drawable.sawa));
        images.add(getDrawable(R.drawable.sekai));
        images.add(getDrawable(R.drawable.senkuu));
        images.add(getDrawable(R.drawable.shuu));
        images.add(getDrawable(R.drawable.twelve));
        images.add(getDrawable(R.drawable.yuno));
        images.add(getDrawable(R.drawable.zero_two));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void level5Load() {
        questions.add("alice");
        questions.add("chitoge");
        questions.add("genos");
        questions.add("haruka");
        questions.add("hei");
        questions.add("hestia");
        questions.add("holo");
        questions.add("houtarou");
        questions.add("ichika");
        questions.add("kei");
        questions.add("michiru");
        questions.add("nao");
        questions.add("rin");
        questions.add("sadao");
        questions.add("sasuke");
        questions.add("satsuki");
        questions.add("shinichi");
        questions.add("shouya");
        questions.add("subaru");
        questions.add("yuuta");
        images.add(getDrawable(R.drawable.alice));
        images.add(getDrawable(R.drawable.chitoge));
        images.add(getDrawable(R.drawable.genos));
        images.add(getDrawable(R.drawable.haruka2));
        images.add(getDrawable(R.drawable.hei));
        images.add(getDrawable(R.drawable.hestia));
        images.add(getDrawable(R.drawable.holo));
        images.add(getDrawable(R.drawable.houtarou));
        images.add(getDrawable(R.drawable.ichika));
        images.add(getDrawable(R.drawable.kei));
        images.add(getDrawable(R.drawable.michiru));
        images.add(getDrawable(R.drawable.nao));
        images.add(getDrawable(R.drawable.rin2));
        images.add(getDrawable(R.drawable.sadao));
        images.add(getDrawable(R.drawable.sasuke));
        images.add(getDrawable(R.drawable.satsuki));
        images.add(getDrawable(R.drawable.shinichi));
        images.add(getDrawable(R.drawable.shouya));
        images.add(getDrawable(R.drawable.subaru));
        images.add(getDrawable(R.drawable.yuuta));
    }
}