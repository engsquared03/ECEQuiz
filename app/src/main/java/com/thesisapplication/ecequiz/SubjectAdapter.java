package com.thesisapplication.ecequiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private ArrayList<QuestionsInfo> questionsInfoList;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewSubject;
        private TextView textViewNumberOfQuestions;
        private TextView textViewNumberOfQestionsUsed;
        private TextView textViewStats;

        public SubjectViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewSubject = itemView.findViewById(R.id.subject_item_subject);
            textViewNumberOfQuestions = itemView.findViewById(R.id.subject_item_number_of_questions);
            textViewNumberOfQestionsUsed = itemView.findViewById(R.id.subject_item_number_of_questions_used);
            textViewStats = itemView.findViewById(R.id.subject_item_stats);

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

    public SubjectAdapter(ArrayList<QuestionsInfo> questionsInfoList){
        this.questionsInfoList = questionsInfoList;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_item, parent, false);
        SubjectViewHolder svh = new SubjectViewHolder(v,listener);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        QuestionsInfo currentQuestionsInfo = questionsInfoList.get(position);

        holder.textViewSubject.setText(currentQuestionsInfo.getSubject());
        holder.textViewNumberOfQuestions.setText("Number of questions: "+currentQuestionsInfo.getNumberOfQuestions());
        holder.textViewNumberOfQestionsUsed.setText("Number of questions used: "+currentQuestionsInfo.getNumberOfQuestionsUsed());
        holder.textViewStats.setText(currentQuestionsInfo.getStats()+"%");
    }

    @Override
    public int getItemCount() {
        return questionsInfoList.size();
    }
}
