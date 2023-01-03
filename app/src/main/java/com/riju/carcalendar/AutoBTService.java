package com.riju.carcalendar;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AutoBTService extends Service {

    CarDataSettings settings;
    SharedPreferences shrp;
    ScheduledExecutorService schedule_ex;

    public AutoBTService() {
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        shrp = getSharedPreferences("CarCalendarSettings", MODE_PRIVATE);
        settings = LoadCarDataJson();
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        //BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
//        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//        this.registerReceiver(broadcastReceiver, filter);
//        Log.i("BTReceive", "mutasd mar");

    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        BluetoothDevice device;

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(AutoBTService.this, "onReceive lefutott", Toast.LENGTH_SHORT).show();
            //Log.i("BTReceive", "onReceive lefutott");
            String action = intent.getAction();
            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
            {
                switch(state)
                {
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(AutoBTService.this, "bt bekapcsolva", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(AutoBTService.this, "bt kikapcsolva", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
            {
                Toast.makeText(AutoBTService.this, "szétkapcsolva: " + device.getName(), Toast.LENGTH_SHORT).show();
                AddEndTime();
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
            {
                Toast.makeText(AutoBTService.this, "szétkapcsolva: " + device.getName(),       Toast.LENGTH_SHORT).show();
                AddEndTime();
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