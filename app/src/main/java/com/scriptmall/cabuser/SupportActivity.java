package com.scriptmall.cabuser;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupportActivity extends AppCompatActivity {

    private List<OlaClone> catList = new ArrayList<>();
    private RecyclerView recyclerView;
    SupportAdapter mAdapter;
    String catid, catname, catimg;
    ProgressDialog loading;
    ArrayList<Integer> alImage;
    AutoCompleteTextView autosearch;
    private ArrayList<String> searchList;
    List<String> list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.setLayoutManager(new GridLayoutManager(this, getGridSpanCount(this)));

        searchList = new ArrayList<String>();

        autosearch = (AutoCompleteTextView) findViewById(R.id.autosearch);
        autosearch.setThreshold(1);

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            Toast.makeText(getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
        } else {
            getData();
        }


        mAdapter = new SupportAdapter(catList);
        recyclerView.setAdapter(mAdapter);

//        prepairData();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                OlaClone hotel = catList.get(position);
                Intent i = new Intent(getApplicationContext(), SupportAnsActivity.class);
                i.putExtra("catList", (Serializable) catList);
                i.putExtra("pos", String.valueOf(position));
                startActivity(i);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

//        List<String> l5 = new ArrayList<String>();
//        l5.add("Faq's"); l5.add("Cab options"); l5.add("Outstaion");l5.add("Rental");l5.add("Payment options");l5.add("Ola select");
//        l5.add("Booking a cab");l5.add("My account");l5.add("Using this app");l5.add("Safty");l5.add("Other services");
//        autosearch.setAdapter((new ArrayAdapter<String>(SupportActivity.this, android.R.layout.simple_list_item_1, l5)));
//
        autosearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                autosearch.setText(((TextView) view).getText());

                String text = autosearch.getText().toString().trim();
                int pos = 0;
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).contains(text))
                        pos = i;
                }
                OlaClone hotel = catList.get(position);
                Intent i = new Intent(getApplicationContext(), SupportAnsActivity.class);
                i.putExtra("catList", (Serializable) catList);
                i.putExtra("pos", String.valueOf(pos));
                startActivity(i);

            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Support");

    }

    public void getData() {
        // Toast.makeText(getApplicationContext(),purpose,Toast.LENGTH_LONG).show();
        loading = ProgressDialog.show(SupportActivity.this, "Please wait...", "Fetching...", false, false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.FAQ_URL,
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
                        Toast.makeText(SupportActivity.this, message, Toast.LENGTH_LONG).show();
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
            int success = jsonObject1.getInt("success");
            if (success == 1) {
                JSONArray data = jsonObject1.getJSONArray("data");
                catList.clear();
                for (int i = 0; i < data.length(); i++) {

                    JSONObject eve = data.getJSONObject(i);
                    String faq_id = eve.getString("faq_id");
                    String ques = eve.getString("ques");
                    String ans = eve.getString("ans");

                    list.add(ques);
                    autosearch.setAdapter((new ArrayAdapter<String>(SupportActivity.this, R.layout.simple_spinner_item, list)));

//                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                    OlaClone ed = new OlaClone();
                    ed.setFaq_id(faq_id);
                    ed.setQues(ques);
                    ed.setAns(ans);
                    catList.add(ed);
//                    Toast.makeText(getApplicationContext(), (CharSequence) eventsList,Toast.LENGTH_LONG).show();

                }
            }

            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
//            recyclerView.setLayoutManager(new GridLayoutManager(this, MainActivity.getGridSpanCount(this)));
            mAdapter = new SupportAdapter(this, recyclerView, catList);
            recyclerView.setAdapter(mAdapter);
//
        } catch (JSONException e) {
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
        switch (item.getItemId()) {
            case android.R.id.home:
//                super.onBackPressed();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return true;
    }


}
