package com.scriptmall.cabuser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditprofileActivity extends AppCompatActivity {


    EditText tvfname,tvlname,tvumail,tvuaddr,tvcity,tvzip,tvstate,tvcountry;
    TextView tvumobile;
    AutoCompleteTextView etcity,etstate,etcountry;
    String city_name,state_name,country_name;
    ProgressDialog loading;
    String uid,fname,lname,mail,addr,phone,zip,city,state,country;
    Spinner spincountry,spinstate,spincity;
    List<String> countrylist = new ArrayList<String>();
    List<String> stateList = new ArrayList<String>();
    List<String> cityList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Config.UID_SHARED_PREF,"Not Available");

        Intent i=getIntent();
        fname=i.getStringExtra("fname");
        lname=i.getStringExtra("lname");
        mail=i.getStringExtra("mail");
        addr=i.getStringExtra("addr");
        phone=i.getStringExtra("phone");
        zip=i.getStringExtra("zip");
        city=i.getStringExtra("city");
        state=i.getStringExtra("state");
        country=i.getStringExtra("country");

        tvfname = (EditText) findViewById(R.id.tvfname);
        tvlname = (EditText) findViewById(R.id.tvlname);
        tvumail = (EditText) findViewById(R.id.tvumail);
        tvuaddr = (EditText) findViewById(R.id.tvaddr);
        tvumobile = (TextView) findViewById(R.id.tvumobile);
        tvzip = (EditText) findViewById(R.id.tvzip);
        etcity = (AutoCompleteTextView) findViewById(R.id.etcity);
        etstate = (AutoCompleteTextView) findViewById(R.id.etstate);
        etcountry = (AutoCompleteTextView) findViewById(R.id.etcountry);


        tvfname.setText(fname);
        tvlname.setText(lname);
        tvumail.setText(mail);
        tvumobile.setText(phone);
        tvuaddr.setText(addr);
        etcountry.setText(country);
        etstate.setText(state);
        etcity.setText(city);
        tvzip.setText(zip);


        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        }else {
            getCityData();
        }

        Button btnsubmit=(Button)findViewById(R.id.button);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mail=tvumail.getText().toString().trim();
                fname=tvfname.getText().toString().trim();
                lname=tvlname.getText().toString().trim();
                phone=tvumobile.getText().toString().trim();
                addr=tvuaddr.getText().toString().trim();
                city=etcity.getText().toString().trim();
                state=etstate.getText().toString().trim();
                country=etcountry.getText().toString().trim();
                zip=tvzip.getText().toString().trim();

                if(!fname.equals("") && !lname.equals("") && !mail.equals("") && !phone.equals("") &&
                        !addr.equals("") && !city.equals("") && !state.equals("") && !country.equals("")  && !zip.equals("")){
                    ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                    if (netInfo == null) {
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    }else {
                        submitToDb();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), "Enter all details", Toast.LENGTH_SHORT).show();
                }

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update profile");


    }

    public void loadSpinnerData(){
        List<String> l5 = new ArrayList<String>();
        l5.add("---Select---"); l5.add("India"); l5.add("America");
        ArrayAdapter<String> dataAdapter5 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, l5);
        dataAdapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spincountry.setAdapter(dataAdapter5);

        List<String> l6 = new ArrayList<String>();
        l6.add("---Select---"); l6.add("Tamil Nadu"); l6.add("Kerala");
        ArrayAdapter<String> dataAdapter6 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, l6);
        dataAdapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinstate.setAdapter(dataAdapter6);

        List<String> l7 = new ArrayList<String>();
        l7.add("---Select---"); l7.add("Chennai"); l7.add("Tiruvannamalai");
        ArrayAdapter<String> dataAdapter7 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, l7);
        dataAdapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spincity.setAdapter(dataAdapter7);

    }

    public void getCityData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.COUNTRY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showJSON(response);
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
                            message="No Data Found";
                        }
                        Toast.makeText(EditprofileActivity.this,message, Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map = new HashMap<String,String>();
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void showJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

            JSONObject jsonObject0 = result.getJSONObject(0);
            JSONArray country_array = jsonObject0.getJSONArray("countrylist");
            for(int i=0;i<country_array.length();i++)
            {
                JSONObject eve = country_array.getJSONObject(i);
                country_name=eve.getString(Config.COUNTRY);
                countrylist.add(country_name);
            }
            etcountry.setAdapter((new ArrayAdapter<String>(EditprofileActivity.this, R.layout.simple_spinner_item, countrylist)));

            JSONObject jsonObject1 = result.getJSONObject(1);
            JSONArray state_array = jsonObject1.getJSONArray("statelist");
            for(int i=0;i<state_array.length();i++)
            {
                JSONObject eve = state_array.getJSONObject(i);
                state_name=eve.getString(Config.STATE);
                stateList.add(state_name);
            }
            etstate.setAdapter((new ArrayAdapter<String>(EditprofileActivity.this, R.layout.simple_spinner_item, stateList)));

            JSONObject jsonObject2 = result.getJSONObject(2);
            JSONArray city_array = jsonObject2.getJSONArray("citylist");
            for(int i=0;i<city_array.length();i++)
            {
                JSONObject eve = city_array.getJSONObject(i);
                city_name=eve.getString(Config.CITY);
                cityList.add(city_name);
            }
            etcity.setAdapter((new ArrayAdapter<String>(EditprofileActivity.this, R.layout.simple_spinner_item, cityList)));

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void submitToDb(){

        mail=tvumail.getText().toString().trim();
        fname=tvfname.getText().toString().trim();
        lname=tvlname.getText().toString().trim();
        phone=tvumobile.getText().toString().trim();
        addr=tvuaddr.getText().toString().trim();
        city=etcity.getText().toString().trim();
        state=etstate.getText().toString().trim();
        country=etcountry.getText().toString().trim();
        zip=tvzip.getText().toString().trim();

        loading = ProgressDialog.show(EditprofileActivity.this,"Please wait...","Sending...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.EDITPROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJSONForRegister(response);
//                        Toast.makeText(EditprofileActivity.this, response, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditprofileActivity.this,message, Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put(Config.UFNAME,fname);
                params.put(Config.ULNAME,lname);
                params.put(Config.UMAIL,mail);
                params.put(Config.UPHONENO,phone);
                params.put(Config.UADDR,addr);
                params.put(Config.CITY,city);
                params.put(Config.STATE,state);
                params.put(Config.COUNTRY,country);
                params.put(Config.ZIP,zip);
                params.put(Config.UID,uid);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
    private void showJSONForRegister(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject eve = result.getJSONObject(0);
            String resp = eve.getString("Result");
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
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
            case android.R.id.home:Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }
}
