package com.lizamathur.roaddefects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.roaddefects.users.User;

import dao.roaddefects.base.Session;

/**
 * The activity that is loaded soon after a user is logged in
 */
public class HomePage extends AppCompatActivity {

    BottomNavigationView navView;
    FloatingActionButton fab;
    Button logout;
    Session session;
    TextView show_permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        session = new Session(this);

        navView = (BottomNavigationView) findViewById(R.id.navView);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        logout = (Button) findViewById(R.id.logout);
        show_permission = (TextView) findViewById(R.id.show_permission);

        CheckStatus checkStatus = new CheckStatus();
        checkStatus.execute();

        logout.setOnClickListener(v -> {
            Toast.makeText(this, session.getName() + " successfully logged out!", Toast.LENGTH_SHORT).show();
            session.setName("");
            session.setId("");
            Intent logout = new Intent(HomePage.this, MainActivity.class);
            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
        });
    }

    private class CheckStatus extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            User obj = new User();
            String status = obj.fetchStatus(Long.parseLong(session.getId()));
            return status;
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
            if (status.equals("new")){
                fab.setVisibility(View.GONE);
                show_permission.setVisibility(View.VISIBLE);
            }else {
                show_permission.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new DefectFragment()).commit();

                navView.setOnNavigationItemSelectedListener(item -> {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.home:
                            selectedFragment = new DefectFragment();
                            break;
                        case R.id.map:
                            selectedFragment = new MapFragment();
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, selectedFragment).addToBackStack(null).commit();
                    return true;
                });

                fab.setOnClickListener(v -> {
                    Intent add = new Intent(HomePage.this, AddActivity.class);
                    startActivity(add);
                });


            }
        }
    }
}