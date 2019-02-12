package com.scriptmall.cabuser;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrackOthersActivity extends AppCompatActivity {

    String tracker_name,tracker_phone,tracker_id;
    String uid;
    private List<OlaClone> trackerList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TrackOthersAdapter mAdapter;
    ProgressDialog loading;

    Button track;
    EditText etrideid;
    Button submit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_others);


        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Config.UID_SHARED_PREF,"Not Available");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        }else{
            getData();
//            prepairData();
        }

        mAdapter = new TrackOthersAdapter(this, recyclerView, trackerList);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                OlaClone ride=trackerList.get(position);
                tracker_id=ride.getTracker_id();
                Intent i=new Intent(getApplicationContext(),TrackerRideListActivity.class);
                i.putExtra("tracker_id",tracker_id);
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        track=(Button)findViewById(R.id.track);
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForRideID();
            }
        });

        getSupportActionBar().setTitle("Track Others");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void showDialogForRideID() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.track_ride);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        etrideid=(EditText)dialog.findViewById(R.id.etrideid);
        submit=(Button)dialog.findViewById(R.id.button);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ride_id=etrideid.getText().toString().trim();
                if(!ride_id.equals("")){
                    Intent i=new Intent(getApplicationContext(),Tracking2Activity.class);
                    i.putExtra("rid", ride_id);
                    startActivity(i);
                }else {
                    Toast.makeText(TrackOthersActivity.this, "Please Enter Valid Raid Id", Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setAttributes(lp);
    }


    public void prepairData(){

        OlaClone ola=new OlaClone("dass","9655159938");
        trackerList.add(ola);
        ola=new OlaClone("dass","9655159938");
        trackerList.add(ola);
        ola=new OlaClone("dass","9655159938");
        trackerList.add(ola);
        ola=new OlaClone("dass","9655159938");
        trackerList.add(ola);

    }

    public void getData() {
        // Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
        loading = ProgressDialog.show(TrackOthersActivity.this,"Please wait...","Fetching...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.TRACKER_NUMBER_URL,
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
                            message="You can't able to track others.";
                        }
                        loading.dismiss();
                        Toast.makeText(TrackOthersActivity.this,message, Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put(Config.UID,uid);

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
                JSONArray data = jsonObject1.getJSONArray("data");
                trackerList.clear();
                for(int i=0;i<data.length();i++)
                {

                    JSONObject eve = data.getJSONObject(i);
                    tracker_id = eve.getString(Config.TRACK_ID);
                    tracker_name=eve.getString(Config.TRACK_NAME);
                    tracker_phone=eve.getString(Config.TRACK_PHONE);

                    OlaClone ed=new OlaClone();
                    ed.setTracker_id(tracker_id);
                    ed.setTracker_name(tracker_name);
                    ed.setTracker_phone(tracker_phone);

                    trackerList.add(ed);

                }
            }
            else {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            }

            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
//            recyclerView.setLayoutManager(new GridLayoutManager(this, MainActivity.getGridSpanCount(this)));
            mAdapter = new TrackOthersAdapter(this, recyclerView, trackerList);
            recyclerView.setAdapter(mAdapter);
//
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
