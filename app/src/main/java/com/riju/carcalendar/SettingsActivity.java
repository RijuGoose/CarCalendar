package com.riju.carcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    CarDataSettings settings = new CarDataSettings();
    SharedPreferences shrp;

    TextView calname;
    TextView btdevname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        shrp = getSharedPreferences("CarCalendarSettings", MODE_PRIVATE);

        createNotificationChannel();

        ActivityCompat.requestPermissions(SettingsActivity.this, new String[]
                        {
                                Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.VIBRATE
                        },
                1);

        if(!shrp.contains("CarDataSettings"))
        {
            initSettings(settings);
        }

        settings = LoadCarDataJson();

        calname = (TextView) this.findViewById(R.id.calendarname);
        btdevname = (TextView) this.findViewById(R.id.btdevicename);

        SetTexts(settings);

//        ListMyCalendars();
    }


    private void initSettings(CarDataSettings set) {
        set.setCalendarName("");
        set.setStartMillis(0);
        set.setEndMillis(0);
        set.setAccountType("com.google");
        SaveCarDataJson(set);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Driving";
            String description = "Sends a notification when the driving ends";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("btnotiend", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void SetTexts(CarDataSettings set)
    {
        calname.setText(set.getCalendarName());
        btdevname.setText(set.getBtDeviceName());
    }

    public void SaveConfig(View view) {
        settings.setCalendarName(calname.getText().toString());
        settings.setBtDeviceName(btdevname.getText().toString());

        SaveCarDataJson(settings);
        Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show();
    }

    private CarDataSettings LoadCarDataJson() {
        Gson gson = new Gson();
        String json = shrp.getString("CarDataSettings", "");
        return gson.fromJson(json, CarDataSettings.class);
    }

    private void SaveCarDataJson(CarDataSettings set) {
        SharedPreferences.Editor editor = shrp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(set);
        editor.putString("CarDataSettings", json);
        editor.apply();
    }

    private long getCalendarId() {
        String[] projection = new String[]{CalendarContract.Calendars._ID};

        String selection = CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ? ";
        // use the same values as above:

        String calendarname = settings.getCalendarName();

        //String[] selArgs = new String[]{"oszvaldgergo20@gmail.com", "com.google", calendarname};
        String[] selArgs = new String[]{ calendarname };
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }

        Cursor cursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, selection,
                selArgs, null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return -1;
    }

    public void ListMyCalendars() {
        String[] projection = new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME};
        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ? AND "
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? ";
        // use the same values as above:
        //String[] selArgs = new String[] { calendarName, CalendarContract.ACCOUNT_TYPE_LOCAL  };
        String[] selArgs = new String[]{"oszvaldgergo20@gmail.com", "com.google"};

        Cursor cursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, selection,
                selArgs, null);

        String out = "";
        while (cursor.moveToNext()) {

            out += cursor.getLong(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2) + "\r\n";
        }

        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
        dlgAlert.setMessage(out);
        dlgAlert.setTitle("App Title");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }
}