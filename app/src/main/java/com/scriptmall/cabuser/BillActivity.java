package com.scriptmall.cabuser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BillActivity extends AppCompatActivity {

    TextView tvpick,tvdrop,tvprice,tvdisprice;

    ImageView driver_img;
    String driverimg,pick,drop,ramount,cabname,date,time,pick_long,pick_lat,drop_long,drop_lat,rid,ridestatus;

    Button submit;
    EditText etreview;
    RatingBar ratebar;
    String getrate,reviewet;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        Intent i=getIntent();
        rid=i.getStringExtra("rid");
        pick=i.getStringExtra("pick");
        drop=i.getStringExtra("drop");
        driverimg=i.getStringExtra("driverimg");
        ramount=i.getStringExtra("ramount");

        tvdisprice=(TextView)findViewById(R.id.tvdisprice);
        tvprice=(TextView)findViewById(R.id.tvprice);
        tvpick=(TextView)findViewById(R.id.autoCompleteTextView);
        tvdrop=(TextView)findViewById(R.id.autoCompleteTextView2);
        driver_img=(ImageView)findViewById(R.id.driver_img);

        tvpick.setText(pick);
        tvdrop.setText(drop);
        tvprice.setText("USHs "+ramount);

        Picasso
                .with(this)
                .load(driverimg)
                .into(driver_img);

        submit=(Button)findViewById(R.id.send);
        etreview=(EditText)findViewById(R.id.etreview);
        ratebar=(RatingBar)findViewById(R.id.get_rating);

        getrate="0.0";

        ratebar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                getrate= String.valueOf(rating);

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewet=etreview.getText().toString().trim();
//                Toast.makeText(ReviewActivity.this, getrate, Toast.LENGTH_SHORT).show();
                if(!reviewet.equals("")){
                    ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                    if (netInfo == null) {
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    }else {
                        insertdb();
                    }
                }else {
                    Toast.makeText(BillActivity.this, "Enter all details", Toast.LENGTH_SHORT).show();
                }
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Your bill");


    }

    private void insertdb() {
        final String review = etreview.getText().toString().trim();

        loading = ProgressDialog.show(BillActivity.this,"Please wait...","Sending...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.POST_REVIEW_URL,
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
                        }else {
                            message="Try Again";
                        }
                        loading.dismiss();
                        Toast.makeText(BillActivity.this,message, Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put(Config.REVIEW,review);
                params.put(Config.RATING,getrate);
                params.put(Config.RID,rid);
//                params.put("rideid",rid);
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
//            Intent i=new Intent(getApplicationContext(),MyRidesActivity.class);
//            startActivity(i);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//        startActivityForResult(intent, 0);
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
            case android.R.id.home:
                super.onBackPressed();
//                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
//                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }
}
