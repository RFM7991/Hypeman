package com.rob.mcphersondev.Hypeman;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

public class VideoGalleryActivity extends AppCompatActivity {

    private  File[] files;
    private  VideoView video;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private VideoRecyclerViewAdapter mAdapter;
    private Button backButon; // set up back button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery);

        // init recyclerview
        recyclerView = findViewById(R.id.recycler);

        // layout manager
        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        // init adapter
        mAdapter = new VideoRecyclerViewAdapter(this, loadVideoPaths());
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

    }

    public ArrayList<String> loadVideoPaths() {
        File vidFolder = getBaseContext().getExternalFilesDir(null);

        files  = vidFolder.listFiles();
        ArrayList<String> fileList = new ArrayList<String>();

        // load from list to array
        for (File e: files) {
            fileList.add(e.getPath());
        }
        Log.d("recycler", " "+ fileList.size());
        return fileList;
    }

    // to do - seek to for all videos for thumbnail
    public void onResume() {
        super.onResume();
    }

    public void launchMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
