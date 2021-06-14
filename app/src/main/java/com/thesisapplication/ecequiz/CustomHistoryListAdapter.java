package com.thesisapplication.ecequiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomHistoryListAdapter extends RecyclerView.Adapter<CustomHistoryListAdapter.CustomHistoryListViewHolder> {
    private Context context;
    private ArrayList<QuizInfo> quizInfoList;
    private QuizInfo quizInfo;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class CustomHistoryListViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewSubject;
        private TextView textViewScore;
        private TextView textViewFirstQuestion;
        private TextView textViewQuizTitle;
        private TextView textViewDate;

        public CustomHistoryListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewSubject = itemView.findViewById(R.id.custom_quiz_history_item_subject);
            textViewScore = itemView.findViewById(R.id.custom_quiz_history_item_score);
            textViewFirstQuestion = itemView.findViewById(R.id.custom_quiz_history_item_question);
            textViewQuizTitle = itemView.findViewById(R.id.custom_quiz_history_item_quiz_title);
            textViewDate = itemView.findViewById(R.id.custom_quiz_history_item_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public CustomHistoryListAdapter(Context context, ArrayList<QuizInfo> quizInfoList){
        this.context = context;
        this.quizInfoList = quizInfoList;
    }

    @NonNull
    @Override
    public CustomHistoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.custom_quiz_history_list_item, parent, false);
        CustomHistoryListViewHolder svh = new CustomHistoryListViewHolder(v,listener);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomHistoryListViewHolder holder, int position) {
        QuizInfo currentQuizInfo = quizInfoList.get(position);

        holder.textViewSubject.setText(""+currentQuizInfo.getSubject());
        holder.textViewScore.setText(currentQuizInfo.getScore()+"/"+currentQuizInfo.getNumberOfQuestions());
        holder.textViewFirstQuestion.setText("1.  "+currentQuizInfo.getFirstQuestion());
        holder.textViewQuizTitle.setText("Custom Quiz Title: "+currentQuizInfo.getQuizTitle());
        holder.textViewDate.setText(currentQuizInfo.getDate());
    }

    @Override
    public int getItemCount() {
        return quizInfoList.size();
    }
}
