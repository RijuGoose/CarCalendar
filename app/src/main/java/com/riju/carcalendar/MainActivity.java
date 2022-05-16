package com.riju.carcalendar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.accounts.Account;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String calendarName = "Car Calendar";
    String calendarDisplayName = "Car Calendar DP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//
//        Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//
//        startActivityForResult(signInIntent, 1);

        int permCalendar = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR);

        if(permCalendar != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, 1);

        }
        if(permCalendar == PackageManager.PERMISSION_GRANTED) {
            long calID = getCalendarId();

            if (calID == -1) {
                Toast.makeText(MainActivity.this, "Naptár nem található", Toast.LENGTH_LONG).show();
                return;
            }

            long startMillis = 0;
            long endMillis = 0;
            Calendar time = Calendar.getInstance();
            time.setTime(new Date());
            startMillis = time.getTimeInMillis();
            time.set(Calendar.HOUR_OF_DAY, time.get(Calendar.HOUR_OF_DAY) + 1);
            endMillis = time.getTimeInMillis();

            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startMillis);
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "Event Example");
            values.put(CalendarContract.Events.DESCRIPTION, "Description to the example event");
            values.put(CalendarContract.Events.CALENDAR_ID, calID);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
            values.put(CalendarContract.Events.EVENT_LOCATION, "New York City");

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);


//_-------------------
            String[] projection = new String[] { CalendarContract.Calendars._ID, CalendarContract.Calendars.ACCOUNT_NAME, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME };
            String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ? AND "
                    + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? ";
            // use the same values as above:
            //String[] selArgs = new String[] { calendarName, CalendarContract.ACCOUNT_TYPE_LOCAL  };
            String[] selArgs = new String[] { "oszvaldgergo20@gmail.com", "com.google"  };

            Cursor cursor = getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, projection, selection,
                    selArgs, null);

            String out = "";
            while (cursor.moveToNext()) {

                out += cursor.getLong(0) + ", " + cursor.getString(1) + ", "  + cursor.getString(2) + "\r\n";
            }

            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage(out);
            dlgAlert.setTitle("App Title");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }

//        URL url = null;
//        try {
//            url = new URL("https://www.googleapis.com/calendar/v3/calendars/[CALENDARID]/events?key=[YOUR_API_KEY] HTTP/1.1");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        HttpURLConnection con = null;
//        try {
//            con = (HttpURLConnection)url.openConnection();
//            con.setRequestMethod("POST");
//
//            con.setRequestProperty("Content-Type", "application/json; utf-8");
//            con.setRequestProperty("Accept", "application/json");
//
//            con.setDoOutput(true);
//
//            String jsonInputString = "{key: value, key: value";
//
//            try(OutputStream os = con.getOutputStream()) {
//                byte[] input = jsonInputString.getBytes("utf-8");
//                os.write(input, 0, input.length);
//            }
//
//            try(BufferedReader br = new BufferedReader(
//                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
//                StringBuilder response = new StringBuilder();
//                String responseLine = null;
//                while ((responseLine = br.readLine()) != null) {
//                    response.append(responseLine.trim());
//                }
//                System.out.println(response.toString());
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private long getCalendarId() {
        String[] projection = new String[] { CalendarContract.Calendars._ID };
        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ? AND "
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? AND "
                + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ? ";
        // use the same values as above:
        //String[] selArgs = new String[] { calendarName, CalendarContract.ACCOUNT_TYPE_LOCAL  };
        String[] selArgs = new String[] { "oszvaldgergo20@gmail.com", "com.google", "Jövőbeli esegfbmények" };
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Hiba", "signInResult:failed code=" + e.getStatusCode());
            //updateUI(null);
        }
    }
}