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
import java.util.Locale;

/**
 * Created by scriptmall on 11/21/2017.
 */
public class OutstationAdapter extends RecyclerView.Adapter<OutstationAdapter.MyViewHolder>{



    private List<OlaClone> catList;
    private Context ctx;
    RecyclerView view;
    ArrayList<Integer> alImage;
    private int selected_position = -1;
    String ride_type,dis;
    float amt;
    String cabtype,cabimg,seats,ptpamt,rentamt,outamt,outroundamt,outwaitingamt,driveramt;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvcabtype,tvseats,tvwaiting,tvamt;
        public ImageView img;
        LinearLayout lin1;


        public MyViewHolder(View view) {
            super(view);
            tvcabtype = (TextView) view.findViewById(R.id.tvcabtype);
            tvseats = (TextView) view.findViewById(R.id.seats);
            tvwaiting = (TextView) view.findViewById(R.id.waiting);
            tvamt = (TextView) view.findViewById(R.id.amt);
            img=(ImageView) view.findViewById(R.id.img);
            lin1=(LinearLayout) view.findViewById(R.id.lin1);

        }
    }

    public OutstationAdapter(List<OlaClone> catList, ArrayList<Integer> alImage) {
        this.catList = catList;
        this.alImage=alImage;

    }

    public OutstationAdapter(Context context, RecyclerView view, List<OlaClone> catList,String ride_type,String dis) {
        this.catList = catList;
        ctx = context;
        this.view=view;
        this.ride_type=ride_type;
        this.dis=dis;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.outstation_cab_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        OlaClone ride = catList.get(position);

        cabtype=ride.getCabtype();
        seats=ride.getSeats();
        outamt=ride.getOutamt();
        outroundamt=ride.getOutroundamt();
        outwaitingamt=ride.getOutwaitingamt();
        driveramt=ride.getDriveramt();
        cabimg=ride.getCabimg();

        holder.tvcabtype.setText(cabtype);
        holder.tvseats.setText(seats+" Seats");
//        holder.tvwaiting.setText("Driver allowance \u20b9 "+driveramt+" / day & waiting chanrge \u20b9 "+outwaitingamt+" / hour is additional");

        if(ride_type.equals("2")){
            amt= Float.valueOf(dis)*Float.valueOf(outamt);
            holder.tvamt.setText("\u20b9 "+String.format(Locale.US, "%1$,.2f",amt));
            holder.tvwaiting.setText("Driver allowance \u20b9 "+driveramt+"/day is additional");

        }else if(ride_type.equals("3")){
            amt= Float.valueOf(dis)*Float.valueOf(outroundamt);
            holder.tvamt.setText("\u20b9 "+String.format(Locale.US, "%1$,.2f",amt));
            holder.tvwaiting.setText("Driver allowance \u20b9 "+driveramt+"/day & waiting chanrge \u20b9 "+outwaitingamt+"/hour is additional");

        }

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
