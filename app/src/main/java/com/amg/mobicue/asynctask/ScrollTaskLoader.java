package com.amg.mobicue.asynctask;

import android.content.AsyncTaskLoader;

import com.amg.mobicue.ui.DisplayActivity;

public class ScrollTaskLoader extends AsyncTaskLoader<Void> {

    private DisplayActivity context;
    public static boolean keepScrolling;
    private int scrollSpeed;

    public ScrollTaskLoader(DisplayActivity context, int scrollSpeed) {
        super(context);
        this.context = context;
        keepScrolling = true;
        this.scrollSpeed = scrollSpeed;
    }

    @Override
    public Void loadInBackground() {
        try {
            Thread.sleep(2000);
            while (keepScrolling) {
                Thread.sleep(20);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        context.scrollView.scrollBy(0, scrollSpeed);
                    }
                });
            }
        } catch (InterruptedException e) {
        }
        return null;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}