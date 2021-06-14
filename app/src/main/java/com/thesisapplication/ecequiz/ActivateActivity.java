package com.thesisapplication.ecequiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static com.thesisapplication.ecequiz.MainActivity.EXTRA_ACTIVATED;

public class ActivateActivity extends AppCompatActivity {
    private static final String PHONE_NUMBER = "+639358124598";

    private Toolbar toolbar;

    private EditText editTextCode;
    private Button buttonEnter, buttonRequestCode;
    private ImageView imageViewRequestCodeResult;
    private ProgressBar progressBarLoading;

    private boolean activated;

    //Firebase
    private String verificationID;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);

        firebaseAuth = FirebaseAuth.getInstance();

        initLayoutComponents();

        buttonRequestCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendVerificationCode(PHONE_NUMBER);
                progressBarLoading.setVisibility(View.VISIBLE);
            }
        });

        buttonEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = editTextCode.getText().toString().trim();

                if(code.isEmpty() || code.length() < 6){
                    editTextCode.setError("Invalid Code");
                    editTextCode.requestFocus();
                    return;
                }

                VerifyCode(code);
                progressBarLoading.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initLayoutComponents(){
        toolbar = findViewById(R.id.toolbar_activate);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Activate");

        editTextCode = (EditText)findViewById(R.id.edit_text_activate_code);
        buttonEnter = (Button)findViewById(R.id.btn_activate_enter_code);
        buttonRequestCode = (Button)findViewById(R.id.btn_activate_request_code);
        imageViewRequestCodeResult = (ImageView)findViewById(R.id.image_view_request_code_result);
        progressBarLoading = (ProgressBar)findViewById(R.id.progress_bar_loading);
    }

    private void SendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallback
        );
    }

    private void VerifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBarLoading.setVisibility(View.INVISIBLE);
                        if(task.isSuccessful()){
                            Toast.makeText(ActivateActivity.this, "Application succesfully activated", Toast.LENGTH_SHORT).show();
                            activated = true;
                            ActivationFinish();
                        }else {
                            Toast.makeText(ActivateActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationID = s;
            progressBarLoading.setVisibility(View.INVISIBLE);
            imageViewRequestCodeResult.setBackgroundResource(R.drawable.correct);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(ActivateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            imageViewRequestCodeResult.setBackgroundResource(R.drawable.incorrect);
        }
    };

    private void ActivationFinish(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ACTIVATED, activated);
        setResult(RESULT_OK, intent);
        finish();
    }
}
