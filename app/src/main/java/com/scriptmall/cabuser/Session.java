package com.scriptmall.cabuser;
import android.content.Context;
import android.content.SharedPreferences;


public class Session {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Context ctx;
    private static final String FIRST_LAUNCH = "_.FIRST_LAUNCH";

    public Session(Context ctx)
    {
        this.ctx=ctx;
        preferences=ctx.getSharedPreferences("cabbook", Context.MODE_PRIVATE);
        editor = preferences.edit();

    }

    public void setFirstLaunch(boolean flag) {
        preferences.edit().putBoolean(FIRST_LAUNCH, flag).apply();
    }
    public boolean isFirstLaunch() {
        return preferences.getBoolean(FIRST_LAUNCH, true);
    }

    public void setLoggedin(boolean loggedin)
    {
        editor.putBoolean("loggedInmode",loggedin);
        if(loggedin == false){
            editor.clear();
        }else if(loggedin == true)
       {
//
//            editor.putString(Config.REGNO_SHARED_PREF, register_no);
//            editor.putString("register_no",Login.register_no);
//            editor.putString("uid",LoginActivity.uid[0]);
//            editor.putString("u_name",LoginActivity.u_name[0]);
//            editor.putString("u_gmail",LoginActivity.emai);
        }


        editor.commit();
    }

    public boolean loggedin()
    {
        return preferences.getBoolean("loggedInmode",false);
    }

}
