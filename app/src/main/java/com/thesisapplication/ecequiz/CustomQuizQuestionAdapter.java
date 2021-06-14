package com.thesisapplication.ecequiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomQuizQuestionAdapter extends RecyclerView.Adapter<CustomQuizQuestionAdapter.CustomQuizQuestionViewHolder> {
    private ArrayList<Question> questionList;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class CustomQuizQuestionViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewQuestion;
        private TextView textViewAnswered;

        public CustomQuizQuestionViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.cqq_list_item_question);
            textViewAnswered = itemView.findViewById(R.id.cqq_list_item_answered);

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

    public CustomQuizQuestionAdapter(ArrayList<Question> questionList){
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public CustomQuizQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_quiz_question_list_item, parent, false);
        CustomQuizQuestionViewHolder svh = new CustomQuizQuestionViewHolder(v,listener);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomQuizQuestionViewHolder holder, int position) {
        Question currentQuestion = questionList.get(position);
        int questionNumber = position + 1;

        holder.textViewQuestion.setText(questionNumber+". "+currentQuestion.getQuestion());
        if(currentQuestion.getUserAnswer() == 0){
            holder.textViewAnswered.setBackgroundResource(R.drawable.answered_false);
        }else {
            holder.textViewAnswered.setBackgroundResource(R.drawable.answered_true);
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
}
