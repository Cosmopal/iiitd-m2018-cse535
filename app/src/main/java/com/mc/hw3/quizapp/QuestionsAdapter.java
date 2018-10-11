package com.mc.hw3.quizapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class QuestionsAdapter
        extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

    private static final String TAG = "QuestionsAdapter";
    private final QuestionListActivity mParentActivity;
    public final List<Question> mValues;
    private final boolean mTwoPane;
    private View previousSelected;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Question item = (Question) view.getTag();
            view.setBackgroundColor(0xFFA8A8A8);
            if (previousSelected != null){
                previousSelected.setBackgroundColor(0xFFFFFFFF);
            }
            previousSelected = view;
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putInt(QuestionDetailFragment.ARG_ITEM_ID, item.Qno);
                QuestionDetailFragment fragment = new QuestionDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.question_detail_container, fragment)
                        .commit();
            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, QuestionDetailActivity.class);
                intent.putExtra(QuestionDetailFragment.ARG_ITEM_ID, item.Qno);

                context.startActivity(intent);
            }
        }
    };

    QuestionsAdapter(QuestionListActivity parent,
                     boolean twoPane) {
        QuestionsDBHelper helper = new QuestionsDBHelper(parent);
        ArrayList<Question> items = helper.getAllQuestions();
        Log.d(TAG,"Constructor: the list obtained is " + items.toString());
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mQnoTv.setText(mValues.get(position).Qno + "");
        holder.mQTextTv.setText(mValues.get(position).qText);

        holder.itemView.setTag(mValues.get(position));
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mQnoTv;
        final TextView mQTextTv;

        ViewHolder(View view) {
            super(view);
            mQnoTv = (TextView) view.findViewById(R.id.qno);
            mQTextTv = (TextView) view.findViewById(R.id.content);
        }
    }
}