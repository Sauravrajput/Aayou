package com.scriptmall.cabuser;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class RegisterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final int MULTIPLE_PERMISSIONS = 10; // code you want.
    EditText etnum;
    Button btn_nxt;
    String phone;
    Session session;
    ProgressDialog loading;
    String selectedCountryCode;
    String[] permissions = new String[]{
            Manifest.permission.CALL_PHONE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NOTIFICATION_POLICY,
            Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
            Manifest.permission.READ_CONTACTS
    };
    private String[] countries = {"+61", "+242", "+243", "+254",
            "+250", "+27", "+249", "+255", "+256"};
    // Images from res/drawable folder
    private int flags[] = {R.drawable.australia, R.drawable.congo, R.drawable.democratic, R.drawable.kenya,
            R.drawable.rwanda, R.drawable.south_africa, R.drawable.sudan, R.drawable.tanzania, R.drawable.ugnada};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        session = new Session(this);
        if (checkPermissions()) {
            //  permissions  granted.
        }

        session = new Session(this);
        if (session.loggedin()) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertForGPS();
        }
        startService(new Intent(this, ScriptsService.class));
        etnum = (EditText) findViewById(R.id.etnum);
        btn_nxt = (Button) findViewById(R.id.btn_nxt);
        btn_nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = etnum.getText().toString().trim();
                if (!phone.equals("")) {
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
                            Toast.makeText(RegisterActivity.this, lvalue, Toast.LENGTH_SHORT).show();
                            moveTaskToBack(true);
                        }
                        */
                        insertdb();

                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Enter phone number", Toast.LENGTH_SHORT).show();

                }
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Enter Mobile number");

        Spinner spinner = (Spinner) findViewById(R.id.simpleSpinner);
        spinner.setOnItemSelectedListener(this);

        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), flags, countries);
        spinner.setAdapter(customAdapter);
    }

    //Performing action onItemSelected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)

    {
//        imgView.setImageResource(flags[position]);
//        country_name.setText(countries[position]);
        selectedCountryCode = countries[position];

    }

    // not needed
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
//            result = ContextCompat.checkSelfPermission(getActivity(),p);
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                } else {
                    // no permissions granted.
                }
                return;
            }
        }
    }

    public void alertForGPS() {
        AlertDialog.Builder al = new AlertDialog.Builder(RegisterActivity.this);
        al.setTitle("Enable GPS");
        al.setCancelable(false);
        al.setMessage("You need to enable GPS for using this app. Please turn on Location Mode as \"HIGH ACCURACY\" in Settings.");


        al.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent callGPSSettingIntent = new Intent(
//                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
            }
        });
        al.show();

    }

    void insertdb() {

        final String phone = etnum.getText().toString().trim();

        loading = ProgressDialog.show(RegisterActivity.this, "Please wait...", "Sending...", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.USERREG_URL,
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
                        } else {
                            message = "Try Again";
                        }
                        loading.dismiss();
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.UPHONENO, phone);
                params.put("country_code", selectedCountryCode);
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

            if (success == 1) {
                Toast.makeText(this, "Registration Successfull, Will send You OTP in SMS", Toast.LENGTH_SHORT).show();
//            phone=etnum.getText().toString().trim();
                Intent i = new Intent(getApplicationContext(), Register2Activity.class);
                i.putExtra("phone", phone);
                startActivity(i);
            } else {
                Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                moveTaskToBack(true);
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }
}
