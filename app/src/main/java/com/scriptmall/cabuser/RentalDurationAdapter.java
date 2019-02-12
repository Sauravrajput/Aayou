package com.scriptmall.cabuser;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scriptmall on 11/22/2017.
 */
public class RentalDurationAdapter extends RecyclerView.Adapter<RentalDurationAdapter.MyViewHolder>{



    private List<OlaClone> catList;
    private Context ctx;
    String cabid,cabname,cabprice,cabtype,cabdesc;
    RecyclerView view;
    ArrayList<Integer> alImage;
    private int selected_position = -1;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        RadioButton butn;
        RadioGroup grp;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.dur);
            butn = (RadioButton) view.findViewById(R.id.radio_male);
            grp = (RadioGroup) view.findViewById(R.id.radiogender);

        }
    }

    public RentalDurationAdapter(List<OlaClone> catList) {
        this.catList = catList;

    }

    public RentalDurationAdapter(Context context, RecyclerView view, List<OlaClone> catList) {
        this.catList = catList;
        ctx = context;
        this.view=view;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.duration_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        OlaClone ride = catList.get(position);

        cabdesc=ride.getCabtime();

        holder.name.setText(cabdesc);


        if(selected_position == position){
            holder.butn.setChecked(true);

        } else {
            holder.butn.setChecked(false);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);
            }
        });


//        Picasso
//                .with(ctx)
//                .load(Config.IMAGES_URL+catimg)
//                .into(holder.img);


    }

    @Override
    public int getItemCount() {
        return catList.size();
    }
}
