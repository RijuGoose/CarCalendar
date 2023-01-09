package com.riju.carcalendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.api.services.calendar.model.Setting;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends Activity {

    CarDataSettings settings = new CarDataSettings();
    SharedPreferences shrp;

    TextView starttime;
    TextView endtime;

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        settings = LoadCarDataJson();
        SetTexts(settings);
        super.onPostResume();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shrp = getSharedPreferences("CarCalendarSettings", MODE_PRIVATE);
        starttime = (TextView) this.findViewById(R.id.starttime);
        endtime = (TextView) this.findViewById(R.id.endtime);

        createNotificationChannel();

        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                    Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.VIBRATE
                },
                1);

//        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, 1);
//        }
////        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CALENDAR}, 1);
////        }
//        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("Permissions", "storage checked");
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
//        }
////        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
////        }
////        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
////            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
////        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
////            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("Permissions", "BT checked");
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH}, 3);
//        }
//        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("Permissions", "BT connect checked");
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 4);
//        }
//        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
//            Log.d("Permissions", "vibrate checked");
//            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.VIBRATE}, 5);
//        }
        //ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, 5);


        if(!shrp.contains("CarDataSettings"))
        {
            initSettings(settings);
        }

        settings = LoadCarDataJson();
        SetTexts(settings);
    }

    private void initSettings(CarDataSettings set) {
        set.setCalendarName("");
        set.setStartMillis(0);
        set.setEndMillis(0);
        set.setAccountType("com.google");
        SaveCarDataJson(set);
    }

//    private long getCalendarId() {
//        String[] projection = new String[]{CalendarContract.Calendars._ID};
//        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ? AND "
//                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? AND "
//                + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ? ";
//        // use the same values as above:
//        //String[] selArgs = new String[] { calendarName, CalendarContract.ACCOUNT_TYPE_LOCAL  };
//        //String[] selArgs = new String[] { "oszvaldgergo20@gmail.com", "com.google", "Jövőbeli események" };
//
//        String calendarname = calname.getText().toString();
//
//        String[] selArgs = new String[]{"oszvaldgergo20@gmail.com", "com.google", calendarname};
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
//            return -1;
//        }
//
//        Cursor cursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, selection,
//                selArgs, null);
//        if (cursor.moveToFirst()) {
//            return cursor.getLong(0);
//        }
//        return -1;
//    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Autó érzékelő";
            String description = "Ha a felhasználó befejezte a vezetést, kap egy értesítést róla.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("btnotiend", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private CarDataSettings LoadCarDataJson() {
        Gson gson = new Gson();
        String json = shrp.getString("CarDataSettings", "");
        return gson.fromJson(json, CarDataSettings.class);
    }

    private void SetTexts(CarDataSettings set)
    {
        //calname.setText(set.getCalendarName());
        //btdevname.setText(set.getBtDeviceName());

        Calendar time = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        time.setTimeInMillis(set.getStartMillis());
        String datestring = sdf.format(time.getTime());
        starttime.setText(datestring);

        time.setTimeInMillis(set.getEndMillis());
        datestring = sdf.format(time.getTime());
        endtime.setText(datestring);
    }

    private void SaveCarDataJson(CarDataSettings set) {
        SharedPreferences.Editor editor = shrp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(set);
        editor.putString("CarDataSettings", json);
        editor.apply();
    }

//    public void AddEventToCalendar(View v) {
//        int permCalendar = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR);
//
//        if (permCalendar == PackageManager.PERMISSION_GRANTED) {
//            long calID = getCalendarId();
//
//            if (calID == -1) {
//                Toast.makeText(MainActivity.this, "Naptár nem található", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            ContentResolver cr = getContentResolver();
//            ContentValues values = new ContentValues();
//            values.put(CalendarContract.Events.DTSTART, settings.getStartMillis());
//            values.put(CalendarContract.Events.DTEND, settings.getEndMillis());
//            values.put(CalendarContract.Events.TITLE, "Autó használat");
//            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().toString());
//            values.put(CalendarContract.Events.CALENDAR_ID, calID);
//
//            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
//        }
//    }


//    public void ListMyCalendars() {
//        String[] projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME};
//        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ? AND "
//                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? ";
//        // use the same values as above:
//        //String[] selArgs = new String[] { calendarName, CalendarContract.ACCOUNT_TYPE_LOCAL  };
//        String[] selArgs = new String[]{"oszvaldgergo20@gmail.com", "com.google"};
//
//        Cursor cursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, selection,
//                selArgs, null);
//
//        String out = "";
//        while (cursor.moveToNext()) {
//
//            out += cursor.getLong(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2) + "\r\n";
//        }
//
//        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
//        Gson gson = new Gson();
//        dlgAlert.setMessage(gson.toJson(settings));
//        dlgAlert.setTitle("App Title");
//        dlgAlert.setPositiveButton("OK", null);
//        dlgAlert.setCancelable(true);
//        dlgAlert.create().show();
//    }


    public void GoToSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}