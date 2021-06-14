package com.thesisapplication.ecequiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.SourceViewHolder> {
    private ArrayList<QuestionsInfo> questionsInfoList;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class SourceViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewSource;
        private TextView textViewNumberOfQuestions;
        private TextView textViewNumberOfQestionsUsed;
        private TextView textViewStats;

        public SourceViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewSource = itemView.findViewById(R.id.source_item_source);
            textViewNumberOfQuestions = itemView.findViewById(R.id.source_item_number_of_questions);
            textViewNumberOfQestionsUsed = itemView.findViewById(R.id.source_item_number_of_questions_used);
            textViewStats = itemView.findViewById(R.id.source_item_stats);

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

    public SourceAdapter(ArrayList<QuestionsInfo> questionsInfoList){
        this.questionsInfoList = questionsInfoList;
    }

    @NonNull
    @Override
    public SourceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.source_item, parent, false);
        SourceViewHolder svh = new SourceViewHolder(v,listener);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull SourceViewHolder holder, int position) {
        QuestionsInfo currentQuestionsInfo = questionsInfoList.get(position);

        holder.textViewSource.setText(currentQuestionsInfo.getSource());
        holder.textViewNumberOfQuestions.setText("Number of questions: "+currentQuestionsInfo.getNumberOfQuestions());
        holder.textViewNumberOfQestionsUsed.setText("Number of questions used: "+currentQuestionsInfo.getNumberOfQuestionsUsed());
        holder.textViewStats.setText(currentQuestionsInfo.getStats()+"%");
    }

    @Override
    public int getItemCount() {
        return questionsInfoList.size();
    }
}
