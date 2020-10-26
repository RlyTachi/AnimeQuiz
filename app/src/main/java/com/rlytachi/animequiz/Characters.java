package com.rlytachi.animequiz;

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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class Characters extends AppCompatActivity {

    LinkedList<String> questions = new LinkedList<>();
    LinkedList<Drawable> images = new LinkedList<>();
    Integer[] imageArrCount;
    int image = 0;
    int heartCount = 3;
    int id;
    int seconds = -1;
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        finished = false;
        imageArrCount = getRandomArray();
        nextQuestion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        image = 0;
        setId(-1);
        heartCount = 3;
        imageArrCount = getRandomArray();
    }

    //Победа
    public void winDialog() {
        Dialog winDialog = new Dialog(Characters.this);
        winDialog.setContentView(R.layout.activity_win_action);
        winDialog.show();
        winDialog.setCancelable(false);
        finished = true;
        final Button okButton = winDialog.findViewById(R.id.ok);
        okButton.setOnClickListener(v -> {
            winDialog.hide();
            startActivity(new Intent(Characters.this, Game.class));
        });
    }

    //Проигрыш
    public void lossDialog() {
        Dialog lossDialog = new Dialog(Characters.this);
        lossDialog.setContentView(R.layout.activity_loss);
        finished = true;
        lossDialog.show();
        lossDialog.setCancelable(false);
        final Button okButton = lossDialog.findViewById(R.id.ok);
        okButton.setOnClickListener(v -> {
            lossDialog.hide();
            startActivity(new Intent(Characters.this, Game.class));
        });
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
        }
        else if (heart2.getVisibility() == View.VISIBLE){
            heart2.setVisibility(View.INVISIBLE);
        }
        else if (heart1.getVisibility() == View.VISIBLE) {
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

        if (image == images.size() && heartCount > 0) winDialog();

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

        }

        //Метод после завершения таймера. Действие требуется только в случае истечения времени.
        @Override
        public void onFinish() {
            if (seconds == 0) {
                minusHeart();
                info.setText(R.string.timesUp);
                info.startAnimation(animEnd);
                info.setVisibility(View.INVISIBLE);
                timesUp = true;
                nextQuestion();
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
                if (finished ) {
                    this.interrupt();
                    myTimer.onFinish();
                    break;
                }
                if (heartCount == 0) {
                    this.interrupt();
                    break;
                }
                if (timesUp) {
                    myTimer.start();
                    timesUp = false;
                }
            }
        }


        //Обработчик нажатий
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                //Назад
                case R.id.backView:
                    startActivity(new Intent(Characters.this, Game.class));
                    myTimer.onFinish();
                    break;

                //Помощь
                case R.id.helpView:
                    //TODO help
                    //заглушка
                    winDialog();
                    myTimer.onFinish();
                    break;

                //Кнопки
                //Если ответ не совпадает, минус сердце
                //Финиш таймера, следующий вопрос и запуск таймера
                case R.id.btnCh1:
                    if (!getAnswer(btn1.getText().toString())) minusHeart();
                    myTimer.onFinish();
                    nextQuestion();
                    myTimer.start();
                    break;

                case R.id.btnCh2:
                    if (!getAnswer(btn2.getText().toString())) minusHeart();
                    myTimer.onFinish();
                    nextQuestion();
                    myTimer.start();
                    break;

                case R.id.btnCh3:
                    if (!getAnswer(btn3.getText().toString())) minusHeart();
                    myTimer.onFinish();
                    nextQuestion();
                    myTimer.start();
                    break;

                case R.id.btnCh4:
                    if (!getAnswer(btn4.getText().toString())) minusHeart();
                    myTimer.onFinish();
                    nextQuestion();
                    myTimer.start();
                    break;
            }
        }
    }
}