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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RateCardActivity extends AppCompatActivity {

    Spinner spincity, spincat;
    String city, cat;

    TextView tvcabtype, tvseats, tvptpamt, tvrentamt, tvoutamt, tvoutround, tvoutwaiting, tvdriveramt;
    String cabtype, cabimg, seats, ptpamt, rentamt, outamt, outroundamt, outwaitingamt, driveramt;
    List<String> list = new ArrayList<String>();
    ProgressDialog loading;
    private List<OlaClone> cabList = new ArrayList<>();
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_card);

        tvcabtype = (TextView) findViewById(R.id.cabtype);
        tvseats = (TextView) findViewById(R.id.seats);
        tvptpamt = (TextView) findViewById(R.id.ptpamt);
        tvrentamt = (TextView) findViewById(R.id.rentamt);
        tvoutamt = (TextView) findViewById(R.id.outamt);
        tvoutround = (TextView) findViewById(R.id.outroundamt);
        tvoutwaiting = (TextView) findViewById(R.id.outwaitingamt);
        tvdriveramt = (TextView) findViewById(R.id.driveramt);

        img = (ImageView) findViewById(R.id.img);

        spincat = (Spinner) findViewById(R.id.spincat);


        spincat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                cat = spincat.getSelectedItem().toString();
                int pos = arg0.getSelectedItemPosition();
                showFair(pos);

            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        spincity = (Spinner) findViewById(R.id.spincity);
        List<String> list2 = new ArrayList<String>();
        list2.add("Chennai");

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spincity.setAdapter(dataAdapter2);
        spincity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                city = spincity.getSelectedItem().toString();
//                Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        } else {
            getData();
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Rate card");


    }

    private void showFair(int pos) {
        OlaClone driver = cabList.get(pos);
        cabtype = driver.getCabtype();
        seats = driver.getSeats();
        ptpamt = driver.getPtpamt();
        rentamt = driver.getRentamt();
        outamt = driver.getOutamt();
        outroundamt = driver.getOutroundamt();
        outwaitingamt = driver.getOutwaitingamt();
        driveramt = driver.getDriveramt();
        cabimg = driver.getCabimg();

        tvcabtype.setText(cabtype);
        tvseats.setText(seats + " seats");
        tvptpamt.setText("\u20b9 " + ptpamt + " per Km");
        tvrentamt.setText("\u20b9 " + rentamt + " per Hour");
        tvoutamt.setText("\u20b9 " + outamt + " per Km");
        tvoutround.setText("\u20b9 " + outroundamt + " per Km");
        tvoutwaiting.setText("\u20b9 " + outwaitingamt + " per Hour");
        tvdriveramt.setText("\u20b9 " + driveramt + " per Day");
        Picasso.with(this).load(cabimg).into(img);

    }

    public void getData() {
        // Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
        loading = ProgressDialog.show(RateCardActivity.this, "Please wait...", "Fetching...", false, false);

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
                        Toast.makeText(RateCardActivity.this, message, Toast.LENGTH_LONG).show();
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

                    list.add(cabtype);
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spincat.setAdapter(dataAdapter);


                }

            }
            else {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
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
