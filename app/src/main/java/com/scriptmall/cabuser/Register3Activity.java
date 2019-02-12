package com.scriptmall.cabuser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Register3Activity extends AppCompatActivity {


    EditText etfname, etmail,etrefcode;
    TextView apply,terms,tv;
    Button btn_nxt;
    String uid,resp,terms_str,fname,lname,mail,addr,phone,zip,city,state,country;;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

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

        etfname=(EditText)findViewById(R.id.etfname);
        etmail=(EditText)findViewById(R.id.etmail);
//        etrefcode=(EditText)findViewById(R.id.etrefcode);
        tv=(TextView)findViewById(R.id.tv);
        terms=(TextView)findViewById(R.id.terms);
        btn_nxt=(Button)findViewById(R.id.btn_nxt);

        tv.setText("Enter details to create account with  "+phone);
        etfname.setText(fname);
        etmail.setText(mail);
//        etrefcode.setVisibility(View.GONE);

//        Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        }else {
            getCityData();
        }



        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {
                    Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                }else {
                    insertdb();
                }
//                Intent i=new Intent(getApplicationContext(),Register4Activity.class);
//                startActivity(i);
            }
        });

//        apply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               etrefcode.setVisibility(View.VISIBLE);
//            }
//        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForPrivacy();
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create account");



    }

    private void showDialogForPrivacy() {
        AlertDialog.Builder al = new AlertDialog.Builder(Register3Activity.this);
        al.setTitle("Terms and Condition");
        al.setMessage(terms_str);
        al.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        al.show();
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
                        Toast.makeText(Register3Activity.this,message, Toast.LENGTH_LONG ).show();
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

            JSONObject jsonObject2 = result.getJSONObject(3);
            JSONArray privacy_array = jsonObject2.getJSONArray("privacylist");
            JSONObject eve1 = privacy_array.getJSONObject(0);
            terms_str=eve1.getString(Config.PRIVACY);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    void insertdb(){

        final String fname = etfname.getText().toString().trim();
        final String mail = etmail.getText().toString().trim();

        loading = ProgressDialog.show(Register3Activity.this,"Please wait...","Sending...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.EDITPROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJson(response);
//                        Toast.makeText(PasswordUpdateActivity.this, response, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Register3Activity.this,message, Toast.LENGTH_LONG ).show();
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

    public void showJson(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject eve = result.getJSONObject(0);
            String resp = eve.getString("Result");
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
            Intent i=new Intent(getApplicationContext(),Register4Activity.class);
            startActivity(i);
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
            case android.R.id.home:
//                super.onBackPressed();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }
}
