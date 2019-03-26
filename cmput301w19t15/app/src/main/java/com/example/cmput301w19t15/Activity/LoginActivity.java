/*
 * Class Name: LoginActivity
 *
 * Version: 1.0
 *
 * Copyright 2019 TEAM GITGOING
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.example.cmput301w19t15.Activity;
//:)
/**
 * Represents an important Tweet
 * @author Thomas, Anjesh, Josh
 * @version 1.0
 * @since 1.0
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.cmput301w19t15.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity{
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private FirebaseAuth auth;
    // UI references.
    private EditText inputEmail, inputPassword, currentFocus;
    private View progressBar;
    private Button btnLogin,btnRegister,btnResetPassword;
    private boolean emailError = false, passwordError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the login form.
        setContentView(R.layout.activity_login);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        //if user is already logged in goto main activity
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        //else let user login
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);

        btnLogin = findViewById(R.id.login_button);
        btnRegister = findViewById(R.id.register_button);
        btnResetPassword = findViewById(R.id.reset_button);
        progressBar = findViewById(R.id.login_progress);

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });

        btnResetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            }
        });
    }

    /**
     * Log the user into the app
     */
    private void loginUser(){
        String email = inputEmail.getText().toString().trim().toLowerCase();
        final String password = inputPassword.getText().toString().trim();

        if(!checkEmail(email) && !checkPassword(password)) {
            progressBar.setVisibility(View.VISIBLE);
            //authenticate user
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // there was an error
                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if(currentFocus != null) {
            currentFocus.requestFocus();
        }
    }

    /**
     * check if the email is the correct format
     * @param email a string which is user's email address
     * @return a boolean, false if email is empty/invalid email address
     * true if email address is valid
     */
    private boolean checkEmail(String email){
        if (email.isEmpty()) {
            emailError = setFocus(inputEmail, "Enter Email");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //if its email does not match email address format
            emailError = setFocus(inputEmail, "Enter a valid Email");
        } else{
            emailError = false;
        }
        return emailError;
    }

    /**
     * check if the password is the correct format
     * @param password a string which is user acc's password
     * @return a boolean, true if the password is valid, return false
     * otherwise
     */
    private boolean checkPassword(String password){
        if (password.isEmpty()) {
            passwordError = setFocus(inputPassword, "Password is required");
        } else if (password.length() < 6) {
            passwordError = setFocus(inputPassword, getString(R.string.minimum_password));
        }else{
            passwordError = false;
        }
        return passwordError;
    }
    private boolean setFocus(EditText editText, String message){
        editText.setError(message);
        currentFocus = editText;
        return true;
    }
}