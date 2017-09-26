package com.amg.mobicue.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.amg.mobicue.AnalyticsApplication;
import com.amg.mobicue.R;
import com.amg.mobicue.adapter.EmptyListInterface;
import com.amg.mobicue.adapter.FilesAdapter;
import com.amg.mobicue.asynctask.AddFileTask;
import com.amg.mobicue.models.File;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.OnClick;

import static com.amg.mobicue.database.FilesDbHelper.BASE_URI;
import static com.amg.mobicue.database.FilesDbHelper.COLUMNS;

public class MainActivity extends AppCompatActivity implements EmptyListInterface {
    private FilesAdapter adapter;
    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        tracker = application.getDefaultTracker();

        loadSavedFiles();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //using tracker variable to set Screen Name
        tracker.setScreenName(getString(R.string.app_name));
        //sending the screen to analytics using ScreenViewBuilder() method
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            try {
                Uri uri = data.getData();
                BufferedReader br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
                StringBuffer content = new StringBuffer();
                String line = null;
                while ((line = br.readLine()) != null) {
                    content.append(line);
                }
                br.close();

                String uriAsString = uri.toString();
                java.io.File file = new java.io.File(uriAsString);
                String filename = file.getName();
                if (uriAsString.startsWith("content://")) {
                    if (uriAsString.contains("com.google.android.apps.docs.storage")) {
                        Cursor cursor = null;
                        try {
                            cursor = getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                filename = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriAsString.contains("com.android.providers.downloads.documents")) {
                        filename = filename.substring(filename.lastIndexOf("%2F") + 3);
                    }
                }

//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        TranslateOptions options = TranslateOptions.newBuilder()
//                                .setApiKey("AIzaSyCxaoLlTmeZodX7x5Q1ghArp53QiT-R8eY")
//                                .build();
//                        Translate translate = options.getService();
//                        final Translation translation =
//                                translate.translate("Hello World",
//                                        Translate.TranslateOption.targetLanguage("de"));
//
//                        Log.e("Translate: ", translation.getTranslatedText());
//
//                        return null;
//                    }
//                }.execute();


                new AddFileTask(this).execute(filename, content, 50, getString(R.string.bold), 1);

                adapter.addFile(new File(filename, content.toString(), 50, getString(R.string.bold), 1));

                if (findViewById(R.id.empty_view).getVisibility() == View.VISIBLE) {
                    findViewById(R.id.empty_view).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Toast.makeText(this, getString(R.string.warning2), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadSavedFiles() {

        new AsyncTask<Void, Void, ArrayList<File>>() {

            @Override
            protected ArrayList<File> doInBackground(Void... params) {
                ArrayList<File> files = new ArrayList<>();
                Cursor cursor = getContentResolver().query(BASE_URI, COLUMNS, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        files.add(new File(cursor.getString(0), cursor.getString(1), cursor.getInt(2), cursor.getString(3), cursor.getInt(4)));
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                return files;
            }

            @Override
            protected void onPostExecute(ArrayList<File> files) {
                if (files.size() == 0) {
                    findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
                }
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                adapter = new FilesAdapter(files, MainActivity.this, MainActivity.this);
                recyclerView.setAdapter(adapter);
            }
        }.execute();
    }

    public void loadFile(View view) {
        Intent intent = new Intent();
        intent.setType("text/plain");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    public void createFile(View view) {
        startActivity(new Intent(this, AddFileActivity.class));
    }

    @Override
    public void onEmptyList() {
        findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
    }
}

