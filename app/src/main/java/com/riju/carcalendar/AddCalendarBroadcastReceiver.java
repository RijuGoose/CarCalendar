package com.riju.carcalendar;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddCalendarBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "AddCalendarBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        long start = intent.getLongExtra("starttime", 0);
        long end = intent.getLongExtra("endtime", 0);
        String calendarname = intent.getStringExtra("calendarname");
        AddEventToCalendar(context, calendarname, start, end);


        Calendar time = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        time.setTimeInMillis(start);
        String datestring = sdf.format(time.getTime());
        String datestringstart = datestring;

        time.setTimeInMillis(end);
        datestring = sdf.format(time.getTime());
        String datestringend = datestring;

        Log.d("BTReceiver", "AddCalendar meghívódott: " + datestringstart + " - " + datestringend + "(" + end + ")");

        Toast.makeText(context, "Bejegyzés hozzáadva: " + calendarname, Toast.LENGTH_LONG).show();


    }

    public void AddEventToCalendar(Context context, String calendarname, long starttime, long endtime) {
        int permCalendar = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR);

        if (permCalendar == PackageManager.PERMISSION_GRANTED) {
            long calID = getCalendarId(context, calendarname);

            if (calID == -1) {
                Toast.makeText(context, "A megadott naptár nem található: " + calendarname, Toast.LENGTH_LONG).show();
                return;
            }

            ContentResolver cr = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, starttime);
            values.put(CalendarContract.Events.DTEND, endtime);
            values.put(CalendarContract.Events.TITLE, "Autó használat");
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().toString());
            values.put(CalendarContract.Events.CALENDAR_ID, calID);

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        }
    }

    private long getCalendarId(Context context, String calendarname) {
        String[] projection = new String[]{CalendarContract.Calendars._ID};
//        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ? AND "
//                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? AND "
//                + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ? ";

        String selection = CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ? ";

//        String[] selArgs = new String[]{"oszvaldgergo20@gmail.com", "com.google", calendarname};
        String[] selArgs = new String[]{calendarname};
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }

        Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, selection,
                selArgs, null);
        if (cursor.moveToFirst()) {
            return cursor.getLong(0);
        }
        return -1;
    }
}
