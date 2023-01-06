package com.riju.carcalendar;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CarBTBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CarBTBroadcastReceiver";
    BluetoothDevice device;

    CarDataSettings settings;
    SharedPreferences shrp;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        shrp = context.getSharedPreferences("CarCalendarSettings", Context.MODE_PRIVATE);
        settings = LoadCarDataJson();

        String action = intent.getAction();
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
        {
            Log.i("BTconnection", "összekapcsolva: " + device.getName());
            Toast.makeText(context, "Autó vezetése elkezdődött", Toast.LENGTH_SHORT).show();
            AddStartTime();
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
        {
            Log.i("BTconnection", "szétkapcsolva: " + device.getName());
            Toast.makeText(context, "Autó vezetése befejeződött", Toast.LENGTH_SHORT).show();

            Intent addcalIntent = new Intent(context, AddCalendarBroadcastReceiver.class);
            addcalIntent.setAction("com.riju.carcalendar.ADD_TO_CALENDAR");
            addcalIntent.putExtra("starttime", settings.getStartMillis());
            addcalIntent.putExtra("endtime", settings.getEndMillis());
            addcalIntent.putExtra("calendarname", settings.getCalendarName());
            PendingIntent notiIntent = PendingIntent.getBroadcast(context, 0, addcalIntent, PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "btnotiend")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText("Befejezted a vezetést. Ide kattintva hozzá tudod adni a naptárhoz.")
                    .setContentTitle("Befejezett vezetés")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setOnlyAlertOnce(true)
                    .setContentIntent(notiIntent)
                    //.addAction(R.drawable.ic_launcher_background, "Naptárhoz adás", notiIntent)
                    .setAutoCancel(true);
//
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(2, builder.build());

            AddEndTime();
        }
    }

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
}
