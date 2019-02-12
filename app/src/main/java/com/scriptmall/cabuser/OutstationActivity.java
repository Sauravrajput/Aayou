package com.scriptmall.cabuser;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OutstationActivity extends AppCompatActivity {

    RadioButton radio_oneway,radio_return;
    RadioGroup radiogroup;
    LinearLayout lin1,lin2;
    RecyclerView recyclerView;
    OutstationAdapter mAdapter;
    private List<OlaClone> cabList = new ArrayList<>();
    ArrayList<Integer> alImage;

    String pic_lat, pic_long, drop_lat, drop_long,dropadr,pickupadr;
    TextView tvpick,tvdrop,tvdate_time_pick,tvdate_time_drop;

    String uid,ride_type,date_time,return_date,datetime;
    String cabtype,cabimg,seats,ptpamt,rentamt,outamt,outroundamt,outwaitingamt,driveramt;
    ProgressDialog loading;
    String dis,dist,time;
    LatLng pickup,drop;

    TextView tvdate,tvtime;
    private int year;
    private int month;
    private int day;
    static final int DATE_PICKER_ID = 1111;
    StringBuilder datestr;
    String timestr="";
    Button ok;

    Button book;
    float amt;
    String cab="0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstation);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Config.UID_SHARED_PREF,"Not Available");

        Intent i = getIntent();
        pic_lat = i.getStringExtra("pic_lat");
        pic_long = i.getStringExtra("pic_long");
        drop_lat = i.getStringExtra("drop_lat");
        drop_long = i.getStringExtra("drop_long");
        dropadr = i.getStringExtra("drop");
        pickupadr = i.getStringExtra("pickup");
        cabtype = i.getStringExtra("cab_type");
        ride_type = i.getStringExtra("ride_type");
        date_time = i.getStringExtra("date_time");

        radio_oneway=(RadioButton)findViewById(R.id.radio_male);
        radio_return=(RadioButton)findViewById(R.id.radio_female);
        radiogroup=(RadioGroup)findViewById(R.id.radiogender);

        tvpick=(TextView)findViewById(R.id.autoCompleteTextView);
        tvdrop=(TextView)findViewById(R.id.autoCompleteTextView2);

        lin1=(LinearLayout)findViewById(R.id.lin1);
        lin2=(LinearLayout)findViewById(R.id.lin2);

        tvdate_time_pick=(TextView)findViewById(R.id.date_time_pick);
        tvdate_time_drop=(TextView)findViewById(R.id.date_time_drop);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        tvpick.setText(pickupadr);
        tvdrop.setText(dropadr);
        if(date_time.equals("")){
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
            datetime = sdf1.format(new Date());
            tvdate_time_pick.setText(datetime);
        }else {
            tvdate_time_pick.setText(date_time);
        }


        prepairData();
//        prepairData2();
//        alImage = new ArrayList<>(Arrays.asList(R.drawable.aa, R.drawable.ab, R.drawable.ac, R.drawable.ad));

        radio_oneway.setChecked(true);
        lin2.setVisibility(View.GONE);
        ride_type="2";
        return_date="00-00-0000 00:00:00 am";

        pickup = new LatLng(Double.parseDouble(pic_lat), Double.parseDouble(pic_long));
        drop = new LatLng(Double.parseDouble(drop_lat), Double.parseDouble(drop_long));

        String url = getDirectionsUrl(pickup, drop);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        }else {
            getCabtype();
        }



        radio_oneway.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lin2.setVisibility(View.GONE);
                    ride_type="2";
                    return_date="00-00-0000 00:00:00 am";
                    prepairData();

//                    mAdapter = new OutstationAdapter(orderList,alImage);
//                    recyclerView.setAdapter(mAdapter);
                }
            }
        });
        radio_return.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    lin2.setVisibility(View.VISIBLE);
                    ride_type="3";
                    prepairData2();
//                    mAdapter = new OutstationAdapter(orderList,alImage);
//                    recyclerView.setAdapter(mAdapter);
                }
            }
        });

        lin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertForDate();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                getCabdetails(position);
                cab="1";
                OlaClone ride = cabList.get(position);
                cabtype=ride.getCabtype();
                seats=ride.getSeats();
                outamt=ride.getOutamt();
                outroundamt=ride.getOutroundamt();
                outwaitingamt=ride.getOutwaitingamt();
                driveramt=ride.getDriveramt();
                cabimg=ride.getCabimg();



            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        book=(Button)findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ride_type.equals("2")){
                    amt= Float.valueOf(dis)*Float.valueOf(outamt);
                }else if(ride_type.equals("3")){
                    amt= Float.valueOf(dis)*Float.valueOf(outroundamt);
                }

                if(date_time.equals("")){
                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa");
                    datetime = sdf1.format(new Date());
                }else {
                    datetime=date_time;
                }


                SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                String lstatus = sharedPreferences.getString("lstatus","Not Available");
                String lvalue = sharedPreferences.getString("lvalue","Not Available");
                if(!lstatus.equals("0")){
                    ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                    if (netInfo == null) {
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    }else {
                        if(cab.equals("1")){
                            if(ride_type.equals("3") && !return_date.equals("00-00-0000 00:00:00 am")){
                                insertdb();
                            }else if(ride_type.equals("3") && return_date.equals("00-00-0000 00:00:00 am")){
                                alertForDate();
                            }else if(ride_type.equals("2") && return_date.equals("00-00-0000 00:00:00 am")){
                                insertdb();
                            }

                        }else {
                            Toast.makeText(getApplicationContext(), "Please Select Your Cab", Toast.LENGTH_SHORT).show();
                        }
                    }
                }else {
                    Toast.makeText(OutstationActivity.this, lvalue, Toast.LENGTH_SHORT).show();
                    moveTaskToBack(true);
                }
            }
        });



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Book your outstation ride");

    }

    private String getDirectionsUrl(LatLng pickup, LatLng drop) {
        String str_origin = "origin=" + pickup.latitude + "," + pickup.longitude;
        String str_dest = "destination=" + drop.latitude + "," + drop.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception when download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            String distance = "";
            String duration = "";
            if(result.size()<1){
                Toast.makeText(getBaseContext(), "Enter correct pickup and drop address", Toast.LENGTH_SHORT).show();
                return;
            }

            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){	// Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(8);
//                lineOptions.color(R.color.blue);
                lineOptions.color(Color.BLUE);

            }
            dist="Distance="+distance;
            dis = distance.replaceAll("[^0-9.]","");
            time="Time="+duration;
        }
    }

    private void getCabtype() {
        // Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
        loading = ProgressDialog.show(OutstationActivity.this,"Please wait...","Fetching...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.RATECARD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJSON(response);
//                        Toast.makeText(MyRidesActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = null;
                        if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        }else {
                            message="No data found";
                        }
                        loading.dismiss();
                        Toast.makeText(OutstationActivity.this,message, Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String, String>();
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void showJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject jsonObject1 = result.getJSONObject(0);
            int success=jsonObject1.getInt("success");
            if (success==1)
            {
                JSONArray data=jsonObject1.getJSONArray("data");
                for(int i=0;i<data.length();i++)
                {

                    JSONObject eve = data.getJSONObject(i);
                    cabtype = eve.getString(Config.CAB_TYPE);
                    seats=eve.getString(Config.SEAT);
                    ptpamt=eve.getString(Config.PTPAMT);
                    rentamt = eve.getString(Config.RENTAMT);
                    outamt=eve.getString(Config.OUTAMT);
                    outroundamt=eve.getString(Config.OUTROUNDAMT);
                    outwaitingamt = eve.getString(Config.OUTWAITING);
                    driveramt=eve.getString(Config.DRIVERAMT);
                    cabimg=eve.getString(Config.CAB_IMG);

                    OlaClone ed=new OlaClone();
                    ed.setCabtype(cabtype);
                    ed.setSeats(seats);
                    ed.setPtpamt(ptpamt);
                    ed.setRentamt(rentamt);
                    ed.setOutamt(outamt);
                    ed.setOutroundamt(outroundamt);
                    ed.setOutwaitingamt(outwaitingamt);
                    ed.setDriveramt(driveramt);
                    ed.setCabimg(cabimg);
                    cabList.add(ed);
                }

            }
            else {
                Toast.makeText(this, "Np data found", Toast.LENGTH_SHORT).show();
            }


            cabList.clear();
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new OutstationAdapter(this, recyclerView,cabList,ride_type,dis);
            recyclerView.setAdapter(mAdapter);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void prepairData() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new OutstationAdapter(this, recyclerView,cabList,ride_type,dis);
        recyclerView.setAdapter(mAdapter);

    }
    private void prepairData2() {
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new OutstationAdapter(this, recyclerView,cabList,ride_type,dis);
        recyclerView.setAdapter(mAdapter);
    }

    private void alertForDate() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.date_time);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        tvdate=(TextView) dialog.findViewById(R.id.tvdate);
        tvtime=(TextView)dialog.findViewById(R.id.tvtime);
        ok=(Button)dialog.findViewById(R.id.button);

        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);
        String a=""; String b="";
        if(day<10){a="0";}if(month+1<10){b="0";}
        datestr=new StringBuilder().append(a).append(day).append("-").append(b).append(month + 1).append("-").append(year).append("");
        tvdate.setText("---Select date---");
        tvtime.setText("---Select Time---");
        tvdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });
        tvtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(OutstationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String am_pm="AM";
                        String mm_precede = "";String hh_precede = "";
                        if(hourOfDay>=12){
                            am_pm="PM";
                            if (hourOfDay >=13 && hourOfDay < 24) {
                                hourOfDay -= 12;
                            }
                            else {
                                hourOfDay = 12;
                            }
                        } else if (hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        if (minute < 10) { mm_precede = "0";  }
                        if (hourOfDay < 10) {
                            hh_precede = "0";
                        }
                        timestr=hh_precede+hourOfDay + ":" +mm_precede+ minute+":00 "+am_pm;
                        tvtime.setText(timestr);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    return_date=String.valueOf(datestr)+" "+timestr;
                    tvdate_time_drop.setText(return_date);
                    dialog.dismiss();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                return new DatePickerDialog(this, from_dateListener, year, month,day);
        }
        return null;
    }
    private DatePickerDialog.OnDateSetListener from_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            String a=""; String b="";
            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;
            if(day<10){a="0";}if(month+1<10){b="0";}
            datestr=new StringBuilder().append(a).append(day).append("-").append(b).append(month + 1).append("-").append(year).append("");
            tvdate.setText(datestr);
        }
    };


    private void insertdb() {

        loading = ProgressDialog.show(OutstationActivity.this,"Please wait...","Sending...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.BOOKING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJson(response);
//                        Toast.makeText(ConfrimActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = null;
                        if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        }else {
                            message="Try Again";
                        }
                        loading.dismiss();
                        Toast.makeText(OutstationActivity.this,message, Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put(Config.RDATE,datetime);
                params.put(Config.RETDATE,return_date);
                params.put(Config.RPICKUP,pickupadr);
                params.put(Config.RDROP,dropadr);
                params.put(Config.PIC_LAT_LANG,pic_lat+","+pic_long);
                params.put(Config.DROP_LATLANG,drop_lat+","+drop_long);
                params.put(Config.RAMT,String.valueOf(amt));
                params.put(Config.UID,uid);
                params.put(Config.CAB_TYPE,cabtype);
                params.put(Config.RTYPE,ride_type);
                return params;
            }
//            @Override
//            public Priority getPriority() {
//                return Priority.IMMEDIATE;
//            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void showJson(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject eve = result.getJSONObject(0);

            String resp=eve.getString("Result");
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
            if(resp.equalsIgnoreCase("Booking successfull")){
                AlertDialog.Builder al=new AlertDialog.Builder(this);
                al.setTitle("Success");
                al.setMessage("Your booking request sent successfully. Any of our driver will accept your ride immediately. Thank you. ");
                al.setPositiveButton("My Rides", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i=new Intent(getApplicationContext(),MyRidesActivity.class);
                        startActivity(i);
                    }
                });
                al.setNegativeButton("New Booking", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                    }
                });
                al.show();
            }


        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.prof, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch(item.getItemId()){
            case android.R.id.home:Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }



}
