package com.lizamathur.roaddefects;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.roaddefects.defect.Defect;

import dao.roaddefects.base.Session;
import dao.roaddefects.defects.DefectsDAO;

/**
 * Update/Edit already added defects
 */
public class UpdateDefectActivity extends AppCompatActivity {

    TextInputEditText length, width, depth, description, short_description;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button btn_update, btn_change_status;
    ProgressBar progressBar;
    Session session;

    int active;

    Defect defect= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_defect);
        session = new Session(this);

        active = getIntent().getIntExtra("active", 1);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            defect = new Defect();
            defect.setDefect(bundle.getString("defect"));
            defect.setSubType(bundle.getString("sub_type"));
            defect.setImgName(bundle.getString("img_name"));
            defect.setDescription(bundle.getString("description"));
            defect.setShortDescription(bundle.getString("short_description"));
            defect.setSeverity(bundle.getString("severity"));
            defect.setLength(bundle.getDouble("length"));
            defect.setDepth(bundle.getDouble("depth"));
            defect.setWidth(bundle.getDouble("width"));
            defect.setLatitude(bundle.getDouble("latitude"));
            defect.setLongitude(bundle.getDouble("longitude"));
            defect.setUserId(bundle.getInt("user_id"));
            defect.setStatus(bundle.getString("status"));
//            Toast.makeText(this, defect.toString(), Toast.LENGTH_SHORT).show();
        }

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        btn_update = (Button) findViewById(R.id.btn_update);
        btn_change_status = (Button) findViewById(R.id.btn_change_status);


        if (session.getRole().equals("admin"))
            btn_change_status.setVisibility(View.VISIBLE);

        if (active == 0){
            btn_update.setVisibility(View.GONE);
            btn_change_status.setVisibility(View.GONE);
        }

        length = (TextInputEditText) findViewById(R.id.length);
        width = (TextInputEditText) findViewById(R.id.width);
        depth = (TextInputEditText) findViewById(R.id.depth);
        description = (TextInputEditText) findViewById(R.id.description);
        short_description = (TextInputEditText) findViewById(R.id.short_description);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        length.setText(defect.getLength() + "");
        width.setText(defect.getWidth() + "");
        depth.setText(defect.getDepth() + "");
        description.setText(defect.getDescription());
        short_description.setText(defect.getShortDescription());

        if (defect.getSeverity().equals("Low")){
            radioButton = (RadioButton) findViewById(R.id.low);
            radioButton.setChecked(true);
        } else if (defect.getSeverity().equals("Medium")){
            radioButton = (RadioButton) findViewById(R.id.medium);
            radioButton.setChecked(true);
        } else {
            radioButton = (RadioButton) findViewById(R.id.high);
            radioButton.setChecked(true);
        }


        btn_update.setOnClickListener(view -> {
            defect.setLength(Double.parseDouble(length.getText().toString()));
            defect.setWidth(Double.parseDouble(width.getText().toString()));
            defect.setDepth(Double.parseDouble(depth.getText().toString()));
            defect.setDescription(description.getText().toString());
            defect.setShortDescription(short_description.getText().toString());
            int id = radioGroup.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(id);
            defect.setSeverity(radioButton.getText().toString());
            UpdateDefect update = new UpdateDefect();
            update.execute();
        });

        btn_change_status.setOnClickListener(v -> {
            if (defect.getStatus().equals("active")) {
                UpdateStatus task = new UpdateStatus();
                task.execute();
            }
        });

    }

    private class UpdateDefect extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            DefectsDAO dao = new DefectsDAO();
            boolean success = dao.updateDefect(defect);
            if(success)
                return "Record Updated Successfully!";
            return "Something went wrong!";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(UpdateDefectActivity.this, s, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private class UpdateStatus extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            DefectsDAO dao = new DefectsDAO();
            boolean success = dao.updateStatus(defect.getImgName());
            if (success)
                return "Status Updated!";
            return "Something went Wrong!";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(UpdateDefectActivity.this, s, Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}