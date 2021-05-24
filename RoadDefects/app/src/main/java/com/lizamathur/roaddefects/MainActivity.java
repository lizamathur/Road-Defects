package com.lizamathur.roaddefects;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.roaddefects.admin.AdminHomeActivity;
import com.roaddefects.users.User;

import dao.roaddefects.base.Session;

/**
 * The login screen
 */
public class MainActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    ProgressBar progressBar;

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new Session(this);

        // check if some user/admin is already logged in
        if (!session.getId().equals("")){
            Intent autoLogin;
            if(!session.getRole().equals("admin"))
                autoLogin = new Intent(MainActivity.this, HomePage.class);
            else
                autoLogin = new Intent(MainActivity.this, AdminHomeActivity.class);
            startActivity(autoLogin);
            finish();
        }

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        login.setOnClickListener(v -> {
            SignInTask signInTask = new SignInTask();
            signInTask.execute();
        });
    }

    public void sign_up(View view) {
        Intent intent = new Intent(MainActivity.this, SignUp.class);
        startActivity(intent);
    }

    private class SignInTask extends AsyncTask<String, String, String> {

        String user_email = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        String result = "";
        boolean success = false;

        User response = null;

        @Override
        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            if(user_email.equals("") || pass.equals(""))
                result = "Please enter all the fields!";
            else {
                // check if user entered correct username and password
                User user = new User(user_email, pass);
                response = user.getUser();
                if(response != null) {
                    session.setId(String.valueOf(response.getId()));
                    session.setMobile(String.valueOf(response.getMobile()));
                    session.setName(response.getName());
                    session.setRole(response.getRole());
                    result = "Welcome, " + response.getName() + "!";
                    success = true;
                }else
                    result = "Incorrect email or password!";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String string){
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
            if (success){
                Intent intent;
                if (session.getRole().equals("normal"))
                    intent = new Intent(MainActivity.this, HomePage.class);
                else
                    intent = new Intent(MainActivity.this, AdminHomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}