package com.thesisapplication.ecequiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreateCustomQuizActivity extends AppCompatActivity {
    private Toolbar toolbarCreateCQ;
    private TextView textViewNOQ;
    private EditText editTextQuizTitle;
    private Button buttonAdd, buttonFinish;

    private Spinner spinnerSubject, spinnerSource, spinnerCategory, spinnerNOQ;
    private SpinnerAdapter spinnerAdapterSubject, spinnerAdapterSource, spinnerAdapterCategory, spinnerAdapterNOQ;

    private String subject, source, category;
    private List<String> listOfSubject, listOfSource, listOfCategory, listOfNOQ, listAdded;
    private int subjectPosition, sourcePosition, categoryposition, noqPosition;
    private int createCQCode;
    private int numberOfQuestions, questionCount, totalNOQ;
    private String quizTitle;

    private ArrayList<QuizInfo> quizInfoList;

    private RecyclerView recyclerView;
    private CreateCustomQuizAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_custom_quiz);

        getSubjectList();
        getSourceList();
        getCategoryList();
        setListOfNOQ();
        initLayoutComponents();
        setAddButtonListener();
        setButtonFinishListener();
    }

    private void initLayoutComponents() {
        toolbarCreateCQ = findViewById(R.id.toolbar_create_custom_quiz);
        setSupportActionBar(toolbarCreateCQ);
        getSupportActionBar().setTitle("Create Custom Quiz");

        recyclerView = findViewById(R.id.recycler_view_create_cq);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        textViewNOQ = (TextView) findViewById(R.id.create_cq_text_view_noq);
        editTextQuizTitle = (EditText)findViewById(R.id.create_cq_edit_text_quiz_title);
        buttonAdd = (Button) findViewById(R.id.create_cq_btn_add);
        buttonFinish = (Button) findViewById(R.id.create_cq_btn_finish);

        spinnerSubject = (Spinner) findViewById(R.id.create_cq_spinner_subject);
        spinnerSource = (Spinner) findViewById(R.id.create_cq_spinner_source);
        spinnerCategory = (Spinner) findViewById(R.id.create_cq_spinner_category);
        spinnerNOQ = (Spinner) findViewById(R.id.create_cq_spinner_noq);

        setSubjectSpinner();
        setSourceSpinner();
        setCategorySpinner();
        setNOQSpinner();

        quizInfoList = new ArrayList<>();
        listAdded = new ArrayList<>();
        adapter = new CreateCustomQuizAdapter(quizInfoList);
        recyclerView.setAdapter(adapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                quizInfoList.remove(viewHolder.getAdapterPosition());
                listAdded.remove(viewHolder.getAdapterPosition());
                updateTotalNOQ();
                adapter.notifyDataSetChanged();
            }
        }).attachToRecyclerView(recyclerView);
    }



    private void setAddButtonListener() {
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(subject == null){
                    Toast.makeText(getApplicationContext(), "Please select a subject", Toast.LENGTH_SHORT).show();
                }else {
                    if (source == null) {
                        Toast.makeText(getApplicationContext(), "Please select a source", Toast.LENGTH_SHORT).show();
                    } else {
                        if (category == null) {
                            Toast.makeText(getApplicationContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                        } else {
                            if (numberOfQuestions == 0) {
                                Toast.makeText(getApplicationContext(), "Please select the number of questions", Toast.LENGTH_SHORT).show();
                            } else {
                                if (numberOfQuestions > questionCount) {
                                    Toast.makeText(getApplicationContext(), "Please select a lower value for the number of questions", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (listAdded.contains(source + category)) {
                                        Toast.makeText(getApplicationContext(), "The source and category selected is already added ", Toast.LENGTH_SHORT).show();
                                    } else {
//                                        totalNOQ = totalNOQ + numberOfQuestions;
                                        QuizInfo quizInfo = new QuizInfo();
                                        quizInfo.setSubject(subject);
                                        quizInfo.setSource(source);
                                        quizInfo.setCategory(category);
                                        quizInfo.setNumberOfQuestions(numberOfQuestions);
                                        quizInfoList.add(quizInfo);
                                        adapter.notifyDataSetChanged();
                                        listAdded.add(source + category);
                                        updateTotalNOQ();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void updateTotalNOQ(){
        totalNOQ = 0;
        for(int i = 0; i < quizInfoList.size(); i++){
            QuizInfo quizInfo = quizInfoList.get(i);
            totalNOQ = totalNOQ + quizInfo.getNumberOfQuestions();
        }
    }

    private void setButtonFinishListener(){
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quizTitle = editTextQuizTitle.getText().toString();
                if(quizTitle.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please input quiz title", Toast.LENGTH_SHORT).show();
                }else {
                    if(quizInfoList.size() == 0){
                        Toast.makeText(getApplicationContext(), "Please add some topics", Toast.LENGTH_SHORT).show();
                    }else {
                        databaseAccess.open();
                        databaseAccess.createCustomQuiz(quizInfoList, quizTitle, totalNOQ);
                        databaseAccess.close();
                        Toast.makeText(getApplicationContext(), "Quiz create successful", Toast.LENGTH_SHORT).show();
                        goCustomQuizListAct();
                    }
                }
            }
        });
    }

    private void setSubjectSpinner() {
        spinnerAdapterSubject = new ArrayAdapter<String>(this, R.layout.text_view_spinner_stats, listOfSubject);
        ((ArrayAdapter<String>) spinnerAdapterSubject).setDropDownViewResource(R.layout.text_view_spinner_stats);
        spinnerSubject.setAdapter(spinnerAdapterSubject);
        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectPosition = position;
                if (position != 0) {
                    createCQCode = 1;
                    subject = listOfSubject.get(position);
                    getSourceList();
                    setSourceSpinner();
                    spinnerSubject.setEnabled(false);
                    spinnerSource.setEnabled(true);
                    getQuestionCount();
                } else {
                    spinnerSource.setEnabled(false);
                    spinnerCategory.setEnabled(false);
                    spinnerNOQ.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setSourceSpinner() {
        spinnerAdapterSource = new ArrayAdapter<String>(this, R.layout.text_view_spinner_stats, listOfSource);
        ((ArrayAdapter<String>) spinnerAdapterSource).setDropDownViewResource(R.layout.text_view_spinner_stats);
        spinnerSource.setAdapter(spinnerAdapterSource);

        spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sourcePosition = position;
                if (position == 0) {
                    if (createCQCode != 0 && subjectPosition != 0) {
                        source = null;
                        category = null;
                        createCQCode = 1;
                        getCategoryList();
                        setCategorySpinner();
                        getQuestionCount();
                        spinnerCategory.setEnabled(false);
                        spinnerNOQ.setEnabled(false);
                    }
                }
                if (position != 0) {
                    source = listOfSource.get(position);
                    createCQCode = 2;
                    getCategoryList();
                    setCategorySpinner();
                    getQuestionCount();
                    spinnerCategory.setEnabled(true);
                    spinnerNOQ.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCategorySpinner() {
        spinnerAdapterCategory = new ArrayAdapter<String>(this, R.layout.text_view_spinner_stats, listOfCategory);
        ((ArrayAdapter<String>) spinnerAdapterCategory).setDropDownViewResource(R.layout.text_view_spinner_stats);
        spinnerCategory.setAdapter(spinnerAdapterCategory);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoryposition = position;
                if (position == 0) {
                    if (createCQCode != 0 && sourcePosition != 0) {
                        category = null;
                        createCQCode = 2;
                        getQuestionCount();
                        spinnerNOQ.setEnabled(false);
                    }
                }
                if (position != 0) {
                    category = listOfCategory.get(position);
                    createCQCode = 3;
                    getQuestionCount();
                    spinnerNOQ.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setNOQSpinner() {
        spinnerAdapterNOQ = new ArrayAdapter<String>(this, R.layout.text_view_spinner_stats, listOfNOQ);
        ((ArrayAdapter<String>) spinnerAdapterNOQ).setDropDownViewResource(R.layout.text_view_spinner_stats);
        spinnerNOQ.setAdapter(spinnerAdapterNOQ);

        spinnerNOQ.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                noqPosition = position;
                if (position != 0) {
                    if (position != 6) {
                        numberOfQuestions = Integer.parseInt(listOfNOQ.get(position));
                    } else {
                        numberOfQuestions = questionCount;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getQuestionCount() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        questionCount = databaseAccess.getQuestionCount(createCQCode, subject, source, category);
        databaseAccess.close();
        textViewNOQ.setText("Number of Questions (" + questionCount + ")");
        if(noqPosition == 6){
            numberOfQuestions = questionCount;
        }
    }

    private void getSubjectList() {
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        listOfSubject = databaseAccess.getListOfSubject();
        databaseAccess.close();
        listOfSubject.add(0, "Select Subject");
    }

    private void getSourceList() {
        if (subjectPosition == 0) {
            listOfSource = new ArrayList<>();
            listOfSource.add("Select Source");
        } else {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            listOfSource = databaseAccess.getListOfSource(subject);
            databaseAccess.close();
            listOfSource.add(0, "Select Source");
        }
    }

    private void getCategoryList() {
        if (sourcePosition == 0) {
            listOfCategory = new ArrayList<>();
            listOfCategory.add("Select Category");
        } else {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            listOfCategory = databaseAccess.getListOfCategory(subject, source);
            databaseAccess.close();
            listOfCategory.add(0, "Select Category");
        }
    }

    private void setListOfNOQ() {
        listOfNOQ = new ArrayList<>();
        listOfNOQ.add("Select Number of Questions");
        listOfNOQ.add("5");
        listOfNOQ.add("10");
        listOfNOQ.add("15");
        listOfNOQ.add("20");
        listOfNOQ.add("25");
        listOfNOQ.add("All Questions");
    }

    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateCustomQuizActivity.this);
        builder.setTitle("Cancel Create Quiz");
        builder.setMessage("Are you sure?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goCustomQuizListAct();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void goCustomQuizListAct(){
        Intent intent = new Intent(CreateCustomQuizActivity.this, CustomQuizListActivity.class);
        finish();
        startActivity(intent);
    }
}
