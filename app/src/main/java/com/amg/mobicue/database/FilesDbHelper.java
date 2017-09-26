package com.amg.mobicue.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class FilesDbHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "files";

    private static final String AUTHORITY = "com.amg.filesprovider";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    public static final String FILE_NAME = "name";
    public static final String FILE_CONTENT = "content";
    public static final String TEXT_SIZE = "size";
    public static final String TEXT_STYLE = "style";
    public static final String SCROLL_SPEED = "speed";


    public static final String COLUMNS[] = {FILE_NAME, FILE_CONTENT, TEXT_SIZE, TEXT_STYLE, SCROLL_SPEED};

    private static final String DATABASE_NAME = "files.db";
    private static final int DATABASE_VERSION = 1;

    public FilesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                FILE_NAME + " TEXT PRIMARY KEY, " +
                FILE_CONTENT + " TEXT, " +
                TEXT_SIZE + " INTEGER, " +
                TEXT_STYLE + " TEXT, " +
                SCROLL_SPEED + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
