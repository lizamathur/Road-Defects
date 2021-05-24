package com.roaddefects.defect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lizamathur.roaddefects.HomePage;
import com.lizamathur.roaddefects.MapFragment;
import com.lizamathur.roaddefects.R;
import com.roaddefects.admin.ShowLocation;

import java.util.List;

public class DefectsAdapter extends ArrayAdapter<Defect> {

    private Context mContext;
    private int mResource;
    private String role;

    public DefectsAdapter(@NonNull Context context, int resource, @NonNull List<Defect> objects, String role) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.role = role;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);
        ImageView defectImg = convertView.findViewById(R.id.defectImg_f);
        TextView defectType = convertView.findViewById(R.id.defectType_f);
        TextView subType = convertView.findViewById(R.id.subType_f);
        TextView width = convertView.findViewById(R.id.width_f);
        TextView length = convertView.findViewById(R.id.length_f);
        TextView depth = convertView.findViewById(R.id.depth_f);
        TextView severity = convertView.findViewById(R.id.severity_f);
        TextView status = convertView.findViewById(R.id.status);
        ImageView showLocation = convertView.findViewById(R.id.showLocation);


        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Defect obj = getItem(position);
                bundle.putDouble("latitude", obj.getLatitude());
                bundle.putDouble("longitude", obj.getLongitude());

                String details = obj.defectDetailsForMap();
                bundle.putString("details", details);

                if (role.equals("normal")) {
                    MapFragment mapFragment = new MapFragment();
                    mapFragment.setArguments(bundle);
                    ((HomePage) mContext).getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_layout, mapFragment).addToBackStack(null).commit();
                } else {
                    Intent intent = new Intent(getContext(), ShowLocation.class);
                    intent.putExtra("latitude", obj.getLatitude());
                    intent.putExtra("longitude", obj.getLongitude());
                    intent.putExtra("details", details);
                    mContext.startActivity(intent);
                }

            }
        });

        defectImg.setImageBitmap(getItem(position).getImg());


        defectType.setText(getItem(position).getDefect());

        subType.setText(" - " + getItem(position).getSubType());
        width.setText("Width: " + getItem(position).getWidth() + " cm");
        length.setText("Length: " + getItem(position).getLength() + " cm");
        if (getItem(position).getDepth() != 0){
            depth.setVisibility(View.VISIBLE);
            depth.setText("Depth: " + getItem(position).getDepth() + " cm");
        }
        severity.setText("Severity: " + getItem(position).getSeverity());
        status.setText(" | " + getItem(position).getStatus());
        return convertView;
    }

}
