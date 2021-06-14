package com.thesisapplication.ecequiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowResultActivity extends AppCompatActivity {

    private TextView textViewSubject, textViewSource, textViewCategory, textViewScore, textViewPlayAgain, textViewHome;
    private QuizInfo quizInfo;

    private String subject, source, category;
    private int numberOfQuestions, score, quizID;

    private ArrayList<Question> questionList;

    private RecyclerView recyclerView;
    private HistoryQuestionAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_result);



        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        quizInfo = databaseAccess.getQuizInfo();
        initLayoutComponents();
        questionList = databaseAccess.getHistoryQuestions(quizID);
        databaseAccess.close();

        adapter = new HistoryQuestionAdapter(questionList);
        recyclerView.setAdapter(adapter);

        textViewPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseAccess.open();
                databaseAccess.playAgainSetup();
                databaseAccess.close();

                goPlayAgain();
            }
        });

        textViewHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHomeMenu();
            }
        });

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
                if(note == null){
                    note = "";
                }
                ShowEditNoteDialog(id, note, ShowResultActivity.this);
            }

            @Override
            public void onEditQuestion(int position) {

            }
        });

    }

    public static void ShowEditNoteDialog(final int questionID, final String note, Context context){
        final Dialog notesDialog = new Dialog(context);
        notesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        notesDialog.setContentView(R.layout.dialog_popup_notes);
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        final EditText editTextNotes;
        ImageButton btnClose;

        editTextNotes = (EditText) notesDialog.findViewById(R.id.edit_text_popup_notes);
        btnClose = (ImageButton) notesDialog.findViewById(R.id.image_btn_close_popup_notes);
        if(note.isEmpty()){
            editTextNotes.setHint("Write your notes here");
        }else {
            editTextNotes.setText(""+note);
        }
        editTextNotes.setSelection(editTextNotes.getText().length());
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editNote = editTextNotes.getText().toString().trim();
                databaseAccess.open();
                databaseAccess.updateNotes(questionID, editNote);
                databaseAccess.close();
                notesDialog.dismiss();
            }
        });
        notesDialog.setCanceledOnTouchOutside(false);
        notesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        notesDialog.show();
    }

    @Override
    public void onBackPressed() {
        goHomeMenu();
    }

    private void initLayoutComponents(){
        textViewSubject = (TextView)findViewById(R.id.text_view_result_subject);
        textViewSource = (TextView)findViewById(R.id.text_view_result_source);
        textViewCategory = (TextView)findViewById(R.id.text_view_result_category);
        textViewScore = (TextView)findViewById(R.id.text_view_result_score);
        textViewPlayAgain = (TextView)findViewById(R.id.text_view_result_play_again);
        textViewHome = (TextView)findViewById(R.id.text_view_result_home);
        Toolbar toolbar = findViewById(R.id.toolbar_quiz_result);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Result");

        quizID = quizInfo.getQuizID();
        subject = quizInfo.getSubject();
        source = quizInfo.getSource();
        category = quizInfo.getCategory();
        numberOfQuestions = quizInfo.getNumberOfQuestions();
        score = quizInfo.getScore();

        textViewSubject.setText(subject);
        textViewSource.setText(source);
        textViewCategory.setText(category);
        textViewScore.setText(score + " / "+ numberOfQuestions);

        recyclerView = findViewById(R.id.recycler_view_result_questions);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void goPlayAgain(){
        Intent intentPlayAgain = new Intent(ShowResultActivity.this, QuizActivity.class);
        finish();
        startActivity(intentPlayAgain);
    }

    private void goHomeMenu(){
        Intent intentHome = new Intent(ShowResultActivity.this, MainActivity.class);
        finish();
        startActivity(intentHome);
    }
}
