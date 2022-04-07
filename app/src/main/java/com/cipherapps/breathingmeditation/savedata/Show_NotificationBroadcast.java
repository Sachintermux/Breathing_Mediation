package com.cipherapps.breathingmeditation.savedata;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.cipherapps.breathingmeditation.R;
import com.cipherapps.breathingmeditation.activitys.MainActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

public class Show_NotificationBroadcast extends BroadcastReceiver {
    private SharePrefDataSave sharePrefDataSave = new SharePrefDataSave();

    @Override
    public void onReceive( Context context, Intent intent ) {

        String data = sharePrefDataSave.getData(context,context.getString(R.string.RemindListTAG),"");
        if(data != "") {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<RemindModels>>() {
            }.getType();
            ArrayList<RemindModels> models = gson.fromJson(data, type);

            for (int i = 0; i < models.size(); i++) {
                String title = models.get(i).getName();
                String shortDesc = models.get(i).getShortDescription();

                Calendar calendar = Calendar.getInstance();
                int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                if(models.get(i).getHour() == hour24hrs && models.get(i).getMinute() == minutes) {
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    MediaPlayer mediaPlayer = MediaPlayer.create(context,alarmSound);
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion( MediaPlayer mediaPlayer ) {
                            mediaPlayer.release();
                        }
                    });
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_edit_notifications)
                                    .setContentTitle(title)
                                    .setContentText(shortDesc)
                                    .setAutoCancel(true)
                            .setSound(alarmSound)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


                    Intent resultIntent = new Intent(context, MainActivity.class);

                    PendingIntent resultPendingIntent =
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    resultIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(resultPendingIntent);
                    String channel_Id = "Hello";
                    NotificationManager mNotifyMgr =
                            (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(channel_Id,"Human Raddable " , NotificationManager.IMPORTANCE_HIGH);
                        mNotifyMgr.createNotificationChannel(channel);
                        mBuilder.setChannelId(channel_Id);
                    }
//                    Intent notificationIntent = new Intent(context, MainActivity.class);
//
//                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//                    PendingIntent intent = PendingIntent.getActivity(context, 0,
//                            notificationIntent, 0);
//
//                    notification.setLatestEventInfo(context, title, message, intent);
//                    notification.flags |= Notification.FLAG_AUTO_CANCEL;
                    int id = sharePrefDataSave.getData(context,"NotificationID_TAG",0);
                     id++;
                    sharePrefDataSave.savedData(context,"NotificationID_TAG",id);
                    mNotifyMgr.notify(id, mBuilder.build());

                }
            }
        }
    }

}