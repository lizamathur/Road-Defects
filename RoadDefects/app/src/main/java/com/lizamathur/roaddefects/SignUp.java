package com.lizamathur.roaddefects;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.roaddefects.users.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import dao.roaddefects.base.ConnectionHelper;

/**
 * Sign up Page
 */
public class SignUp extends AppCompatActivity {

    EditText userName, mobile, email, password, confirmPassword;
    Button register;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = (EditText) findViewById(R.id.userName);
        mobile = (EditText) findViewById(R.id.mobile);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
        register = (Button) findViewById(R.id.register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        register.setOnClickListener(view -> {
            SignUpTask signUpTask = new SignUpTask();
            signUpTask.execute();
        });
    }

    public void sign_in(View view) {
        Intent intent = new Intent(SignUp.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private class SignUpTask extends AsyncTask<String, String, String> {

        String user_name = userName.getText().toString().trim();
        String user_mobile = mobile.getText().toString().trim();
        String user_email = email.getText().toString().trim();
        String user_pass = password.getText().toString().trim();
        String cPass = confirmPassword.getText().toString().trim();

        String result = "";

        @Override
        protected void onPreExecute() {
            Log.v("Async", "In pre Execute");
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.v("Async", "In Background");
            if (user_name.equals("") || user_mobile.equals("") || user_email.equals("") || user_pass.equals("") || cPass.equals(""))
                result = "Please enter all the fields";
            else if(user_mobile.length() != 10)
                result = "Mobile number field should be of 10 digits!";
            else if(user_pass.compareTo(cPass) != 0)
                result = "Passwords do not match!";
            else {
                User user = new User(user_name, user_pass, user_email, Long.parseLong(user_mobile));
                boolean success = user.addUser();
                if (success)
                    result = "Registration Successful!";
                else
                    result = "Something went wrong!";
            }
            return result;
        }

        protected void onPostExecute(String string){
            Log.v("Async", "In post Execute");
            if(result.compareTo("Registration Successful!") == 0)
                finish();
            Toast.makeText(SignUp.this, result, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}