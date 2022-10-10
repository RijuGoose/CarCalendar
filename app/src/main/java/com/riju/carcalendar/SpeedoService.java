package com.riju.carcalendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Formatter;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpeedoService extends Service implements LocationListener {

    ScheduledExecutorService schedule_ex;

    public SpeedoService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            doStuff();
        }
        this.updateSpeed(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        schedule_ex = Executors.newScheduledThreadPool(1);

        Toast.makeText(this, "Start lefutott", Toast.LENGTH_SHORT).show();
        schedule_ex.scheduleAtFixedRate(new Runnable(){

            int counter = 0;
            final Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            @Override
            public void run() {
                counter++;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(SpeedoService.this, "speedonoti")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentText(counter + " másodperc letelt")
                        .setContentTitle("asd")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setOnlyAlertOnce(true);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SpeedoService.this);
                notificationManager.notify(2, builder.build());

                final VibrationEffect vibreff;

                vibreff = VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.cancel();
                vibrator.vibrate(vibreff);
            }
        }, 3, 3, TimeUnit.SECONDS);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(location != null)
        {
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

    @SuppressLint("MissingPermission")
    private void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null)
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500,1, this);
            //Toast.makeText(this, "Waiting anything pls...", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Waiting for GPS connection...", Toast.LENGTH_SHORT).show();
    }

    private void updateSpeed(CLocation location){
        float nCurrentSpeed = 0;
        if(location != null){
            nCurrentSpeed = location.getSpeed();
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "speedonoti")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentText("Sebesség amikor észrevettem: " + nCurrentSpeed + " km/h")
                    .setContentTitle("asd")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(2, builder.build());
    }
}