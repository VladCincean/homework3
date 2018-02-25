package com.example.vlad.caloriecounter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.vlad.caloriecounter.activity.admin.FoodAdminActivity;
import com.example.vlad.caloriecounter.activity.user.FoodRecordListActivity;
import com.example.vlad.caloriecounter.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginMainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginMainActivity.class.getName();

    private EditText editTextEmail;
    private EditText editTextPassword;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbRefUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_login_main);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressbar);

        findViewById(R.id.buttonLogin).setOnClickListener(this);
        findViewById(R.id.buttonSignUp).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mDbRefUsers = FirebaseDatabase.getInstance().getReference("users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        // check if the user is already signed in and update the UI accordingly
        if (mAuth.getCurrentUser() != null) {
            postLogin();
        }
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");

        switch (view.getId()) {
            case R.id.buttonLogin:
                login();
                break;
            case R.id.buttonSignUp:
                signUp();
                break;
        }
    }

    private boolean validateCredentials(String email, String password) {
        if (email.isEmpty()) {
            editTextEmail.setError("Email is required");
            editTextEmail.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please input a valid email");
            editTextEmail.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Minimum password length is 6");
            editTextPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void saveUserToDb(String email, boolean isAdmin) {
        String id = mDbRefUsers.push().getKey();
        User user = new User(id, email, isAdmin);
        mDbRefUsers.child(user.getId()).setValue(user);
    }

    private void signUp() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (!validateCredentials(email, password)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Log.d(TAG, "createUserWithEmailAndPassword:success");
                            saveUserToDb(email, false);
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Successfully signed up. You can now login",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Log.w(TAG, "createUserWithEmailAndPassword:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "You are already registered",
                                        Toast.LENGTH_SHORT
                                ).show();
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        task.getException().getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                    }
                });
    }

    private void login() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!validateCredentials(email, password)) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmailAndPassword:success");
                            postLogin();
                        } else {
                            Log.w(TAG, "signInWithEmailAndPassword:failure", task.getException());
                            Toast.makeText(
                                    getApplicationContext(),
                                    task.getException().getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                });
    }

    // se executa dupa login; lanseaza o noua activitate
    private void postLogin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            finish();
            if ("admin@vlad.caloriecounter.com".equals(currentUser.getEmail())) {
                Intent intent = new Intent(LoginMainActivity.this, FoodAdminActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginMainActivity.this, FoodRecordListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }
}
