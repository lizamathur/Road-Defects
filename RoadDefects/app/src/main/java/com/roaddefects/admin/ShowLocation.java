package com.roaddefects.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lizamathur.roaddefects.R;
import com.roaddefects.defect.PositionAndDefect;

import java.util.HashMap;
import java.util.Map;

/**
 * Displays marker at the location of a particular defect
 */
public class ShowLocation extends AppCompatActivity {

    double latitude, longitude;
    String details;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);

        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);
        details = getIntent().getStringExtra("details");

        FetchMap task = new FetchMap();
        task.execute();

    }

    private class FetchMap extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            return "OK";
        }

        @Override
        protected void onPostExecute(String s) {
            if (s.equals("OK")) {
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng location = null;

                        location = new LatLng(latitude, longitude);

                        googleMap.addMarker(new MarkerOptions().position(location).title("Details").snippet(details));
                        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                            @Override
                            public View getInfoWindow(Marker marker) {
                                return null;
                            }

                            @Override
                            public View getInfoContents(Marker marker) {

                                LinearLayout info = new LinearLayout(ShowLocation.this);
                                info.setOrientation(LinearLayout.VERTICAL);

                                TextView title = new TextView(ShowLocation.this);
                                title.setTextColor(Color.GRAY);
                                title.setGravity(Gravity.CENTER);
                                title.setTypeface(null, Typeface.BOLD);
                                title.setText(marker.getTitle());

                                TextView snippet = new TextView(ShowLocation.this);
                                snippet.setTextColor(Color.BLACK);
                                snippet.setText(marker.getSnippet());

                                info.addView(title);
                                info.addView(snippet);

                                return info;
                            }
                        });

                        if (location != null)
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                    }
                });
            }
        }
    }
}