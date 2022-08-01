package com.example.walikeproject;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.walikeproject.Models.Users;
import com.example.walikeproject.databinding.ActivitySignInBinding;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {
    ActivitySignInBinding SingInBinding;
    FirebaseAuth firebaseAuthSignInActivity;
    FirebaseDatabase firebaseDatabase;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SingInBinding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(SingInBinding.getRoot());

        firebaseAuthSignInActivity = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        SingInBinding.SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!SingInBinding.emailSignIn.getText().toString().isEmpty()
                        && !SingInBinding.passwordSignIn.getText().toString().isEmpty()) {
                    //get the data first
                    String email = SingInBinding.emailSignIn.getText().toString();
                    String pass = SingInBinding.passwordSignIn.getText().toString();
                    //pass the data to Firebase auth
                    firebaseAuthSignInActivity.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent goToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(goToMainActivity);
                            } else {
                                Toast.makeText(SignInActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(SignInActivity.this, "Please Fill your Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });


        SingInBinding.SignUpSignInPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToSignUpActivity = new Intent(SignInActivity.this, SingUpActivity.class);
                startActivity(goToSignUpActivity);
            }
        });
        SingInBinding.GoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        SingInBinding.GoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    int RC_SIGN_IN = 65;
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d("TAG", "firebase auth with google :" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());


            }catch (Exception e){
                Log.w(TAG, "Google Sign In Failed");
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential  = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuthSignInActivity.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("TAG", "Sign In With Credential Success");
                            FirebaseUser user = firebaseAuthSignInActivity.getCurrentUser();

                            Users userGmail = new Users();
                            userGmail.setUserId(user.getUid());
                            userGmail.setUsername(user.getDisplayName());
                            userGmail.setProfileImage(user.getPhotoUrl().toString());
                            firebaseDatabase.getReference().child("Users").child(user.getUid()).setValue(userGmail);
                            Intent goToMainActIntent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(goToMainActIntent);
                        }else{
                            Log.w("TAG", "Sign In With Credential Failure");
                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuthSignInActivity.getCurrentUser();
        if (firebaseAuthSignInActivity.getCurrentUser() != null) {
            Intent goToMainActivity = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(goToMainActivity);
        }
    }
}

