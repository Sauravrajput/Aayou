package com.scriptmall.cabuser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
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

//import static android.provider.ContactsContract.Directory.DISPLAY_NAME;

public class ProfileActivity extends AppCompatActivity {

    TextView tvname, tvmail, tvaddr, tvmobile, tvcity, tvzip, tvstate, tvcountry;
    EditText tvr1, tvr2, tvr3, tvr4, rname1, rname2, rname3, rname4;
    TextView update;
    ProgressDialog loading;
    String uid, fname, lname, umail, addr, mobile, zip, city, state, country, r1, r2, r3, r4, track_status, rn1, rn2, rn3, rn4;

    static Switch sb;
    TextView tvenable;
    RelativeLayout con1, con2, con3, con4;
    final int RQS_PICKCONTACT = 1;
    final int contact1 = 1;
    final int contact2 = 2;
    final int contact3 = 3;
    final int contact4 = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Config.UID_SHARED_PREF, "Not Available");


        tvname = (TextView) findViewById(R.id.tvuname);
        tvmail = (TextView) findViewById(R.id.tvumail);
        tvaddr = (TextView) findViewById(R.id.tvaddr);
        tvmobile = (TextView) findViewById(R.id.tvumobile);
        tvcity = (TextView) findViewById(R.id.tvcity);
        tvzip = (TextView) findViewById(R.id.tvzip);
        tvstate = (TextView) findViewById(R.id.tvstate);
        tvcountry = (TextView) findViewById(R.id.tvcountry);


        rname1 = (EditText) findViewById(R.id.rname1);
        rname2 = (EditText) findViewById(R.id.rname2);
        rname3 = (EditText) findViewById(R.id.rname3);
        rname4 = (EditText) findViewById(R.id.rname4);

        tvr1 = (EditText) findViewById(R.id.tvr1);
        tvr2 = (EditText) findViewById(R.id.tvr2);
        tvr3 = (EditText) findViewById(R.id.tvr3);
        tvr4 = (EditText) findViewById(R.id.tvr4);
        update = (TextView) findViewById(R.id.update);

        sb = (Switch) findViewById(R.id.switch1);
        tvenable = (TextView) findViewById(R.id.tvenable);


        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        } else {
            getData();
        }

        sb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sb.isChecked()) {
                    track_status = "1";
                    tvenable.setText("Enabled");

                } else {
                    track_status = "0";
                    tvenable.setText("Disabled");

                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {
                    Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                } else {
                    submitToDB();
                }
            }
        });

        con1 = (RelativeLayout) findViewById(R.id.con1);
        con2 = (RelativeLayout) findViewById(R.id.con2);
        con3 = (RelativeLayout) findViewById(R.id.con3);
        con4 = (RelativeLayout) findViewById(R.id.con4);
        con1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts(contact1);
            }
        });
        con2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts(contact2);
            }
        });
        con3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts(contact3);
            }
        });
        con4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContacts(contact4);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

    }

    private void getContacts(int contact) {
        final Uri uriContact = ContactsContract.Contacts.CONTENT_URI;
        Intent intentPickContact = new Intent(Intent.ACTION_PICK, uriContact);
        startActivityForResult(intentPickContact, contact);
    }

    private void getData() {
        loading = ProgressDialog.show(ProfileActivity.this, "Please wait...", "Sending...", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.PROFILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJson(response);
//                        Toast.makeText(ProfileActivity.this, response, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.UID, uid);
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
            int success = eve.getInt("success");
            String  result1=eve.getString("Result");
            if (success == 1) {
                JSONObject data =eve.getJSONObject("data");
                fname = data.getString(Config.UFNAME);
                lname = data.getString(Config.ULNAME);
                umail = data.getString(Config.UMAIL);
                mobile = data.getString(Config.UPHONENO);
                addr = data.getString(Config.UADDR);
                country = data.getString(Config.COUNTRY);
                state = data.getString(Config.STATE);
                city = data.getString(Config.CITY);
                zip = data.getString(Config.ZIP);
                r1 = data.getString(Config.RELATIVE_1);
                r2 = data.getString(Config.RELATIVE_2);
                r3 = data.getString(Config.RELATIVE_3);
                r4 = data.getString(Config.RELATIVE_4);
                rn1 = data.getString(Config.RELATIVE_NAME_1);
                rn2 = data.getString(Config.RELATIVE_NAME_2);
                rn3 = data.getString(Config.RELATIVE_NAME_3);
                rn4 = data.getString(Config.RELATIVE_NAME_4);
                track_status = data.getString(Config.TRACK_STATUS);

                tvname.setText(fname + " " + lname);
                tvmail.setText(umail);
                tvmobile.setText(mobile);
                tvaddr.setText(addr);
                tvcountry.setText(country);
                tvstate.setText(state);
                tvcity.setText(city);
                tvzip.setText(zip);
                tvr1.setText(r1);
                tvr2.setText(r2);
                tvr3.setText(r3);
                tvr4.setText(r4);

                if (!rn1.equals("")) {
                    rname1.setText(rn1);
                }
                if (!rn2.equals("")) {
                    rname2.setText(rn2);
                }
                if (!rn3.equals("")) {
                    rname3.setText(rn3);
                }
                if (!rn4.equals("")) {
                    rname4.setText(rn4);
                }

                if (track_status.equals("1")) {
                    sb.setChecked(true);
                } else if (track_status.equals("0")) {
                    sb.setChecked(false);
                }

            }
            else
            {
                Toast.makeText(this, result1, Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void submitToDB() {

        r1 = tvr1.getText().toString().trim();
        r2 = tvr2.getText().toString().trim();
        r3 = tvr3.getText().toString().trim();
        r4 = tvr4.getText().toString().trim();
        rn1 = rname1.getText().toString().trim();
        rn2 = rname2.getText().toString().trim();
        rn3 = rname3.getText().toString().trim();
        rn4 = rname4.getText().toString().trim();

        loading = ProgressDialog.show(ProfileActivity.this, "Please wait...", "Sending...", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.EDIT_NUMBER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showSubmitJson(response);
//                        Toast.makeText(ProfileActivity.this, response, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.UID, uid);
                params.put(Config.RELATIVE_1, r1);
                params.put(Config.RELATIVE_2, r2);
                params.put(Config.RELATIVE_3, r3);
                params.put(Config.RELATIVE_4, r4);
                params.put(Config.RELATIVE_NAME_1, rn1);
                params.put(Config.RELATIVE_NAME_2, rn2);
                params.put(Config.RELATIVE_NAME_3, rn3);
                params.put(Config.RELATIVE_NAME_4, rn4);
                params.put(Config.TRACK_STATUS, track_status);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showSubmitJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject eve = result.getJSONObject(0);
            String resp = eve.getString("Result");
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case contact1:
                    Uri returnUri = data.getData();
                    Cursor cursor = getContentResolver().query(returnUri, null, null, null, null);

                    if (cursor.moveToNext()) {
                        String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        String stringHasPhoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                        String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        rname1.setText(name);
                        if (stringHasPhoneNumber.equalsIgnoreCase("1")) {
                            Cursor cursorNum = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID, null, null);
                            //Get the first phone number
                            if (cursorNum.moveToNext()) {
                                int columnIndex_number = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                String phonenum = cursorNum.getString(columnIndex_number);
                                phonenum = phonenum.replaceAll(" ", "");
                                if (phonenum.startsWith("+91")) {
                                    phonenum = phonenum.replace("+91", "");
                                }
                                tvr1.setText(phonenum);
                            }

                        } else {
                            tvr1.setText("NO Phone Number");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "NO data!", Toast.LENGTH_LONG).show();
                    }
                    break;

                case contact2:
                    Uri returnUri2 = data.getData();
                    Cursor cursor2 = getContentResolver().query(returnUri2, null, null, null, null);

                    if (cursor2.moveToNext()) {
                        int columnIndex_ID = cursor2.getColumnIndex(ContactsContract.Contacts._ID);
                        String contactID = cursor2.getString(columnIndex_ID);

                        int columnIndex_HASPHONENUMBER = cursor2.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                        String stringHasPhoneNumber = cursor2.getString(columnIndex_HASPHONENUMBER);
                        String name = cursor2.getString(cursor2.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        rname2.setText(name);

                        if (stringHasPhoneNumber.equalsIgnoreCase("1")) {
                            Cursor cursorNum = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID, null, null);
                            //Get the first phone number
                            if (cursorNum.moveToNext()) {
                                int columnIndex_number = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                String phonenum = cursorNum.getString(columnIndex_number);
                                phonenum = phonenum.replaceAll(" ", "");
                                if (phonenum.startsWith("+91")) {
                                    phonenum = phonenum.replace("+91", "");
                                }
                                tvr2.setText(phonenum);
                            }

                        } else {
                            tvr2.setText("NO Phone Number");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "NO data!", Toast.LENGTH_LONG).show();
                    }
                    break;

                case contact3:
                    Uri returnUri3 = data.getData();
                    Cursor cursor3 = getContentResolver().query(returnUri3, null, null, null, null);

                    if (cursor3.moveToNext()) {
                        int columnIndex_ID = cursor3.getColumnIndex(ContactsContract.Contacts._ID);
                        String contactID = cursor3.getString(columnIndex_ID);
                        String name = cursor3.getString(cursor3.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        rname3.setText(name);

                        int columnIndex_HASPHONENUMBER = cursor3.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                        String stringHasPhoneNumber = cursor3.getString(columnIndex_HASPHONENUMBER);

                        if (stringHasPhoneNumber.equalsIgnoreCase("1")) {
                            Cursor cursorNum = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID, null, null);
                            //Get the first phone number
                            if (cursorNum.moveToNext()) {
                                int columnIndex_number = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                String phonenum = cursorNum.getString(columnIndex_number);
                                phonenum = phonenum.replaceAll(" ", "");
                                if (phonenum.startsWith("+91")) {
                                    phonenum = phonenum.replace("+91", "");
                                }
                                tvr3.setText(phonenum);
                            }

                        } else {
                            tvr3.setText("NO Phone Number");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "NO data!", Toast.LENGTH_LONG).show();
                    }
                    break;

                case contact4:
                    Uri returnUri4 = data.getData();
                    Cursor cursor4 = getContentResolver().query(returnUri4, null, null, null, null);

                    if (cursor4.moveToNext()) {
                        int columnIndex_ID = cursor4.getColumnIndex(ContactsContract.Contacts._ID);
                        String contactID = cursor4.getString(columnIndex_ID);

                        int columnIndex_HASPHONENUMBER = cursor4.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
                        String stringHasPhoneNumber = cursor4.getString(columnIndex_HASPHONENUMBER);
                        String name = cursor4.getString(cursor4.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        rname4.setText(name);

                        if (stringHasPhoneNumber.equalsIgnoreCase("1")) {
                            Cursor cursorNum = getContentResolver().query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID, null, null);
                            //Get the first phone number
                            if (cursorNum.moveToNext()) {
                                int columnIndex_number = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                                String phonenum = cursorNum.getString(columnIndex_number);
                                phonenum = phonenum.replaceAll(" ", "");
                                if (phonenum.startsWith("+91")) {
                                    phonenum = phonenum.replace("+91", "");
                                }
                                tvr4.setText(phonenum);
                            }

                        } else {
                            tvr4.setText("NO Phone Number");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "NO data!", Toast.LENGTH_LONG).show();
                    }
                    break;

            }
//            if(requestCode == RQS_PICKCONTACT){
//                Uri returnUri = data.getData();
//                Cursor cursor = getContentResolver().query(returnUri, null, null, null, null);
//
//                if(cursor.moveToNext()){
//                    int columnIndex_ID = cursor.getColumnIndex(ContactsContract.Contacts._ID);
//                    String contactID = cursor.getString(columnIndex_ID);
//
//                    int columnIndex_HASPHONENUMBER = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
//                    String stringHasPhoneNumber = cursor.getString(columnIndex_HASPHONENUMBER);
//
//                    if(stringHasPhoneNumber.equalsIgnoreCase("1")){
//                        Cursor cursorNum = getContentResolver().query(
//                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactID,null,null);
//                        //Get the first phone number
//                        if(cursorNum.moveToNext()){
//                            int columnIndex_number = cursorNum.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                            String phonenum = cursorNum.getString(columnIndex_number);
//                            phonenum=phonenum.replaceAll(" ","");
//                            if(phonenum.startsWith("+91")){
//                                phonenum=phonenum.replace("+91","");
//                            }
//                            tvr1.setText(phonenum);
//                        }
//
//                    }else{
//                        tvr1.setText("NO Phone Number");
//                    }
//                }else{
//                    Toast.makeText(getApplicationContext(), "NO data!", Toast.LENGTH_LONG).show();
//                }
//            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.send:
                Intent i = new Intent(getApplicationContext(), EditprofileActivity.class);
                i.putExtra("fname", fname);
                i.putExtra("lname", lname);
                i.putExtra("mail", umail);
                i.putExtra("phone", mobile);
                i.putExtra("addr", addr);
                i.putExtra("city", city);
                i.putExtra("state", state);
                i.putExtra("country", country);
                i.putExtra("zip", zip);
                startActivityForResult(i, 0);
                break;
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, 0);
                break;
//            case R.id.send1:
//                final Intent in;
//                in = new Intent(this, PasswordUpdateActivity.class);
////                in.putExtra("uid",uid);
//                startActivity(in);

        }
        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivityForResult(intent, 0);
    }

}
