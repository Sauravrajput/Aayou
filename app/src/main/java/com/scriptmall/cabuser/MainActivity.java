package com.scriptmall.cabuser;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {
    Calendar c;
    private RecyclerView recyclerView;
    MainAdapter mAdapter;
    ArrayList<Integer> alImage;
    Marker mCurrLocationMarker;
    GoogleMap googleMap, googleMap2;
    MarkerOptions markerOptions;
    LatLng latLng;
    GPSTracker gps, gps2;
    ArrayList<LatLng> markerPoints, markerPoints2;
    LatLng user_pic, user_drop;
    String lati, longi;
    Marker marker;
    public LatLng mCenterLatLong;
    String pic_lat, pic_long, drop_lat, drop_long;
    TextView txtPickupTit, txtDropTit;

    private static final String LOG_TAG = "MainActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mAutocompleteTextView, mAutocompleteTextView2;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter, mPlaceArrayAdapter2;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));


    Button now, later;

    TextView tvdate, tvtime;
    private int year;
    private int month;
    private int day;
    static final int DATE_PICKER_ID = 1111;
    StringBuilder datestr;
    String timestr = "";
    Button ok;


    Session session;
    String pickup, drop, picaddr;
    String cabtype, seats, ptpamt, rentamt, outamt, outroundamt, outwaitingamt, driveramt, cabimg;
    List<String> list = new ArrayList<String>();
    ProgressDialog loading;
    private List<OlaClone> cabList = new ArrayList<>();
    private String[] city = {"Arua", "Bundibugyo", "Bushenyi", "Entebbe", "Fort Portal", "Gulu", "Hoima", "Ibanda", "Jinja", "Kampala", "Kamuli", "Kapchorwa", "Kasese",
            "Kayunga", "Kiboga", "Kigumba", "Kiryadongo", "Kisoro", "Kitgum", "Kotido", "Kumi", "Lira", "Lugazi", "Lyantonde", "Kabale", "Luwero", "Masaka", "Masindi", "Mbale", "Moroto", "Moyo", "Iganga",
            "Mbarara", "Mityana", "Mpigi", "Mubende", "Mukono", "Nairobi", "Nebbi", "Packwach", "Paraa", "Rukungiri", "Soroti", "Tororo", "Wobulenzi"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Get Your Appointment");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        txtPickupTit=(TextView)findViewById(R.id.txtPickupTit);
        txtDropTit=(TextView)findViewById(R.id.txtDropTit);

        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/agency_fb_bold.ttf");
        txtPickupTit.setTypeface(face);
        txtDropTit.setTypeface(face);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        session = new Session(this);
        if (!session.loggedin()) {
            logout();
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            alertForGPS();
        }
        startService(new Intent(this, ScriptsService.class));
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        } else {
            getCabtype();
        }

        cabtype = "";
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                OlaClone driver = cabList.get(position);
                cabtype = driver.getCabtype();
                seats = driver.getSeats();
                ptpamt = driver.getPtpamt();
                rentamt = driver.getRentamt();
                outamt = driver.getOutamt();
                outroundamt = driver.getOutroundamt();
                outwaitingamt = driver.getOutwaitingamt();
                driveramt = driver.getDriveramt();

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Config.CAB_TYPE, cabtype);
                editor.putString(Config.SEAT, seats);
                editor.putString(Config.PTPAMT, ptpamt);
                editor.putString(Config.RENTAMT, rentamt);
                editor.putString(Config.OUTAMT, outamt);
                editor.putString(Config.OUTROUNDAMT, outroundamt);
                editor.putString(Config.OUTWAITING, outwaitingamt);
                editor.putString(Config.DRIVERAMT, driveramt);
                editor.commit();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

//        markerPoints = new ArrayList<LatLng>();
        gps = new GPSTracker(this);
        // check if GPS enabled
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            pic_lat = String.valueOf(latitude);
            pic_long = String.valueOf(longitude);
            getAddress(pic_lat + "," + pic_long);
//            LatLng latLngg = new LatLng(latitude, longitude);
//            MarkerOptions markerOptions = new MarkerOptions();
//            markerOptions.position(latLngg);
//            markerOptions.title("Current Position");
//            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//            mCurrLocationMarker = googleMap.addMarker(markerOptions);
//
//            //move map camera
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
//            Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_SHORT).show();
        } else {
            gps.showSettingsAlert();
        }

//       currntLoc(pic_lat,pic_long);


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
//        supportMapFragment.getView().setVisibility(View.INVISIBLE);
        user_pic = new LatLng(Double.parseDouble(pic_lat), Double.parseDouble(pic_long));
        initAuto();

        now = (Button) findViewById(R.id.now);
        later = (Button) findViewById(R.id.later);
        now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drop = mAutocompleteTextView2.getText().toString().trim();
                pickup = mAutocompleteTextView.getText().toString().trim();
                if (!cabtype.equals("")) {
                    if (!cabtype.equals("Rental") && !drop.equals("")) {
                        ridenow();
                    } else if (cabtype.equals("Rental")) {
                        ridenow();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter your drop location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please Select Ambulance Category", Toast.LENGTH_SHORT).show();
                }
            }
        });


        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drop = mAutocompleteTextView2.getText().toString().trim();
                pickup = mAutocompleteTextView.getText().toString().trim();
                if (!cabtype.equals("")) {
                    if (!cabtype.equals("Rental") && !drop.equals("")) {
                        alertForDate();
                    } else if (cabtype.equals("Rental")) {
                        alertForDate();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter your drop location", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please Select Ambulance Category", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getCabtype() {
        // Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
        loading = ProgressDialog.show(MainActivity.this, "Please wait...", "Fetching...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CABCAT_URL,
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
                            final Dialog dialog = new Dialog(MainActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.popup_no_internet);

                            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
//                                text.setText(msg);

                            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();


                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else {
                            message = "No data found";
                        }
                        loading.dismiss();
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
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
            int success=jsonObject1.getInt("success");
            if (success==1){
                JSONArray data = jsonObject1.getJSONArray("data");
                cabList.clear();
                for (int i = 0; i < data.length(); i++) {

                    JSONObject eve = result.getJSONObject(i);
                    //  cabtype = eve.getString(Config.CAB_TYPE);


                    OlaClone ed = new OlaClone();
                    ed.setCabtype(eve.getString(Config.CAB_TYPE));

                    cabList.add(ed);
                }

            }

            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new MainAdapter(this, recyclerView, cabList);
            recyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void ridenow() {

        drop = mAutocompleteTextView2.getText().toString().trim();
        pickup = mAutocompleteTextView.getText().toString().trim();
        if (pickup.equals("")) {
            pickup = picaddr;
        } else {
            pickup = mAutocompleteTextView.getText().toString().trim();
            convertAddress(pickup);
        }
        if (!drop.equals("") && cabtype.length() > 0) {
            convertAddress2(drop);
            convertAddress(pickup);

            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.city_dialogbox);

            ListView recCity = (ListView) dialog.findViewById(R.id.recCity);
            CityListAdapter cityListAdapter = new CityListAdapter(MainActivity.this, city);
            recCity.setAdapter(cityListAdapter);
            recCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String cityName = city[i];
                    if (drop_lat==null || drop_lat.equals(null) || drop_lat==null) {
                        convertAddress2(drop);
                        convertAddress(pickup);
                        Toast.makeText(getApplicationContext(), "Please try again with correct details", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        Intent ii = new Intent(getApplicationContext(), ConfrimActivity.class);
                        ii.putExtra("pic_lat", pic_lat);
                        ii.putExtra("pic_long", pic_long);
                        ii.putExtra("drop_lat", drop_lat);
                        ii.putExtra("drop_long", drop_long);
                        ii.putExtra("drop", drop);
                        ii.putExtra("pickup", pickup);
                        ii.putExtra("ride_type", "Point to Point");
                        ii.putExtra("cab_type", cabtype);
                        ii.putExtra("date_time", "");
                        ii.putExtra("city_name", cityName);
                        startActivity(ii);
                    }
                }
            });

            dialog.show();


            //old alert box
//            AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
//            al.setTitle("Confirm");
//            al.setMessage("Are you sure to continue");
//            al.setCancelable(true);
//            al.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                    if (drop_lat.equals("") || drop_lat.equals(null) || drop_lat.equals("0.0")) {
//                        convertAddress2(drop);
//                        convertAddress(pickup);
//                        Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Intent i = new Intent(getApplicationContext(), ConfrimActivity.class);
//                        i.putExtra("pic_lat", pic_lat);
//                        i.putExtra("pic_long", pic_long);
//                        i.putExtra("drop_lat", drop_lat);
//                        i.putExtra("drop_long", drop_long);
//                        i.putExtra("drop", drop);
//                        i.putExtra("pickup", pickup);
//                        i.putExtra("ride_type", "Point to Point");
//                        i.putExtra("cab_type", cabtype);
//                        i.putExtra("date_time", "");
//                        startActivity(i);
//                    }
//                }
//
//            });
//            al.show();
        } else if (cabtype.equals(null)) {
            Toast.makeText(getApplicationContext(), "Please select service type", Toast.LENGTH_SHORT).show();

        }

    }

    private void ridelater(final String date_time) {
        drop = mAutocompleteTextView2.getText().toString().trim();
        pickup = mAutocompleteTextView.getText().toString().trim();
        if (pickup.equals("")) {
            pickup = picaddr;
        } else {
            pickup = mAutocompleteTextView.getText().toString().trim();
            convertAddress(pickup);
        }
        if (!drop.equals("")) {
            convertAddress2(drop);
            convertAddress(pickup);

            AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
            al.setTitle("Confirm");
            al.setMessage("Are you sure to continue");
            al.setCancelable(true);
            al.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    if (drop_lat.equals("") || drop_lat.equals(null) || drop_lat.equals("0.0")) {
                        convertAddress2(drop);
                        convertAddress(pickup);
                        Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent i = new Intent(getApplicationContext(), LaterConfrimActivity.class);
                        i.putExtra("pic_lat", pic_lat);
                        i.putExtra("pic_long", pic_long);
                        i.putExtra("drop_lat", drop_lat);
                        i.putExtra("drop_long", drop_long);
                        i.putExtra("drop", drop);
                        i.putExtra("pickup", pickup);
                        i.putExtra("ride_type", "Point to Point");
                        i.putExtra("cab_type", cabtype);
                        i.putExtra("date_time", date_time);
                        startActivity(i);
                    }
                }

            });
            al.show();
        } else if (cabtype.equals(null)) {
            Toast.makeText(getApplicationContext(), "Please select service type", Toast.LENGTH_SHORT).show();

        }
    }


    private void alertForDate() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.date_time);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        tvdate = (TextView) dialog.findViewById(R.id.tvdate);
        tvtime = (TextView) dialog.findViewById(R.id.tvtime);
        ok = (Button) dialog.findViewById(R.id.button);

        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        String a = "";
        String b = "";
        if (day < 10) {
            a = "0";
        }
        if (month + 1 < 10) {
            b = "0";
        }
        datestr = new StringBuilder().append(a).append(day).append("-").append(b).append(month + 1).append("-").append(year).append("");
        tvdate.setText(datestr);
        tvdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });
        tvtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String am_pm = "AM";
                        String mm_precede = "";
                        String hh_precede = "";
                        if (hourOfDay >= 12) {
                            am_pm = "PM";
                            if (hourOfDay >= 13 && hourOfDay < 24) {
                                hourOfDay -= 12;
                            } else {
                                hourOfDay = 12;
                            }
                        } else if (hourOfDay == 0) {
                            hourOfDay = 12;
                        }
                        if (minute < 10) {
                            mm_precede = "0";
                        }
                        if (hourOfDay < 10) {
                            hh_precede = "0";
                        }
                        timestr = hh_precede + hourOfDay + ":" + mm_precede + minute + ":00 " + am_pm;
                        tvtime.setText(timestr);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        });
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timestr.equals("") || timestr.equals("null")) {

                    Toast.makeText(MainActivity.this, "Please select time", Toast.LENGTH_SHORT).show();
                } else {
                    ridelater(String.valueOf(datestr) + " " + timestr);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setAttributes(lp);
    }

    public void initAuto() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);
        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);

        mAutocompleteTextView2 = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView2);
        mAutocompleteTextView2.setThreshold(3);
        mAutocompleteTextView2.setOnItemClickListener(mAutocompleteClickListener2);
        mPlaceArrayAdapter2 = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView2.setAdapter(mPlaceArrayAdapter2);


//        String pic_addr=mAutocompleteTextView.getText().toString();
//
//        convertAddress(pic_addr);

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);

            String a = String.valueOf(item.description);
            convertAddress(a);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {

            }
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListener2
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter2.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);

            String a = String.valueOf(item.description);
            convertAddress2(a);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback2);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
            try {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {

            }
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback2
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();

        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

        mPlaceArrayAdapter2.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

//        Toast.makeText(this,
//                "Google Places API connection failed with error code:" +
//                        connectionResult.getErrorCode(),
//                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
        mPlaceArrayAdapter2.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    public void convertAddress(String pic_addr) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + pic_addr + "&sensor=false";
        url = url.replaceAll(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        showJSONAddress(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");

                            double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            pic_lat = String.valueOf(lat);
                            pic_long = String.valueOf(lng);
                            user_pic = new LatLng(lat, lng);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        String message = null;
                        if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                            final Dialog dialog = new Dialog(MainActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.popup_no_internet);

                            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
//                                text.setText(msg);

                            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else {
                            message = "No Data Found";
                        }
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void convertAddress2(String drop_addr) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + drop_addr + "&sensor=false&key=AIzaSyDrJlYmi86il9sTTZOrXTk8uGD5jkQr2LY";
        url = url.replaceAll(" ", "%20");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        showJSONAddress(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lng");

                            double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                                    .getJSONObject("geometry").getJSONObject("location")
                                    .getDouble("lat");
                            drop_lat = String.valueOf(lat);
                            drop_long = String.valueOf(lng);
                            user_drop = new LatLng(lat, lng);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = null;
                        if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                            final Dialog dialog = new Dialog(MainActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.popup_no_internet);

                            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
//                                text.setText(msg);

                            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else {
                            message = "No Data Found";
                        }
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, from_dateListener, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener from_dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            datestr = new StringBuilder().append(day).append("-").append(month + 1).append("-").append(year).append("");
            tvdate.setText(datestr);
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {

//            Toast.makeText(MainActivity.this, "You have no offer", Toast.LENGTH_SHORT).show();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        } else if (id == R.id.account) {
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        } else if (id == R.id.myrides) {
            Intent i = new Intent(getApplicationContext(), MyRidesActivity.class);
            startActivity(i);
        } else if (id == R.id.track) {
            Intent i = new Intent(getApplicationContext(), TrackOthersActivity.class);
            startActivity(i);
        } else if (id == R.id.ratecard) {
            Intent i = new Intent(getApplicationContext(), RateCardActivity.class);
            startActivity(i);
        } else if (id == R.id.support) {
            Intent i = new Intent(getApplicationContext(), SupportActivity.class);
            startActivity(i);
        } else if (id == R.id.share) {
            shareApp();
        }
        else if (id == R.id.g_sttting) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }

        else if (id == R.id.logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        if (map != null) {
            setUpMap();
//            setUpMap2();

        }

    }


    private void setUpMap2() {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        googleMap.setMyLocationEnabled(true);
//        googleMap.setTrafficEnabled(true);
//        googleMap.setIndoorEnabled(true);
//        googleMap.setBuildingsEnabled(true);
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user_drop, 15.0f));
//        currntLoc(pic_lat,pic_long);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.addMarker(new MarkerOptions()
                .position(user_drop).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Drop"));
    }

    public void setUpMap() {

        // googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
//        googleMap.setMyLocationEnabled(true);
//        googleMap.setTrafficEnabled(true);
//        googleMap.setIndoorEnabled(true);
//        googleMap.setBuildingsEnabled(true);
//        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(user_pic, 15.0f));
//        currntLoc(pic_lat,pic_long);
        googleMap.addMarker(new MarkerOptions()
                .position(user_pic).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Pickup"));
    }

    public void alertForGPS() {
        AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
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

    private void getAddress(String s) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + s + "&key=AIzaSyDrJlYmi86il9sTTZOrXTk8uGD5jkQr2LY";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        showJSONAddress(response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray result = jsonObject.getJSONArray("results");
                            JSONObject eve = result.getJSONObject(0);
                            String strAddress = eve.getString("formatted_address");
                            picaddr= strAddress.replaceAll("Level 2,","");
                            mAutocompleteTextView.setText(picaddr);
//                            Toast.makeText(MainActivity.this, picaddr, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = null;
                        if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError || error instanceof TimeoutError) {
                            message = "Cannot connect to Internet...Please check your connection!";
                            final Dialog dialog = new Dialog(MainActivity.this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setCancelable(false);
                            dialog.setContentView(R.layout.popup_no_internet);

                            TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
//                                text.setText(msg);

                            Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
                            dialogButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });

                            dialog.show();
                        } else if (error instanceof ParseError) {
                            message = "Parsing error! Please try again after some time!!";
                        } else {
                            message = "No Data Found";
                        }
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                return map;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        // sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg) + getPackageName());

        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    public void logout() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        } else {
            session.setLoggedin(false);
            finish();
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        }
    }

    private class CityListAdapter extends BaseAdapter {
        private int flags[];
        String[] cityList;
        private LayoutInflater inflater;

        CityListAdapter(Context applicationContext, String[] cityList) {
            this.flags = flags;
            this.cityList = cityList;
            inflater = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return cityList.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            view = inflater.inflate(R.layout.cell_city_select, viewGroup, false);

            TextView txtCityName = (TextView) view.findViewById(R.id.txtCityName);
            txtCityName.setText(cityList[i]);
            return view;
        }
    }
}
