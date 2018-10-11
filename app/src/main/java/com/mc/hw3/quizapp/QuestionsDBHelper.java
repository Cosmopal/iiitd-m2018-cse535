package com.mc.hw3.quizapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class QuestionsDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Questions.db";
    public static final int DATABSE_VERSION = 1;

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + QuestionsContract.TABLE_NAME + "(" +
                    QuestionsContract.COLUMN_NAME_QNO + " INTEGER PRIMARY KEY," +
                    QuestionsContract.COLUMN_NAME_QTEXT + " TEXT," +
                    QuestionsContract.COLUMN_NAME_ANSWER + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + QuestionsContract.TABLE_NAME;
    private static final String TAG = "DBHelper";

    public QuestionsDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABSE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        writeQuestion(db, new Question(1, "Apple starts with A"));
        writeQuestion(db, new Question(2, "Apple starts with B"));
        writeQuestion(db, new Question(3, "Banana is a Vegetable"));
        writeQuestion(db, new Question(4, "Earth is Flat"));
        writeQuestion(db, new Question(5, "We are stardust"));
        writeQuestion(db, new Question(6, "Your Birthday is in December"));
        writeQuestion(db, new Question(7, "Your name starts with A"));
        writeQuestion(db, new Question(8, "Humans have 10 hands"));
        writeQuestion(db, new Question(9, "Humans have 10 fingers"));
        writeQuestion(db, new Question(10, "Whiteboards are white"));
        writeQuestion(db, new Question(11, "All Mobiles are black"));
        writeQuestion(db, new Question(12, "God is your friend"));
        writeQuestion(db, new Question(13, "Night is dark"));
        writeQuestion(db, new Question(14, "Sun rises from the East"));
        writeQuestion(db, new Question(15, "You love Computer Science"));
        writeQuestion(db, new Question(16, "People sleep at night"));
        writeQuestion(db, new Question(17, "Computers were invented by Charles Babbage"));
        writeQuestion(db, new Question(18, "Stephen Hawking is still alive"));
        writeQuestion(db, new Question(19, "Albert Einstein gave his first paper in 1905"));
        writeQuestion(db, new Question(20, "Newton invented Calculus"));
        writeQuestion(db, new Question(21, "All hair are black"));
        writeQuestion(db, new Question(22, "All mobiles run on battery"));
        writeQuestion(db, new Question(23, "Google has only Google Search"));
        writeQuestion(db, new Question(24, "Planets revolve around the Sun"));
        writeQuestion(db, new Question(25, "People need food to live"));
        writeQuestion(db, new Question(26, "Apollo 13 landed on the Moon"));
        writeQuestion(db, new Question(27, "ISRO launched Mangalyaan"));
        writeQuestion(db, new Question(28, "Earth's atmosphere is getting polluted"));
        writeQuestion(db, new Question(29, "One should not waste water"));
        writeQuestion(db, new Question(30, "Life is God's greatest Gift"));

    }

    private long writeQuestion(SQLiteDatabase db, Question question) {
        return writeQuestion(db, question, false);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
//        db.close();
    }

    public ArrayList<Question> getAllQuestions(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                QuestionsContract.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        int qNoColumnIndex = cursor.getColumnIndexOrThrow(QuestionsContract.COLUMN_NAME_QNO);
        int qTextColumnIndex = cursor.getColumnIndexOrThrow(QuestionsContract.COLUMN_NAME_QTEXT);
        int answerColumnIndex = cursor.getColumnIndexOrThrow(QuestionsContract.COLUMN_NAME_ANSWER);
        ArrayList<Question> questions = new ArrayList<Question>();
        while (cursor.moveToNext()){
            int qno = cursor.getInt(qNoColumnIndex);
            String qtext = cursor.getString(qTextColumnIndex);
            String answer = cursor.getString(answerColumnIndex);

            questions.add(new Question(qno, qtext, answer));
        }
        return questions;

    }

    public Question getQuestion(int qid){
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = QuestionsContract.COLUMN_NAME_QNO + " = ?";
        String[] selectionArgs = {qid + ""};
        Cursor cursor = db.query(
                QuestionsContract.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        int qNoColumnIndex = cursor.getColumnIndexOrThrow(QuestionsContract.COLUMN_NAME_QNO);
        int qTextColumnIndex = cursor.getColumnIndexOrThrow(QuestionsContract.COLUMN_NAME_QTEXT);
        int answerColumnIndex = cursor.getColumnIndexOrThrow(QuestionsContract.COLUMN_NAME_ANSWER);
        if (cursor.moveToNext()) {
            int qno = cursor.getInt(qNoColumnIndex);
            String qtext = cursor.getString(qTextColumnIndex);
            String answer = cursor.getString(answerColumnIndex);
            Question q =new Question(qno, qtext, answer);
            Log.d(TAG, "getQuestion with id = " + qid + ", Question = " + q.toString());
            return q;
        }
        else{
            Log.e(TAG,"Cursor didn't find any question with qno = " + qid);
            return null;
        }
    }

    public long writeQuestion(Question q){
        SQLiteDatabase db = this.getWritableDatabase();
        return writeQuestion(db, q, true);

    }

    public long writeQuestion(SQLiteDatabase db, Question q, boolean update){
        ContentValues values = new ContentValues();
        values.put(QuestionsContract.COLUMN_NAME_QNO, q.Qno);
        values.put(QuestionsContract.COLUMN_NAME_QTEXT, q.qText);
        values.put(QuestionsContract.COLUMN_NAME_ANSWER, q.savedAnswer);

        String selection = QuestionsContract.COLUMN_NAME_QNO + " = ?";
        String[] selectionArgs = {q.Qno + ""};

        if (update) {
            return db.update(QuestionsContract.TABLE_NAME, values, selection, selectionArgs);
        }
        else{
            return db.insert(QuestionsContract.TABLE_NAME, null, values);
        }
    }
}
