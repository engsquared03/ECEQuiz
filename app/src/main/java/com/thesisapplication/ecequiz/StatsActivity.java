package com.thesisapplication.ecequiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StatsActivity extends AppCompatActivity {
    private Toolbar toolbarStats;

    private Button buttonPerformance, buttonQuestions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        initLayoutComponents();
        buttonPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatsActivity.this, StatsPerformanceActivity.class);
                finish();
                startActivity(intent);
            }
        });

        buttonQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StatsActivity.this, StatsQuestionActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void initLayoutComponents(){
        toolbarStats = findViewById(R.id.toolbar_stats);
        setSupportActionBar(toolbarStats);
        getSupportActionBar().setTitle("Stats");

        buttonPerformance = (Button)findViewById(R.id.stats_button_performance);
        buttonQuestions = (Button)findViewById(R.id.stats_button_questions);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(StatsActivity.this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
