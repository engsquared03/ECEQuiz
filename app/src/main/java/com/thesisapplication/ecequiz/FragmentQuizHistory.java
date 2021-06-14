package com.thesisapplication.ecequiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FragmentQuizHistory extends Fragment {

    private View v;
    private RecyclerView recyclerView;
    private ArrayList<QuizInfo> quizInfoList;
    private QuizHistoryAdapter adapter;

    public FragmentQuizHistory(ArrayList<QuizInfo> quizInfoList) {
        this.quizInfoList = quizInfoList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_quiz_history, container, false);
        recyclerView = (RecyclerView)v.findViewById(R.id.recycler_view_fq);
        adapter = new QuizHistoryAdapter(getContext(),quizInfoList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new QuizHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                QuizInfo quizInfo = quizInfoList.get(position);
                showHistoryItem(quizInfo.getQuizID(), position);
                getActivity().finish();
            }
        });
        return v;
    }

    private void showHistoryItem(int quizID, int infoPosition){
        Intent intent = new Intent(getContext(), HistoryQuizActivity.class);
        intent.putExtra("quizID", quizID);
        intent.putExtra("position", infoPosition);
        startActivity(intent);
    }
}
