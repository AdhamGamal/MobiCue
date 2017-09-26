package com.amg.mobicue.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.amg.mobicue.R;
import com.amg.mobicue.ui.DisplayActivity;
import com.amg.mobicue.ui.MainActivity;

public class MobiCueWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mobi_cue_widget);

            views.setOnClickPendingIntent(R.id.title_label, PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));

            views.setRemoteAdapter(R.id.listView, new Intent(context, WidgetService.class));

            views.setPendingIntentTemplate(R.id.listView, PendingIntent.getActivity(context, 0, new Intent(context, DisplayActivity.class), 0));

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            ComponentName cn = new ComponentName(context, ListProvider.class);
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.listView);
        }
        super.onReceive(context, intent);
    }
}

