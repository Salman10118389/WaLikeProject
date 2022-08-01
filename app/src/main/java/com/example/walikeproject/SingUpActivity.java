package com.example.walikeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.walikeproject.Models.Users;
import com.example.walikeproject.databinding.ActivitySingUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SingUpActivity extends AppCompatActivity {
    ActivitySingUpBinding binding;

    private FirebaseAuth firebaseAuthSignUp;
    private FirebaseDatabase database;
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySingUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuthSignUp = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        binding.SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.userNameSignUp.getText().toString().isEmpty()
                        && !binding.passwordSignUp.getText().toString().isEmpty()
                        && !binding.emailSignUp.getText().toString().isEmpty()) {
                    firebaseAuthSignUp.createUserWithEmailAndPassword(
                                    binding.emailSignUp.getText().toString(),
                                    binding.passwordSignUp.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        //getDataFromXml
                                        Users user = new Users(binding.userNameSignUp.getText().toString(),
                                                binding.emailSignUp.getText().toString(),
                                                binding.passwordSignUp.getText().toString());

                                        String id = task.getResult().getUser().getUid();
                                        database.getReference().child("Users").child(id).setValue(user);
                                        Toast.makeText(SingUpActivity.this, "Sign Up Successfull", Toast.LENGTH_SHORT).show();
                                        Intent toSignInAct = new Intent(SingUpActivity.this, SignInActivity.class);
                                        startActivity(toSignInAct);
                                    } else {
                                        Toast.makeText(SingUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SingUpActivity.this, "Enter your Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.SignInTextSignUpPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSignInAct = new Intent(SingUpActivity.this, SignInActivity.class);
                startActivity(goToSignInAct);
            }
        });

    }
}