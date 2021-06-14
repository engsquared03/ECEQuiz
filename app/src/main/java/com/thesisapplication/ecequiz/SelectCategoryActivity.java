package com.thesisapplication.ecequiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SelectCategoryActivity extends AppCompatActivity {
    private String category;
    private int quizID;
    private int numberOfQuestions;
    private int random;

    private String currentDate;

    private Toolbar toolbarCategory;
    private Spinner spinnerNumberOfQuestions;
    private SpinnerAdapter spinnerAdapterNOQ;

    private Spinner spinnerRandomChoices;
    private SpinnerAdapter spinnerAdapterRandomChoices;

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<QuestionsInfo> categoryInfoList;
    private QuestionsInfo questionsInfo;

    private ArrayList<Integer> listNumberOfQuestions;
    private List<String> randomChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        setListNumberOfQuestions();
        setRandomChoices();
        initLayoutComponents();
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        quizID = databaseAccess.getCurrentQuizID();
        categoryInfoList = databaseAccess.getCategoryList();
        databaseAccess.close();

        adapter = new CategoryAdapter(categoryInfoList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                questionsInfo = categoryInfoList.get(position);
                category = questionsInfo.getCategory();

                if (questionsInfo.getNumberOfQuestions() < numberOfQuestions) {
                    Toast.makeText(getApplicationContext(), "Please select a lower number of questions.", Toast.LENGTH_SHORT).show();
                } else {
                    databaseAccess.open();
                    databaseAccess.finalQuizSetup(quizID, category, numberOfQuestions, random, currentDate);
                    databaseAccess.close();
                    goQuizActivity();
                }
            }
        });
    }

    private void initLayoutComponents() {
        toolbarCategory = findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbarCategory);
        getSupportActionBar().setTitle("Category");

        recyclerView = findViewById(R.id.recycler_view_category);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        currentDate = new SimpleDateFormat("EEE, d MMM yyyy  HH:mm aaa", Locale.getDefault()).format(new Date());

        setSpinnerNumberOfQuestions();
        setSpinnerRandomChoices();
    }

    private void setSpinnerNumberOfQuestions() {
        spinnerNumberOfQuestions = (Spinner) findViewById(R.id.category_spinner_number_of_questions);
        spinnerAdapterNOQ = new ArrayAdapter<Integer>(this, R.layout.text_view_spinner, listNumberOfQuestions);
        ((ArrayAdapter<Integer>) spinnerAdapterNOQ).setDropDownViewResource(R.layout.text_view_spinner);
        spinnerNumberOfQuestions.setAdapter(spinnerAdapterNOQ);
        spinnerNumberOfQuestions.setSelection(0);
        numberOfQuestions = listNumberOfQuestions.get(0);
        spinnerNumberOfQuestions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberOfQuestions = listNumberOfQuestions.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setSpinnerRandomChoices() {
        spinnerRandomChoices = (Spinner) findViewById(R.id.category_spinner_random_choices);
        spinnerAdapterRandomChoices = new ArrayAdapter<String>(this, R.layout.text_view_spinner, randomChoices);
        ((ArrayAdapter<String>) spinnerAdapterNOQ).setDropDownViewResource(R.layout.text_view_spinner);
        spinnerRandomChoices.setAdapter(spinnerAdapterRandomChoices);
        spinnerRandomChoices.setSelection(1);
        spinnerRandomChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    random = 1;
                }
                if (position == 1) {
                    random = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void goQuizActivity() {
        Intent intentQuiz = new Intent(SelectCategoryActivity.this, QuizActivity.class);
        finish();
        startActivity(intentQuiz);
    }

    @Override
    public void onBackPressed() {
        goSelectSources();
    }

    private void goSelectSources(){
        Intent intent = new Intent(SelectCategoryActivity.this, SelectSourcesActivity.class);
        finish();
        startActivity(intent);
    }


    private void setListNumberOfQuestions() {
        listNumberOfQuestions = new ArrayList<>();
        listNumberOfQuestions.add(new Integer(10));
        listNumberOfQuestions.add(new Integer(20));
        listNumberOfQuestions.add(new Integer(30));
        listNumberOfQuestions.add(new Integer(40));
        listNumberOfQuestions.add(new Integer(50));
    }

    private void setRandomChoices() {
        randomChoices = new ArrayList<>();
        randomChoices.add("Yes");
        randomChoices.add("No");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Toast.makeText(getApplicationContext(), "Char lang, Wala pa ni! Haha", Toast.LENGTH_SHORT).show();
        return true;
    }
}
