package com.example.rob.Hypeman;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CameraTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);

        // camera
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, Camera2VideoFragment.newInstance())
                    .commit();

        }

    }
}
