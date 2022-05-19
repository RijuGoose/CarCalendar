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
import android.os.Environment;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    CarDataSettings settings = new CarDataSettings();
    long startMillis = 0;
    long endMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
//
//        Intent signInIntent = GoogleSignIn.getClient(this, gso).getSignInIntent();
//        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
//
//        startActivityForResult(signInIntent, 1);

        SaveCarDataJson(settings);

        TextView starttime = (TextView) this.findViewById(R.id.starttime);

        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(Long.parseLong(getCarDataJson("starttime")));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String datestring = sdf.format(time.getTime());
        starttime.setText(datestring);
    }

    private long getCalendarId() {
        String[] projection = new String[] { CalendarContract.Calendars._ID };
        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ? AND "
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ? AND "
                + CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + " = ? ";
        // use the same values as above:
        //String[] selArgs = new String[] { calendarName, CalendarContract.ACCOUNT_TYPE_LOCAL  };
        //String[] selArgs = new String[] { "oszvaldgergo20@gmail.com", "com.google", "Jövőbeli események" };

        String calendarname = ((TextView)findViewById(R.id.calendarname)).getText().toString();

        String[] selArgs = new String[] { "oszvaldgergo20@gmail.com", "com.google", calendarname };
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

    public void AddStartTime(View v)
    {
        TextView starttime = (TextView)this.findViewById(R.id.starttime);

        Calendar time = Calendar.getInstance();
        time.setTime(new Date());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String datestring = sdf.format(new Date());
        starttime.setText(datestring);

        startMillis = time.getTimeInMillis();
        settings.setStartMillis(time.getTimeInMillis());

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("starttime", startMillis);

            String userString = jsonObject.toString();
// Define the File Path and its Name
            File file = new File(getFilesDir(), "cardata.json");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(userString);
            bufferedWriter.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String getCarDataJson(String attribute)
    {
        File file = new File(getFilesDir(), "cardata.json");
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();
            while (line != null){
                stringBuilder.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

            String response = stringBuilder.toString();

            JSONObject jsonObject  = new JSONObject(response);
            return jsonObject.get(attribute).toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "";
    }

    private void SaveCarDataJson(CarDataSettings set)
    {

        JSONObject jsonObject = new JSONObject();

        Gson gson = new Gson();
        String json = gson.toJson(set);
        try {
            jsonObject.put("starttime", startMillis);

            String userString = jsonObject.toString();
// Define the File Path and its Name
            File file = new File(getFilesDir(), "cardata.json");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(userString);
            bufferedWriter.close();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    public void AddEndTime(View v)
    {
        TextView endtime = (TextView)this.findViewById(R.id.endtime);
        Calendar time = Calendar.getInstance();
        time.setTime(new Date());

        endMillis = time.getTimeInMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        String datestring = sdf.format(new Date());
        endtime.setText(datestring);
    }

    public void AddEventToCalendar(View v)
    {
        int permCalendar = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CALENDAR);

//                if(permCalendar != PackageManager.PERMISSION_GRANTED)
//                {
//                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, 1);
//
//                }
        if(permCalendar == PackageManager.PERMISSION_GRANTED) {
            long calID = getCalendarId();

            if (calID == -1) {
                Toast.makeText(MainActivity.this, "Naptár nem található", Toast.LENGTH_LONG).show();
                return;
            }


            ContentResolver cr = getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, Long.parseLong(getCarDataJson("starttime")));
            values.put(CalendarContract.Events.DTEND, endMillis);
            values.put(CalendarContract.Events.TITLE, "Autó használat");
            //values.put(CalendarContract.Events.DESCRIPTION, "Description to the example event");
            values.put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().getTimeZone().toString());
            values.put(CalendarContract.Events.CALENDAR_ID, calID);

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        }
    }

    public void ListMyCalendars()
    {
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

    public void SaveConfig(View view)
    {
        ListMyCalendars(); //külön gombra elérhető naptárak megjelenítése ?
        //kéne egy objektum, ami tárolja az összes elmentendő adatot
    }
}