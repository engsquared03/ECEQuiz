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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SelectSubjectActivity extends AppCompatActivity {
    private String subject;
    private int quizID;

    private Toolbar toolbarSubject;

    private RecyclerView recyclerView;
    private SubjectAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<QuestionsInfo> subjectInfoList;
    private QuestionsInfo questionsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        initLayoutComponents();
        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        quizID = databaseAccess.getQuizID();
        subjectInfoList = databaseAccess.getSubjectList();
        databaseAccess.close();
        adapter = new SubjectAdapter(subjectInfoList);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                questionsInfo = subjectInfoList.get(position);
                subject = questionsInfo.getSubject();

                databaseAccess.open();
                databaseAccess.setSubject(quizID, subject);
                databaseAccess.close();

                goSelectSourcesActivity();
            }
        });
    }

    private void initLayoutComponents(){
        toolbarSubject = findViewById(R.id.toolbar_subject);
        setSupportActionBar(toolbarSubject);
        getSupportActionBar().setTitle("Subject");

        recyclerView = findViewById(R.id.recycler_view_subject);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void goSelectSourcesActivity(){
        Intent intentSelectSources = new Intent(SelectSubjectActivity.this, SelectSourcesActivity.class);
        finish();
        startActivity(intentSelectSources);
    }

    @Override
    public void onBackPressed() {
        goMainMenu();
    }

    private void goMainMenu(){
        Intent intent = new Intent(SelectSubjectActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
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
