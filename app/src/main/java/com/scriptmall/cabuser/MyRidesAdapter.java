package com.scriptmall.cabuser;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by scriptmall on 11/23/2017.
 */
public class MyRidesAdapter extends RecyclerView.Adapter<MyRidesAdapter.MyViewHolder>{



    private List<OlaClone> rideList;
    private Context ctx;
    private int selected_position = -1;
    String cabnum,driver,drivernum,ride_status;
    RecyclerView view;
    ArrayList<Integer> alImage,alImage2;
    String rdate, rpickup,rdrop,ramount,ride_type,ridestatus,driver_img,cabtype,cabimg,rid;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvdate_time, tvpickup,tvdrop,tvcab_rid,tvstatus,tvamt;
        ImageView img_cancel,cab_img;
        CircleImageView driverimg;

        public MyViewHolder(View view) {
            super(view);
            tvdate_time = (TextView) view.findViewById(R.id.tvdate_time);
            tvpickup = (TextView) view.findViewById(R.id.autoCompleteTextView);
            tvdrop = (TextView) view.findViewById(R.id.autoCompleteTextView2);
            tvcab_rid = (TextView) view.findViewById(R.id.tvcab_rid);
            tvstatus = (TextView) view.findViewById(R.id.tvstatus);
            tvamt = (TextView) view.findViewById(R.id.amt);

            img_cancel = (ImageView) view.findViewById(R.id.img_cancel);
            cab_img = (ImageView) view.findViewById(R.id.cab_img);
            driverimg = (CircleImageView) view.findViewById(R.id.driver_img);


        }
    }

    public MyRidesAdapter( List<OlaClone> rideList,ArrayList<Integer> alImage,ArrayList<Integer> alImage2) {
        this.rideList = rideList;
        this.alImage=alImage;
        this.alImage2=alImage2;

    }

    public MyRidesAdapter(Context context, RecyclerView view, List<OlaClone> rideList) {
        this.rideList = rideList;
        this.ctx = context;
        this.view=view;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myrides_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        OlaClone ride = rideList.get(position);


        rdate=ride.getRdate();
        rpickup=ride.getRpickup();
        rdrop=ride.getRdrop();
        ramount=ride.getRamount();
        ridestatus=ride.getRidestatus();
        driver_img=ride.getDriver_img();
        rid=ride.getRid();
        ride_type=ride.getRide_type();
        cabimg=ride.getCabimg();
        cabtype=ride.getCabtype();


        holder.tvpickup.setText(rpickup);
        holder.tvdrop.setText(rdrop);
        holder.tvdate_time.setText(rdate);
        holder.tvcab_rid.setText(cabtype+" . "+rid);

        if(ride_type.equals("0")){
            holder.tvamt.setText("Total Amount : USHs "+ramount);
        }else if(ride_type.equals("1")){
            holder.tvamt.setText("Total Amount : USHs "+ramount);
        }else if(ride_type.equals("2")){
            holder.tvamt.setText("Total Amount : USHs "+ramount);
        }else if(ride_type.equals("3")){
            holder.tvamt.setText("Total Amount : USHs "+ramount);
        }
        holder.img_cancel.setVisibility(View.GONE);

//        holder.cab_img.setImageResource(alImage.get(position));

//        holder.driverimg.setImageResource(alImage2.get(position));





        if(ridestatus.equals("2")){
            holder.tvstatus.setTextColor(Color.GREEN);
            holder.tvstatus.setText("Finished");
            holder.img_cancel.setVisibility(View.INVISIBLE);
        }else if(ridestatus.equals("1")){
            holder.tvstatus.setTextColor(Color.MAGENTA);
            holder.tvstatus.setText("Accepted");
            holder.img_cancel.setVisibility(View.INVISIBLE);
        }else if(ridestatus.equals("4")){
            holder.tvstatus.setTextColor(Color.MAGENTA);
            holder.tvstatus.setText("Waiting starts");
            holder.img_cancel.setVisibility(View.INVISIBLE);
        }else if(ridestatus.equals("5")){
            holder.tvstatus.setTextColor(Color.MAGENTA);
            holder.tvstatus.setText("Return");
            holder.img_cancel.setVisibility(View.INVISIBLE);
        }else if(ridestatus.equals("6")){
            holder.tvstatus.setTextColor(Color.MAGENTA);
            holder.tvstatus.setText("On Ride");
            holder.img_cancel.setVisibility(View.INVISIBLE);
        }else if(ridestatus.equals("3")){
            holder.tvstatus.setVisibility(View.VISIBLE);
            holder.tvstatus.setText("Cancelled");
            holder.img_cancel.setVisibility(View.VISIBLE);
        }else if(ridestatus.equals("0")){
            holder.tvstatus.setTextColor(Color.BLUE);
            holder.tvstatus.setText("Pending");
            holder.img_cancel.setVisibility(View.INVISIBLE);
        }

        Picasso
                .with(ctx)
                .load(driver_img)
                .into(holder.driverimg);
        Picasso
                .with(ctx)
                .load(cabimg)
                .into(holder.cab_img);

//        Toast.makeText(ctx, "teat", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }
}
