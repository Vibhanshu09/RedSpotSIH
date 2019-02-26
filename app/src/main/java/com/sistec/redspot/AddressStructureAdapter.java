package com.sistec.redspot;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AddressStructureAdapter extends ArrayAdapter<AddressStructure> {
    public AddressStructureAdapter(Context context, ArrayList<AddressStructure> addressStructures) {
        super(context, 0, addressStructures);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.danger_zone_list_view,parent,false);
        }
        AddressStructure currentAdd = getItem(position);
        TextView tvLat = listItemView.findViewById(R.id.tv_danger_lat);
        TextView tvLng = listItemView.findViewById(R.id.tv_danger_lng);
        TextView tvLoc = listItemView.findViewById(R.id.tv_danger_locality);
        TextView tvDist = listItemView.findViewById(R.id.tv_dist_frm_you);

        tvLat.setText("" +currentAdd.getLatitude());
        tvLng.setText("" + currentAdd.getLongitude());
        tvLoc.setText("" + currentAdd.getSub_locality());

        Location dangerLoc = new Location("");
        dangerLoc.setLatitude(currentAdd.getLatitude());
        dangerLoc.setLongitude(currentAdd.getLongitude());
        float distance = dangerLoc.distanceTo(currentAdd.getCurrLocation());
        tvDist.setText("" + distance);
        if (distance < 200){
            tvDist.setTextColor(Color.RED);
        }

        return listItemView;

    }
}
