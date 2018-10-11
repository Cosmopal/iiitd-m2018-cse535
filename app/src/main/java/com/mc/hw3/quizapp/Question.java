package com.mc.hw3.quizapp;

public class Question {
    int Qno;
    String qText;
    String savedAnswer;

    public Question(int qno, String qText, String savedAnswer) {
        Qno = qno;
        this.qText = qText;
        this.savedAnswer = savedAnswer;
    }

    public Question(int qno, String qText) {
        Qno = qno;
        this.qText = qText;
    }

    @Override
    public String toString() {
        return "Question{" +
                "Qno=" + Qno +
                ", qText='" + qText + '\'' +
                ", savedAnswer='" + savedAnswer + '\'' +
                '}';
    }

    public String getCsvString(){
        return Qno + ", " + qText + ", " + savedAnswer + "\n";
    }
}
