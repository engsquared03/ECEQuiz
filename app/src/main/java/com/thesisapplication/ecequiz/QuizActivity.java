package com.thesisapplication.ecequiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private TextView textViewSubject, textViewSource, textViewCategory, textViewScore, textViewQuestion, textViewOption1, textViewOption2, textViewOption3, textViewOption4, textViewConfirmBtn;
    private TextView textViewResult;
    private EditText editTextNotes;

    private String subject, source, category;
    private int quizID;
    private QuizInfo quizInfo;

    private ArrayList<Question> questionsList;

    private int random;
    private int numberOfQuestions;
    private int questionCount;
    private int score;
    private String userAnswerString;
    private int userAnswerInt;
    private Question currentQuestion;
    private boolean answered;
    private boolean shown;
    private int correctAnswer;

    private Dialog notesDialog;
    private String notes;

    private long backPressedTime;

    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        initLayoutComponents();

        choicesSetOnClickListener();
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        quizInfo = databaseAccess.getQuizInfo();
        setQuizInfo();
        if (random == 1) {
            questionsList = databaseAccess.getQuestionRandom(subject, source, category, numberOfQuestions);
        } else {
            questionsList = databaseAccess.getQuestion(subject, source, category, numberOfQuestions);
        }
        databaseAccess.close();
        showNextQuestion();
        ConfirmNextBtnClickListener();
        hideNotes();
    }

    private void ConfirmNextBtnClickListener() {
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        textViewConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (userAnswerInt != 0) {
                        if (questionCount == 1) {
                            databaseAccess.open();
                            databaseAccess.putQuestionListInHistory(questionsList, quizID);
                            databaseAccess.close();
                        }
                        checkAnswer();
                        if (currentQuestion.getCorrect() != correctAnswer) {
                            databaseAccess.open();
                            databaseAccess.updateRecordCorrect(currentQuestion.getId(), correctAnswer);
                            databaseAccess.close();
                        }
                        databaseAccess.open();
                        databaseAccess.updateUseOfQuestionCount(currentQuestion.getId());
                        databaseAccess.updateHistoryTable(quizID, currentQuestion.getId(), userAnswerInt, correctAnswer, userAnswerString);
                        databaseAccess.close();
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select your answer", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (questionCount == numberOfQuestions) {
                        finishQuiz();
                    } else {
                        if (shown) {
                            hideNotes();
                        }
                        showNextQuestion();
                    }
                }
            }
        });
    }

    private void showNotes() {
        editTextNotes.setBackgroundResource(R.drawable.notes_frame_2);
        notes = currentQuestion.getNote();
        if (currentQuestion.getNote() == null) {
            editTextNotes.setHint("Add notes here.");

        } else {
            editTextNotes.setText(currentQuestion.getNote());
        }
        editTextNotes.setSelection(editTextNotes.getText().length());
        editTextNotes.setPaddingRelative(15, 65, 10, 20);
        editTextNotes.setInputType(InputType.TYPE_CLASS_TEXT);
        editTextNotes.setEnabled(true);
        editTextNotes.setClickable(true);
        editTextNotes.requestFocus();
    }

    private void hideNotes() {
        String note = editTextNotes.getText().toString().trim();
        if(note.equals(notes) == false){
            currentQuestion.setNote(note);
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            databaseAccess.updateNotes(currentQuestion.getId(), note);
            databaseAccess.close();
        }
        editTextNotes.setBackground(null);
        editTextNotes.setText(null);
        editTextNotes.setHint(null);
        editTextNotes.setPaddingRelative(0, 0, 0, 0);

        editTextNotes.setInputType(InputType.TYPE_NULL);
        editTextNotes.setEnabled(false);
        editTextNotes.setClickable(false);
    }

    /*public void ShowPopupEditNotes(View v) {
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        final EditText editTextNotes;
        ImageButton btnClose;

        notesDialog.setContentView(R.layout.dialog_popup_notes);

        editTextNotes = (EditText) notesDialog.findViewById(R.id.edit_text_popup_notes);
        btnClose = (ImageButton) notesDialog.findViewById(R.id.image_btn_close_popup_notes);
        editTextNotes.setText(currentQuestion.getNote());
        editTextNotes.setSelection(editTextNotes.getText().length());
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notes = editTextNotes.getText().toString();
                currentQuestion.setNote(notes);
                databaseAccess.open();
                databaseAccess.updateNotes(currentQuestion.getId(), notes);
                databaseAccess.close();
                textViewNotes.setText(notes);
                notesDialog.dismiss();
            }
        });
        notesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        notesDialog.show();
    }*/

    private void initLayoutComponents() {
        textViewSubject = (TextView) findViewById(R.id.text_view_subject);
        textViewSource = (TextView) findViewById(R.id.text_view_source);
        textViewCategory = (TextView) findViewById(R.id.text_view_category);
        textViewScore = (TextView) findViewById(R.id.text_view_score);
        textViewQuestion = (TextView) findViewById(R.id.text_view_question);
        textViewOption1 = (TextView) findViewById(R.id.text_view_option1);
        textViewOption2 = (TextView) findViewById(R.id.text_view_option2);
        textViewOption3 = (TextView) findViewById(R.id.text_view_option3);
        textViewOption4 = (TextView) findViewById(R.id.text_view_option4);
        textViewConfirmBtn = (TextView) findViewById(R.id.text_view_confirm_btn);
        textViewResult = (TextView) findViewById(R.id.text_view_result);
        editTextNotes = (EditText) findViewById(R.id.edit_text_notes);
        scrollView = (ScrollView) findViewById(R.id.scrollview_quiz);
        scrollView.setFocusableInTouchMode(true);
        scrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        Toolbar toolbar = findViewById(R.id.toolbar_quiz);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Quiz");
    }

    private void setQuizInfo() {
        quizID = quizInfo.getQuizID();
        subject = quizInfo.getSubject();
        source = quizInfo.getSource();
        category = quizInfo.getCategory();
        numberOfQuestions = quizInfo.getNumberOfQuestions();
        random = quizInfo.getRandom();

        textViewSubject.setText(subject);
        textViewSource.setText(source);
        textViewCategory.setText(category);
    }

    private void choicesSetOnClickListener() {
        textViewOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAnswerString = currentQuestion.getOption1();
                userAnswerInt = 1;
                setDefaultBackground();
                textViewOption1.setBackgroundResource(R.drawable.boarder_choices);
            }
        });
        textViewOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAnswerString = currentQuestion.getOption2();
                userAnswerInt = 2;
                setDefaultBackground();
                textViewOption2.setBackgroundResource(R.drawable.boarder_choices);
            }
        });
        textViewOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAnswerString = currentQuestion.getOption3();
                userAnswerInt = 3;
                setDefaultBackground();
                textViewOption3.setBackgroundResource(R.drawable.boarder_choices);
            }
        });
        textViewOption4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAnswerString = currentQuestion.getOption4();
                userAnswerInt = 4;
                setDefaultBackground();
                textViewOption4.setBackgroundResource(R.drawable.boarder_choices);
            }
        });
    }

    private void setDefaultBackground() {
        textViewOption1.setBackground(null);
        textViewOption2.setBackground(null);
        textViewOption3.setBackground(null);
        textViewOption4.setBackground(null);
        textViewResult.setBackground(null);
    }

    private void showNextQuestion() {
        userAnswerInt = 0;
        setButtonOn();
        setDefaultBackground();

        if (questionCount == 0) {
            textViewScore.setText(0 + " / " + numberOfQuestions);
        }

        if (questionCount < numberOfQuestions) {
            currentQuestion = questionsList.get(questionCount);

            textViewQuestion.setText((questionCount + 1) + ".  " + currentQuestion.getQuestion());
            textViewOption1.setText("A.  " + currentQuestion.getOption1());
            textViewOption2.setText("B.  " + currentQuestion.getOption2());
            textViewOption3.setText("C.  " + currentQuestion.getOption3());
            textViewOption4.setText("D.  " + currentQuestion.getOption4());

            questionCount++;
            questionCount = questionCount;
            answered = false;
            textViewConfirmBtn.setText("Confirm");
        }
        scrollView.scrollTo(0, 0);
    }

    private void checkAnswer() {
        answered = true;
//        currentQuestion.setUserAnswer(userAnswerString);
        if (userAnswerInt == currentQuestion.getAnswerNumber()) {
            correctAnswer = 1;
            score++;
            textViewScore.setText(score + " / " + numberOfQuestions);
        } else {
            correctAnswer = 0;
        }
        showSolution();
    }

    private void showSolution() {
        if (userAnswerInt != currentQuestion.getAnswerNumber()) {
            textViewResult.setBackgroundResource(R.drawable.incorrect);
            switch (userAnswerInt) {
                case 1:
                    textViewOption1.setBackgroundResource(R.drawable.boarder_incorrect);
                    break;
                case 2:
                    textViewOption2.setBackgroundResource(R.drawable.boarder_incorrect);
                    break;
                case 3:
                    textViewOption3.setBackgroundResource(R.drawable.boarder_incorrect);
                    break;
                case 4:
                    textViewOption4.setBackgroundResource(R.drawable.boarder_incorrect);
                    break;
            }
        } else {
            textViewResult.setBackgroundResource(R.drawable.correct);
        }
        switch (currentQuestion.getAnswerNumber()) {
            case 1:
                textViewOption1.setBackgroundResource(R.drawable.boarder_correct);
                break;
            case 2:
                textViewOption2.setBackgroundResource(R.drawable.boarder_correct);
                break;
            case 3:
                textViewOption3.setBackgroundResource(R.drawable.boarder_correct);
                break;
            case 4:
                textViewOption4.setBackgroundResource(R.drawable.boarder_correct);
                break;
        }
        setButtonOff();
        if (questionCount < numberOfQuestions) {
            textViewConfirmBtn.setText("Next");
        } else {
            textViewConfirmBtn.setText("Finish");
        }
    }

    private void setButtonOff() {
        textViewOption1.setClickable(false);
        textViewOption2.setClickable(false);
        textViewOption3.setClickable(false);
        textViewOption4.setClickable(false);
    }

    private void setButtonOn() {
        textViewOption1.setClickable(true);
        textViewOption2.setClickable(true);
        textViewOption3.setClickable(true);
        textViewOption4.setClickable(true);
    }


    private void finishQuiz() {
        Intent showResultActivity = new Intent(QuizActivity.this, ShowResultActivity.class);
        finish();
        startActivity(showResultActivity);
    }

    @Override
    public void onBackPressed() {
        if (questionCount == 1 && !answered) {
            goSelectCategoryActivity();
        }
        if (questionCount != 1 || answered) {
            if (backPressedTime + 2000 > System.currentTimeMillis()) {
                finishQuiz();
            } else {
                Toast.makeText(this, "Press back again to finish quiz", Toast.LENGTH_SHORT).show();
            }

            backPressedTime = System.currentTimeMillis();
        }
    }

    private void goSelectCategoryActivity() {
        Intent goSelectCategory = new Intent(QuizActivity.this, SelectCategoryActivity.class);
        finish();
        startActivity(goSelectCategory);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (answered) {
            if (!shown) {
                showNotes();
                shown = true;
            } else {
                hideNotes();
                shown = false;
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please answer the question first", Toast.LENGTH_SHORT).show();
        }
        return true;
    }
}
