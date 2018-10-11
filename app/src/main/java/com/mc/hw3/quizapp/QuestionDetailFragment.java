package com.mc.hw3.quizapp;

import android.app.Activity;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


/**
 * A fragment representing a single Question detail screen.
 * This fragment is either contained in a {@link QuestionListActivity}
 * in two-pane mode (on tablets) or a {@link QuestionDetailActivity}
 * on handsets.
 */
public class QuestionDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";
    private static final String TAG = "QDetailFrag";

    private Button saveBtn;
    private RadioButton falseBtn;
    private RadioButton trueBtn;
    private RadioGroup radioGroup;

    private Question mItem;

    private QuestionsDBHelper helper;

    public QuestionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            helper = new QuestionsDBHelper(getContext());
//            mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
            Log.d(TAG,"Qno is " + getArguments().getInt(ARG_ITEM_ID));
            mItem = helper.getQuestion(getArguments().getInt(ARG_ITEM_ID));

            AppCompatActivity activity = (AppCompatActivity) this.getActivity();
            /*CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.Qno + "");
            }*/
            /*Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
            activity.setSupportActionBar(toolbar);
            toolbar.setTitle(mItem.Qno + "");*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.question_detail, container, false);
        saveBtn = (Button) rootView.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        // Show the dummy content as text in a TextView.
        radioGroup = rootView.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                clickRadioButton(checkedId, ((RadioButton)group.findViewById(checkedId)).isChecked());
            }
        });
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.question_text)).setText(mItem.qText);
            String ans = mItem.savedAnswer;
            if (ans!=null) {
                if (ans.equals("True")) {
                    radioGroup.check(R.id.true_radio);
                } else if (ans.equals("False")) {
                    radioGroup.check(R.id.false_radio);
                }
            }

        }

        return rootView;
    }

    public void clickRadioButton(int id, boolean checked){
        String ans = null;

        switch (id){
            case R.id.true_radio:
                if (checked){
//                if (((RadioButton) view).isChecked()) {
                    ans = "True";
                }
                break;
            case R.id.false_radio:
                if (checked){
//                if( ((RadioButton) view).isChecked()){
                    ans = "False";
                }
        }
        mItem.savedAnswer = ans;
//        save(view);
    }

    public void save(){
        QuestionsDBHelper helper = new QuestionsDBHelper(getContext());
        helper.writeQuestion(mItem);
    }

    @Override
    public void onDestroy() {
        helper.close();
        super.onDestroy();
    }
}
