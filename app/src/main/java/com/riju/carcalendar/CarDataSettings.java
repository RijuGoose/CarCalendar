package com.riju.carcalendar;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

public class CarDataSettings {
    private long startMillis;
    private long endMillis;
    private String calendarName;
    private String btDeviceName;
    private String accountType; // "com.google"
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

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBtDeviceName() {
        return btDeviceName;
    }

    public void setBtDeviceName(String btDeviceName) {
        this.btDeviceName = btDeviceName;
    }

    public Boolean getShowNotification() { return showNotification; }

    public void setShowNotification(Boolean showNotification) { this.showNotification = showNotification; }
}
