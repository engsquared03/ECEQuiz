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

public class SelectSourcesActivity extends AppCompatActivity {
    private String source;
    private int quizID;

    private Toolbar toolbarSources;

    private RecyclerView recyclerView;
    private SourceAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<QuestionsInfo> sourceInfoList;
    private QuestionsInfo questionsInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sources);

        initLayoutComponents();

        final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        quizID = databaseAccess.getCurrentQuizID();
        sourceInfoList = databaseAccess.getSourceList();
        databaseAccess.close();
        adapter = new SourceAdapter(sourceInfoList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new SourceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                questionsInfo = sourceInfoList.get(position);
                source = questionsInfo.getSource();

                databaseAccess.open();
                databaseAccess.setSource(quizID, source);
                databaseAccess.close();

                goSelectCategoryActivity();
            }
        });
    }

    private void initLayoutComponents(){
        toolbarSources = findViewById(R.id.toolbar_sources);
        setSupportActionBar(toolbarSources);
        getSupportActionBar().setTitle("Sources");

        recyclerView = findViewById(R.id.recycler_view_sources);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void goSelectCategoryActivity(){
        Intent intentSelectCategory = new Intent(SelectSourcesActivity.this, SelectCategoryActivity.class);
        finish();
        startActivity(intentSelectCategory);
    }

    @Override
    public void onBackPressed() {
        goSelectSubject();
    }

    private void goSelectSubject(){
        Intent intent = new Intent(SelectSourcesActivity.this, SelectSubjectActivity.class);
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
