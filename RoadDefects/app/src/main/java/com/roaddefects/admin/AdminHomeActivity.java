package com.roaddefects.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.lizamathur.roaddefects.MainActivity;
import com.lizamathur.roaddefects.R;
import com.roaddefects.users.User;

import java.util.List;

import dao.roaddefects.base.Session;
import dao.roaddefects.users.UserDAO;

/**
 * The activity that is loaded soon after an admin is logged in
 */
public class AdminHomeActivity extends AppCompatActivity {

    Button logout;
    CardView approve_view, approved_view, list_a_defect_view, list_c_defect_view, list_a_locations_view;

    Session session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        session = new Session(this);

        logout = (Button) findViewById(R.id.logout);
        approve_view = (CardView) findViewById(R.id.approve_view);
        approved_view = (CardView) findViewById(R.id.approved_view);
        list_a_defect_view = (CardView) findViewById(R.id.list_a_defect_view);
        list_c_defect_view = (CardView) findViewById(R.id.list_c_defect_view);
        list_a_locations_view = (CardView) findViewById(R.id.list_a_locations_view);

        logout.setOnClickListener(view -> {
            Toast.makeText(this, session.getName() + " successfully logged out!", Toast.LENGTH_SHORT).show();
            session.setName("");
            session.setId("");
            Intent logout = new Intent(AdminHomeActivity.this, MainActivity.class);
            logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logout);
        });

        approve_view.setOnClickListener(view -> {
            Intent intent = new Intent(AdminHomeActivity.this, ApproveNewUser.class);
            startActivity(intent);
        });

        approved_view.setOnClickListener(view -> {
            Intent intent = new Intent(AdminHomeActivity.this, ApprovedUsers.class);
            startActivity(intent);
        });

        list_a_defect_view.setOnClickListener(view -> {
            Intent intent = new Intent(AdminHomeActivity.this, ActiveDefects.class);
            startActivity(intent);
        });

        list_c_defect_view.setOnClickListener(view -> {
            Intent intent = new Intent(AdminHomeActivity.this, ActiveDefects.class);
            intent.putExtra("active", 0);
            startActivity(intent);
        });

        list_a_locations_view.setOnClickListener(view -> {
            Intent intent = new Intent(AdminHomeActivity.this, ActiveLocationActivity.class);
            startActivity(intent);
        });

    }
}