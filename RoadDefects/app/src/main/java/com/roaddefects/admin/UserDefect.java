package com.roaddefects.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lizamathur.roaddefects.R;
import com.lizamathur.roaddefects.UpdateDefectActivity;
import com.roaddefects.defect.Defect;
import com.roaddefects.defect.DefectsAdapter;

import java.util.List;

import dao.roaddefects.defects.DefectsDAO;

/**
 * Lists all the defects of the user that the admin selected
 * This page is only for admins
 */
public class UserDefect extends AppCompatActivity {

    int user_id;
    String user_name, user_email;
    Long user_mobile;

    ListView listView;
    ProgressBar progressBar;
    TextView noData, u_name, u_mobile, u_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_defect);
        user_id = getIntent().getIntExtra("user_id", -1);
        user_name = getIntent().getStringExtra("user_name");
        user_email = getIntent().getStringExtra("user_email");
        user_mobile = getIntent().getLongExtra("user_mobile", 0);

        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noData = (TextView) findViewById(R.id.noData);

        u_name = (TextView) findViewById(R.id.u_name);
        u_mobile = (TextView) findViewById(R.id.u_mobile);
        u_email = (TextView) findViewById(R.id.u_email);

        u_name.setText(user_name);
        u_email.setText(user_email);
        u_mobile.setText(user_mobile + "");

        FetchUserDefect task = new FetchUserDefect();
        task.execute();


    }

    @Override
    protected void onResume() {
        super.onResume();
        FetchUserDefect task = new FetchUserDefect();
        task.execute();
    }

    private class FetchUserDefect extends AsyncTask<String, List<Defect>, List<Defect>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            noData.setText("");
        }

        @Override
        protected List<Defect> doInBackground(String... strings) {
            DefectsDAO dao = new DefectsDAO();
            List<Defect> defects = dao.getDefectRecords(user_id);
            return defects;
        }

        @Override
        protected void onPostExecute(List<Defect> defects) {
            progressBar.setVisibility(View.INVISIBLE);
            if (defects.size() == 0)
                noData.setText("Nothing to show here!");
            DefectsAdapter defectsAdapter = new DefectsAdapter(UserDefect.this, R.layout.single_defect, defects, "admin");
            listView.setAdapter(defectsAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Defect defect = (Defect) parent.getItemAtPosition(position);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("latitude", defect.getLatitude());
                    bundle.putDouble("longitude", defect.getLongitude());
                    bundle.putString("defect", defect.getDefect());
                    bundle.putString("status", defect.getStatus());
                    bundle.putString("sub_type", defect.getSubType());
                    bundle.putDouble("length", defect.getLength());
                    bundle.putDouble("width", defect.getWidth());
                    bundle.putDouble("depth", defect.getDepth());
                    bundle.putString("severity", defect.getSeverity());
                    bundle.putString("description", defect.getDescription());
                    bundle.putString("short_description", defect.getShortDescription());
                    bundle.putString("img_name", defect.getImgName());
                    bundle.putInt("user_id", defect.getUserId());

                    if (defect.getStatus().equals("active")) {
                        Intent intent = new Intent(UserDefect.this, UpdateDefectActivity.class);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(UserDefect.this, "The complaint has already been processed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}