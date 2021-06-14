package com.thesisapplication.ecequiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomQuizListAdapter extends RecyclerView.Adapter<CustomQuizListAdapter.CustomQuizListViewHolder> {
    private ArrayList<QuizInfo> quizInfoList;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class CustomQuizListViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewSubject;
        private TextView textViewQuizTitle;
        private TextView textViewNOQ;

        public CustomQuizListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewSubject = itemView.findViewById(R.id.cq_list_item_subject);
            textViewQuizTitle = itemView.findViewById(R.id.cq_list_item_quiz_title);
            textViewNOQ = itemView.findViewById(R.id.cq_list_item_noq);

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

    public CustomQuizListAdapter(ArrayList<QuizInfo> quizInfoList){
        this.quizInfoList = quizInfoList;
    }

    @NonNull
    @Override
    public CustomQuizListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_quiz_list_item, parent, false);
        CustomQuizListViewHolder svh = new CustomQuizListViewHolder(v,listener);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomQuizListViewHolder holder, int position) {
        QuizInfo quizInfo = quizInfoList.get(position);

        holder.textViewSubject.setText(quizInfo.getSubject());
        holder.textViewQuizTitle.setText(quizInfo.getQuizTitle());
        holder.textViewNOQ.setText(String.valueOf(quizInfo.getTotalNOQ()));

        holder.itemView.setTag(quizInfo.getQuizID());
    }

    @Override
    public int getItemCount() {
        return quizInfoList.size();
    }
}
