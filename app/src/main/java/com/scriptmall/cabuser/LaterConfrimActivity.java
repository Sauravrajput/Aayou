package com.scriptmall.cabuser;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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
import java.util.Locale;
import java.util.Map;

public class LaterConfrimActivity extends AppCompatActivity implements OnMapReadyCallback {

    String pic_lat, pic_long, drop_lat, drop_long, dropadr, pickupadr;
    GoogleMap googleMap, googleMap2;
    MarkerOptions markerOptions;
    LatLng latLng;
    GPSTracker gps;
    ArrayList<LatLng> markerPoints;
    LatLng pickup, drop;
    String dist, dis, time;
    TextView picktv, droptv, ridetype, tvdate_time;
    Spinner spinpay;
    String paytype;
    String datetime, ride_type;
    TextView tvcab, tvamt, tvapply;
    EditText etcoupen;
    RecyclerView recycler_view;
    Button book;
    String uid, ride;
    float amt;
    String cabtype, seats, ptpamt, rentamt, outamt, outroundamt, outwaitingamt, driveramt;
    ProgressDialog loading;
    String ambulanceListingId;
    private ArrayList<AmbulanceListing> ambulanceListings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_later_confrim);

        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        uid = sharedPreferences.getString(Config.UID_SHARED_PREF, "Not Available");
        ptpamt = sharedPreferences.getString(Config.PTPAMT, "Not Available");


        Intent i = getIntent();
        pic_lat = i.getStringExtra("pic_lat");
        pic_long = i.getStringExtra("pic_long");
        drop_lat = i.getStringExtra("drop_lat");
        drop_long = i.getStringExtra("drop_long");
        dropadr = i.getStringExtra("drop");
        pickupadr = i.getStringExtra("pickup");
        cabtype = i.getStringExtra("cab_type");
        ride_type = i.getStringExtra("ride_type");
        datetime = i.getStringExtra("date_time");

        picktv = (TextView) findViewById(R.id.autoCompleteTextView);
        droptv = (TextView) findViewById(R.id.autoCompleteTextView2);
        tvdate_time = (TextView) findViewById(R.id.date_time);

        picktv.setText(pickupadr);
        droptv.setText(dropadr);
        tvdate_time.setText(datetime);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        supportMapFragment.getView().setVisibility(View.INVISIBLE);

        pickup = new LatLng(Double.parseDouble(pic_lat), Double.parseDouble(pic_long));
        drop = new LatLng(Double.parseDouble(drop_lat), Double.parseDouble(drop_long));

        String url = getDirectionsUrl(pickup, drop);
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);

        ridetype = (TextView) findViewById(R.id.spinpay);
        ridetype.setText(ride_type);


        tvcab = (TextView) findViewById(R.id.cabname);
        tvamt = (TextView) findViewById(R.id.amt);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        recycler_view.setHasFixedSize(true);
        recycler_view.setLayoutManager(new LinearLayoutManager(this));
        getAmbulanceListing();
        recycler_view.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycler_view, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                AmbulanceListing ambulanceListing = ambulanceListings.get(position);
                ambulanceListingId = ambulanceListing.getId();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
//        tvapply=(TextView)findViewById(R.id.apply);
        tvcab.setText(cabtype);
//        tvapply.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                alertForCoupen();
//            }
//        });

        book = (Button) findViewById(R.id.book);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ambulanceListingId.equals(null)) {
                    ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                    if (netInfo == null) {
                        Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    } else {
                       /* SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                        String lstatus = sharedPreferences.getString("lstatus","Not Available");
                        String lvalue = sharedPreferences.getString("lvalue","Not Available");
                        if(!lstatus.equals("0")){
                            insertdb();
                        }else {
                            Toast.makeText(LaterConfrimActivity.this, lvalue, Toast.LENGTH_SHORT).show();
                            moveTaskToBack(true);
                        }
                        */
                        insertdb();

                    }
                } else {
                    Toast.makeText(LaterConfrimActivity.this, "Enter valuable pickup and drop address", Toast.LENGTH_SHORT).show();
                }
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(cabtype);

    }

    private void alertForCoupen() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.apply_coupen);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        etcoupen = (EditText) dialog.findViewById(R.id.etcoupen);

        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setAttributes(lp);

    }

    private String getDirectionsUrl(LatLng pickup, LatLng drop) {
        String str_origin = "origin=" + pickup.latitude + "," + pickup.longitude;
        String str_dest = "destination=" + drop.latitude + "," + drop.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
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

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;
        if (map != null) {
            setUpMap();
        }

    }

    public void setUpMap() {

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickup, 15.0f));
        googleMap.addMarker(new MarkerOptions()
                .position(pickup).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Pickup"));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(drop, 15.0f));
        googleMap.addMarker(new MarkerOptions()
                .position(drop).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Drop"));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void insertdb() {

        loading = ProgressDialog.show(LaterConfrimActivity.this, "Please wait...", "Sending...", false, true);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.BOOKING_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJson(response);
//                        Toast.makeText(ConfrimActivity.this, response, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LaterConfrimActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put(Config.RDATE, datetime);
                params.put(Config.RETDATE, "00-00-0000 00:00:00 am");
                params.put(Config.RPICKUP, pickupadr);
                params.put(Config.RDROP, dropadr);
                params.put(Config.PIC_LAT_LANG, pic_lat + "," + pic_long);
                params.put(Config.DROP_LATLANG, drop_lat + "," + drop_long);
                params.put(Config.RAMT, String.valueOf(amt));
                params.put(Config.UID, uid);
                params.put(Config.CAB_TYPE, cabtype);
                params.put(Config.RTYPE, "0");
                params.put("d_id", ambulanceListingId);
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
            Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
            if (resp.equalsIgnoreCase("Booking successfull")) {
                AlertDialog.Builder al = new AlertDialog.Builder(this);
                al.setTitle("Success");
                al.setMessage("Your booking request sent successfully. Any of our driver will accept your ride immediately. Thank you. ");
                al.setPositiveButton("My Rides", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), MyRidesActivity.class);
                        startActivity(i);
                    }
                });
                al.setNegativeButton("New Booking", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                    }
                });
                al.show();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void calculateAmt() {
        amt = Float.valueOf(dis) * Float.valueOf(ptpamt);
        tvamt.setText("\u20b9 " + String.format(Locale.US, "%1$,.2f", amt));
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

    private void getAmbulanceListing() {
        // Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
        loading = ProgressDialog.show(LaterConfrimActivity.this, "Please wait...", "Fetching...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.AMBULANCE_LISTING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        showJSON(response);
//                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LaterConfrimActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("lat", pic_lat);
                params.put("long", pic_long);
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
                    String name = eve.getString("name");
                    String rentalFare = eve.getString("fair");
                    String id = eve.getString("id");
                    AmbulanceListing ambulanceListing = new AmbulanceListing();
                    ambulanceListing.setName(name);
                    ambulanceListing.setRentalFare(rentalFare);
                    ambulanceListing.setId(id);
                    ambulanceListings.add(ambulanceListing);
                }

            } else {
                Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
            }

            ambulanceListings.clear();
            AmbulanceListingAdapter ambulanceListingAdapter = new AmbulanceListingAdapter(this, ambulanceListings);
            recycler_view.setAdapter(ambulanceListingAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                Toast.makeText(getBaseContext(), "Enter correct pickup and drop address", Toast.LENGTH_SHORT).show();
                amt = 0;
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
                lineOptions.width(8);
//                lineOptions.color(R.color.blue);
                lineOptions.color(Color.BLUE);

            }
            dist = "Distance=" + distance;
            dis = distance.replaceAll("[^0-9.]", "");

            time = "Time=" + duration;
//            Toast.makeText(getApplicationContext(), dis, Toast.LENGTH_SHORT).show();

            googleMap.addPolyline(lineOptions);

            // calculateAmt();
        }
    }

}
