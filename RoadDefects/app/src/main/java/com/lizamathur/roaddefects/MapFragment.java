package com.lizamathur.roaddefects;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roaddefects.defect.PositionAndDefect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.roaddefects.base.Session;
import dao.roaddefects.defects.DefectsDAO;

/**
 * Displays markers on the map where a defect has been detected
 */
public class MapFragment extends Fragment {

    double latitude, longitude;
    String defect, details;
    Session session;
    long current_user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);
        session = new Session(getContext());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            details = bundle.getString("details");

            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    LatLng location = new LatLng(latitude, longitude);
                    googleMap.addMarker(new MarkerOptions().position(location).snippet(details).title("Details"));

                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {

                            LinearLayout info = new LinearLayout(getContext());
                            info.setOrientation(LinearLayout.VERTICAL);

                            TextView title = new TextView(getContext());
                            title.setTextColor(Color.GRAY);
                            title.setGravity(Gravity.CENTER);
                            title.setTypeface(null, Typeface.BOLD);
                            title.setText(marker.getTitle());

                            TextView snippet = new TextView(getContext());
                            snippet.setTextColor(Color.BLACK);
                            snippet.setText(marker.getSnippet());

                            info.addView(title);
                            info.addView(snippet);

                            return info;
                        }
                    });

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 18));
                }
            });


        }else{
            current_user_id = Long.parseLong(session.getId());
            GetAllLocations obj = new GetAllLocations();
            obj.execute();
        }




        return v;
    }


    private class GetAllLocations extends AsyncTask<String, List<PositionAndDefect>, List<PositionAndDefect>> {

        @Override
        protected List<PositionAndDefect> doInBackground(String... strings) {
            List<PositionAndDefect> list = new ArrayList<>();
            DefectsDAO dao = new DefectsDAO();
            list = dao.getAllLocations(current_user_id);
            return list;
        }

        @Override
        protected void onPostExecute(List<PositionAndDefect> s) {
            super.onPostExecute(s);
            if(s.size() > 0){
                SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
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

                                    LinearLayout info = new LinearLayout(getContext());
                                    info.setOrientation(LinearLayout.VERTICAL);

                                    TextView title = new TextView(getContext());
                                    title.setTextColor(Color.GRAY);
                                    title.setGravity(Gravity.CENTER);
                                    title.setTypeface(null, Typeface.BOLD);
                                    title.setText(marker.getTitle());

                                    TextView snippet = new TextView(getContext());
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