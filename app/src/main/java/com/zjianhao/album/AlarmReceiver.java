package com.zjianhao.album;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.zjianhao.holder.SettingHolder;

public class AlarmReceiver extends BroadcastReceiver {
    public static SettingHolder mySetting;
    @Override
    public void onReceive(Context context, Intent intent){
        //Toast.makeText(context,"정해진 시간에 백그라운드가 돌아갑니다.",Toast.LENGTH_SHORT).show();
        mySetting = settingActivity.mySetting;
       Intent serviceIntent = new Intent(context,FileUploaderService.class);
        context.startService(serviceIntent);
        /*
        Log.d("alarmrceiver","this is reserved function");
        NotificationCompat.Builder mBuilder = new android.support.v7.app.NotificationCompat.Builder(context)
                .setContentTitle("Smart Album")
                .setContentText("Background executing...")
                .setDefaults(Notification.DEFAULT_VIBRATE);
        */
    }
}
