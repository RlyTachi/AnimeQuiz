package com.rlytachi.animequiz.levels;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.rlytachi.animequiz.Game;
import com.rlytachi.animequiz.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class Characters extends AppCompatActivity implements View.OnClickListener {

    Thread failure = new Failure();
    HashMap<Integer, String> questions = new HashMap<>();
    LinkedList<Drawable> images = new LinkedList<>();
    boolean correctAnswer = false;
    Integer[] imageArrCount;
    int image = 0;
    Dialog winDialog = new Dialog(Characters.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters);

        //Images
        final ImageView backView = (ImageView) findViewById(R.id.backView);
        final ImageView helpView = (ImageView) findViewById(R.id.helpView);
        //Buttons
        final Button btn1 = (Button) findViewById(R.id.btnCh1);
        final Button btn2 = (Button) findViewById(R.id.btnCh2);
        final Button btn3 = (Button) findViewById(R.id.btnCh3);
        final Button btn4 = (Button) findViewById(R.id.btnCh4);

        //Click listener
        backView.setOnClickListener(this);
        helpView.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);

        //HashMap
        questions.put(0, "Baka");
        questions.put(1, "Baka2");
        questions.put(2, "Phos");
        questions.put(3, "Phos2");

        //Linked list
        images.add(getDrawable(R.drawable.ch1));
        images.add(getDrawable(R.drawable.ch2));
        images.add(getDrawable(R.drawable.ch3));
        images.add(getDrawable(R.drawable.ch4));

        //Dialog window
        

    }

    @Override
    protected void onStart() {
        super.onStart();

        imageArrCount = getRandomArray();
        nextQuestion();
        failure.start();
    }

    @Override
    public void onClick(View v) {
        //Buttons
        final Button btn1 = (Button) findViewById(R.id.btnCh1);
        final Button btn2 = (Button) findViewById(R.id.btnCh2);
        final Button btn3 = (Button) findViewById(R.id.btnCh3);
        final Button btn4 = (Button) findViewById(R.id.btnCh4);
        switch (v.getId()) {
            case R.id.backView:
                startActivity(new Intent(this, Game.class));
                break;
            case R.id.helpView:
                //TODO: Help activity
                //startActivity(new Intent(this, Help.class));
                break;

            case R.id.btnCh1:
                getAnswer(btn1.getText().toString());

                nextQuestion();
                break;

            case R.id.btnCh2:
                getAnswer(btn2.getText().toString());

                nextQuestion();
                break;

            case R.id.btnCh3:
                getAnswer(btn3.getText().toString());

                nextQuestion();
                break;

            case R.id.btnCh4:
                getAnswer(btn4.getText().toString());

                nextQuestion();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        image = 0;
        imageArrCount = getRandomArray();
    }

    public void nextQuestion() {
        final ImageView characterView = (ImageView) findViewById(R.id.characterView);
        final Button btn1 = (Button) findViewById(R.id.btnCh1);
        final Button btn2 = (Button) findViewById(R.id.btnCh2);
        final Button btn3 = (Button) findViewById(R.id.btnCh3);
        final Button btn4 = (Button) findViewById(R.id.btnCh4);

        Integer[] arr = getRandomArray();

        btn1.setText(questions.get(arr[0]));
        btn2.setText(questions.get(arr[1]));
        btn3.setText(questions.get(arr[2]));
        btn4.setText(questions.get(arr[3]));

        try {
            characterView.setImageDrawable(images.get(imageArrCount[image]));
            image++;
        } catch (IndexOutOfBoundsException e) {
            e.getStackTrace();
            System.out.println("Done");
        }
    }

    public boolean getAnswer(String imageChoice) {
        if (imageChoice.equals(questions.get(0))) {
            correctAnswer = true;
            return true;
        }
        correctAnswer = false;
        return false;
    }

    public Integer[] getRandomArray() {
        Integer[] arr = new Integer[images.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        Collections.shuffle(Arrays.asList(arr));

        return arr;
    }


    class Failure extends Thread {

        @Override
        public void run() {
            super.run();

        }
    }
}