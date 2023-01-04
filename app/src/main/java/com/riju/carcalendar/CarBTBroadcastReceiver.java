package com.riju.carcalendar;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CarBTBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "CarBTBroadcastReceiver";
    BluetoothDevice device;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//        final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
//        if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED))
//        {
//            Toast.makeText(context.getApplicationContext(), "státusz változott", Toast.LENGTH_SHORT).show();
//
//            switch(state)
//            {
//                case BluetoothAdapter.STATE_ON:
//                    Toast.makeText(context.getApplicationContext(), "bt bekapcsolva", Toast.LENGTH_SHORT).show();
//                    break;
//                case BluetoothAdapter.STATE_OFF:
//                    Toast.makeText(context.getApplicationContext(), "bt kikapcsolva", Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }
        Intent notif = new Intent();
        notif.setAction("com.riju.carcalendar.CARBT_CONNECTION");
        if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action))
        {
            Log.i("BTconnection", "összekapcsolva: " + device.getName());
            //Toast.makeText(context.getApplicationContext(), "összekapcsolva: " + device.getName(), Toast.LENGTH_SHORT).show();
            notif.putExtra("status", "Connected");
            notif.putExtra("devicename", device.getName());
            context.sendBroadcast(notif);
            //AddStartTime();
        }
        else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action))
        {
            Log.i("BTconnection", "szétkapcsolva: " + device.getName());
            //Toast.makeText(context.getApplicationContext(), "szétkapcsolva: " + device.getName(), Toast.LENGTH_SHORT).show();
            notif.putExtra("status", "Disconnected");
            notif.putExtra("devicename", device.getName());
            context.sendBroadcast(notif);
            //AddEndTime();
        }
    }
}