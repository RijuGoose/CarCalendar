package com.riju.carcalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AutoBTService extends Service {

    CarDataSettings settings;
    SharedPreferences shrp;

    public AutoBTService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        shrp = getSharedPreferences("CarCalendarSettings", MODE_PRIVATE);
        settings = LoadCarDataJson();

        //IntentFilter filter = new IntentFilter("com.riju.carcalendar.CARBT_CONNECTION");
        //this.registerReceiver(broadcastReceiver, filter);
    }



    @Override
    public void onDestroy() {
        this.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.i("BTReceive", "CARBT fogadása lefutott");
            final String state = intent.getStringExtra("status");
            final String devname = intent.getStringExtra("devicename");

            if(state.equals("Connected") && devname.equals(settings.getBtDeviceName()))
            {
                Toast.makeText(AutoBTService.this, "Autó vezetése elkezdődött", Toast.LENGTH_SHORT).show();
                Log.i("BTReceive", "Autó vezetése elkezdődött");
                AddStartTime();
            }
            else if(state.equals("Disconnected") && devname.equals(settings.getBtDeviceName()))
            {
                Toast.makeText(AutoBTService.this, "Autó vezetése befejeződött", Toast.LENGTH_SHORT).show();
                Log.i("BTReceive", "Autó vezetése befejeződött");
                AddEndTime();

                Intent addcalIntent = new Intent(getApplicationContext(), AddCalendarBroadcastReceiver.class);
                addcalIntent.setAction("com.riju.carcalendar.ADD_TO_CALENDAR");
                addcalIntent.putExtra("starttime", 0);
                addcalIntent.putExtra("endtime", 0);
                addcalIntent.putExtra("calendarname", "");
                PendingIntent notiIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, addcalIntent, PendingIntent.FLAG_IMMUTABLE);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "btnotiend")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentText("Befejezted a vezetést. Ide kattintva hozzá tudod adni a naptárhoz.")
                        .setContentTitle("Befejezett vezetés")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(notiIntent)
                        //.addAction(R.drawable.ic_launcher_background, "Naptárhoz adás", notiIntent)
                        .setAutoCancel(true);
//
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(2, builder.build());
            }
        }
    };

    public void AddStartTime()
    {
        Calendar time = Calendar.getInstance();
        time.setTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String datestring = sdf.format(new Date());

        settings.setStartMillis(time.getTimeInMillis());
        SaveCarDataJson(settings);
    }

    public void AddEndTime() {
        Calendar time = Calendar.getInstance();
        time.setTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String datestring = sdf.format(new Date());

        settings.setEndMillis(time.getTimeInMillis());
        SaveCarDataJson(settings);
    }

    private void SaveCarDataJson(CarDataSettings set) {
        SharedPreferences.Editor editor = shrp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(set);
        editor.putString("CarDataSettings", json);
        editor.apply();
    }

    private CarDataSettings LoadCarDataJson() {
        Gson gson = new Gson();
        String json = shrp.getString("CarDataSettings", "");
        return gson.fromJson(json, CarDataSettings.class);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        schedule_ex = Executors.newScheduledThreadPool(1);

//        Toast.makeText(AutoBTService.this, "Start lefutott", Toast.LENGTH_SHORT).show();
//        schedule_ex.scheduleAtFixedRate(new Runnable(){
//
//            int counter = 0;
//            final Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
//            @Override
//            public void run() {
//                counter++;
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(SpeedoService.this, "speedonoti")
//                        .setSmallIcon(R.drawable.ic_launcher_background)
//                        .setContentText(counter + " másodperc letelt")
//                        .setContentTitle("asd")
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                        .setOnlyAlertOnce(true);
//
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SpeedoService.this);
//                notificationManager.notify(2, builder.build());
//
//                final VibrationEffect vibreff;
//
//                vibreff = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
//                vibrator.cancel();
//                //vibrator.vibrate(vibreff);
//            }
//        }, 3, 3, TimeUnit.SECONDS);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}