package com.example.techy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        email = findViewById(R.id.emailLogin);
        password = findViewById(R.id.passwordLogin);
        loginBtn = findViewById(R.id.loginBtn);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        signup = findViewById(R.id.loginTxt);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); //per rimuovere l'activity register dallo stack delle activity. premendo il tasto "back", non tornerò a questa activity.
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

    }

    private void login(){

        String emailText = email.getText().toString();
        String passwordtext = password.getText().toString();

        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Log in");
        progress.setMessage("Attendi...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        mAuth.signInWithEmailAndPassword(emailText, passwordtext).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progress.dismiss();
                    Intent intent = new Intent(Login.this, Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); //per rimuovere l'activity register dallo stack delle activity. premendo il tasto "back", non tornerò a questa activity.
                    startActivity(intent);
                }else{
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),"Credenziali errate",Toast.LENGTH_LONG).show();

                }
            }
        });

    }

}