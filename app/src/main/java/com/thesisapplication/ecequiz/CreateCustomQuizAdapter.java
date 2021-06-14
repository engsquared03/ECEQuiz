package com.thesisapplication.ecequiz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CreateCustomQuizAdapter extends RecyclerView.Adapter<CreateCustomQuizAdapter.CreateCustomQuizViewHolder> {
    private ArrayList<QuizInfo> quizInfoList;
    private OnItemClickListener listener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static class CreateCustomQuizViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewCategory;
        private TextView textViewSource;
        private TextView textViewNOQ;

        public CreateCustomQuizViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            textViewCategory = itemView.findViewById(R.id.create_cq_item_category);
            textViewSource = itemView.findViewById(R.id.create_cq_item_source);
            textViewNOQ = itemView.findViewById(R.id.create_cq_item_noq);

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

    public CreateCustomQuizAdapter(ArrayList<QuizInfo> quizInfoList){
        this.quizInfoList = quizInfoList;
    }

    @NonNull
    @Override
    public CreateCustomQuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.create_cq_item, parent, false);
        CreateCustomQuizViewHolder svh = new CreateCustomQuizViewHolder(v,listener);
        return svh;
    }

    @Override
    public void onBindViewHolder(@NonNull CreateCustomQuizViewHolder holder, int position) {
        QuizInfo quizInfo = quizInfoList.get(position);

        holder.textViewCategory.setText(quizInfo.getCategory());
        holder.textViewSource.setText(quizInfo.getSource());
        holder.textViewNOQ.setText(String.valueOf(quizInfo.getNumberOfQuestions()));
    }

    @Override
    public int getItemCount() {
        return quizInfoList.size();
    }

}
