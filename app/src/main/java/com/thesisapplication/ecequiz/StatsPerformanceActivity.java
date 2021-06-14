package com.thesisapplication.ecequiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class StatsPerformanceActivity extends AppCompatActivity {
    private TextView textViewPercentage;
    private ProgressBar progressBar;
    private Toolbar toolbarPerformance;

    private Spinner spinnerSubject;
    private SpinnerAdapter spinnerAdapterSubject;
    private Spinner spinnerSource;
    private SpinnerAdapter spinnerAdapterSource;
    private Spinner spinnerCategory;
    private SpinnerAdapter spinnerAdapterCategory;

    private List<String> listOfSubject;
    private List<String> listOfSource;
    private List<String> listOfCategory;

    private String subject;
    private String source;
    private String category;

    private int statsCode;
    private int stat;

    private int subjectPosition;
    private int sourcePosition;
    private int categoryPosition;

    private boolean showStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_performace);

        getSubjectList();
        getSourceList();
        getCategoryList();
        initLayoutComponents();
    }

    private void initLayoutComponents(){
        toolbarPerformance = findViewById(R.id.toolbar_stats_performance);
        setSupportActionBar(toolbarPerformance);
        getSupportActionBar().setTitle("Performance");
        textViewPercentage =(TextView)findViewById(R.id.text_view_stats_performance_percentage);
        progressBar = findViewById(R.id.progress_bar_stats_performance);
        spinnerSubject = (Spinner)findViewById(R.id.stats_performance_spinner_subject);
        spinnerSource = (Spinner)findViewById(R.id.stats_performance_spinner_source);
        spinnerCategory = (Spinner)findViewById(R.id.stats_performance_spinner_category);
        getStats();
        setSubjectSpinner();
        showStats = false;
    }

    private void setSubjectSpinner(){
        spinnerAdapterSubject = new ArrayAdapter<String>(this, R.layout.text_view_spinner_stats, listOfSubject);
        ((ArrayAdapter<String>) spinnerAdapterSubject).setDropDownViewResource(R.layout.text_view_spinner_stats);
        spinnerSubject.setAdapter(spinnerAdapterSubject);
        spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectPosition = position;
                if(position == 0){
                    statsCode = 0;
                    subject = null;
                    getStats();
                    getSourceList();
                    setSourceSpinner();
                    getCategoryList();
                    setCategorySpinner();
                }else {
                    statsCode = 1;
                    subject = listOfSubject.get(position);
                    getStats();
                    getSourceList();
                    setSourceSpinner();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setSourceSpinner(){
        spinnerAdapterSource = new ArrayAdapter<String>(this, R.layout.text_view_spinner_stats, listOfSource);
        ((ArrayAdapter<String>) spinnerAdapterSource).setDropDownViewResource(R.layout.text_view_spinner_stats);
        spinnerSource.setAdapter(spinnerAdapterSource);

        spinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sourcePosition = position;
                if(position == 0){
                    if(statsCode != 0 && subjectPosition != 0) {
                        source = null;
                        statsCode = 1;
                        getStats();
                        getCategoryList();
                        setCategorySpinner();
                    }
                }
                if(position !=0){
                    source = listOfSource.get(position);
                    statsCode = 2;
                    getStats();
                    getCategoryList();
                    setCategorySpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCategorySpinner(){
        spinnerAdapterCategory = new ArrayAdapter<String>(this, R.layout.text_view_spinner_stats, listOfCategory);
        ((ArrayAdapter<String>) spinnerAdapterCategory).setDropDownViewResource(R.layout.text_view_spinner_stats);
        spinnerCategory.setAdapter(spinnerAdapterCategory);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    if(statsCode != 0 && sourcePosition != 0){
                        category = null;
                        statsCode = 2;
                        getStats();
                    }
                }
                if(position != 0){
                    category = listOfCategory.get(position);
                    statsCode = 3;
                    getStats();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getSubjectList(){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        listOfSubject = databaseAccess.getListOfSubject();
        databaseAccess.close();
        listOfSubject.add(0, "Select Subject");
    }

    private void getSourceList(){
        if(subjectPosition == 0){
            listOfSource = new ArrayList<>();
            listOfSource.add("Select Source");
        }else {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            listOfSource = databaseAccess.getListOfSource(subject);
            databaseAccess.close();
            listOfSource.add(0, "Select Source");
        }
    }

    private void getCategoryList(){
        if(sourcePosition == 0){
            listOfCategory = new ArrayList<>();
            listOfCategory.add("Select Category");
        }else {
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
            databaseAccess.open();
            listOfCategory = databaseAccess.getListOfCategory(subject, source);
            databaseAccess.close();
            listOfCategory.add(0, "Select Category");
        }
    }

    private void getStats(){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        switch (statsCode){
            case 0:
                stat = 0;
                break;
            case 1:
                stat = databaseAccess.getSubjectStat(subject);
                break;
            case 2:
                stat = databaseAccess.getSourceStat(subject, source);
                break;
            case 3:
                stat = databaseAccess.getCategoryStat(subject, source, category);
                break;
        }
        databaseAccess.close();
        textViewPercentage.setText(stat+"%");
        progressBar.setProgress(stat);
        if(showStats){
            Toast.makeText(getApplicationContext(), "Stats selection update successful", Toast.LENGTH_SHORT).show();
        }
        showStats = true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StatsPerformanceActivity.this, StatsActivity.class);
        finish();
        startActivity(intent);
    }

}
