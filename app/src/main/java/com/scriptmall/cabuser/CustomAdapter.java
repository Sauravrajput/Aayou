package com.scriptmall.cabuser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by admin on 7/23/2018.
 */

public class CustomAdapter extends BaseAdapter
{
    private int flags[];
    private String[] countryNames;
    private LayoutInflater inflater;

    CustomAdapter(Context applicationContext, int[] flags, String[] countryNames)
    {
        this.flags = flags;
        this.countryNames = countryNames;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount()
    {
        return flags.length;
    }

    @Override
    public Object getItem(int i)
    {
        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {

        view = inflater.inflate(R.layout.cell_country_select, viewGroup, false);
        ImageView flag_icon = (ImageView) view.findViewById(R.id.imageView);
        TextView country_name = (TextView) view.findViewById(R.id.textView);
        flag_icon.setImageResource(flags[i]);
        country_name.setText(countryNames[i]);
        return view;
    }
}