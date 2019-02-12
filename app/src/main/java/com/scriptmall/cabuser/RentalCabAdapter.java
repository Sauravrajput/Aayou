package com.scriptmall.cabuser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scriptmall on 11/22/2017.
 */
public class RentalCabAdapter extends RecyclerView.Adapter<RentalCabAdapter.MyViewHolder>{



    private List<OlaClone> catList;
    private Context ctx;
    String cabtype,cabimg,seats,ptpamt,rentamt,outamt,outroundamt,outwaitingamt,driveramt;
    RecyclerView view;
    ArrayList<Integer> alImage;
    private int selected_position = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name,price,tvseats,desc;
        public ImageView img;
        LinearLayout lin1;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.cabtype);
            price = (TextView) view.findViewById(R.id.amt);
            tvseats = (TextView) view.findViewById(R.id.seats);
            img=(ImageView) view.findViewById(R.id.img);
            lin1 = (LinearLayout) view.findViewById(R.id.lin1);

        }
    }

    public RentalCabAdapter(List<OlaClone> catList, ArrayList<Integer> alImage) {
        this.catList = catList;
        this.alImage=alImage;

    }

    public RentalCabAdapter(Context context, RecyclerView view, List<OlaClone> catList) {
        this.catList = catList;
        ctx = context;
        this.view=view;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rental_cab_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        OlaClone ride = catList.get(position);

        cabtype=ride.getCabtype();
        seats=ride.getSeats();
        rentamt=ride.getRentamt();
        cabimg=ride.getCabimg();



        holder.name.setText(cabtype);
        holder.tvseats.setText(seats+" Seats");
//        holder.desc.setText(cabdesc);
        holder.price.setText("\u20B9 "+rentamt+" per Hour");

        Picasso
                .with(ctx)
                .load(cabimg)
                .into(holder.img);

        if(selected_position == position){
            holder.lin1.setBackgroundResource(R.drawable.yello_bg);

        } else {
            holder.lin1.setBackgroundResource(R.color.white);

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
