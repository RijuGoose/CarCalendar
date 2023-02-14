package com.riju.carcalendar;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

public class CarDataSettings {
    private long startMillis;
    private long endMillis;
    private String calendarName;
    private String btDeviceName;
    private String btDeviceMAC;
    private Boolean showNotification;

    public CarDataSettings() {

    }


    public long getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public void setEndMillis(long endMillis) {
        this.endMillis = endMillis;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getBtDeviceName() {
        return btDeviceName;
    }

    public void setBtDeviceName(String btDeviceName) {
        this.btDeviceName = btDeviceName;
    }

    public Boolean getShowNotification() { return showNotification; }

    public void setShowNotification(Boolean showNotification) { this.showNotification = showNotification; }

    public String getBtDeviceMAC() { return btDeviceMAC; }

    public void setBtDeviceMAC(String btDeviceMAC) { this.btDeviceMAC = btDeviceMAC; }
}
