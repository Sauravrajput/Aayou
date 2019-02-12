package com.scriptmall.cabuser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scriptmall on 11/23/2017.
 */
public class TrackOthersAdapter extends RecyclerView.Adapter<TrackOthersAdapter.MyViewHolder>{



    private List<OlaClone> rideList;
    private Context ctx;
    RecyclerView view;
    String tracker_name,tracker_phone,tracker_id;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvname, tvmobile;
        ImageView go;

        public MyViewHolder(View view) {
            super(view);
            tvname = (TextView) view.findViewById(R.id.tvname);
            tvmobile = (TextView) view.findViewById(R.id.tvmobile);
            go = (ImageView) view.findViewById(R.id.img2);

        }
    }

    public TrackOthersAdapter( List<OlaClone> rideList,ArrayList<Integer> alImage,ArrayList<Integer> alImage2) {
        this.rideList = rideList;


    }

    public TrackOthersAdapter(Context context, RecyclerView view, List<OlaClone> rideList) {
        this.rideList = rideList;
        this.ctx = context;
        this.view=view;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tracker_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        OlaClone ride = rideList.get(position);
        tracker_name=ride.getTracker_name();
        tracker_phone=ride.getTracker_phone();
        tracker_id=ride.getTracker_id();


        holder.tvname.setText(tracker_name);
        holder.tvmobile.setText(tracker_phone);

    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }
}
