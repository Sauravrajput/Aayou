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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyRidesActivity extends AppCompatActivity {

    String uid, rdate, rpickup, rdrop, ramount, cabtype, cabimg, ridestatus, driver_img, driver_name, rid;
    private List<OlaClone> movieList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MyRidesAdapter mAdapter;
    ProgressDialog loading;
    ArrayList<Integer> alImage, alImage2;

    String pic_lat, pic_lng, drop_lat, drop_lng, ride_type;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Config.UID_SHARED_PREF, "Not Available");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        session = new Session(this);
        if (!session.loggedin()) {
            ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
            } else {
                session.setLoggedin(false);
                finish();
                startActivity(new Intent(MyRidesActivity.this, RegisterActivity.class));
            }
        } else {
            ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
            if (netInfo == null) {
                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
            } else {
                getData();
            }

        }


//        alImage = new ArrayList<>(Arrays.asList(R.drawable.aa, R.drawable.ab, R.drawable.aa));
//        alImage2 = new ArrayList<>(Arrays.asList(R.drawable.man1, R.drawable.man1, R.drawable.man1));

//        prepareMovieData();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

//        mAdapter = new MyRidesAdapter(movieList,alImage,alImage2);
//        recyclerView.setAdapter(mAdapter);


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                OlaClone ride = movieList.get(position);
                ridestatus = ride.getRidestatus();

                if (ridestatus.equals("0")) {
                    Toast.makeText(MyRidesActivity.this, "Appointment still in queue, Thanks for patience\nLong click to cancel appointment.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(getApplicationContext(), MyRidesCancelActivity.class);
                    i.putExtra("rideList", (Serializable) movieList);
                    i.putExtra("pos", String.valueOf(position));
                    startActivity(i);
                }
            }

            @Override
            public void onLongClick(View view, int position) {

                OlaClone ride = movieList.get(position);
                ridestatus = ride.getRidestatus();
                rid = ride.getRid();

                if (ridestatus.equals("0")) {
                    AlertDialog.Builder al = new AlertDialog.Builder(MyRidesActivity.this);
                    al.setTitle("Confirm");
                    al.setMessage("Are you sure want to cancel this request?");
                    al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                            if (netInfo == null) {
                                Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                            } else {
                                insertdb(rid);
                            }
                        }
                    });
                    al.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    al.show();

                }

            }
        }));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Appointments");


    }


    private void prepareMovieData() {
        OlaClone movie = new OlaClone("23-11-2017", "no:6/7,2nd cross street,sasthri nagar,adyar,chennai", "no:280/1, melmanmalai, chengam, tiruvannamalai",
                "10.30 AM", "0", "PHP2017112301", "Mini");
        movieList.add(movie);

        movie = new OlaClone("22-11-2017", "no:6/7,2nd cross street,sasthri nagar,adyar,chennai", "no:280/1, melmanmalai, chengam, tiruvannamalai",
                "11.30 AM", "1", "PHP2017112301", "Micro");
        movieList.add(movie);

        movie = new OlaClone("20-11-2017", "no:6/7,2nd cross street,sasthri nagar,adyar,chennai", "no:280/1, melmanmalai, chengam, tiruvannamalai",
                "12.45 AM", "2", "PHP2017112301", "Mini");
        movieList.add(movie);


    }

    public void getData() {
        // Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
        loading = ProgressDialog.show(MyRidesActivity.this, "Please wait...", "Fetching...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.MY_RIDES_URL,
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
                        Toast.makeText(MyRidesActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.UID, uid);

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
            int successs = jsonObject1.getInt("success");
            if (successs == 1) {
                JSONArray data = jsonObject1.getJSONArray("data");
                movieList.clear();
                for (int i = 0; i < data.length(); i++) {

                    JSONObject eve = data.getJSONObject(i);
                    rid = eve.getString(Config.RID);
                    ramount = eve.getString(Config.RAMT);
                    rdate = eve.getString(Config.RDATE);
                    rpickup = eve.getString(Config.RPICKUP);
                    rdrop = eve.getString(Config.RDROP);
                    pic_lat = eve.getString(Config.PIC_LAT);
                    pic_lng = eve.getString(Config.PIC_LONG);
                    drop_lat = eve.getString(Config.DROP_LAT);
                    drop_lng = eve.getString(Config.DROP_LONG);
                    ridestatus = eve.getString(Config.RIDE_STATUS);
                    driver_img = eve.getString(Config.DRI_IMG);
                    driver_name = eve.getString(Config.DRI_NAME);
                    ride_type = eve.getString(Config.RTYPE);
                    cabtype = eve.getString(Config.CAB_TYPE);
                    cabimg = eve.getString(Config.CAB_IMG);

//                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    OlaClone ed = new OlaClone();
                    ed.setRid(rid);
                    ed.setRamount(ramount);
                    ed.setRdate(rdate);
                    ed.setRpickup(rpickup);
                    ed.setRdrop(rdrop);
                    ed.setDrop_lat(drop_lat);
                    ed.setDrop_lng(drop_lng);
                    ed.setPic_lat(pic_lat);
                    ed.setPic_lng(pic_lng);
                    ed.setRidestatus(ridestatus);
                    ed.setDriver_img(driver_img);
                    ed.setDriver_name(driver_name);
                    ed.setRide_type(ride_type);
                    ed.setCabimg(cabimg);
                    ed.setCabtype(cabtype);
//
                    movieList.add(ed);
//                    Toast.makeText(getApplicationContext(), (CharSequence) eventsList,Toast.LENGTH_LONG).show();

                }
            } else {
                Toast.makeText(this, "no data found", Toast.LENGTH_SHORT).show();
            }
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
//            recyclerView.setLayoutManager(new GridLayoutManager(this, MainActivity.getGridSpanCount(this)));
            mAdapter = new MyRidesAdapter(this, recyclerView, movieList);
            recyclerView.setAdapter(mAdapter);
//
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void insertdb(final String rid) {

        loading = ProgressDialog.show(MyRidesActivity.this, "Please wait...", "Sending...", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CANCEL_BOOKING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJson(response);
//                        Toast.makeText(BillActivity.this, response, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MyRidesActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.RID, rid);
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
            int success = eve.getInt("success");
            if (success == 0) {
                Toast.makeText(this, "You cant able to cancel this ride.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MyRidesActivity.class);
                startActivity(i);
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
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }

}
