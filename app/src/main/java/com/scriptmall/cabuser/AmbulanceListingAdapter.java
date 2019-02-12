package com.scriptmall.cabuser;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by admin on 6/2/2018.
 */

public class AmbulanceListingAdapter extends RecyclerView.Adapter<AmbulanceListingAdapter.MyviewHolder> {

    ArrayList<AmbulanceListing> ambulanceListings;
    Activity activity;

    String type;
    String strFrom;
    private int pos = -1;

    public AmbulanceListingAdapter(Activity activity, ArrayList<AmbulanceListing> ambulanceListings) {
        this.ambulanceListings=ambulanceListings;
        this.activity=activity;
    }

    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_ambulance_listing, parent, false);
        return new AmbulanceListingAdapter.MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyviewHolder holder, final int i) {

        holder.txtName.setText(ambulanceListings.get(i).getName());
        holder.txtRentalFare.setText("USHs "+ambulanceListings.get(i).getRentalFare());
        holder.layAmbulanceItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyItemChanged(pos);
                pos=i;
                notifyItemChanged(pos);
            }
        });

        if(pos==i){
            holder.layAmbulanceItem.setBackgroundColor(Color.parseColor("#D4D4D4"));
        }else {
            holder.layAmbulanceItem.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

//holder.layAmbulanceItem.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View view) {
//
//    }
//});
    }

    @Override
    public int getItemCount() {
        return ambulanceListings.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtRentalFare;
        private LinearLayout layAmbulanceItem;
        public MyviewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtRentalFare = (TextView) view.findViewById(R.id.txtRentalFare);
            layAmbulanceItem=(LinearLayout)view.findViewById(R.id.layAmbulanceItem);
        }
    }
}
