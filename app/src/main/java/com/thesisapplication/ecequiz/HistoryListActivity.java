package com.thesisapplication.ecequiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class HistoryListActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private HistoryViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);

        toolbar = findViewById(R.id.toolbar_history_2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("History");

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        ArrayList<QuizInfo> quizInfoList = databaseAccess.getHistoryList();
        ArrayList<QuizInfo> customInfoList = databaseAccess.getHistoryListCustom();
        databaseAccess.close();

        tabLayout = (TabLayout) findViewById(R.id.history_tab_layout);
        viewPager = (ViewPager)findViewById(R.id.history_view_pager);
        adapter = new HistoryViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FragmentQuizHistory(quizInfoList), "Quiz");
        adapter.AddFragment(new FragmentCustomHistory(customInfoList), "Custom");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        goMainMenu();
    }

    private void goMainMenu(){
        Intent intent = new Intent(HistoryListActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
