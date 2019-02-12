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
import java.util.Map;

public class SupportAnsActivity extends AppCompatActivity {

    TextView tvques,tvans,tvyes,tvno;
    String catid,catname,pos,subcatname;
    String ques,ans,faq_id;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_ans);

        Intent i=getIntent();
        pos=i.getStringExtra("pos");
        ArrayList<OlaClone> catList =  (ArrayList<OlaClone>)getIntent().getSerializableExtra("catList");
        OlaClone ride=catList.get(Integer.parseInt(pos));
        faq_id=ride.getFaq_id();
        ques=ride.getQues();
        ans=ride.getAns();

//        Toast.makeText(SupportAnsActivity.this, catname, Toast.LENGTH_SHORT).show();


        tvques=(TextView)findViewById(R.id.tvques);
        tvans=(TextView)findViewById(R.id.tvans);
        tvyes=(TextView)findViewById(R.id.tvyes);
        tvno=(TextView)findViewById(R.id.tvno);

        String[] strArray = ques.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }

        tvques.setText(builder.toString());
        tvans.setText(ans);

        tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {
                    Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                }else{
                    getData("1");
                }
            }
        });
        tvno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {
                    Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                }else{
                    getData("0");
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Learn more");


    }

    private void getData(final String s) {

        loading = ProgressDialog.show(SupportAnsActivity.this,"Please wait...","Fetching...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.FAQ_RATE_URL,
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
                        Toast.makeText(SupportAnsActivity.this,message, Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put("faq_id",faq_id);
                params.put("status",s);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void showJSON(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

            JSONObject eve = result.getJSONObject(0);
            String resp=eve.getString("Result");
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
            Intent i=new Intent(getApplicationContext(),SupportActivity.class);
            startActivity(i);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }
}
