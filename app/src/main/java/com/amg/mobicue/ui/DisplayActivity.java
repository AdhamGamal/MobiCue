package com.amg.mobicue.ui;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amg.mobicue.R;
import com.amg.mobicue.asynctask.ScrollTaskLoader;
import com.amg.mobicue.asynctask.UpdateFileTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.amg.mobicue.asynctask.ScrollTaskLoader.keepScrolling;
import static com.amg.mobicue.database.FilesDbHelper.BASE_URI;
import static com.amg.mobicue.database.FilesDbHelper.FILE_NAME;
import static com.amg.mobicue.database.FilesDbHelper.SCROLL_SPEED;
import static com.amg.mobicue.database.FilesDbHelper.TEXT_SIZE;
import static com.amg.mobicue.database.FilesDbHelper.TEXT_STYLE;


public class DisplayActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Void> {

    public
    @BindView(R.id.scrollview)
    ScrollView scrollView;
    @BindView(R.id.content)
    TextView content;
    @BindView(R.id.size)
    SeekBar sizeSeekBar;
    @BindView(R.id.size_display)
    TextView sizeDisplay;
    private int size;
    @BindView(R.id.bold)
    CheckBox bold;
    @BindView(R.id.italic)
    CheckBox italic;
    private String style;
    @BindView(R.id.speed)
    SeekBar speedSeekBar;
    @BindView(R.id.speed_display)
    TextView speedDisplay;
    private int speed;


    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();

    private final Runnable mHidePart2Runnable = new Runnable() {
        @Override
        public void run() {
            content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @BindView(R.id.fullscreen_content_controls)
    View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        ButterKnife.bind(this);

        mVisible = true;

        Intent intent = getIntent();
        final String fileName = intent.getExtras().getString(getString(R.string.filename));
        String fileContent = intent.getExtras().getString(getString(R.string.content));
        size = intent.getExtras().getInt(getString(R.string.textsize));
        style = intent.getExtras().getString(getString(R.string.textstyle));
        speed = intent.getExtras().getInt(getString(R.string.scrollspeed));

        setTitle(fileName);

        content.setText(fileContent);
        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

        sizeDisplay.setText(String.valueOf(size));

        sizeSeekBar.setProgress(size);
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                size = progress;
                if (size == 0) {
                    size = 1;
                }
                content.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
                sizeDisplay.setText(String.valueOf(size));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new UpdateFileTask(getBaseContext()).execute(TEXT_SIZE, String.valueOf(seekBar.getProgress()), fileName);
            }
        });

        switch (style) {
            case "bold":
                content.setTypeface(content.getTypeface(), Typeface.BOLD);
                bold.setChecked(true);
                break;
            case "italic":
                content.setTypeface(content.getTypeface(), Typeface.ITALIC);
                italic.setChecked(true);
                break;
            case "bold_italic":
                content.setTypeface(content.getTypeface(), Typeface.BOLD_ITALIC);
                bold.setChecked(true);
                italic.setChecked(true);
                break;
        }

        bold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked && italic.isChecked()) {
                    content.setTypeface(content.getTypeface(), Typeface.BOLD_ITALIC);
                    style = getString(R.string.bold_italic);
                } else if (isChecked && !italic.isChecked()) {
                    content.setTypeface(null, Typeface.BOLD);
                    style = getString(R.string.bold);
                } else if (!isChecked && italic.isChecked()) {
                    content.setTypeface(null, Typeface.ITALIC);
                    style = getString(R.string.italic);
                } else {
                    content.setTypeface(null, Typeface.NORMAL);
                    style = getString(R.string.normal);
                }
                new UpdateFileTask(getBaseContext()).execute(TEXT_STYLE, style, fileName);
            }
        });

        italic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (bold.isChecked() && isChecked) {
                    content.setTypeface(content.getTypeface(), Typeface.BOLD_ITALIC);
                    style = getString(R.string.bold_italic);
                } else if (!bold.isChecked() && isChecked) {
                    content.setTypeface(null, Typeface.ITALIC);
                    style = getString(R.string.italic);
                } else if (bold.isChecked() && !isChecked) {
                    content.setTypeface(null, Typeface.BOLD);
                    style = getString(R.string.bold);
                } else {
                    content.setTypeface(null, Typeface.NORMAL);
                    style = getString(R.string.normal);
                }
                new UpdateFileTask(getBaseContext()).execute(TEXT_STYLE, style, fileName);
            }
        });

        speedDisplay.setText(String.valueOf(speed));

        speedSeekBar.setProgress(speed);
        speedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                speed = progress;
                if (speed == 0) {
                    speed = 1;
                }
                speedDisplay.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new UpdateFileTask(getBaseContext()).execute(SCROLL_SPEED, String.valueOf(speed), fileName);
            }
        });

    }

    @OnClick(R.id.content)
    public void content(View view) {
        toggle();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private static int id = 1;

    private void toggle() {
        if (mVisible) {
            hide();
            getLoaderManager().initLoader(id, null, this);
        } else {
            keepScrolling = false;
            show();
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    private void show() {
        content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }


    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        Log.e("Speed Is :", speed + "");
        return new ScrollTaskLoader(this, speed);
    }

    @Override
    public void onLoadFinished(Loader<Void> arg0, Void arg1) {
        getLoaderManager().destroyLoader(id);
    }

    @Override
    public void onLoaderReset(Loader<Void> arg) {
    }
}


