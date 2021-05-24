package com.lizamathur.roaddefects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.roaddefects.defect.Defect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.roaddefects.base.Globals;
import dao.roaddefects.base.Session;
import dao.roaddefects.defects.DefectsDAO;

/**
 * Add a defect using this activity
 * Available only for users and not for admins
 */
public class AddActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteDefects, autoCompleteType;
    TextInputEditText length, width, depth, description, short_description;
    TextInputLayout subType_dropdown, width_text;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Button btn_insert;
    ImageView btn_picture;

    String selectedDefect = "", selectedType = "";
    String selectedL = "", selectedW = "", selectedD = "";
    String selectedSeverity = "";

    LocationManager locationManager;
    Location location;

    double latitude, longitude;
    static final int REQUEST_IMAGE_CAPTURE = 2;

    String currentPhotoPath, imageFileName = "";

    Uri photoURI = null;

    byte[] bitmapdata = new byte[20000];
    boolean imgSelected = false;

    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        session = new Session(this);

        autoCompleteDefects = (AutoCompleteTextView) findViewById(R.id.defect);
        autoCompleteType = (AutoCompleteTextView) findViewById(R.id.subType);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        subType_dropdown = (TextInputLayout) findViewById(R.id.subType_dropdown);
        width_text = (TextInputLayout) findViewById(R.id.width_text);
        btn_picture = (ImageView) findViewById(R.id.btn_picture);
        btn_insert = (Button) findViewById(R.id.btn_insert);

        length = (TextInputEditText) findViewById(R.id.length);
        width = (TextInputEditText) findViewById(R.id.width);
        depth = (TextInputEditText) findViewById(R.id.depth);
        description = (TextInputEditText) findViewById(R.id.description);
        short_description = (TextInputEditText) findViewById(R.id.short_description);

        //check if location permission is given by the user
        if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        btn_picture.setOnClickListener(view -> {

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(this,
                            "com.lizamathur.roaddefects.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        btn_insert.setOnClickListener(view -> {

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (!provider.contains("gps")) {
                final Intent intentGps = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intentGps);
            }
            if (ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                //fetch user's location
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    Log.v("GPS Location", "Lat: " + latitude + "; Long: " + longitude);
                }
            }

            if(latitude != 0 && longitude != 0) {

                selectedL = length.getText().toString().trim();
                selectedW = width.getText().toString().trim();
                selectedD = depth.getText().toString().trim();

                if (subType_dropdown.getVisibility() == View.GONE)
                    selectedType = "";
                if (width_text.getVisibility() == View.GONE)
                    selectedD = "0";

                int selectedRadio = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(selectedRadio);
                selectedSeverity = radioButton.getText().toString();

                if (!imgSelected)
                    Toast.makeText(this, "Select Image!", Toast.LENGTH_SHORT).show();
                else if (selectedL.equals("") || selectedW.equals(""))
                    Toast.makeText(this, "Please enter all the fields!", Toast.LENGTH_SHORT).show();
                else {
                    InsertRecord insertRecord = new InsertRecord();
                    insertRecord.execute();
                }
            }else
                Toast.makeText(this, "Location can't be fetched!", Toast.LENGTH_SHORT).show();
        });

        FetchDefects fetchDefects = new FetchDefects();
        fetchDefects.execute();
    }

    private File createImageFile() throws IOException {
        //create name for the image
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "img_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            ByteArrayOutputStream blob = new ByteArrayOutputStream();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(currentPhotoPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            byte[] buf = new byte[20000];
            int n;
            try {
                while (-1 != (n = fis.read(buf)))
                    blob.write(buf, 0, n);
            } catch (IOException e) {
                e.printStackTrace();
            }

            imgSelected = true;

            btn_picture.setAlpha(1f);
            btn_picture.setImageURI(null);
            btn_picture.setImageURI(photoURI);
            btn_picture.setBackgroundColor(Color.TRANSPARENT);

            bitmapdata = blob.toByteArray();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                boolean showRationale = shouldShowRequestPermissionRationale(permissions[0]);
                if (!showRationale && (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, "The app requires Location permission!", Toast.LENGTH_SHORT).show();
                }else if (ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(AddActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
//                    ActivityCompat.requestPermissions(AddActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                    break;
        }
    }

    /**
     * Fetches all defect names to displays in a drop-down
     */
    private class FetchDefects extends AsyncTask<String, List<String>, List<String>>{

        @Override
        protected List<String> doInBackground(String... strings) {

            DefectsDAO dao = new DefectsDAO();
            List<String> defects = dao.getDefects();
            return defects;
        }

        @Override
        protected void onPostExecute(List<String> defects) {
            ArrayAdapter arrayAdapter = new ArrayAdapter(AddActivity.this, R.layout.option_item, defects);
            autoCompleteDefects.setText(arrayAdapter.getItem(0).toString(), false);
            selectedDefect = arrayAdapter.getItem(0).toString();
            autoCompleteDefects.setAdapter(arrayAdapter);

            autoCompleteDefects.setOnItemClickListener((parent, view, position, id) -> {
                if (position != 0) {
                    String item = parent.getItemAtPosition(position).toString();
                    selectedDefect = item;

                    FetchSubtype fetch = new FetchSubtype();
                    fetch.execute();
                }else {
                    subType_dropdown.setVisibility(View.GONE);
                    selectedType = "";
                }
            });
        }
    }

    /**
     * Fetches all sub-types of a particular defect and displays in a drop-down
     */
    private class FetchSubtype extends AsyncTask<String, List<String>, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            subType_dropdown.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<String> doInBackground(String... strings) {
            DefectsDAO dao = new DefectsDAO();
            List<String> subType = dao.getSubType(selectedDefect);
            return subType;
        }

        @Override
        protected void onPostExecute(List<String> subTypes) {
            if(subTypes.size() > 0) {
                ArrayAdapter arrayAdapter2 = new ArrayAdapter(AddActivity.this, R.layout.option_item, subTypes);
                autoCompleteType.setText(arrayAdapter2.getItem(0).toString(), false);
                selectedType = subTypes.get(0);
                autoCompleteType.setAdapter(arrayAdapter2);
                autoCompleteType.setOnItemClickListener((parent, view, position, id) -> {
                    selectedType = parent.getItemAtPosition(position).toString();
                });
            }else{
                selectedType = "";
                subType_dropdown.setVisibility(View.GONE);
            }

        }
    }

    /**
     * Fetches all inputs of the users and creates a background task to upload them to the database
     */
    private class InsertRecord extends AsyncTask<String, String, String>{

        String result = "";
        @Override
        protected String doInBackground(String... strings) {

            Defect defect = new Defect();
            defect.setSubType(selectedType);
            defect.setDefect(selectedDefect);
            defect.setLength(Double.parseDouble(selectedL));
            defect.setWidth(Double.parseDouble(selectedW));
            defect.setDepth(Double.parseDouble(selectedD));
            defect.setLatitude(latitude);
            defect.setLongitude(longitude);
            defect.setSeverity(selectedSeverity);
            defect.setDescription(description.getText().toString().trim());
            defect.setShortDescription(short_description.getText().toString().trim());
            defect.setUserId(Integer.parseInt(session.getId()));
            defect.setImgName(imageFileName+".jpg");



            DefectsDAO defectsDAO = new DefectsDAO();
            boolean success = defectsDAO.addDefectRecord(defect);

            if (success) {
                uploadImage();
                result = "Defect Successfully recorded!";
            }
            else
                result = "Something went wrong!";
            return result;
        }

        private void uploadImage() {
            Globals globals = new Globals();
            String uploadUrl = globals.getBaseUrl() + "imageupload.php";
            RequestQueue requestQueue;
            requestQueue = Volley.newRequestQueue(AddActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, uploadUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            })

            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", imageFileName+".jpg");
                    params.put("image", Base64.encodeToString(bitmapdata, Base64.DEFAULT));
                    return params;
                }
            };

            requestQueue.add(stringRequest);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(AddActivity.this, result, Toast.LENGTH_SHORT).show();
            Intent restart = new Intent(AddActivity.this, AddActivity.class);
            startActivity(restart);
            finish();
        }
    }

}