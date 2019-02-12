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
 * Created by scriptmall on 11/24/2017.
 */
public class SupportAdapter  extends RecyclerView.Adapter<SupportAdapter.MyViewHolder>{



    private List<OlaClone> subcatList;
    private Context ctx;
    String ques,ans,faq_id;
    RecyclerView view;
    ArrayList<Integer> alImage;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView img;


        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tvcat);
            img=(ImageView) view.findViewById(R.id.img);


        }
    }

    public SupportAdapter( List<OlaClone> subcatList) {
        this.subcatList = subcatList;

    }

    public SupportAdapter(Context context, RecyclerView view, List<OlaClone> subcatList) {
        this.subcatList = subcatList;
        ctx = context;
        this.view=view;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.support, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        OlaClone ride = subcatList.get(position);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //status switch and DB update
//                notifyItemChanged(selected_position);
//                selected_position = position;
//                notifyItemChanged(selected_position);
////                ((CheckBox) holder.cb).isChecked();
//            }
//        });

        faq_id=ride.getFaq_id();
        ques=ride.getQues();
        ans=ride.getAns();

        String[] strArray = ques.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }

        holder.name.setText(builder.toString());
//        holder.img.setImageResource(alImage.get(position));


//        animate(holder.img);


    }


//    public void animate(ImageView viewHolder) {
//        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(ctx, R.anim.bounce_interpolator);
////        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(ctx, R.anim.anticipate_overshoot_interpolator);
//        viewHolder.setAnimation(animAnticipateOvershoot);
//    }

    @Override
    public int getItemCount() {
        return subcatList.size();
    }
}
