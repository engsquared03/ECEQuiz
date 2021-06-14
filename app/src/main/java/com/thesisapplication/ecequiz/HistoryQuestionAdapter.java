package com.thesisapplication.ecequiz;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryQuestionAdapter extends RecyclerView.Adapter<HistoryQuestionAdapter.HistoryQuestionViewHolder> {
    private ArrayList<Question> questionList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onViewNote(int position);
        void onEditQuestion(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class HistoryQuestionViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewQuestion;
        private TextView textViewOption1;
        private TextView textViewOption2;
        private TextView textViewOption3;
        private TextView textViewOption4;
        private TextView textViewResult;
        private ImageView imageViewNote;
        private ImageView imageViewEditQuestion;

        public HistoryQuestionViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewQuestion = itemView.findViewById(R.id.result_question_item_question);
            textViewOption1 = itemView.findViewById(R.id.result_question_item_option1);
            textViewOption2 = itemView.findViewById(R.id.result_question_item_option2);
            textViewOption3 = itemView.findViewById(R.id.result_question_item_option3);
            textViewOption4 = itemView.findViewById(R.id.result_question_item_option4);
            textViewResult = itemView.findViewById(R.id.result_question_item_result);
            imageViewNote = itemView.findViewById(R.id.image_view_notes);
            imageViewEditQuestion = itemView.findViewById(R.id.image_view_edit_question);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            imageViewNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onViewNote(position);
                        }
                    }
                }
            });

            imageViewEditQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onEditQuestion(position);
                        }
                    }
                }
            });
        }
    }

    public HistoryQuestionAdapter(ArrayList<Question> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public HistoryQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_question_item, parent, false);
        HistoryQuestionViewHolder svh = new HistoryQuestionViewHolder(v, listener);
        return svh;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull HistoryQuestionViewHolder holder, int position) {
        int questionNumber = position + 1;
        Question currentQuestion = questionList.get(position);
        int questionID = currentQuestion.getId();

        holder.textViewQuestion.setText(questionNumber + ". " + currentQuestion.getQuestion());
        holder.textViewOption1.setText("A. " + currentQuestion.getOption1());
        holder.textViewOption2.setText("B. " + currentQuestion.getOption2());
        holder.textViewOption3.setText("C. " + currentQuestion.getOption3());
        holder.textViewOption4.setText("D. " + currentQuestion.getOption4());

        holder.textViewOption1.setBackground(null);
        holder.textViewOption2.setBackground(null);
        holder.textViewOption3.setBackground(null);
        holder.textViewOption4.setBackground(null);

        if (currentQuestion.getCorrectAnswer() == 1) {
            holder.textViewResult.setBackgroundResource(R.drawable.correct);
        }

        if (currentQuestion.getCorrectAnswer() == 0) {
            if (currentQuestion.getUserAnswer() == 0) {
                holder.textViewResult.setBackgroundResource(R.drawable.unanswered);
            } else {
                holder.textViewResult.setBackgroundResource(R.drawable.incorrect);
            }
            switch (currentQuestion.getUserAnswer()) {
                case 1:
                    holder.textViewOption1.setBackgroundResource(R.drawable.boarder_incorrect);
                    break;
                case 2:
                    holder.textViewOption2.setBackgroundResource(R.drawable.boarder_incorrect);
                    break;
                case 3:
                    holder.textViewOption3.setBackgroundResource(R.drawable.boarder_incorrect);
                    break;
                case 4:
                    holder.textViewOption4.setBackgroundResource(R.drawable.boarder_incorrect);
                    break;
            }
        }

        switch (currentQuestion.getAnswerNumber()) {
            case 1:
                holder.textViewOption1.setBackgroundResource(R.drawable.boarder_correct);
                break;
            case 2:
                holder.textViewOption2.setBackgroundResource(R.drawable.boarder_correct);
                break;
            case 3:
                holder.textViewOption3.setBackgroundResource(R.drawable.boarder_correct);
                break;
            case 4:
                holder.textViewOption4.setBackgroundResource(R.drawable.boarder_correct);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }
}
