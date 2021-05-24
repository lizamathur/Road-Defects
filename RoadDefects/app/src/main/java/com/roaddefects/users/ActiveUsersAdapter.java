package com.roaddefects.users;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lizamathur.roaddefects.R;

import java.util.List;

public class ActiveUsersAdapter extends ArrayAdapter<User> {

    private Context mContext;
    private int mResource;

    public ActiveUsersAdapter(@NonNull Context context, int resource, @NonNull List<User> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        convertView = layoutInflater.inflate(mResource, parent, false);

        TextView name = convertView.findViewById(R.id.new_user_name);
        TextView email = convertView.findViewById(R.id.new_user_email);
        TextView mobile = convertView.findViewById(R.id.new_user_mobile);
        CheckBox checkBox = convertView.findViewById(R.id.new_checkbox);

        checkBox.setVisibility(View.GONE);

        name.setText(getItem(position).getName());
        email.setText(getItem(position).getEmail());
        mobile.setText(getItem(position).getMobile() + "");

        return convertView;

    }
}
