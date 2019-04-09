package com.rob.mcphersondev.Hypeman;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.CustomViewHolder> {

    ArrayList<String> videoPaths;
    Activity activity;
    private ImageView videoView;
    private Button playButton;
    public  String PACKAGE_NAME;

    public  class CustomViewHolder extends RecyclerView.ViewHolder {

        // provide a reference to the view for each data item
        // complex data item may need more than one view per item
        // you provide access to all the iews for a data item in a view holder

        // specify data items
        public View view;
        public CardView videoCard;
        public ImageView video;
        public Button playButton, shareButton, deleteButton;
        public String PATH;
        public int POSITION;
        public TextView timeStamp;
        public String TIME;

        public CustomViewHolder(View v) {
            super(v);
            view = v;
            video = view.findViewById(R.id.video_view);
            videoCard = view.findViewById(R.id.videoCard);
            playButton = view.findViewById(R.id.play_button);
            shareButton = view.findViewById(R.id.share_button);
            deleteButton = view.findViewById(R.id.delete_button);
            timeStamp = view.findViewById(R.id.timeStamp);

            // listeners
            // play
            playButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.d("RFMG", "Down");
                        playButton.setAlpha((float) 0.5);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        Log.d("RFMG", "Up");
                        playButton.setAlpha((float) 1.0);
                        launchVideoViewer(PATH);
                    }
                    return false;
                }
            });

            // share
            shareButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.d("RFMG", "Down");
                        shareButton.setAlpha((float) 0.5);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        Log.d("RFMG", "Up");
                        shareButton.setAlpha((float) 1.0);
                        launchShare(PATH);
                    }
                    return false;
                }
            });

            // delete
            deleteButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        Log.d("RFMG", "Down");
                        deleteButton.setAlpha((float) 0.5);
                    }
                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        Log.d("RFMG", "Up");
                        deleteButton.setAlpha((float) 1.0);
                        launchDeleteDialog(PATH, POSITION);
                    }
                    return false;
                }
            });


        }
    }

    // Adapter constructor
    public VideoRecyclerViewAdapter(Activity act, ArrayList<String> data) {
        videoPaths = data;
        activity = act;
    }

    public VideoRecyclerViewAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_card, parent, false);
        CustomViewHolder vh = new CustomViewHolder(v);

        // package name
        PACKAGE_NAME = activity.getApplicationContext().getPackageName();
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        Log.d("adapter", "bind holder");

        // set position for reference during removal
        holder.POSITION = position;

        // get file
        final String vidPath = videoPaths.get(position);
        // set file
        holder.PATH = vidPath;

        // set video view
        videoView = holder.video;

        String time = getTimeStamp(vidPath);
        holder.timeStamp.setText(time);

        // set thumbnail
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(vidPath,
                MediaStore.Images.Thumbnails.MINI_KIND);

        videoView.setImageBitmap(thumb);

        playButton = holder.playButton;

    }

    @Override
    public int getItemCount() {
        return videoPaths.size();
    }

    public void launchVideoViewer(String p) {
        File f = new File(p);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        // < v24
     //   intent.setDataAndType(Uri.fromFile(f), "video/*");

        Uri apkURI = FileProvider.getUriForFile(
                activity,
                PACKAGE_NAME + ".provider", f);
        intent.setDataAndType(apkURI, "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        activity.startActivity(intent);
    }

    public void launchShareOld(String p) {

        // launch share video intent
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("video/mp4");
        File vid = new File(p);
        Uri uri = Uri.fromFile(vid);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        // this.onPause();
        activity.startActivity(Intent.createChooser(sendIntent, "Share video"));
    }

    public void launchShare(String p) {
        File file = new File(p);
        Intent install = new Intent(Intent.ACTION_SEND);
        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        // New Approach
        Uri apkURI = FileProvider.getUriForFile(
                activity.getBaseContext(),
                activity.getApplicationContext()
                        .getPackageName() + ".provider", file);
        install.setDataAndType(apkURI, "video/mp4");
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // End New Approach
        activity.startActivity(install);

    }

    public void launchDeleteDialog(String p, int pos) {
        final String path = p;
        final int position = pos;

        File f = new File(path);
        String fileName = f.getName();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        Log.d("RFMD", "Delete");
                        deleteFile(path, position);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Are you sure you want to delete video " + fileName + "?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void deleteFile(String p, int pos) {
        File f = new File(p);

        // delete file
        f.delete();

        //adjust adapter
        videoPaths.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, videoPaths.size());

    }

    public String getTimeStamp(String p) {
        // set video time
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //use one of overloaded setDataSource() functions to set your data source
        File f = new File(p);
        if (!p.isEmpty()) {
            retriever.setDataSource(activity, Uri.fromFile(new File(p)));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long timeInMillisec = Long.parseLong(time);
            retriever.release();

            // convert to minutes and seconds
            long totSec = timeInMillisec / 1000;
            String min = "" + totSec / 60;
            String sec = "" + totSec % 60;

            // format seconds
            if (sec.length() < 2) {
                sec = "0" + sec;
            }

            String ts = min + ":" + sec;

            return ts;
        }
        else {
            return "";
        }
    }

}