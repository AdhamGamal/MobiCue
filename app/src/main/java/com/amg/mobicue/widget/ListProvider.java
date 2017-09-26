package com.amg.mobicue.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.amg.mobicue.R;
import com.amg.mobicue.models.File;

import java.util.ArrayList;

import static com.amg.mobicue.database.FilesDbHelper.BASE_URI;
import static com.amg.mobicue.database.FilesDbHelper.COLUMNS;

public class ListProvider implements RemoteViewsFactory {
    private ArrayList<File> files = new ArrayList();
    private Context context;

    public ListProvider(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate() {
        loadSavedFiles();
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
        files.clear();
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.widget_file_layout);
        remoteView.setTextViewText(R.id.file_name, files.get(position).getFILE_NAME());

        Intent intent = new Intent();
        intent.putExtra(context.getString(R.string.filename), files.get(position).getFILE_NAME());
        intent.putExtra(context.getString(R.string.content), files.get(position).getFILE_CONTENT());
        intent.putExtra(context.getString(R.string.textsize), files.get(position).getTEXT_SIZE());
        intent.putExtra(context.getString(R.string.textstyle), files.get(position).getTEXT_STYLE());
        intent.putExtra(context.getString(R.string.scrollspeed), files.get(position).getSCROLL_SPEED());
        remoteView.setOnClickFillInIntent(R.id.root, intent);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void loadSavedFiles() {

        files = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(BASE_URI, COLUMNS, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                files.add(new File(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getInt(4)));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }
}