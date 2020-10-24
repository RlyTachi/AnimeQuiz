package com.rlytachi.animequiz.levels;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rlytachi.animequiz.Game;
import com.rlytachi.animequiz.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class Characters extends AppCompatActivity implements View.OnClickListener {

    LinkedList<String> questions = new LinkedList<>();
    LinkedList<Drawable> images = new LinkedList<>();
    Integer[] imageArrCount;
    int image = 0;
    int heartCount = 3;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters);

        //Images
        final ImageView backView = findViewById(R.id.backView);
        final ImageView helpView = findViewById(R.id.helpView);
        //Buttons
        final Button btn1 = findViewById(R.id.btnCh1);
        final Button btn2 = findViewById(R.id.btnCh2);
        final Button btn3 = findViewById(R.id.btnCh3);
        final Button btn4 = findViewById(R.id.btnCh4);
        final TextView info = findViewById(R.id.informationText);

        //Click listener
        backView.setOnClickListener(this);
        helpView.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);

        //Linked list
        questions.add("Первый");
        questions.add("Второй");
        questions.add("Третий");
        questions.add("Четвертый");
        questions.add("Пятый");
        questions.add("Шестой");
        questions.add("Седьмой");
        images.add(getDrawable(R.drawable.ch1));
        images.add(getDrawable(R.drawable.ch2));
        images.add(getDrawable(R.drawable.ch3));
        images.add(getDrawable(R.drawable.ch4));
        images.add(getDrawable(R.drawable.heart));
        images.add(getDrawable(R.drawable.play));
        images.add(getDrawable(R.drawable.emilia));

    }

    @Override
    protected void onStart() {
        super.onStart();

        imageArrCount = getRandomArray();
        nextQuestion();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        //Buttons
        final Button btn1 = findViewById(R.id.btnCh1);
        final Button btn2 = findViewById(R.id.btnCh2);
        final Button btn3 = findViewById(R.id.btnCh3);
        final Button btn4 = findViewById(R.id.btnCh4);
        switch (v.getId()) {
            case R.id.backView:
                startActivity(new Intent(this, Game.class));
                break;
            case R.id.helpView:
                winDialog();
                break;

            case R.id.btnCh1:
                if (!getAnswer(btn1.getText().toString())) minusHeart();

                nextQuestion();
                break;

            case R.id.btnCh2:
                if (!getAnswer(btn2.getText().toString())) minusHeart();

                nextQuestion();
                break;

            case R.id.btnCh3:
                if (!getAnswer(btn3.getText().toString())) minusHeart();

                nextQuestion();
                break;

            case R.id.btnCh4:
                if (!getAnswer(btn4.getText().toString())) minusHeart();

                nextQuestion();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        image = 0;
        setId(-1);
        heartCount = 3;
        imageArrCount = getRandomArray();
    }

    public void winDialog() {
        //Dialog window
        Dialog winDialog = new Dialog(Characters.this);
        winDialog.setContentView(R.layout.activity_win_action);
        winDialog.show();
        winDialog.setCancelable(false);
        final Button okButton = winDialog.findViewById(R.id.ok);
        okButton.setOnClickListener(v -> {
            winDialog.hide();
            startActivity(new Intent(Characters.this, Game.class));
        });
    }

    public void lossDialog() {
        //Dialog window
        Dialog lossDialog = new Dialog(Characters.this);
        lossDialog.setContentView(R.layout.activity_loss);
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
        final TextView info = findViewById(R.id.informationText);
        boolean answer = questions.get(getId()).equals(str);
        info.setText(answer ? R.string.correct : R.string.wrong);
        return questions.get(getId()).equals(str);
    }

    public void minusHeart() {
        final ImageView heart1 = findViewById(R.id.heart1);
        final ImageView heart2 = findViewById(R.id.heart2);
        final ImageView heart3 = findViewById(R.id.heart3);

        heartCount--;
        if (heart3.getVisibility() == View.VISIBLE) heart3.setVisibility(View.INVISIBLE);
        else if (heart2.getVisibility() == View.VISIBLE) heart2.setVisibility(View.INVISIBLE);
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

        if (image == images.size()) winDialog();

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
        for (int i = 0; i < arrForButtons.length; i++) {
            if (arr[i] == getId()) continue;
            arrForButtons[i] = arr[i];
        }

        for (int i = 0; i < arrForButtons.length; i++) {
            if (arrForButtons[i] == null) {
                arrForButtons[i] = getId();
                break;
            }
        }

        if (arrForButtons[0] == getId()) return arrForButtons;
        else if (arrForButtons[1] == getId()) return arrForButtons;
        else if (arrForButtons[2] == getId()) return arrForButtons;
        else if (arrForButtons[3] == getId()) return arrForButtons;
        else arrForButtons[0] = getId();


        Collections.shuffle(Arrays.asList(arrForButtons));

        return arrForButtons;
    }
}