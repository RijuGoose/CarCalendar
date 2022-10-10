package com.riju.carcalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Formatter;
import java.util.Locale;

public class SpeedoTestActivity extends AppCompatActivity implements LocationListener {

    TextView speedview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedo_test);

        speedview = findViewById(R.id.speeview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            doStuff();
        }
        this.updateSpeed(null);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //Toast.makeText(this, "es ez meghivodik?", Toast.LENGTH_SHORT).show();
        if(location != null)
        {
            //Toast.makeText(this, "Ez meghivodik?", Toast.LENGTH_SHORT).show();
            CLocation myLocation = new CLocation(location);
            this.updateSpeed(myLocation);
        }
    }

    /*@Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        LocationListener.super.onStatusChanged(provider, status, extras);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }*/

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStuff();
            } else {
                finish();
            }
    }

    @SuppressLint("MissingPermission")
    private void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);
        }
        Toast.makeText(this, "Waiting for GPS connection...", Toast.LENGTH_SHORT).show();
    }

    private void updateSpeed(CLocation location){
        float nCurrentSpeed = 0, nCurrentAccuracy = 0;
        double nCurrentAltitude = 0;
        if(location != null){
            nCurrentSpeed = location.getSpeed();
            nCurrentAccuracy = location.getAccuracy();
            nCurrentAltitude = location.getAltitude();
            //Toast.makeText(this, "sebesség lekérése", Toast.LENGTH_SHORT).show();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        //strCurrentSpeed = strCurrentSpeed.replace(" ", "0");

        strCurrentSpeed = "sebesség: " + nCurrentSpeed + " " +
                        "pontosság: " + nCurrentAccuracy +" " +
                        "valami: " + nCurrentAltitude + " ";

        speedview.setText(strCurrentSpeed);
    }
}