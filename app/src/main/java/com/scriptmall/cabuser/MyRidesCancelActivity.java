package com.scriptmall.cabuser;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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

public class MyRidesCancelActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView tvdriver_name,tvcab,tvprice,tvpick,tvdrop;
    ImageView driver_img,img_cancel,cab_img;
    Button btcancel,btnoffer,btnsupport,btn_track;
    String driver_name,pick,drop,price,cabtype,cabimg,time,pic_lng,pic_lat,drop_lng,drop_lat,rid,ridestatus,position;
    RelativeLayout rel_price;
    String rdate,ramount,rpickup,rdrop,driverimg;

    LatLng user_pic,user_drop;
    GoogleMap googleMap;
    EditText etcoupen;
    Button ok;
    ProgressDialog loading;
    RelativeLayout rel1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_rides_cancel);


        Intent i=getIntent();
        position=i.getStringExtra("pos");

        ArrayList<OlaClone> rideList =  (ArrayList<OlaClone>)getIntent().getSerializableExtra("rideList");

        OlaClone ride=rideList.get(Integer.parseInt(position));
        rid=ride.getRid();
        rdate=ride.getRdate();
        rpickup=ride.getRpickup();
        rdrop=ride.getRdrop();
        ramount=ride.getRamount();
        ridestatus=ride.getRidestatus();
        driverimg=ride.getDriver_img();
        pic_lat=ride.getPic_lat();
        pic_lng=ride.getPic_lng();
        drop_lat=ride.getDrop_lat();
        drop_lng=ride.getDrop_lng();
        driver_name=ride.getDriver_name();
        cabtype=ride.getCabtype();
        cabimg=ride.getCabimg();
//        rid=i.getStringExtra("rid");
//        pick=i.getStringExtra("rpickup");
//        drop=i.getStringExtra("rdrop");
//        cabname=i.getStringExtra("cabname");
//        time=i.getStringExtra("rtime");
//        date=i.getStringExtra("rdate");
//        ridestatus=i.getStringExtra("ridestatus");

        btcancel=(Button)findViewById(R.id.btn_cancel);
//        btnoffer=(Button)findViewById(R.id.btn_offer);
        btnsupport=(Button)findViewById(R.id.btn_support);
        btn_track=(Button)findViewById(R.id.btn_track);

        driver_img=(ImageView)findViewById(R.id.driver_img);
        img_cancel=(ImageView)findViewById(R.id.img_cancel);
        cab_img=(ImageView)findViewById(R.id.cab_img);

        rel_price=(RelativeLayout)findViewById(R.id.rel_price);


        tvdriver_name=(TextView)findViewById(R.id.tvdriver_name);
        tvcab=(TextView)findViewById(R.id.tvcab);
        tvprice=(TextView)findViewById(R.id.tvprice);
        tvpick=(TextView)findViewById(R.id.autoCompleteTextView);
        tvdrop=(TextView)findViewById(R.id.autoCompleteTextView2);

//        convertAddress(pick);
//        convertAddress2(drop);

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        user_pic = new LatLng(Double.parseDouble(pic_lat), Double.parseDouble(pic_lng));
        user_drop = new LatLng(Double.parseDouble(drop_lat), Double.parseDouble(drop_lng));

        String url = getDirectionsUrl(user_pic, user_drop);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

        rel1=(RelativeLayout)findViewById(R.id.rel1);
        rel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ntnhg will happend
            }
        });


        tvcab.setText(cabtype+" . "+rdate);
        tvpick.setText(rpickup);
        tvdrop.setText(rdrop);
        tvdriver_name.setText(driver_name);
        Picasso .with(this).load(driverimg).into(driver_img);
        Picasso .with(this).load(cabimg).into(cab_img);

        if(ridestatus.equals("3")){
            img_cancel.setImageResource(R.drawable.cancelled);
            tvprice.setText("USHs 0");
            btcancel.setEnabled(false);
//            btnoffer.setEnabled(false);
            btn_track.setEnabled(false);
        }else if(ridestatus.equals("2")){
            img_cancel.setImageResource(R.drawable.tint_finish);
            tvprice.setText("USHs "+ramount);
            btcancel.setEnabled(false);
//            btnoffer.setEnabled(false);
            btn_track.setEnabled(false);
        }else if(ridestatus.equals("1")) {
            img_cancel.setImageResource(R.drawable.accept);
            rel_price.setVisibility(View.GONE);
            btcancel.setEnabled(true);
//            btnoffer.setEnabled(false);
            btn_track.setEnabled(true);
        }else if(ridestatus.equals("4") || ridestatus.equals("5") || ridestatus.equals("6")){
            btcancel.setEnabled(false);
            img_cancel.setImageResource(R.drawable.accept);
            btn_track.setEnabled(true);
//            btnoffer.setEnabled(false);
        }

        btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo == null) {
                    Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                }else {
                    insertdb();
                }
            }
        });
//        btnoffer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                alertForCoupen();
//            }
//        });
        btn_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),TrackingActivity.class);
                i.putExtra("rid", rid);
                i.putExtra("ridestatus",ridestatus);
                startActivity(i);
            }
        });

        btnsupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),SupportActivity.class);
                startActivity(i);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(cabtype+" "+rid);


    }

    private void insertdb() {

        loading = ProgressDialog.show(MyRidesCancelActivity.this,"Please wait...","Sending...",false,true);
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
                        }else {
                            message="Try Again";
                        }
                        loading.dismiss();
                        Toast.makeText(MyRidesCancelActivity.this,message, Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put(Config.RID,rid);
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
            int success = eve.getInt("success");
            if (success==0)
            {
                Toast.makeText(this, "You cant able to cancel this ride.", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(this, resp, Toast.LENGTH_SHORT).show();
                Intent i=new Intent(getApplicationContext(),MyRidesActivity.class);
                startActivity(i);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void alertForCoupen() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.apply_coupen_offer);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        etcoupen=(EditText)dialog.findViewById(R.id.etcoupen);

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
    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
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
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);
            }catch(Exception e){
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
            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){	// Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
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
            googleMap.addPolyline(lineOptions);
        }
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
//        googleMap.setMyLocationEnabled(true);
//        googleMap.setTrafficEnabled(true);
//        googleMap.setIndoorEnabled(true);
//        googleMap.setBuildingsEnabled(true);
//        googleMap.getUiSettings().setZoomControlsEnabled(false);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user_pic, 12.0f));
//        currntLoc(pic_lat,pic_long);
        googleMap.addMarker(new MarkerOptions()
                .position(user_pic).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Pickup"));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user_drop, 12.0f));
//        currntLoc(pic_lat,pic_long);
        googleMap.addMarker(new MarkerOptions()
                .position(user_drop).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Drop"));
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(ridestatus.equals("2")){
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.review, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch(item.getItemId()){

            case android.R.id.home:Intent intent = new Intent(getApplicationContext(), MyRidesActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.review:
                Intent  in = new Intent(this, BillActivity.class);
                in.putExtra("drop", rdrop);
                in.putExtra("pick", rpickup);
                in.putExtra("rid", rid);
                in.putExtra("driverimg", driverimg);
                in.putExtra("ramount", ramount);
                startActivity(in);




        }
        return true;
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MyRidesActivity.class);
        startActivityForResult(intent, 0);
    }





}
