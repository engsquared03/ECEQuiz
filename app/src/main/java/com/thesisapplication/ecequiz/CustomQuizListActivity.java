package com.thesisapplication.ecequiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomQuizListActivity extends AppCompatActivity {
    private Toolbar toolbarCustomQuizList;
    private Button buttonCreateCustomQuiz;

    private RecyclerView recyclerView;
    private CustomQuizListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<QuizInfo> quizInfoList;
    private ArrayList<QuizInfo> customQuizInfos;
    private QuizInfo quizInfo;
    private boolean previewShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initLayoutComponents();
    }

    private void setButtonCreateCustomQuiz(){
        buttonCreateCustomQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomQuizListActivity.this, CreateCustomQuizActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void initLayoutComponents(){
        setContentView(R.layout.activity_custom_quiz_list);
        previewShown = false;
        toolbarCustomQuizList = findViewById(R.id.toolbar_custom_quiz_list);
        setSupportActionBar(toolbarCustomQuizList);
        getSupportActionBar().setTitle("Custom Quiz");

        recyclerView = findViewById(R.id.recycler_view_custom_quiz_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        getQuizInfoList();
        adapter = new CustomQuizListAdapter(quizInfoList);
        recyclerView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int direction) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomQuizListActivity.this);
                builder.setTitle("Finish Quiz");
                builder.setMessage("Are you sure?");
                builder.setNegativeButton("No", null);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteQuiz((int)viewHolder.itemView.getTag(), viewHolder.getAdapterPosition());
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        }).attachToRecyclerView(recyclerView);

        buttonCreateCustomQuiz = (Button)findViewById(R.id.button_create_custom_quiz);
        setButtonCreateCustomQuiz();

        adapter.setOnItemClickListener(new CustomQuizListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                quizInfo = quizInfoList.get(position);
                initLayoutPreview();
            }
        });
    }

    private void deleteQuiz(int id, int position){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        databaseAccess.deleteCustomQuiz(id);
        databaseAccess.close();
        quizInfoList.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void initLayoutPreview(){
        setContentView(R.layout.custom_quiz_info_preview);
        Toolbar toolbar = findViewById(R.id.toolbar_custom_quiz_info_preview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(quizInfo.getQuizTitle());
//        TextView textViewNOQ = (TextView) findViewById(R.id.custom_quiz_info_preview_noq);
        TextView textViewSubject = (TextView) findViewById(R.id.custom_quiz_info_preview_subject);
        TextView textViewSources = (TextView) findViewById(R.id.custom_quiz_info_preview_sources);
        TextView textViewCategories = (TextView) findViewById(R.id.custom_quiz_info_preview_categories);
        Button buttonStart = (Button)findViewById(R.id.custom_quiz_info_preview_btn_start);

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        customQuizInfos = databaseAccess.getCustomQuizInfo(quizInfo.getQuizID());
        databaseAccess.close();
//        textViewNOQ.setText(""+quizInfo.getTotalNOQ());
        textViewSubject.setText(quizInfo.getSubject());
        String sources = null;
        String categories = null;
        List<String> listOfSources = new ArrayList<>();
        for(int i = 0; i < customQuizInfos.size(); i++){
            QuizInfo quizInfo = customQuizInfos.get(i);
            if(!listOfSources.contains(quizInfo.getSource())){
                listOfSources.add(quizInfo.getSource());
                if(i == 0){
                    sources = "("+(i+1)+") "+quizInfo.getSource();
                    categories = "("+(listOfSources.size())+") "+quizInfo.getCategory();
                }else {
                    sources = sources+"\n("+(i+1)+") "+quizInfo.getSource();
                    categories = categories+"\n("+(listOfSources.size())+") "+quizInfo.getCategory();
                }
            }else {
                categories = categories+"\n("+(listOfSources.size())+") "+quizInfo.getCategory();
            }
        }
        textViewSources.setText(sources);
        textViewCategories.setText(categories);
        previewShown = true;

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomQuizListActivity.this, CustomQuizActivity.class);
                intent.putExtra("customQuizID", quizInfo.getQuizID());
                intent.putExtra("quizTitle", quizInfo.getQuizTitle());
                finish();
                startActivity(intent);
            }
        });
    }

    private void getQuizInfoList(){
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        quizInfoList = databaseAccess.getCustomQuizList();
        databaseAccess.close();
    }

    @Override
    public void onBackPressed(){
        if(previewShown){
            initLayoutComponents();
        }else {
            Intent intent = new Intent(CustomQuizListActivity.this, MainActivity.class);
            finish();
            startActivity(intent);
        }
    }
}
