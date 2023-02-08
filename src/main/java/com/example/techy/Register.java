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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText email, password, confirmPassword;
    Button register;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    TextView goToLogin;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myref = database.getReference("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();

        email = findViewById(R.id.emailTextBox);
        password = findViewById(R.id.passowrdTextBox);
        confirmPassword = findViewById(R.id.confirmPswTextBox);
        register = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.loginTxt);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); //per rimuovere l'activity register dallo stack delle activity. premendo il tasto "back", non tornerò a questa activity.
                startActivity(intent);
            }
        });

    }

    private void register(){

        String emailText = email.getText().toString();
        String passwordText = password.getText().toString();
        String confirmPswText = confirmPassword.getText().toString();



        if(passwordText.isEmpty() || passwordText.length()<6){
            password.setError("La tua password deve contenere almeno 6 caratteri");
            return;
        }


        if(!passwordText.equals(confirmPswText)){
            confirmPassword.setError("Le passwords non sono uguali");
            return;
        }

        //registrazione
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Sign up");
        progress.setMessage("Attendi...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        mAuth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    User user = new User(false, emailText);

                    myref.child(task.getResult().getUser().getUid()).setValue(user);

                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),"Il tuo account è stato creato con successo",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(Register.this, Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); //per rimuovere l'activity register dallo stack delle activity. premendo il tasto "back", non tornerò a questa activity.
                    startActivity(intent);
                }else{
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(),"Errore",Toast.LENGTH_SHORT).show();

                }

            }
        });



    }



}