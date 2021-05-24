package com.roaddefects.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.lizamathur.roaddefects.R;
import com.roaddefects.users.ActiveUsersAdapter;
import com.roaddefects.users.User;
import com.roaddefects.users.UsersAdapter;

import java.util.List;

import dao.roaddefects.users.UserDAO;

/**
 * Shows a list of all the users who are allowed to add defects
 * This page is only for admins
 */
public class ApprovedUsers extends AppCompatActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_users);

        listView = (ListView) findViewById(R.id.listView);

        FetchActiveUsers task = new FetchActiveUsers();
        task.execute();

    }

    private class FetchActiveUsers extends AsyncTask<String, List<User>, List<User>> {

        @Override
        protected List<User> doInBackground(String... strings) {
            UserDAO dao = new UserDAO();
            List<User> activeUsers = dao.fetchUsers("active");
            return activeUsers;
        }

        @Override
        protected void onPostExecute(List<User> users) {
            super.onPostExecute(users);
            ActiveUsersAdapter adapter = new ActiveUsersAdapter(ApprovedUsers.this, R.layout.single_user, users);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ApprovedUsers.this, UserDefect.class);
                    intent.putExtra("user_id", users.get(position).getId());
                    intent.putExtra("user_name", users.get(position).getName());
                    intent.putExtra("user_email", users.get(position).getEmail());
                    intent.putExtra("user_mobile", users.get(position).getMobile());
                    startActivity(intent);
                }
            });

        }
    }
}