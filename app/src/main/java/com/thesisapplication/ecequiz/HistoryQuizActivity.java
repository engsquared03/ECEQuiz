package com.thesisapplication.ecequiz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import static com.thesisapplication.ecequiz.ShowResultActivity.*;
import java.util.ArrayList;

public class HistoryQuizActivity extends AppCompatActivity {
    private int quizID;
    private int position;

    private TextView textViewSubject, textViewSource, textViewCategory, textViewScore;

    private ArrayList<QuizInfo> quizInfoList;
    private ArrayList<Question> questionList;
    private QuizInfo quizInfo;

    private RecyclerView recyclerView;
    private HistoryQuestionAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getExtraQuizID();

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        quizInfoList = databaseAccess.getHistoryList();
        questionList = databaseAccess.getHistoryQuestions(quizID);
        databaseAccess.close();
        initLayoutComponents();

        adapter = new HistoryQuestionAdapter(questionList);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new HistoryQuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onViewNote(int position) {
                Question question = questionList.get(position);
                int id = question.getId();
                databaseAccess.open();
                String note = databaseAccess.getNote(id);
                databaseAccess.close();
                ShowEditNoteDialog(id, note, HistoryQuizActivity.this);

            }

            @Override
            public void onEditQuestion(int position) {
                Question question = questionList.get(position);
                final int id = question.getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryQuizActivity.this);
                builder.setTitle("Edit question");
                builder.setMessage("Are you sure?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(HistoryQuizActivity.this, EditQuestionActivity.class);
                        intent.putExtra("editQuestionID", id);
                        startActivity(intent);
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }


    private void getExtraQuizID(){
        if(getIntent().hasExtra("quizID")){
            quizID = getIntent().getExtras().getInt("quizID");
        }
        if(getIntent().hasExtra("position")){
            position = getIntent().getExtras().getInt("position");
        }
    }

    private void initLayoutComponents(){
        textViewSubject = (TextView)findViewById(R.id.text_view_history_subject);
        textViewSource = (TextView)findViewById(R.id.text_view_history_source);
        textViewCategory = (TextView)findViewById(R.id.text_view_history_category);
        textViewScore = (TextView)findViewById(R.id.text_view_history_score);
        Toolbar toolbar = findViewById(R.id.toolbar_history_quiz);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("History");

        quizInfo = quizInfoList.get(position);
        textViewSubject.setText(quizInfo.getSubject());
        textViewSource.setText(quizInfo.getSource());
        textViewCategory.setText(quizInfo.getCategory());
        textViewScore.setText(quizInfo.getScore()+" / "+quizInfo.getNumberOfQuestions());

        recyclerView = findViewById(R.id.recycler_view_history_questions);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onBackPressed() {
        goHistoryLisActivity();
    }

    private void goHistoryLisActivity(){
        Intent intent = new Intent(HistoryQuizActivity.this, HistoryListActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryQuizActivity.this);
        builder.setTitle("Delete question set");
        builder.setMessage("Are you sure?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseAccess.open();
                databaseAccess.deleteQuizHistory(quizID);
                databaseAccess.close();
                goHistoryLisActivity();

                Toast.makeText(getApplicationContext(),"Question set deleted", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return true;
    }
}
