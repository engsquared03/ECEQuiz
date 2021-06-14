package com.thesisapplication.ecequiz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class EditQuestionActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private EditText editQuestion, editOption1, editOption2, editOption3, editOption4;

    private String questionText, option1, option2, option3, option4;
    private int answerSelection;

    private Question question, editedQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question);

        initLayoutComponents();
        int questionID = getIntent().getIntExtra("editQuestionID", 1);
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        question = databaseAccess.getQuestionFixedChoices(questionID);
        databaseAccess.close();
        editQuestion.setText("" + question.getQuestion());
        editQuestion.setSelection(editQuestion.getText().length());
        editOption1.setText("" + question.getOption1());
        editOption2.setText("" + question.getOption2());
        editOption3.setText("" + question.getOption3());
        editOption4.setText("" + question.getOption4());

        editedQuestion = new Question();
    }

    private void getAllText() {
        questionText = editQuestion.getText().toString().trim();
        option1 = editOption1.getText().toString().trim();
        option2 = editOption2.getText().toString().trim();
        option3 = editOption3.getText().toString().trim();
        option4 = editOption4.getText().toString().trim();
    }

    private void setEditQuestion(){
        editedQuestion.setQuestion(questionText);
        editedQuestion.setOption1(option1);
        editedQuestion.setOption2(option2);
        editedQuestion.setOption3(option3);
        editedQuestion.setOption4(option4);
    }

    private boolean questionIsEdited() {
        if (questionText.equals(question.getQuestion())
                && option1.equals(question.getOption1())
                && option2.equals(question.getOption2())
                && option3.equals(question.getOption3())
                && option4.equals(question.getOption4())){
            return false;
        }else {return true;}
    }

    private void initLayoutComponents() {
        toolbar = findViewById(R.id.toolbar_edit_question);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Question");

        editQuestion = (EditText) findViewById(R.id.edit_question);
        editOption1 = (EditText) findViewById(R.id.edit_option1);
        editOption2 = (EditText) findViewById(R.id.edit_option2);
        editOption3 = (EditText) findViewById(R.id.edit_option3);
        editOption4 = (EditText) findViewById(R.id.edit_option4);

    }


    @Override
    public void onBackPressed() {
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        getAllText();
        if(questionIsEdited()){
            AlertDialog.Builder builder = new AlertDialog.Builder(EditQuestionActivity.this);
            builder.setTitle("Save changes?");
            builder.setNegativeButton("No", null);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setEditQuestion();
                    databaseAccess.open();
                    databaseAccess.applyEditQuestions(question.getId(), editedQuestion, question);
                    databaseAccess.close();
                    Toast.makeText(getApplicationContext(), "Question edit succcesful.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else {finish();}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        getAllText();
        if(questionIsEdited()){
            AlertDialog.Builder builder = new AlertDialog.Builder(EditQuestionActivity.this);
            builder.setTitle("Save changes?");
            builder.setNegativeButton("No", null);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setEditQuestion();
                    databaseAccess.open();
                    databaseAccess.applyEditQuestions(question.getId(), editedQuestion, question);
                    databaseAccess.close();
                    Toast.makeText(getApplicationContext(), "Question edit succcesful.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }else {finish();}
        return true;
    }
}
