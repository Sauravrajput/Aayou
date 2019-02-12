package com.scriptmall.cabuser;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scriptmall on 11/20/2017.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder>{



    private List<OlaClone> catList;
    private Context ctx;
    String cabid,cabname,cabimg;
    RecyclerView view;
    ArrayList<Integer> alImage;
    private int selected_position = -1;
    String cabtype,seats,ptpamt,rentamt,outamt,outroundamt,outwaitingamt,driveramt;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,time;
        public ImageView img;
        RelativeLayout lin1;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            time = (TextView) view.findViewById(R.id.time);
            img=(ImageView) view.findViewById(R.id.img);
            lin1=(RelativeLayout)view.findViewById(R.id.lin1);

        }
    }

    public MainAdapter(List<OlaClone> catList, ArrayList<Integer> alImage) {
        this.catList = catList;
        this.alImage=alImage;

    }

    public MainAdapter(Context context, RecyclerView view, List<OlaClone> catList) {
        this.catList = catList;
        ctx = context;
        this.view=view;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cab_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        OlaClone ride = catList.get(position);

        cabtype=ride.getCabtype();
        seats=ride.getSeats();
        cabimg=ride.getCabimg();




        if(cabtype.equals("VIP")){
            holder.name.setTextColor(Color.BLUE);
            holder.time.setVisibility(View.GONE);
        }else if(cabtype.equals("EXECUTIVE")){
            holder.name.setTextColor(Color.BLUE);
            holder.time.setVisibility(View.GONE);
        }else if(cabtype.equals("STANDARD")){
            holder.name.setTextColor(Color.BLUE);
            holder.time.setVisibility(View.GONE);
        }
        else {
            holder.name.setTextColor(Color.DKGRAY);
            holder.time.setVisibility(View.VISIBLE);
        }

        holder.name.setText(cabtype);
       // holder.time.setText(seats+" Seats");
       /* Picasso
                .with(ctx)
                .load(cabimg)
                .into(holder.img);
                */

//        holder.img.setImageResource(alImage.get(position));


        if(selected_position == position){
            holder.lin1.setBackgroundResource(R.drawable.circle_yello);

        } else {
            holder.lin1.setBackgroundResource(R.drawable.circle);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);
            }
        });







    }

    @Override
    public int getItemCount() {
        return catList.size();
    }
}
