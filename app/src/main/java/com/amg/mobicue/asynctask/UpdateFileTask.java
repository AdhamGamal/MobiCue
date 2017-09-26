package com.amg.mobicue.asynctask;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.amg.mobicue.R;
import com.amg.mobicue.widget.MobiCueWidget;

import static com.amg.mobicue.database.FilesDbHelper.BASE_URI;
import static com.amg.mobicue.database.FilesDbHelper.FILE_NAME;
import static com.amg.mobicue.database.FilesDbHelper.TEXT_STYLE;


public class UpdateFileTask extends AsyncTask<String, Void, Void> {
    private Context context;

    public UpdateFileTask(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        ContentValues values = new ContentValues();
        values.put(params[0], params[1]);
        context.getContentResolver().update(BASE_URI, values, FILE_NAME + "=?", new String[]{params[2]});
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.notifyAppWidgetViewDataChanged(
                appWidgetManager.getAppWidgetIds(new ComponentName(context, MobiCueWidget.class)), R.id.listView);
    }
}
