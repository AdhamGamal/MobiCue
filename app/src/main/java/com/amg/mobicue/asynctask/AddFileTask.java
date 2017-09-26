package com.amg.mobicue.asynctask;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.amg.mobicue.R;
import com.amg.mobicue.widget.MobiCueWidget;

import static com.amg.mobicue.database.FilesDbHelper.BASE_URI;
import static com.amg.mobicue.database.FilesDbHelper.FILE_CONTENT;
import static com.amg.mobicue.database.FilesDbHelper.FILE_NAME;
import static com.amg.mobicue.database.FilesDbHelper.SCROLL_SPEED;
import static com.amg.mobicue.database.FilesDbHelper.TEXT_SIZE;
import static com.amg.mobicue.database.FilesDbHelper.TEXT_STYLE;

public class AddFileTask extends AsyncTask<Object, Void, Void> {
    private Context context;

    public AddFileTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Object... params) {
        ContentValues values = new ContentValues();
        values.put(FILE_NAME, params[0].toString());
        values.put(FILE_CONTENT, params[1].toString());
        values.put(TEXT_SIZE, params[2].toString());
        values.put(TEXT_STYLE, params[3].toString());
        values.put(SCROLL_SPEED, params[4].toString());

        context.getContentResolver().insert(BASE_URI, values);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetManager.getAppWidgetIds(new ComponentName(context, MobiCueWidget.class)), R.id.listView);

    }
}