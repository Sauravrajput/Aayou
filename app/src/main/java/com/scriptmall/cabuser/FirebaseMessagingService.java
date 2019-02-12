package com.scriptmall.cabuser;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Adminx on 1/30/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

//        if(remoteMessage.getData().get("type").equals("wishes"))
//        {
            showWishesNotification(remoteMessage.getData().get("message"));
//        }
    }

    private void showWishesNotification(String message) {

        SharedPreferences preferences = getSharedPreferences(Activity.class.getSimpleName(), Context.MODE_PRIVATE);
        int notification_num = preferences.getInt("notification_num",0);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent i = new Intent(getApplicationContext(),MyRidesActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setSound(alarmSound)
                .setSmallIcon(R.drawable.noti)
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.noti))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//        {
////            builder.setColor(context.getResources().getColor(R.color.red));
//            builder.setSmallIcon(R.drawable.noti);
//            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.noti));
//        }

        NotificationManager manage = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manage.notify(notification_num,builder.build());
//        NotificationManagerCompat manage = (NotificationManagerCompat) getSystemService(NOTIFICATION_SERVICE);
//        manage.notify(notification_num,builder.build());

        SharedPreferences.Editor editor = preferences.edit();
        notification_num++;
        editor.putInt("notification_num",notification_num);
        editor.commit();
    }




}
