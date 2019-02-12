package com.scriptmall.cabuser;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RentalActivity extends AppCompatActivity {

    TextView tvpick, tvdate_Time, tvridetype;
    String pic_lat, pic_long, drop_lat, drop_long, dropadr, pickupadr, datetime;


    RecyclerView recyclerView;

    RentalCabAdapter mAdapter;

    String ride_type, date_time, uid;


    Button book;
    String cabtype, cabimg, seats, ptpamt, rentamt, outamt, outroundamt, outwaitingamt, driveramt;
    List<String> list = new ArrayList<String>();
    ProgressDialog loading;
    private List<OlaClone> cabList = new ArrayList<>();
    String cab = "0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rental);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Config.UID_SHARED_PREF, "Not Available");

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


        tvpick = (TextView) findViewById(R.id.autoCompleteTextView);
        tvdate_Time = (TextView) findViewById(R.id.date_time);
        tvridetype = (TextView) findViewById(R.id.spinpay);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);


        if (date_time.equals("")) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
            datetime = sdf1.format(new Date());
            tvdate_Time.setText(datetime);
        } else {
            tvdate_Time.setText(date_time);
        }


        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        } else {
            getCabtype();
        }


        tvridetype.setText(ride_type);
        tvpick.setText(pickupadr);
        cabtype = "";

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
//                getCabdetails(position);
                cab = "1";
                OlaClone ride = cabList.get(position);
                cabtype = ride.getCabtype();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        book = (Button) findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cab.equals("1")) {
                    ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                    if (netInfo == null) {
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    } else {
                        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        String lstatus = sharedPreferences.getString("lstatus", "Not Available");
                        String lvalue = sharedPreferences.getString("lvalue", "Not Available");
                       /* if(!lstatus.equals("0")){
                            insertdb();
                        }else {
                            Toast.makeText(RentalActivity.this, lvalue, Toast.LENGTH_SHORT).show();
                            moveTaskToBack(true);
                        }
                        */
                        insertdb();

                    }
                } else {
                    Toast.makeText(RentalActivity.this, "Please Select Your Cab", Toast.LENGTH_SHORT).show();
                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Book your ride");

    }

    private void getCabtype() {
        // Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
        loading = ProgressDialog.show(RentalActivity.this, "Please wait...", "Fetching...", false, false);

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
                        } else {
                            message = "No data found";
                        }
                        loading.dismiss();
                        Toast.makeText(RentalActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
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
            int success = jsonObject1.getInt("success");
            if (success == 1) {
                JSONArray data = jsonObject1.getJSONArray("data");
                cabList.clear();
                for (int i = 0; i < data.length(); i++) {

                    JSONObject eve = data.getJSONObject(i);
                    cabtype = eve.getString(Config.CAB_TYPE);
                    seats = eve.getString(Config.SEAT);
                    ptpamt = eve.getString(Config.PTPAMT);
                    rentamt = eve.getString(Config.RENTAMT);
                    outamt = eve.getString(Config.OUTAMT);
                    outroundamt = eve.getString(Config.OUTROUNDAMT);
                    outwaitingamt = eve.getString(Config.OUTWAITING);
                    driveramt = eve.getString(Config.DRIVERAMT);
                    cabimg = eve.getString(Config.CAB_IMG);

                    OlaClone ed = new OlaClone();
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
            } else {
                Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
            }

            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new RentalCabAdapter(this, recyclerView, cabList);
            recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void insertdb() {

        if (date_time.equals("")) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa");
            datetime = sdf1.format(new Date());
        } else {
            datetime = date_time;
        }


        loading = ProgressDialog.show(RentalActivity.this, "Please wait...", "Sending...", false, true);
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
                        } else {
                            message = "Try Again";
                        }
                        loading.dismiss();
                        Toast.makeText(RentalActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.RDATE, datetime);
                params.put(Config.RETDATE, "00-00-0000 00:00:00 am");
                params.put(Config.RPICKUP, pickupadr);
                params.put(Config.RDROP, dropadr);
                params.put(Config.PIC_LAT_LANG, pic_lat + "," + pic_long);
                params.put(Config.DROP_LATLANG, drop_lat + "," + drop_long);
                params.put(Config.RAMT, "0");
                params.put(Config.UID, uid);
                params.put(Config.CAB_TYPE, cabtype);
                params.put(Config.RTYPE, "1");
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

    public void showJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject eve = result.getJSONObject(0);

            String resp = eve.getString("Result");
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
            if (resp.equalsIgnoreCase("Booking successfull")) {
                AlertDialog.Builder al = new AlertDialog.Builder(this);
                al.setTitle("Success");
                al.setMessage("Your booking request sent successfully. Any of our driver will accept your ride immediately. Thank you. ");
                al.setPositiveButton("My Rides", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), MyRidesActivity.class);
                        startActivity(i);
                    }
                });
                al.setNegativeButton("New Booking", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    }
                });
                al.show();
            }


        } catch (JSONException e) {
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
        switch (item.getItemId()) {
            case android.R.id.home:
//                super.onBackPressed();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }


}
