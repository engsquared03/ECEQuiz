package com.thesisapplication.ecequiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class QuizHistoryAdapter extends RecyclerView.Adapter<QuizHistoryAdapter.HistoryListViewHolder> {
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

    public static class HistoryListViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewSubject;
        private TextView textViewScore;
        private TextView textViewFirstQuestion;
        private TextView textViewSource;
        private TextView textViewCategory;
        private TextView textViewDate;

        public HistoryListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewSubject = itemView.findViewById(R.id.history_list_item_subject);
            textViewScore = itemView.findViewById(R.id.history_list_item_score);
            textViewFirstQuestion = itemView.findViewById(R.id.history_list_item_question);
            textViewSource = itemView.findViewById(R.id.history_list_item_source);
            textViewCategory = itemView.findViewById(R.id.history_list_item_category);
            textViewDate = itemView.findViewById(R.id.history_list_item_date);

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

    public QuizHistoryAdapter(Context context,ArrayList<QuizInfo> quizInfoList){
        this.context = context;
        this.quizInfoList = quizInfoList;
    }

    @NonNull
    @Override
    public HistoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.quiz_history_list_item, parent, false);
        HistoryListViewHolder svh = new HistoryListViewHolder(v,listener);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryListViewHolder holder, int position) {
        QuizInfo currentQuizInfo = quizInfoList.get(position);

        holder.textViewSubject.setText(currentQuizInfo.getSubject());
        holder.textViewScore.setText(currentQuizInfo.getScore()+"/"+currentQuizInfo.getNumberOfQuestions());
        holder.textViewFirstQuestion.setText("1.  "+currentQuizInfo.getFirstQuestion());
        holder.textViewSource.setText("Source: "+currentQuizInfo.getSource());
        holder.textViewCategory.setText("Category: "+currentQuizInfo.getCategory());
        holder.textViewDate.setText(currentQuizInfo.getDate());
    }

    @Override
    public int getItemCount() {
        return quizInfoList.size();
    }
}
