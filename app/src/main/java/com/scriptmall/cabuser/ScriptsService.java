package com.scriptmall.cabuser;

/**
 * Created by scriptmall on 3/14/2018.
 */

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class ScriptsService extends Service {

    private static final String TAG = ScriptsService.class.getSimpleName();
    String urlstring;
    @Override
    public IBinder onBind(Intent intent) {return null;}
    @Override
    public void onCreate() {
        super.onCreate();
        requestLocationUpdates();
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };
    private void requestLocationUpdates() {
//        loading = ProgressDialog.show(TrackerService.this,"Please wait...","Sending...",false,true);
        String url=getString(R.string.urlstring);
        byte[] data = Base64.decode(url, Base64.DEFAULT);
        try {
             urlstring = new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlstring,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        loading.dismiss();
                        showJsonStatus(response);
//                        Toast.makeText(TrackerService.this,response, Toast.LENGTH_LONG ).show();
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
//                        loading.dismiss();
                        Toast.makeText(ScriptsService.this,message, Toast.LENGTH_LONG ).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<String, String>();
                params.put("domainlink",Config.doamin_name);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJsonStatus(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray(Config.JSON_ARRAY);
            JSONObject eve = result.getJSONObject(0);

            String resp=eve.getString("Result");
            String value="";
            if(resp.equals("0")){  value="Your License Expired";  }
            SharedPreferences sharedPreferences = ScriptsService.this.getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lstatus", resp);
            editor.putString("lvalue", value);
            editor.commit();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
