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

import com.lizamathur.roaddefects.R;
import com.lizamathur.roaddefects.UpdateDefectActivity;
import com.roaddefects.defect.Defect;
import com.roaddefects.defect.DefectsAdapter;

import java.util.List;

import dao.roaddefects.defects.DefectsDAO;

/**
 * Shows all active defects in a list
 * This page is only for admins
 */
public class ActiveDefects extends AppCompatActivity {

    ListView listView;
    ProgressBar progressBar;
    TextView noData;
    int active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_defects);

        active = getIntent().getIntExtra("active", 1);

        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        noData = (TextView) findViewById(R.id.noData);

        FetchAllActiveDefects task = new FetchAllActiveDefects();
        task.execute();

    }

    private class FetchAllActiveDefects extends AsyncTask<String, List<Defect>, List<Defect>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            noData.setText("");
        }

        @Override
        protected List<Defect> doInBackground(String... strings) {
            DefectsDAO dao = new DefectsDAO();
            List<Defect> defects = dao.getAllActiveDefects(active);
            return defects;
        }

        @Override
        protected void onPostExecute(List<Defect> defects) {
            progressBar.setVisibility(View.INVISIBLE);
            if (defects.size() == 0)
                noData.setText("Nothing to show here!");
            DefectsAdapter defectsAdapter = new DefectsAdapter(ActiveDefects.this, R.layout.single_defect, defects, "admin");
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


                    Intent intent = new Intent(ActiveDefects.this, UpdateDefectActivity.class);
                    intent.putExtra("bundle", bundle);
                    intent.putExtra("active", active);
                    startActivity(intent);
                }
            });

        }
    }
}