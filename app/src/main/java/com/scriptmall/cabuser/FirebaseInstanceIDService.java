package com.scriptmall.cabuser;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;

/**
 * Created by Adminx on 1/30/2017.
 */

public class FirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String reg_token = "REG_TOKEN";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(reg_token,token);
//        Toast.makeText(getApplicationContext(), "token="+token, Toast.LENGTH_SHORT).show();
//        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("fcm_token", token);
//        editor.commit();

        registerToken(token);
    }

    private void registerToken(final String token) {
        SharedPrefManager.getInstance(getApplicationContext()).saveDeviceToken(token);
//        String url="http://massmailscript.com/demo/taxibooking_app/restapi/driver/token.php";
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
////                         showJSON(response);
////                        Toast.makeText(getApplication(), response, Toast.LENGTH_SHORT).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        String message = null;
//                        if (error instanceof NetworkError || error instanceof AuthFailureError || error instanceof NoConnectionError || error instanceof TimeoutError) {
//                            message = "Cannot connect to Internet...Please check your connection!";
//                        } else if (error instanceof ParseError) {
//                            message = "Parsing error! Please try again after some time!!";
//                        }else {
//                            message="No data found";
//                        }
//
//                    }
//                }){
//            @Override
//            protected Map<String,String> getParams() throws AuthFailureError {
//                HashMap<String,String> params = new HashMap<String, String>();
//                params.put("token",token);
//                return params;
//            }
//        };
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
    }


}
