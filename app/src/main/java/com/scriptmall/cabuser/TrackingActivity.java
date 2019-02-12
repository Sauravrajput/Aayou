package com.scriptmall.cabuser;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TrackingActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView tvpick, tvdrop, tvdriver_name, tvdriver_rating, tvcab_num, tvprice, tvcab, tvotp;

    Button btncancel, btncall;
    String uid, rpickup, rdrop, ramount, fcm_pwd, driver_img, driver_name, rid, email, number, rating, otp, phone;
    String pic_lat, pic_lng, drop_lat, drop_lng, ride_type;
    GoogleMap googleMap;
    GPSTracker gps;
    ArrayList<LatLng> markerPoints;
    LatLng user_pic;
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 100;
    ProgressDialog loading;
    String pick, drop, ridestatus, cabtype, cabimg, strDriverId;
    ImageView driverimg, img;
    private static final String TAG = TrackingActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap mMap;
    LatLngBounds.Builder builder;
    RelativeLayout sos;
    Timer timer;
    private int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);


        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Config.UID_SHARED_PREF, "Not Available");

        SharedPreferences prefs = getSharedPreferences("ADMIN_DRIVER_ID", MODE_PRIVATE);
        strDriverId = prefs.getString("DRIVER_ID", null);

        Intent i = getIntent();
        rid = i.getStringExtra("rid");
        ridestatus = i.getStringExtra("ridestatus");

        tvdriver_name = (TextView) findViewById(R.id.tvdriver_name);
        tvcab = (TextView) findViewById(R.id.tvcab);
        tvprice = (TextView) findViewById(R.id.tvprice);
        tvpick = (TextView) findViewById(R.id.autoCompleteTextView);
        tvdrop = (TextView) findViewById(R.id.autoCompleteTextView2);
        tvdriver_rating = (TextView) findViewById(R.id.tvdriver_rating);
        tvcab_num = (TextView) findViewById(R.id.tvcab_num);
        tvotp = (TextView) findViewById(R.id.tvotp);

        driverimg = (ImageView) findViewById(R.id.driver_img);
        img = (ImageView) findViewById(R.id.img);
        btncancel = (Button) findViewById(R.id.btn_cancel);
        btncall = (Button) findViewById(R.id.btn_call);


//        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
//        if (netInfo == null) {
//            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
//        }else{
//            getData();
//        }

        markerPoints = new ArrayList<LatLng>();
        gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            pic_lat = String.valueOf(latitude);
            pic_lng = String.valueOf(longitude);
        } else {
            gps.showSettingsAlert();
        }


        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        } else {
            final Handler handler = new Handler();
            timer = new Timer();
            TimerTask doAsynchronousTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            /// send lat and long here to your server
                            // check if GPS enabled

                            // \n is for new line
//            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();

                            getDrivderLocationData();
                        }
                    });
                }
            };
            timer.schedule(doAsynchronousTask, 0, 2000);

        }

        user_pic = new LatLng(Double.parseDouble(pic_lat), Double.parseDouble(pic_lng));

        tvpick.setText(pick);
        tvdrop.setText(drop);

        if (ridestatus.equals("1")) {
            btncancel.setEnabled(true);
        } else if (ridestatus.equals("4") || ridestatus.equals("5") || ridestatus.equals("6")) {
            btncancel.setEnabled(false);
        }


        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {
                    Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                } else {
                    insertdb();
                }
            }
        });

        btncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
                } else {
//                    //Open call function
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            }
        });


        sos = (RelativeLayout) findViewById(R.id.sos);
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000); //You can manage the time of the blink with this parameter
        anim.setStartOffset(10);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        sos.startAnimation(anim);

        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder al = new AlertDialog.Builder(TrackingActivity.this);
                al.setIcon(R.drawable.sos);
                al.setTitle("Emergency Alert");
                al.setMessage("Are you sure want to send emergency alert to your saved contatcs");
                al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendAlert();
//                        Toast.makeText(TrackingActivity.this, rid, Toast.LENGTH_SHORT).show();
                    }
                });
                al.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                al.show();
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pickup Arriving");


    }

    public void getDrivderLocationData() {
        // Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
//        loading = ProgressDialog.show(TrackingActivity.this,"Please wait...","Fetching...",false,false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.DRIVER_LOCATION_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        loading.dismiss();
                        showDriverLocationJSON(response);
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
                        Toast.makeText(TrackingActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("driver_id", strDriverId);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    LatLng driverLatLong;

    private void showDriverLocationJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);

            JSONObject eve = result.getJSONObject(0);
            int success = eve.getInt("success");
            if (success == 1) {
                JSONObject data = eve.getJSONObject("data");
                JSONArray result2 = data.getJSONArray(Config.JSON_ARRAY);
                JSONObject eve2 = result2.getJSONObject(0);
                String driverLat = eve2.getString("lat");
                String driverLong = eve2.getString("lon");
                driverLatLong = new LatLng(Double.parseDouble(driverLat), Double.parseDouble(driverLong));
                drawRoute(driverLatLong);
//            mMap.addPolyline(new PolylineOptions().add(driverLatLong))
//            mMap.addMarker(new MarkerOptions()
//                    .position(driverLatLong).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                    .title("Driver"));

                MarkerOptions markerOptionsDriver = new MarkerOptions();
                markerOptionsDriver.position(driverLatLong);
                mMap.clear();
                markerOptionsDriver.title("Driver");
                markerOptionsDriver.icon(BitmapDescriptorFactory.fromResource(R.drawable.ambu_marker));
                markerOptionsDriver.getPosition();
                mMap.addMarker(markerOptionsDriver);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user_pic, 18.0f));
//        currntLoc(pic_lat,pic_long);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                MarkerOptions markerOptionsPickup = new MarkerOptions();
                markerOptionsPickup.position(user_pic);
                markerOptionsPickup.title("Pickup");
                markerOptionsPickup.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                markerOptionsPickup.getPosition();
                mMap.addMarker(markerOptionsPickup);
//            mMap.addMarker(new MarkerOptions()
//                    .position(user_pic).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
//                    .title("Pickup"));
            }
            else
            {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void drawRoute(LatLng driver_latlong) {
        String url = getDirectionsUrl(user_pic, driver_latlong);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }

    private String getDirectionsUrl(LatLng user_pic, LatLng driver_latlong) {
        String str_origin = "origin=" + user_pic.latitude + "," + user_pic.longitude;
        String str_dest = "destination=" + driver_latlong.latitude + "," + driver_latlong.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=AIzaSyDrJlYmi86il9sTTZOrXTk8uGD5jkQr2LY";
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    public void getData() {
        // Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
        loading = ProgressDialog.show(TrackingActivity.this, "Please wait...", "Fetching...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.TRACKING_URL,
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
                        Toast.makeText(TrackingActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.RID, rid);

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

            JSONObject eve = result.getJSONObject(0);
            success = eve.getInt("success");
            if (success == 1)
            {
                JSONObject data = eve.getJSONObject("data");
                ramount = data.getString(Config.RAMT);
                email = data.getString(Config.UMAIL);
                rpickup = data.getString(Config.RPICKUP);
                rdrop = data.getString(Config.RDROP);
                pic_lat = data.getString(Config.PIC_LAT);
                pic_lng = data.getString(Config.PIC_LONG);
                drop_lat = data.getString(Config.DROP_LAT);
                drop_lng = data.getString(Config.DROP_LONG);
                number = data.getString(Config.NUMBER);
                rating = data.getString(Config.RATING);
                otp = data.getString(Config.OTP);
                driver_img = data.getString(Config.DRI_IMG);
                driver_name = data.getString(Config.DRI_NAME);
                phone = data.getString(Config.UPHONENO);
                fcm_pwd = data.getString("fcm_password");
                cabtype = data.getString(Config.CAB_TYPE);
                cabimg = data.getString(Config.CAB_IMG);

                if (rating.length() == 1) {
                    tvdriver_rating.setText(" " + rating + ".0");
                } else {
                    tvdriver_rating.setText(" " + rating);
                }

                tvdriver_name.setText(driver_name);
//            tvdriver_rating.setText("Rating: "+rating);
                tvpick.setText(rpickup);
                tvdrop.setText(rdrop);
                tvprice.setText("Estimated cash to be paid:  USHs " + ramount);
                tvcab_num.setText(number);
                tvotp.setText(" Code " + otp + " ");
                Picasso
                        .with(this)
                        .load(driver_img)
                        .into(driverimg);
//            Picasso
//                    .with(this)
//                    .load(cabimg)
//                    .into(img);
                tvcab.setText(cabtype + " . " + rid);
                tvcab.setVisibility(View.INVISIBLE);
                user_pic = new LatLng(Double.parseDouble(pic_lat), Double.parseDouble(pic_lng));

                FirebaseAuth.getInstance().signInWithEmailAndPassword(
                        email, fcm_pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String fcm_id = user.getUid();
//                    subscribeToUpdates();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("locations").child(fcm_id);
                            ref.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    setMarker(dataSnapshot);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Log.d(TAG, "firebase auth success");
                        } else {
                            Log.d(TAG, "firebase auth failed");
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void insertdb() {

        loading = ProgressDialog.show(TrackingActivity.this, "Please wait...", "Sending...", false, true);
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
                        Toast.makeText(TrackingActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.RID, rid);
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

    public void sendAlert() {
        loading = ProgressDialog.show(TrackingActivity.this, "Please wait...", "Sending...", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.SOS_ALERT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showAlertJson(response);
//                        Toast.makeText(TrackingActivity.this, response, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TrackingActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.RID, rid);
                params.put(Config.UID, uid);
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

    private void showAlertJson(String response) {
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
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMaxZoomPreference(17);
        getData();
        if (map != null) {
            setUpMap();
        }
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.toString();

        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
//        Toast.makeText(this, value.get("latitude").toString(), Toast.LENGTH_SHORT).show();
        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            mMap.clear();
            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title(driver_name).icon(bitmapDescriptorFromVector(this, R.drawable.auto)).position(location)));
        } else {
            mMarkers.get(key).setPosition(location);
        }
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 5000));
            }
        });
        //  mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user_pic, 18.0f));
//        currntLoc(pic_lat,pic_long);
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(new MarkerOptions()
                .position(user_pic).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Pickup"));
    }

    @Override
    public void onBackPressed() {
        FirebaseAuth.getInstance().signOut();
//        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MyRidesActivity.class);
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
                FirebaseAuth.getInstance().signOut();
//                super.onBackPressed();
                Intent intent = new Intent(getApplicationContext(), MyRidesActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.d("Exception when download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;

    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            String distance = "";
            String duration = "";
            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Routes Available", Toast.LENGTH_SHORT).show();
                return;
            }

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
            }
            mMap.addPolyline(lineOptions);
        }
    }
}
