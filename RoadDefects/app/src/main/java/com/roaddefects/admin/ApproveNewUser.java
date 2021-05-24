package com.roaddefects.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lizamathur.roaddefects.MapFragment;
import com.lizamathur.roaddefects.R;
import com.roaddefects.defect.Defect;
import com.roaddefects.defect.DefectsAdapter;
import com.roaddefects.users.User;
import com.roaddefects.users.UsersAdapter;

import java.util.ArrayList;
import java.util.List;

import dao.roaddefects.users.UserDAO;

/**
 * Displays a list of all the users who are yet to get permission
 * This page is only for admins
 */
public class ApproveNewUser extends AppCompatActivity {

    ListView listView;
    ProgressBar progressBar;
    TextView noData;
    Button update;

    List<Integer> selected = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_new_user);

        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noData = (TextView) findViewById(R.id.noData);
        update = (Button) findViewById(R.id.update);

        FetchNewUsers task_fetch = new FetchNewUsers();
        task_fetch.execute();

        update.setOnClickListener(view -> {
            if (selected.size() > 0) {
                String users = selected.toString();
                ApproveUsers task = new ApproveUsers();
                task.execute();
            } else
                Toast.makeText(this, "Select users to approve!", Toast.LENGTH_SHORT).show();
        });
    }

    private class FetchNewUsers extends AsyncTask<String, List<User>, List<User>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            noData.setText("");
        }

        @Override
        protected List<User> doInBackground(String... strings) {
            UserDAO dao = new UserDAO();
            List<User> newUsers = dao.fetchUsers("new");
            return newUsers;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            progressBar.setVisibility(View.INVISIBLE);
            selected.clear();
            if (users.size() == 0)
                noData.setText("No Approvals Needed!");
            UsersAdapter usersAdapter = new UsersAdapter(ApproveNewUser.this, R.layout.single_user, users, selected);
            listView.setAdapter(usersAdapter);
        }
    }

    private class ApproveUsers extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            UserDAO dao = new UserDAO();
            boolean success = dao.approveUsers(selected);
            if (success)
                return "User(s) approved!";
            return "Something went wrong!";
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(ApproveNewUser.this, s, Toast.LENGTH_SHORT).show();
            FetchNewUsers task_fetch = new FetchNewUsers();
            task_fetch.execute();
        }
    }
}