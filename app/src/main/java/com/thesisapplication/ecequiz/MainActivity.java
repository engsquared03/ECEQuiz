package com.thesisapplication.ecequiz;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String SHARED_PREFS_ACTIVATION = "sharedPrefsActivation";
    private static final String KEY_ACTIVATION = "activation";

    private static final int REQUEST_ACTIVATE = 1;
    public static final String EXTRA_ACTIVATED = "extra_activate";

    private boolean activated;

    private Button buttonPlay, buttonStats, buttonHistory, buttonSetting, buttonCustom;

    private ImageView imageViewActivation;
    private LinearLayout layoutActivation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setButtons();
        getActivation();

        if (activated) {
            imageViewActivation.setBackgroundResource(R.drawable.correct);
        } else {
            imageViewActivation.setBackgroundResource(R.drawable.incorrect);
        }
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();
        databaseAccess.deleteUnusedQuizInfo();
        databaseAccess.close();
    }

    private void setButtons() {
        buttonPlay = (Button) findViewById(R.id.btn_quiz);
        buttonCustom = (Button) findViewById(R.id.btn_custom);
        buttonStats = (Button) findViewById(R.id.btn_stats);
        buttonHistory = (Button) findViewById(R.id.btn_history);
        buttonSetting = (Button) findViewById(R.id.btn_setting);
        imageViewActivation = (ImageView) findViewById(R.id.image_view_activation);
        layoutActivation = (LinearLayout) findViewById(R.id.layout_activitaion);

        layoutActivation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (activated) {
                    Toast.makeText(getApplicationContext(), "Application is already ACTIVATED", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(MainActivity.this, ActivateActivity.class);
                    startActivityForResult(intent, REQUEST_ACTIVATE);
                }
            }
        });

        buttonPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activated){
                    Intent intentPlay = new Intent(MainActivity.this, SelectSubjectActivity.class);
                    startActivity(intentPlay);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Application is not ACTIVATED", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activated){
                    Intent intentPlay = new Intent(MainActivity.this, CustomQuizListActivity.class);
                    startActivity(intentPlay);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Application is not ACTIVATED", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activated){
                    Intent intentStats = new Intent(MainActivity.this, StatsActivity.class);
                    startActivity(intentStats);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Application is not ACTIVATED", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activated){
                    Intent intentHistory = new Intent(MainActivity.this, HistoryListActivity.class);
                    startActivity(intentHistory);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Application is not ACTIVATED", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activated){
                    Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intentSetting);
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "Application is not ACTIVATED", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Leave ECE Quiz");
        builder.setMessage("Are you sure?");
        builder.setNegativeButton("No", null);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getActivation() {
        /*SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_ACTIVATION, MODE_PRIVATE);
        activated = prefs.getBoolean(KEY_ACTIVATION, false);*/
        activated = true;
    }

    private void updateActivation() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_ACTIVATION, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_ACTIVATION, activated);
        editor.apply();

        imageViewActivation.setBackgroundResource(R.drawable.correct);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ACTIVATE) {
            if (resultCode == RESULT_OK) {
                boolean resultActivated = data.getExtras().getBoolean(EXTRA_ACTIVATED);
                if (resultActivated) {
                    activated = resultActivated;
                    updateActivation();
                }
            }
        }
    }
}
