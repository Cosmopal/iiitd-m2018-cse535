package com.mc.hw3.quizapp;

import android.provider.BaseColumns;

public class QuestionsContract implements BaseColumns{
        public static final String TABLE_NAME = "questions_table";
        public static final String COLUMN_NAME_QNO = "qno";
        public static final String COLUMN_NAME_QTEXT = "qtext";
        public static final String COLUMN_NAME_ANSWER = "answer";
}
