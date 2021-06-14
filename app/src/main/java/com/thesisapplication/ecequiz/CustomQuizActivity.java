package com.thesisapplication.ecequiz;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.thesisapplication.ecequiz.ShowResultActivity.ShowEditNoteDialog;

public class CustomQuizActivity extends AppCompatActivity {
    private TextView textViewSubject, textViewNOQAns;
    private Button buttonFinish;
    private int customQuizID;
    private int itemsAnswered;
    private int questionNumber;
    private int score;
    private boolean questionListShown;
    private boolean questionShown;
    private boolean resultShown;
    private int layoutCode;

    //Layout Question
    private TextView textViewQuestion, textViewOption1, textViewOption2, textViewOption3, textViewOption4;
    private Button buttonConfirm, buttonSkip;
    private int qPosition;


    private String title;
    private String userAnswerString;
    private int userAnswerInt;
    private int correctAnswer;

    private ArrayList<Question> questionList;
    private Question currentQuestion;

    private RecyclerView recyclerView;
    private CustomQuizQuestionAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private Toolbar toolbarCustomQuiz, toolbarCustomQuizQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_quiz);

        getExtra();
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        questionList = databaseAccess.getCustomQuizQuestions(customQuizID);
        databaseAccess.close();
        initLayoutQuestionList();


    }

    private void initLayoutQuestionList() {
        questionShown = false;
        layoutCode = 1;
        toolbarCustomQuiz = findViewById(R.id.toolbar_custom_quiz);
        setSupportActionBar(toolbarCustomQuiz);
        getSupportActionBar().setTitle(title);

        textViewSubject = (TextView) findViewById(R.id.custom_quiz_subject);
        textViewNOQAns = (TextView) findViewById(R.id.custom_quiz_number_of_questions_answered);
        buttonFinish = (Button) findViewById(R.id.custom_quiz_btn_finish);

        currentQuestion = questionList.get(0);
        textViewSubject.setText(currentQuestion.getSubject());
        textViewNOQAns.setText(itemsAnswered + " / " + questionList.size());

        recyclerView = findViewById(R.id.recycler_view_custom_quiz_question);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CustomQuizQuestionAdapter(questionList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new CustomQuizQuestionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                setContentView(R.layout.custom_quiz_question);
                initLayoutQuestion();
                qPosition = position;
                showQuestion();
                QuestionSetOnClickListener();
            }
        });

        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomQuizActivity.this);
                builder.setTitle("Finish Quiz");
                builder.setMessage("Are you sure?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initLayoutResult();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void initLayoutQuestion() {
        layoutCode = 2;
        toolbarCustomQuizQuestion = findViewById(R.id.toolbar_custom_quiz_question);
        setSupportActionBar(toolbarCustomQuizQuestion);
        getSupportActionBar().setTitle("Question");

        textViewQuestion = (TextView) findViewById(R.id.custom_quiz_question_text_view_question);
        textViewOption1 = (TextView) findViewById(R.id.custom_quiz_question_text_view_option1);
        textViewOption2 = (TextView) findViewById(R.id.custom_quiz_question_text_view_option2);
        textViewOption3 = (TextView) findViewById(R.id.custom_quiz_question_text_view_option3);
        textViewOption4 = (TextView) findViewById(R.id.custom_quiz_question_text_view_option4);
        buttonConfirm = (Button) findViewById(R.id.custom_quiz_question_btn_confirm);
        buttonSkip = (Button) findViewById(R.id.custom_quiz_question_btn_skip);
    }

    private void initLayoutResult() {
        layoutCode = 3;
        setContentView(R.layout.custom_quiz_result);
        Toolbar toolbar = findViewById(R.id.toolbar_custom_quiz_result);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Result");

        TextView textViewSubject = (TextView) findViewById(R.id.custom_quiz_result_subject);
        TextView textViewScore = (TextView) findViewById(R.id.custom_quiz_result_score);
        textViewSubject.setText(currentQuestion.getSubject());
        textViewScore.setText(score + " / " + questionList.size());

        RecyclerView recyclerViewResult = findViewById(R.id.recycler_view_custom_quiz_result);
        recyclerViewResult.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerResult = new LinearLayoutManager(this);
        recyclerViewResult.setLayoutManager(layoutManagerResult);
        HistoryQuestionAdapter adapterResult = new HistoryQuestionAdapter(questionList);


        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        databaseAccess.putCustomQuizInHistory(questionList, customQuizID, title);
        databaseAccess.close();
        adapterResult.setOnItemClickListener(new HistoryQuestionAdapter.OnItemClickListener() {
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
                ShowEditNoteDialog(id, note, CustomQuizActivity.this);

            }

            @Override
            public void onEditQuestion(int position) {

            }
        });

        recyclerViewResult.setAdapter(adapterResult);


    }

    private void showQuestion() {
        setDefaultBackground();
        userAnswerInt = 0;
        currentQuestion = questionList.get(qPosition);
        if(layoutCode == 2 && questionShown){
            if(itemsAnswered != questionList.size()) {
                while (currentQuestion.isAnswered()) {
                    if ((qPosition + 1) != questionList.size()) {
                        qPosition++;
                    } else {
                        qPosition = 0;
                    }
                    currentQuestion = questionList.get(qPosition);
                }
            }else {
                initLayoutResult();
            }
        }
        questionNumber = qPosition + 1;
        textViewQuestion.setText(questionNumber + ". " + currentQuestion.getQuestion());
        textViewOption1.setText("A. " + currentQuestion.getOption1());
        textViewOption2.setText("B. " + currentQuestion.getOption2());
        textViewOption3.setText("C. " + currentQuestion.getOption3());
        textViewOption4.setText("D. " + currentQuestion.getOption4());

        if(currentQuestion.isAnswered()){
            switch (currentQuestion.getUserAnswer()){
                case 1:
                    textViewOption1.setBackgroundResource(R.drawable.boarder_choices);
                    break;
                case 2:
                    textViewOption2.setBackgroundResource(R.drawable.boarder_choices);
                    break;
                case 3:
                    textViewOption3.setBackgroundResource(R.drawable.boarder_choices);
                    break;
                case 4:
                    textViewOption4.setBackgroundResource(R.drawable.boarder_choices);
                    break;
            }
        }
        questionShown = true;
    }

    private void QuestionSetOnClickListener() {
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
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
                showQuestion();
            }
        });

        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((qPosition + 1) != questionList.size()) {
                    qPosition++;
                }else {
                    qPosition = 0;
                }
                showQuestion();
            }
        });
    }

    private void setDefaultBackground() {
        textViewOption1.setBackground(null);
        textViewOption2.setBackground(null);
        textViewOption3.setBackground(null);
        textViewOption4.setBackground(null);
    }

    private void checkAnswer() {
        if(userAnswerInt == 0){
            Toast.makeText(getApplicationContext(), "Please select your answer", Toast.LENGTH_SHORT).show();
        }else {
            if (!currentQuestion.isAnswered()) {
                itemsAnswered++;
            }
            currentQuestion.setUserAnswer(userAnswerInt);
            currentQuestion.setUserAnswerString(userAnswerString);
            if (userAnswerInt == currentQuestion.getAnswerNumber()) {
                if (!currentQuestion.isAnswered() || currentQuestion.getCorrect() == 0) {
                    score++;
                }
                correctAnswer = 1;
                currentQuestion.setCorrectAnswer(correctAnswer);
            } else {

                if (currentQuestion.isAnswered() && currentQuestion.getCorrect() == 1) {
                    score--;
                }
                correctAnswer = 0;
                currentQuestion.setCorrectAnswer(correctAnswer);
            }
            currentQuestion.setCorrect(correctAnswer);
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            if (currentQuestion.getCorrect() != correctAnswer) {
                databaseAccess.updateRecordCorrect(currentQuestion.getId(), correctAnswer);
            }
            databaseAccess.updateUseOfQuestionCount(currentQuestion.getId());
            databaseAccess.close();
            currentQuestion.setAnswered(true);
        }
    }

    private void getExtra() {
        if (getIntent().hasExtra("customQuizID")) {
            customQuizID = getIntent().getExtras().getInt("customQuizID");
        }

        if (getIntent().hasExtra("quizTitle")) {
            title = getIntent().getExtras().getString("quizTitle");
        }
    }

    @Override
    public void onBackPressed() {
        switch (layoutCode){
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomQuizActivity.this);
                builder.setTitle("Finish Quiz");
                builder.setMessage("Are you sure?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initLayoutResult();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case 2:
                setContentView(R.layout.activity_custom_quiz);
                initLayoutQuestionList();
                break;
            case 3:
                Intent intent = new Intent(CustomQuizActivity.this, CustomQuizListActivity.class);
                finish();
                startActivity(intent);
        }
    }

}
