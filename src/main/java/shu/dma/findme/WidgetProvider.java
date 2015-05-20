package shu.dma.findme;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Findme Home Screen Mini Widget.
 * Refresh when the target text was tapped. Auto-refresh every 20 minutes.
 *
 * Created by YunHsiao on 2014/8/1.
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final String WIDGET_RECEIVING_ITEM = Constants.PACKAGE_NAME + ".WIDGET_RECEIVING_ITEM";
    private static final String WIDGET_LAST_ID = Constants.PACKAGE_NAME + ".WIDGET_LAST_ID";
    private static final String WIDGET_MAX_LINE_NUMBER = Constants.PACKAGE_NAME + ".WIDGET_TARGET_MAX_LINE_NUMBER";
    private static final String WIDGET_RIGHT = Constants.PACKAGE_NAME + ".WIDGET_RIGHT";
    private static final String WIDGET_WRONG = Constants.PACKAGE_NAME + ".WIDGET_WRONG";
    //protected static final String GRID_EXTRA = Constants.PACKAGE_NAME + ".GRID_EXTRA";
    private static RemoteViews remoteViews;
    private static int lastID = R.id.w01;
    private static String[] lastPair = {"止", "正"};
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            //Log.d("Widget", "------------------Updating---------------");
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_initial);
            refreshContent(context, remoteViews);
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.d("Widget", "------------------On Receive: " + intent.getAction());
        super.onReceive(context, intent);
        if (remoteViews == null) remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_initial);
        if (intent.getAction().equals(WIDGET_WRONG)) {
            remoteViews.setTextColor(context.getSharedPreferences("data", 0).getInt(WIDGET_LAST_ID, lastID), Color.WHITE);
            lastID = Integer.parseInt(intent.getStringExtra(WIDGET_RECEIVING_ITEM));
            remoteViews.setTextColor(lastID, Color.RED);
            SharedPreferences.Editor editor = context.getSharedPreferences("data", 0).edit();
            editor.putInt(WIDGET_LAST_ID, lastID);
            editor.apply();
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(new ComponentName(context, WidgetProvider.class), remoteViews);
        } else if (intent.getAction().equals(WIDGET_RIGHT)) {
            remoteViews.setTextColor(Integer.parseInt(intent.getStringExtra(WIDGET_RECEIVING_ITEM)), Color.GREEN);
            remoteViews.setTextColor(context.getSharedPreferences("data", 0).getInt(WIDGET_LAST_ID, lastID), Color.WHITE);
            refreshContent(context, remoteViews);
            remoteViews.setTextColor(Integer.parseInt(intent.getStringExtra(WIDGET_RECEIVING_ITEM)), Color.WHITE);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(new ComponentName(context, WidgetProvider.class), remoteViews);
        }
    }
    @Override
    public void onAppWidgetOptionsChanged (Context context, AppWidgetManager
            appWidgetManager, int appWidgetId, Bundle newOptions) {
        updateWidget(context, appWidgetManager, appWidgetId, newOptions);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updateWidget(Context context, AppWidgetManager appWidgetManager,
                              int appWidgetId, Bundle newOptions) {
        int mln = Constants.WIDGET_MATRIX_HEIGHT;
        int max = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        int min = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        boolean isKeyGuard = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1)
                                == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;
        if (isKeyGuard) {
            if (min <= 200) mln = 3;
            else if (min <= 400) mln = 9;
            if (!context.getSharedPreferences("data", 0).getBoolean("ac_keyguard", false)) {
                SharedPreferences.Editor editor = context.getSharedPreferences("data", 0).edit();
                editor.putBoolean("ac_keyguard", true);
                editor.apply();
                Toast.makeText(context,
                        context.getString(R.string.eac_keyguard), Toast.LENGTH_LONG).show();
            }
        } else {
            if (min <= 160) mln = 4;
            else if (min <= 205) mln = 5;
            else if (min <= 250) mln = 6;
            else if (min <= 295) mln = 8;
            else if (min <= 340) mln = 9;
            if (!context.getSharedPreferences("data", 0).getBoolean("ac_homescreen", false)) {
                SharedPreferences.Editor editor = context.getSharedPreferences("data", 0).edit();
                editor.putBoolean("ac_homescreen", true);
                editor.apply();
                Toast.makeText(context,
                        context.getString(R.string.eac_homescreen), Toast.LENGTH_LONG).show();
            }
        }
        if (context.getSharedPreferences("data", 0).getBoolean("showResizeInfo", false))
        Toast.makeText(context,
                "[ " + min + " - " + max + ": " + mln + " ]", Toast.LENGTH_LONG).show();
        //Log.d("Widget", "[ " + min + " - " + max + ": " + mln + " ]");/**/
        SharedPreferences.Editor editor = context.getSharedPreferences("data", 0).edit();
        editor.putInt(WIDGET_MAX_LINE_NUMBER, mln);
        editor.apply();
        if (remoteViews == null) remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_initial);
        refreshContent(context, remoteViews);
        appWidgetManager.updateAppWidget(new ComponentName(context, WidgetProvider.class), remoteViews);
    }

    /*private int getTargetMaxLineNumber(Context context, int appWidgetId) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Bundle range = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int max = range.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        int min = range.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        Log.d("Size", "[ " + min + " - " + max + " ]");
        if (min <= 120) return 4;
        else if (min <= 190) return 6;
        else if (min <= 260) return 8;
        else return 9;
    }*/

    private void refreshContent(Context context, RemoteViews remoteViews) {
        Intent Tintent;
        PendingIntent Tpi;
        Intent Fintent;
        PendingIntent Fpi;
        int targetMaxLineNumber = context.getSharedPreferences("data", 0).getInt(WIDGET_MAX_LINE_NUMBER, 9);
        int position = (int) (Math.random() * (context.getString(R.string.findme_widget_t).length() - 1));
        lastPair[0] = String.valueOf(context.getString(R.string.findme_widget_t).charAt(position));
        lastPair[1] = String.valueOf(context.getString(R.string.findme_widget_f).charAt(position));
        int lastTarget = (int) (Math.random() * (Constants.WIDGET_MATRIX_WIDTH * targetMaxLineNumber - 1));
        for (int i = 0; i <= Constants.WIDGET_MATRIX - 1; i++) {
            if (i == lastTarget) {
                remoteViews.setTextViewText(R.id.w01 + i, lastPair[0]);
                Tintent = new Intent(WIDGET_RIGHT);
                Tintent.putExtra(WIDGET_RECEIVING_ITEM, String.valueOf(R.id.w01 + i));
                Tintent.putExtra(WIDGET_MAX_LINE_NUMBER, targetMaxLineNumber);
                Tpi = PendingIntent.getBroadcast(context, 0, Tintent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.w01 + i, Tpi);
            } else if (i > Constants.WIDGET_MATRIX_WIDTH * targetMaxLineNumber - 1) {
                remoteViews.setTextViewText(R.id.w01 + i, "");
            } else {
                remoteViews.setTextViewText(R.id.w01 + i, lastPair[1]);
                Fintent = new Intent(WIDGET_WRONG);
                Fintent.putExtra(WIDGET_RECEIVING_ITEM, String.valueOf(R.id.w01 + i));
                Fpi = PendingIntent.getBroadcast(context, R.id.w01 + i, Fintent, PendingIntent.FLAG_UPDATE_CURRENT);
                remoteViews.setOnClickPendingIntent(R.id.w01 + i, Fpi);
            }
        }
    }
}
