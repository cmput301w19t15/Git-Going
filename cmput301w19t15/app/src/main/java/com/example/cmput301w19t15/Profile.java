package com.example.cmput301w19t15;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * A login screen that offers login via email/password.
 */
public class Profile extends AppCompatActivity{
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private FirebaseAuth auth;
    // UI references.
    private EditText inputUsername, inputPassword, currentFocus;
    private View progressBar;
    private Button save;
    private boolean usernameError = false, passwordError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up the login form.
        setContentView(R.layout.activity_profile);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        //if user is already logged in goto main activity
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Profile.this, MainActivity.class));
            finish();
        }
        //else let user login
        inputUsername = findViewById(R.id.username);
        inputPassword = findViewById(R.id.password);

        save = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.login_progress);

        save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = inputUsername.getText().toString().trim().toLowerCase();
                final String password = inputPassword.getText().toString().trim();

                if (username.isEmpty()) {
                    usernameError = setFocus(inputUsername, "Enter Email or Username");
                } else if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                    //if its email do nothing
                    usernameError = false;
                } else{
                    //get email corresponding to username if its not email
                    DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users");
                    userReference.orderByChild("username").equalTo(username).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            TextView hiddenEmail = findViewById(R.id.hiddenEmail);
                            if(dataSnapshot.exists()){
                                for(DataSnapshot userID: dataSnapshot.getChildren()){
                                    hiddenEmail.setText(userID.child("email").getValue().toString());
                                }
                            }else{
                                hiddenEmail.setText("text");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}

                    });
                    TextView hiddenEmail = findViewById(R.id.hiddenEmail);
                    if(username.equals(hiddenEmail.getText().toString())) {
                        usernameError = true;
                        usernameError = setFocus(inputUsername, "Email or Username does not Exist");
                    }else{
                        usernameError = false;
                    }
                    username = hiddenEmail.getText().toString();
                }

                if (password.isEmpty()) {
                    passwordError = setFocus(inputPassword, "Password is required");
                } else if (password.length() < 6) {
                    passwordError = setFocus(inputPassword, getString(R.string.minimum_password));
                }else{
                    passwordError = false;
                }
                if(!usernameError && !passwordError) {
                    progressBar.setVisibility(View.VISIBLE);
                    //authenticate user
                    auth.signInWithEmailAndPassword(username, password).addOnCompleteListener(Profile.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                startActivity(new Intent(Profile.this, MainActivity.class));
                                finish();
                            } else {
                                // there was an error
                                Toast.makeText(Profile.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                if(currentFocus != null)
                    currentFocus.requestFocus();
            }
        });

        btnRegister.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, RegisterActivity.class));
                finish();
            }
        });

        btnResetPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, RegisterActivity.class));
                finish();
            }
        });
    }
    private boolean setFocus(EditText editText, String message){
        editText.setError(message);
        currentFocus = editText;
        return true;
    }
}