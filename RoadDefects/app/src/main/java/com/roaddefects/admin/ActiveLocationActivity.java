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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.roaddefects.defects.DefectsDAO;

/**
 * Places a marker at all positions where a defect is yet to be corrected
 */
public class ActiveLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_location);

        FetchAllActiveLoc task_fetch = new FetchAllActiveLoc();
        task_fetch.execute();
    }

    private class FetchAllActiveLoc extends AsyncTask<String, List<PositionAndDefect>, List<PositionAndDefect>> {
        @Override
        protected List<PositionAndDefect> doInBackground(String... strings) {
            List<PositionAndDefect> list = new ArrayList<>();
            DefectsDAO dao = new DefectsDAO();
            list = dao.getAllActiveLocations();
            return list;
        }

        @Override
        protected void onPostExecute(List<PositionAndDefect> s) {
            if(s.size() > 0){
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng location = null;
                        Map<String, Integer> map = new HashMap<>();
                        int color = 1;
                        for (PositionAndDefect loc : s) {
                            location = new LatLng(loc.getLatitude(), loc.getLongitude());
                            if (!map.containsKey(loc.getDefect())) {
                                map.put(loc.getDefect(), color);
                                color *= 40;
                            }
                            googleMap.addMarker(new MarkerOptions().position(location).title("Details").snippet(loc.defectDetailsForMap())).setIcon(BitmapDescriptorFactory.defaultMarker(map.get(loc.getDefect())));
                            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                @Override
                                public View getInfoWindow(Marker marker) {
                                    return null;
                                }

                                @Override
                                public View getInfoContents(Marker marker) {

                                    LinearLayout info = new LinearLayout(ActiveLocationActivity.this);
                                    info.setOrientation(LinearLayout.VERTICAL);

                                    TextView title = new TextView(ActiveLocationActivity.this);
                                    title.setTextColor(Color.GRAY);
                                    title.setGravity(Gravity.CENTER);
                                    title.setTypeface(null, Typeface.BOLD);
                                    title.setText(marker.getTitle());

                                    TextView snippet = new TextView(ActiveLocationActivity.this);
                                    snippet.setTextColor(Color.BLACK);
                                    snippet.setText(marker.getSnippet());

                                    info.addView(title);
                                    info.addView(snippet);

                                    return info;
                                }
                            });
                        }
                        if (location != null)
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16));
                    }
                });
            }
        }
    }
}