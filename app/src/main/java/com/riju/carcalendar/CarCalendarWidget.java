package com.riju.carcalendar;

import static android.content.Context.MODE_PRIVATE;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Implementation of App Widget functionality.
 */
public class CarCalendarWidget extends AppWidgetProvider {

    static CarDataSettings settings = new CarDataSettings();
    static SharedPreferences shrp;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        shrp = context.getSharedPreferences("CarCalendarSettings", MODE_PRIVATE);
        settings = LoadCarDataJson();

        Calendar time = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");

        time.setTimeInMillis(settings.getStartMillis());
        String startstring = sdf.format(time.getTime());

        time.setTimeInMillis(settings.getEndMillis());
        String endstring = sdf.format(time.getTime());

        //CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.car_calendar_widget);

        String out = startstring + "\n - \n";
        if(settings.getEndMillis() > settings.getStartMillis())
        {
            out += endstring;
        }

        views.setTextViewText(R.id.startendtime, out);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews rviews = new RemoteViews(context.getPackageName(), R.layout.car_calendar_widget);
        views.setOnClickPendingIntent(R.id.ccwidget_layout, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, rviews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);


        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private static CarDataSettings LoadCarDataJson() {
        Gson gson = new Gson();
        String json = shrp.getString("CarDataSettings", "");
        return gson.fromJson(json, CarDataSettings.class);
    }
}