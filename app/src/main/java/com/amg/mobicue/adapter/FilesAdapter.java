package com.amg.mobicue.adapter;

import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amg.mobicue.R;
import com.amg.mobicue.models.File;
import com.amg.mobicue.ui.DisplayActivity;
import com.amg.mobicue.widget.ListProvider;
import com.amg.mobicue.widget.MobiCueWidget;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.amg.mobicue.database.FilesDbHelper.BASE_URI;
import static com.amg.mobicue.database.FilesDbHelper.FILE_NAME;


public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ItemHolder> {

    private ArrayList<File> files;
    private Context context;
    private EmptyListInterface emptyListInterface;

    public FilesAdapter(ArrayList<File> files, Context context, EmptyListInterface emptyListInterface) {
        this.files = files;
        this.context = context;
        this.emptyListInterface = emptyListInterface;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.file_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void addFile(File file) {
        files.add(file);
        notifyDataSetChanged();
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements OnClickListener {
        @BindView(R.id.file_name)
        TextView fileName;
        @BindView(R.id.delete)
        ImageButton delete;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void onBind(int position) {
            fileName.setText(files.get(position).getFILE_NAME());
        }

        @OnClick(R.id.delete)
        public void delete(View view) {
            Snackbar.make(view, context.getString(R.string.Confirm_Deletion), Snackbar.LENGTH_LONG)
                    .setAction(context.getString(R.string.delete), new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    context.getContentResolver().delete(BASE_URI, FILE_NAME + " = ?",
                                            new String[]{files.get(getAdapterPosition()).getFILE_NAME()});
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    files.remove(getAdapterPosition());
                                    notifyDataSetChanged();
                                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                                    appWidgetManager.notifyAppWidgetViewDataChanged(
                                            appWidgetManager.getAppWidgetIds(new ComponentName(context, MobiCueWidget.class)),
                                            R.id.listView);
                                    if (files.size() == 0) {
                                        emptyListInterface.onEmptyList();
                                    }
                                }
                            }.execute();
                        }
                    }).show();
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Intent intent = new Intent(context, DisplayActivity.class);
            intent.putExtra(context.getString(R.string.filename), files.get(position).getFILE_NAME());
            intent.putExtra(context.getString(R.string.content), files.get(position).getFILE_CONTENT());
            intent.putExtra(context.getString(R.string.textsize), files.get(position).getTEXT_SIZE());
            intent.putExtra(context.getString(R.string.textstyle), files.get(position).getTEXT_STYLE());
            intent.putExtra(context.getString(R.string.scrollspeed), files.get(position).getSCROLL_SPEED());
            Log.e("SPEEF:", files.get(position).getSCROLL_SPEED() + "");
            context.startActivity(intent);
        }
    }
}