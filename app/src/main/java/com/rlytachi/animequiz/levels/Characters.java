package com.rlytachi.animequiz.levels;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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

import com.rlytachi.animequiz.Game;
import com.rlytachi.animequiz.R;
import com.rlytachi.animequiz.Score;


import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class Characters extends AppCompatActivity {

    //Shared Preferences
    public static final String APP_PREFERENCES = "mySettings";
    public static final String APP_PREFERENCES_FIRST_SESSION_CHARACTERS = "firstSessionCharacters";
    public static final String[] APP_PREFERENCES_PROMO_CODES = {"promoCode1", "promoCode2", "promoCode3", "promoCode4"};
    public static final int PROMO_VIEW_TARGET = 3;

    String[] promoStorage = new String[]{"promocode1", "promocode2", "promocode3", "promocode4"};
    LinkedList<String> questions = new LinkedList<>();
    LinkedList<Drawable> images = new LinkedList<>();
    Integer[] imageArrCount;
    int image = 0;
    int heartCount = 3;
    int id;
    int seconds = -1;
    int scoreEarnedSession = 0;
    int promoViewCount = 0;
    boolean firstSessionCh;
    boolean finished = false;
    boolean timesUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters);

        //Linked list
        questions.add("Kanna");
        questions.add("Rabbit");
        questions.add("Phosphophyllite");
        questions.add("Phos broken");
        questions.add("Heart");
        questions.add("Play");
        questions.add("Emilia");
        images.add(getDrawable(R.drawable.ch1));
        images.add(getDrawable(R.drawable.ch2));
        images.add(getDrawable(R.drawable.ch3));
        images.add(getDrawable(R.drawable.ch4));
        images.add(getDrawable(R.drawable.heart));
        images.add(getDrawable(R.drawable.play));
        images.add(getDrawable(R.drawable.emilia));

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAction();
        imageArrCount = getRandomArray();
        nextQuestion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveAction();
        image = 0;
        setId(-1);
        heartCount = 3;
        imageArrCount = getRandomArray();
    }

    public void saveAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = mShared.edit();
        editor.putBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS, firstSessionCh);
        editor.putInt(Game.APP_PREFERENCES_GLOBAL_SCORE, Score.getScore());

        for (int i = 0; i < promoStorage.length; i++) {
            editor.putString(APP_PREFERENCES_PROMO_CODES[i], promoStorage[i]);
        }

        editor.apply();
    }

    public void loadAction() {
        SharedPreferences mShared = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mShared.contains(APP_PREFERENCES_FIRST_SESSION_CHARACTERS)) {
            firstSessionCh = mShared.getBoolean(APP_PREFERENCES_FIRST_SESSION_CHARACTERS, firstSessionCh);
        } else firstSessionCh = true;
        for (int i = 0; i < promoStorage.length; i++) {
            promoStorage[i] = mShared.getString(APP_PREFERENCES_PROMO_CODES[i], null);
        }
    }


    //todo fix save promo codes
    @SuppressLint("SetTextI18n")
    public void promoDialog() {
        Dialog promoDialog = new Dialog(Characters.this);
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

    public boolean promoStorage(String promoCode) {
        for (int i = 0; i < promoStorage.length; i++) {
            if (promoStorage[i] == null) continue;
            if (promoCode.equals(promoStorage[i].toLowerCase())) {
                promoStorage[i] = null;
                return true;
            }
        }
        return false;
    }

    //Победа
    @SuppressLint("SetTextI18n")
    public void winDialog() {
        try {
            Dialog winDialog = new Dialog(Characters.this);
            winDialog.setContentView(R.layout.activity_win_action);
            winDialog.show();
            winDialog.setCancelable(false);
            finished = true;
            final Button okButton = winDialog.findViewById(R.id.ok);
            final TextView earnedView = winDialog.findViewById(R.id.earned);
            if (!firstSessionCh) scoreEarnedSession = 2;
            earnedView.setText(getString(R.string.earned) + " " + scoreEarnedSession + " " + getString(R.string.scores));
            Score.addScore(scoreEarnedSession);
            firstSessionCh = false;
            saveAction();
            okButton.setOnClickListener(v -> {
                winDialog.hide();
                startActivity(new Intent(Characters.this, Game.class));
            });
        } catch (RuntimeException ignore) {
        }
    }

    //Проигрыш
    public void lossDialog() {
        Dialog lossDialog = new Dialog(Characters.this);
        lossDialog.setContentView(R.layout.activity_loss);
        finished = true;
        lossDialog.show();
        lossDialog.setCancelable(false);
        scoreEarnedSession = 0;
        final Button okButton = lossDialog.findViewById(R.id.ok);
        okButton.setOnClickListener(v -> {
            lossDialog.hide();
            startActivity(new Intent(Characters.this, Game.class));
        });
    }

    //Помощь
    @SuppressLint("SetTextI18n")
    public void helpDialog() {
        Dialog helpDialog = new Dialog(Characters.this);
        helpDialog.setContentView(R.layout.activity_help);
        helpDialog.show();

        Animation animButton = AnimationUtils.loadAnimation(Characters.this, R.anim.anim_btn);
        Animation animEnd = AnimationUtils.loadAnimation(this, R.anim.anim_end);
        final Button getHelpBtn = helpDialog.findViewById(R.id.takeHelpBtn);
        final Button getHeartBtn = helpDialog.findViewById(R.id.takeHeartBtn);
        final TextView scoreLeftView = helpDialog.findViewById(R.id.scoreLeftView);
        final TextView notEnoughView = helpDialog.findViewById(R.id.notEnoughView);

        scoreLeftView.setText(getString(R.string.scoreLeft) + " " + Score.getScore());
        getHelpBtn.setOnClickListener(v -> {
            getHelpBtn.setAnimation(animButton);
            getHelpBtn.startAnimation(animButton);
            if (Score.minusScore(5)) {
                getHelpAction();
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
            getHeartBtn.setAnimation(animButton);
            getHeartBtn.startAnimation(animButton);
            if (Score.minusScore(5)) {
                getHeartAction();
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
        if (heart3.getVisibility() == View.VISIBLE) {
            heart3.setVisibility(View.INVISIBLE);
        } else if (heart2.getVisibility() == View.VISIBLE) {
            heart2.setVisibility(View.INVISIBLE);
        } else if (heart1.getVisibility() == View.VISIBLE) {
            heart1.setVisibility(View.INVISIBLE);
            lossDialog();
        }
    }

    //Вовзращает ID изображения, вопроса, ответа в LinkedList.
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
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
        btn1.setText(questions.get(buttonArrCount[0]));
        btn2.setText(questions.get(buttonArrCount[1]));
        btn3.setText(questions.get(buttonArrCount[2]));
        btn4.setText(questions.get(buttonArrCount[3]));

    }

    //Сгенерировать рандомный массив с индексами
    public Integer[] getRandomArray() {
        //если для изображения, то нужно все значения в произвольном порядке
        Integer[] arr = new Integer[questions.size()];

        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        Collections.shuffle(Arrays.asList(arr));

        return arr;
    }

    //Сгенерировать рандомный массив с индексами
    public Integer[] getRandomArrayButton(Integer[] arr) {
        //если для кнопок, то нужно 4 значения с 1 верным

        Integer[] arrForButtons = new Integer[4];
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
            if (heartCount > 1 && seconds == 0) {
                info.setText(R.string.timesUp);
                info.startAnimation(animEnd);
                info.setVisibility(View.INVISIBLE);
                timesUp = true;
                nextQuestion();
            } else if (heartCount == 1) {
                lossDialog();
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
        myTimer myTimer = new myTimer(16000, 1000);

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
            progressBar.setMax(15);

            //Проверка статусов
            while (true) {
                progressBar.setProgress(seconds);
                if (finished) {
                    this.interrupt();
                    break;
                }
                if (heartCount == 0) {
                    myTimer.onFinish();
                    this.interrupt();
                    break;
                }
                if (timesUp) {
                    minusHeart();
                    myTimer.start();
                    timesUp = false;
                }
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
                    startActivity(new Intent(Characters.this, Game.class));
                    myTimer.onFinish();
                    break;

                //Помощь
                case R.id.helpView:
                    helpDialog();
                    break;

                //Кнопки
                //Если ответ не совпадает, минус сердце
                //Финиш таймера, следующий вопрос и запуск таймера
                case R.id.btnCh1:

                    if (!getAnswer(btn1.getText().toString())) {
                        minusHeart();
                        btn2.startAnimation(animation);
                        btn2.clearAnimation();
                    } else {
                        if (firstSessionCh) scoreEarnedSession++;
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

                    if (!getAnswer(btn2.getText().toString())) {
                        minusHeart();
                        btn1.startAnimation(animation);
                        btn1.clearAnimation();
                    } else {
                        if (firstSessionCh) scoreEarnedSession++;
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

                    if (!getAnswer(btn3.getText().toString())) {
                        minusHeart();
                        btn4.startAnimation(animation);
                        btn4.clearAnimation();
                    } else {
                        if (firstSessionCh) scoreEarnedSession++;
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

                    if (!getAnswer(btn4.getText().toString())) {
                        minusHeart();
                        btn3.startAnimation(animation);
                        btn3.clearAnimation();
                    } else {
                        if (firstSessionCh) scoreEarnedSession++;
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
}