package com.amg.mobicue.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.amg.mobicue.R;
import com.amg.mobicue.asynctask.AddFileTask;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import butterknife.OnClick;


public class AddFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_file);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    public void saveFile(View view) {

        String filename = ((TextView) findViewById(R.id.filename)).getText().toString();
        String content = ((TextView) findViewById(R.id.content)).getText().toString();

        if (filename.trim().length() == 0 || content.trim().length() == 0) {
            Snackbar.make(view, getString(R.string.warning), Snackbar.LENGTH_SHORT).show();
        } else {
            new AddFileTask(this).execute(filename, content, 50, getString(R.string.bold), 1);
            startActivity(new Intent(this, MainActivity.class));
        }
    }
}
