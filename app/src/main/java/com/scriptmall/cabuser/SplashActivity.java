package com.scriptmall.cabuser;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity implements Animation.AnimationListener{

    TextView tv;
    ImageView img;
    Animation xtrans,ytrans;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    Thread splashTread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.activity_splash);

//        tv=(TextView)findViewById(R.id.tv);
        img=(ImageView)findViewById(R.id.img);

        StartAnimations();

//        xtrans = AnimationUtils.loadAnimation(getApplicationContext(),
//                R.anim.translate);
//        ytrans = AnimationUtils.loadAnimation(getApplicationContext(),
//                R.anim.ytrans);
//        xtrans.setAnimationListener(this);
//        ytrans.setAnimationListener(this);
//        tv.startAnimation(ytrans);
//        img.startAnimation(xtrans);

//        animate(img);



    }

    public void animate(ImageView viewHolder) {
        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(this, R.anim.bounce_interpolator);
//        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(ctx, R.anim.anticipate_overshoot_interpolator);
        viewHolder.setAnimation(animAnticipateOvershoot);
    }

    private void StartAnimations() {
//        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
//        anim.reset();
//        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
//        l.clearAnimation();
//        l.startAnimation(anim);

        final Animation animAnticipateOvershoot = AnimationUtils.loadAnimation(this, R.anim.bounce_interpolator);
        img.setAnimation(animAnticipateOvershoot);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    // Splash screen pause time
                    splashTread.sleep(4000);
                } catch (InterruptedException e) {
                    // do nothing
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);


                }

            }
        };
        splashTread.start();


    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }

}
