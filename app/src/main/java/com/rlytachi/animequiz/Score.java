package com.rlytachi.animequiz;

import java.util.Scanner;

public class Score extends Thread {

    public static int score;

    public static int getScore() {
        return score;
    }

    public static void setScore(int score) {
        Score.score = score;
    }

    public static void addScore(int score) {
        Score.score += score;
    }

    public static boolean minusScore(int score) {
        if (score < Score.score) {
            Score.score -= score;
            return true;
        }else return false;
    }

    @Override
    public void run() {
        super.run();


    }
}
