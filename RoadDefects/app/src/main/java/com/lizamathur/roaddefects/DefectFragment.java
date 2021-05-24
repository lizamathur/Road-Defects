package com.lizamathur.roaddefects;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.roaddefects.defect.Defect;
import com.roaddefects.defect.DefectsAdapter;

import java.util.List;

import dao.roaddefects.base.Session;
import dao.roaddefects.defects.DefectsDAO;

/**
 * Displays all defects added by a particular user in a list
 */
public class DefectFragment extends Fragment {

    View v = null;
    ListView listView;
    ProgressBar progressBar;
    TextView noData;
    Button refresh;
    private Session session;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_defect, container, false);
        listView = (ListView) v.findViewById(R.id.listView);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        noData = (TextView) v.findViewById(R.id.noData);
        refresh = (Button) v.findViewById(R.id.refresh);


        session = new Session(v.getContext());


        refresh.setOnClickListener(v -> {
            GetDefects defects = new GetDefects();
            defects.execute();
        });

        GetDefects defects = new GetDefects();
        defects.execute();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        GetDefects defects = new GetDefects();
        defects.execute();
    }

    /**
     * Fetches all the defects added by the current user
     */
    private class GetDefects extends AsyncTask<String, List<Defect>, List<Defect>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            noData.setText("");
        }

        @Override
        protected List<Defect> doInBackground(String... strings) {
            Log.v("ListView", "Background");
            DefectsDAO dao = new DefectsDAO();
            List<Defect> defectRecords = dao.getDefectRecords(Integer.parseInt(session.getId()));
            Log.v("ListView", "Got Records: " + defectRecords.size());
            return defectRecords;
        }

        @Override
        protected void onPostExecute(List<Defect> defects) {
            super.onPostExecute(defects);
            progressBar.setVisibility(View.INVISIBLE);
            if (defects.size() == 0)
                noData.setText("Nothing to show here!");
            DefectsAdapter defectsAdapter = new DefectsAdapter(v.getContext(), R.layout.single_defect, defects, "normal");
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
                        Intent intent = new Intent(getContext(), UpdateDefectActivity.class);
                        intent.putExtra("bundle", bundle);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), "The complaint has already been processed!", Toast.LENGTH_SHORT).show();
                    }

//                    MapFragment mapFragment = new MapFragment();
//                    mapFragment.setArguments(bundle);
//                    getFragmentManager().beginTransaction().replace(R.id.fragment_layout, mapFragment).addToBackStack(null).commit();
                }
            });
        }
    }
}