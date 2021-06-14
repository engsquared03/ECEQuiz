package com.thesisapplication.ecequiz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.thesisapplication.ecequiz.ShowResultActivity.ShowEditNoteDialog;

public class HistoryCustomActivity extends AppCompatActivity {

    private int quizID;
    private int position;

    private TextView textViewSubject, textViewQuizTitle, textViewScore;

    private ArrayList<QuizInfo> quizInfoList;
    private ArrayList<Question> questionList;
    private QuizInfo quizInfo;

    private RecyclerView recyclerView;
    private HistoryQuestionAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_custom);

        getExtraQuizID();

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        quizInfoList = databaseAccess.getHistoryListCustom();
        questionList = databaseAccess.getHistoryCustomQuestions(quizID);
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
                ShowEditNoteDialog(id, note, HistoryCustomActivity.this);

            }

            @Override
            public void onEditQuestion(int position) {

            }
        });
    }

    private void initLayoutComponents(){
        textViewSubject = (TextView)findViewById(R.id.text_view_history_custom_subject);
        textViewQuizTitle = (TextView)findViewById(R.id.text_view_history_custom_quiz_title);
        textViewScore = (TextView)findViewById(R.id.text_view_history_custom_score);

        Toolbar toolbar = findViewById(R.id.toolbar_history_custom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("History");

        quizInfo = quizInfoList.get(position);
        textViewSubject.setText(quizInfo.getSubject());
        textViewQuizTitle.setText(quizInfo.getQuizTitle());
        textViewScore.setText(quizInfo.getScore()+" / "+questionList.size());

        recyclerView = findViewById(R.id.recycler_view_history_custom_questions);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void getExtraQuizID(){
        if(getIntent().hasExtra("customQuizID")){
            quizID = getIntent().getExtras().getInt("customQuizID");
        }
        if(getIntent().hasExtra("position")){
            position = getIntent().getExtras().getInt("position");
        }
    }

    @Override
    public void onBackPressed() {
        goHistoryLisActivity();
    }

    private void goHistoryLisActivity(){
        Intent intent = new Intent(HistoryCustomActivity.this, HistoryListActivity.class);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(HistoryCustomActivity.this);
        builder.setTitle("Delete question set");
        builder.setMessage("Are you sure?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseAccess.open();
                databaseAccess.deleteCustomHistory(quizID);
                databaseAccess.close();
                goHistoryLisActivity();

                Toast.makeText(getApplicationContext(),"Question set deleted", Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return super.onOptionsItemSelected(item);
    }
}
