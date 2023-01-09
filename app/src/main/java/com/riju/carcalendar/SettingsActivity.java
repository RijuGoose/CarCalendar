package com.riju.carcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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

        settings = LoadCarDataJson();

        calname = (TextView) this.findViewById(R.id.calendarname);
        btdevname = (TextView) this.findViewById(R.id.btdevicename);

        SetTexts(settings);

//        ListMyCalendars();
        Log.d("BTReceiver", "calendarID: " + getCalendarId());
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
        Toast.makeText(this, "Beállítások elmentve", Toast.LENGTH_SHORT).show();
        finish();
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
//        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ? AND "
//                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? AND "
//                + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ? ";

        String selection = CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ? ";
        // use the same values as above:
        //String[] selArgs = new String[] { calendarName, CalendarContract.ACCOUNT_TYPE_LOCAL  };
        //String[] selArgs = new String[] { "oszvaldgergo20@gmail.com", "com.google", "Jövőbeli események" };

        String calendarname = settings.getCalendarName();

        //String[] selArgs = new String[]{"oszvaldgergo20@gmail.com", "com.google", calendarname};
        String[] selArgs = new String[]{calendarname};
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