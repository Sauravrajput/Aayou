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

public class Register2Activity extends AppCompatActivity {

    EditText etnum;
    Button btn_nxt;
    String phone,otp,uid,resp,fname,email,addr,lname,zip,city,state,country;
    ProgressDialog loading;
    Session session;
    TextView tv;
    private int success;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        Intent i=getIntent();
        phone=i.getStringExtra("phone");

        session = new Session(this);
        if(session.loggedin()){
            startActivity(new Intent(Register2Activity.this,MainActivity.class));
            finish();
        }


        etnum=(EditText)findViewById(R.id.etnum);
        btn_nxt=(Button)findViewById(R.id.btn_nxt);
        tv=(TextView) findViewById(R.id.tv);
        tv.setText("Please wait.\nWe will sent OTP to "+phone);


        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp=etnum.getText().toString().trim();
                String  fcm_token = SharedPrefManager.getInstance(Register2Activity.this).getDeviceToken();
//                Toast.makeText(Register2Activity.this, fcm_token, Toast.LENGTH_SHORT).show();
                if (!otp.equals("")) {
                    ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                    if (netInfo == null) {
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    }else {
                        insertdb(fcm_token);
                    }
                } else {
                    Toast.makeText(Register2Activity.this, "Enter otp number", Toast.LENGTH_SHORT).show();

                }
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Verify number");

    }

    void insertdb(final String fcm_token){

        final String otp = etnum.getText().toString().trim();

        loading = ProgressDialog.show(Register2Activity.this,"Please wait...","Sending...",false,true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.REGISTER2_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJson(response);
//                        Toast.makeText(Register2Activity.this, response, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Register2Activity.this,message, Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put(Config.UPHONENO,phone);
                params.put(Config.OTP,otp);
                params.put(Config.FCM_TOKEN,fcm_token);
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
            resp=eve.getString("Result");
            success=eve.getInt("success");
            uid=eve.getString(Config.UID);
            fname=eve.getString(Config.UFNAME);
            lname=eve.getString(Config.ULNAME);
            email=eve.getString(Config.UMAIL);
            addr=eve.getString(Config.UADDR);
            country=eve.getString(Config.COUNTRY);
            state=eve.getString(Config.STATE);
            city=eve.getString(Config.CITY);
            zip=eve.getString(Config.ZIP);

//            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
//            Intent i=new Intent(getApplicationContext(),Register3Activity.class);
//            i.putExtra("phone",phone);
//            startActivity(i);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        if (success==1) {
            SharedPreferences sharedPreferences = Register2Activity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(Config.LOGGEDIN_SHARED_PREF, true);
            editor.putString(Config.UID_SHARED_PREF, uid);
            editor.commit();

            session.setLoggedin(true);
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
            Intent i=new Intent(getApplicationContext(),Register3Activity.class);
            i.putExtra("fname",fname);
            i.putExtra("lname",lname);
            i.putExtra("mail",email);
            i.putExtra("phone",phone);
            i.putExtra("addr",addr);
            i.putExtra("city",city);
            i.putExtra("state",state);
            i.putExtra("country",country);
            i.putExtra("zip",zip);
            startActivity(i);
        }else{
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
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
