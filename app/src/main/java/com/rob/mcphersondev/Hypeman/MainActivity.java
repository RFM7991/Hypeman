package com.rob.mcphersondev.Hypeman;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraDevice;
import android.media.ImageReader;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;

public class MainActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    // hello test more comments yeah
    
    private static final String CLIENT_ID = "e20e5b14c264467281e102378cf45708";
    private static final String REDIRECT_URI = "https://msuweb.montclair.edu/~mcphersonr1/callback";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private static final int REQUEST_CODE = 1337;

    public JSONArray JSONArr = null;
    public ArrayList<ArrayList<String>> loaded_JSON = new ArrayList<ArrayList<String>>();
    JSONArray finalArray;
    public ArrayList<String> lyricPool = new ArrayList<String>();
    public ArrayList<String> api_Lyrics = new ArrayList<String>();
    public ArrayList<Root> rootPool = new ArrayList<Root>();
    private boolean isRunning;
    public int lyricIndex = 0;
    public int rootsLoaded = 0;
    private final String ROOTS_URL = "https://api.jsonbin.io/b/5c40ab4a7b31f426f85b4cb1";
    private Button galleryButton;

   // public ArrayList<Root> rootArr;
    public ArrayList<Root> rootArr = new ArrayList<>();

    {
        // https://api.jsonbin.io/b/5c40ab4a7b31f426f85b4cb1

    }


    // camera //////////////
    private static final String TAG = "AndroidCameraApi";
    private Button takePictureButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;

    private Camera2VideoFragment cameraFragment;

    private Button infoButton;

    private Bundle savedState;
    ///////////////////////

    // Recorder 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        savedState = savedInstanceState;

        // get Roots from JSON
        try {
            initCAPI("test", ROOTS_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // populate rootPool
        for (int i = 0; i < rootArr.size(); i++) {
            rootPool.add(rootArr.get(i));
        }
        rootPool = shuffleRoots(rootPool);
        loadLyrics(10);

        // Youtube
        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubeplayerfragment);
       youTubePlayerFragment.initialize("AIzaSyBVSgb0m8KxkiAwdFm0YWdsqi5VnC8fh20",this);


        // Intro Alert Message
        new AlertDialog.Builder(this)
                .setMessage(R.string.intro_message)
                .setPositiveButton(android.R.string.ok, null)
                .show();

        cameraFragment = null;

        try {
            pressRoot((Button) findViewById(R.id.rootButton));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // gallery button
        galleryButton = findViewById(R.id.gallery_button);
        galleryButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    galleryButton.setAlpha((float) 0.5);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    galleryButton.setAlpha((float) 1.0);
                    launchGallery();
                }
                return false;
            }
        });

        // info button
        infoButton = findViewById(R.id.info_button);
        infoButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    infoButton.setAlpha((float) 0.5);
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    infoButton.setAlpha((float) 1.0);
                    showInfo();
                }
                return false;
            }
        });
        ping();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    protected void onPause() {
        super.onPause();
        isRunning = false;

    //    if (cameraFragment != null) {
     //       cameraFragment.onPause();
    //    }

    }
    //on resume
    protected void onResume() {
        super.onResume();
        isRunning = true;

    }

    public void ping() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    if (isRunning) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (rootPool.size() <= 15) {
                            ArrayList<Root> rootReload = new ArrayList<Root>();
                            for (int i = 0; i < rootArr.size(); i++) {
                                if (!rootPool.contains(rootArr.get(i)))
                                    rootReload.add(rootArr.get(i));
                            }
                            rootReload = shuffleRoots(rootReload);

                            for (int i = 0; i < rootReload.size(); i++) {
                                rootPool.add(rootReload.get(i));
                            }
                        }

                        if (loaded_JSON.size() < 15 && loaded_JSON.size() != JSONArr.length()) {
                            loadLyrics(1);
                        }
                    }
                    // delay 5 seconds
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        }).start();
    }

    // call API, update Lyric Pool Array
    public void  cAPI(String query, String url) throws JSONException {
        api_Lyrics = new ArrayList<String>();
        RetrieveFeedTask task = new RetrieveFeedTask();
        task.execute(query, url);

        String results = null;
        try {
            results = task.get();
            //    Log.i("INFO", "YOOO Dog" + results);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            JSONArr = new JSONArray(results);
            //    Log.i("INFO", JSONArr.getJSONObject(0).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // populate LyricPool from JSON Array
        for (int i = 0; i < JSONArr.length(); i++) {
            Log.i("Raw", JSONArr.get(i).toString());
        }
        Log.i("length", JSONArr.length() + "");
        JSONArr = shuffle(JSONArr);
        for (int i = 0; i < JSONArr.length(); i++) {
            Log.i("shuffle", JSONArr.get(i).toString());
        }
        Log.i("length", JSONArr.length() + "");

        for (int i =0; i < JSONArr.length(); i++) {
            try {
                api_Lyrics.add(JSONArr.getJSONObject(i).get("word").toString());
                //   Log.i("INFO", lyricPool.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //initCAPI
    // call API, update Lyric Pool Array
    public void  initCAPI(String query, String url) throws JSONException {
        RetrieveFeedTask task = new RetrieveFeedTask();
        task.execute(query, url);

        String results = null;
        try {
            results = task.get();
            //    Log.i("INFO", "YOOO Dog" + results);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {
            JSONArr = new JSONArray(results);
            for (int i =0;i < JSONArr.length(); i++) {
                Log.i("initCAPI", JSONArr.getJSONObject(i).get("url").toString());
                String rootWord = JSONArr.getJSONObject(i).get("root").toString();
                String rootUrl = JSONArr.getJSONObject(i).get("url").toString();
                rootArr.add(new Root(rootWord, rootUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void showInfo()  {
        // Intro Alert Message
        new AlertDialog.Builder(this)
                .setMessage(R.string.intro_message)
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    public void pressCamera(View view) throws IOException {
        RelativeLayout cameraPreview = (RelativeLayout) findViewById(R.id.container2);
        Button cameraButton = (Button) findViewById(R.id.video_switch);
        Drawable cameraOn = getResources().getDrawable(R.drawable.ic_camera_on);
        Drawable cameraOff = getResources().getDrawable(R.drawable.ic_camera_off);
        ImageView headphones = (ImageView) findViewById(R.id.headphones);

    //    Log.e("CameraPreview"," "+ cameraPreview.getVisibility());
        // camera
        if (savedState == null && cameraPreview.getVisibility() == View.INVISIBLE) {
            cameraPreview.setVisibility(View.VISIBLE);


            //test camera
            getFragmentManager().beginTransaction()
                    .add(R.id.container2, Camera2VideoFragment.newInstance(), "cameraFragment")
                    .commit();

            // change camera button
            cameraButton.setBackground(cameraOff);

            // hide headphones
            headphones.setVisibility(View.INVISIBLE);
        }
        else {
            cameraPreview.setVisibility(View.INVISIBLE);


            // remove camera2VideoFragment
            Fragment fragment = getSupportFragmentManager().findFragmentByTag("cameraFragment");
            if(fragment != null)
                getSupportFragmentManager().beginTransaction().remove(fragment).commit();

            // change camera button
            cameraButton.setBackground(cameraOn);

            // show headphones
            headphones.setVisibility(View.VISIBLE);
        }
    }


    /** Called when the user taps the Root button */
    public void pressRoot(View view) throws IOException, JSONException {
        Button rootButton = (Button)findViewById(R.id.rootButton);
        Button lyricButton = (Button)findViewById(R.id.lyricButton);
        Button lastLyric = (Button) findViewById(R.id.lastLyric);
        Button nextLyric = (Button) findViewById(R.id.nextLyric);
        Button moreLyrics = (Button) findViewById(R.id.centerButton);
        TextView root_preview = (TextView) findViewById(R.id.nextRoot);



  //      for (int i = 0; i < loaded_JSON.size(); i++) {
    //        Log.i("all-roots", i + ": " + loaded_JSON.get(i).get(0));
  //      }

        //    Log.i("RootPool", rootPool.size() + "");

        //   Log.i("ghost-2", "" + loaded_JSON.size());

        if (loaded_JSON.size() > 2) {
            String currentRoot = rootPool.get(0).root;
            String currentURL = rootPool.get(0).url;

            // Root preview
            String nextRoot = rootPool.get(1).root;

            // set lyric Pool
            lyricPool = loaded_JSON.get(0);
     //       Log.i("remove1", lyricPool.get(0));
            loaded_JSON.remove(0);
     //       Log.i("remove2", lyricPool.get(0));
/*
            // update rootButton
            rootButton.setText(currentRoot);
            rootPool.remove(0);

            // Root preview
            if (rootPool.size() <= 1) {
                root_preview.setText("");
            } else {
                root_preview.setText(nextRoot);
            }
            pressLyric(moreLyrics);
            rootsLoaded--;
        }
*/
        // new 4:59
        lyricPool.add(currentRoot);

        // update rootButton
        // not changes, just
        rootButton.setText(lyricPool.get(lyricIndex));
        lyricPool.remove(lyricIndex);
        rootPool.remove(0);

        // Root preview
        if (rootPool.size() <= 1) {
            root_preview.setText("");
        } else {
            root_preview.setText(loaded_JSON.get(0).get(0));

        }
        pressLyric(moreLyrics);
        rootsLoaded--;
    }
    }


    /** Called when the user taps the center button */
    public void pressLyric(View view) throws IOException, JSONException {
        Button centerButton = (Button)findViewById(R.id.centerButton);
        Button lowerButton = (Button) findViewById(R.id.lyricButton);
        Button lastLyric = (Button) findViewById(R.id.lastLyric);
        Button nextLyric = (Button) findViewById(R.id.nextLyric);
        Button rootButton = (Button) findViewById(R.id.rootButton);


/*
        for (int i =  0; i < loaded_JSON.get(0).size(); i++) {
            Log.i("lyrics-loaded", i + ": " + loaded_JSON.get(0).get(i));
        }

        for (int i =  0; i < api_Lyrics.size(); i++) {
            Log.i("lyrics-api", i + ": " + api_Lyrics.get(i));
        }
     */
        if (rootButton.getText().toString().equals("Roots")) {
            pressRoot(rootButton);
        } else {
            lyricIndex = 0;
         //   Log.i("pool-size", "" + lyricPool.size());
            if (lyricPool.size() < 3) {
                pressRoot((Button) findViewById(R.id.rootButton));
            } else {
                lowerButton.setText(lyricPool.get(lyricIndex));
                lyricPool.remove(lyricIndex);

                // left button
                lastLyric.setText(lyricPool.get(lyricIndex));
                lyricPool.remove(lyricIndex);

                // right button
                nextLyric.setText(lyricPool.get(lyricIndex));
                lyricPool.remove(lyricIndex);
            }
        }



    }

    /** Called when the user taps the center button */
    public void pressLast(View view) throws IOException, JSONException {
        Button centerButton = (Button)findViewById(R.id.centerButton);
        Button lowerButton = (Button) findViewById(R.id.lyricButton);
        Button lastLyric = (Button) findViewById(R.id.lastLyric);
        Button nextLyric = (Button) findViewById(R.id.nextLyric);
        Button rootButton = (Button) findViewById(R.id.rootButton);

        if (rootButton.getText().toString().equals("Roots")) {
            pressRoot(rootButton);
        } else {
            lyricIndex = 0;

            if (lyricPool.size() < 1) {
                pressRoot((Button) findViewById(R.id.rootButton));
            } else {
                // left button
                rootButton.setText(lastLyric.getText());
                lastLyric.setText(lyricPool.get(lyricIndex));
                lyricPool.remove(lyricIndex);
            }
        }
    }

    /** Called when the user taps the center button */
    public void pressLowerLyric(View view) throws IOException, JSONException {
        Button centerButton = (Button)findViewById(R.id.centerButton);
        Button lowerButton = (Button) findViewById(R.id.lyricButton);
        Button lastLyric = (Button) findViewById(R.id.lastLyric);
        Button nextLyric = (Button) findViewById(R.id.nextLyric);
        Button rootButton = (Button) findViewById(R.id.rootButton);

        if (rootButton.getText().toString().equals("Roots")) {
            pressRoot(rootButton);
        } else {
            lyricIndex = 0;

            if (lyricPool.size() < 1) {
                pressRoot((Button) findViewById(R.id.rootButton));
            } else {
                rootButton.setText(lowerButton.getText());
                lowerButton.setText(lyricPool.get(lyricIndex));
                lyricPool.remove(lyricIndex);
            }
        }
    }
    /** Called when the user taps the center button */
    public void pressNext(View view) throws IOException, JSONException {
        Button centerButton = (Button)findViewById(R.id.centerButton);
        Button lowerButton = (Button) findViewById(R.id.lyricButton);
        Button lastLyric = (Button) findViewById(R.id.lastLyric);
        Button nextLyric = (Button) findViewById(R.id.nextLyric);
        Button rootButton = (Button) findViewById(R.id.rootButton);

        if (rootButton.getText().toString().equals("Roots")) {
            pressRoot(rootButton);
        } else {

            lyricIndex = 0;

            if (lyricPool.size() < 1) {
                pressRoot((Button) findViewById(R.id.rootButton));
            } else {
                // right button
                rootButton.setText(nextLyric.getText());
                nextLyric.setText(lyricPool.get(lyricIndex));
                lyricPool.remove(lyricIndex);
            }
        }
    }
    // sort JSON
    public JSONArray sortBySyllable(JSONArray arr) {
        JSONArray sortedArr = new JSONArray();

        // syllables
        for (int i=1; i <5; i++ ) {
            // check JSON array
            for (int j=0; j< arr.length(); j++) {
                try {
                    //   Log.i("INFO", "Syllables:" + arr.getJSONObject(j).get("syllables").toString());
                    if (arr.getJSONObject(j).get("numSyllables").toString().equals(""+i)) {
                        //    Log.i("INFO", "equal");
                        sortedArr.put(arr.getJSONObject(j));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return sortedArr;
    }

    public JSONArray shuffle(JSONArray arr) throws JSONException {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        JSONArray shuffle = new JSONArray();
        int length = arr.length();

        for (int i=0; i < length; i++) {
            int index = random.nextInt(arr.length());
            shuffle.put(arr.getJSONObject(index));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                arr.remove(index);
            }
        }
        return shuffle;
    }

    public ArrayList<Root> shuffleRoots(ArrayList<Root> arr) {
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        ArrayList<Root> shuffle = new ArrayList<Root>();
        ArrayList<Root> roots = arr;
        int length = roots.size();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(roots.size());
            shuffle.add(roots.get(index));
            roots.remove(index);
        }
        return shuffle;
    }

    // remove Repeats
    public JSONArray clearRepeats(JSONArray sortedArr, JSONObject rootObject) {
        JSONArray oneSyl = getSingleSyllables(sortedArr);
   //     Log.i("Length", "" + oneSyl.length());
        // append route to oneSyl array to be compared

        oneSyl.put(rootObject);
  //      Log.i("length", oneSyl.length() + "");

        for (int i = oneSyl.length(); i < sortedArr.length(); i++) {
            try {
   //             Log.i("More syllables",i + ": " + sortedArr.getJSONObject(i).get("word").toString());
                JSONObject jObj = sortedArr.getJSONObject(i);
                boolean contains = false;
                // compare to single syllable words
                int j = 0;
                while (!contains) {
                    String oneSyllWord = oneSyl.getJSONObject(j).get("word").toString();
     //               Log.i("checker", oneSyllWord);
                    // compare to each 1 syllable word
                    if (jObj.get("word").toString().contains(oneSyllWord) && oneSyllWord.length() > 3) {
                        contains = true;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
          //                  Log.i("Contains", jObj.get("word").toString() + " Contains " + oneSyllWord);
                            sortedArr.remove(i);
                            i--;
                        }
                    }
                    j++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return sortedArr;
    }
    public ArrayList<Root> swapRoots(ArrayList<Root> input) {
        ArrayList<Root> output = input;

        return output;
    }

    public JSONArray getSingleSyllables(JSONArray arr) {
        JSONArray oneSyl = new JSONArray();

        for (int i =0; i < arr.length(); i++) {
            try {
                JSONObject jObj = arr.getJSONObject(i);
                if (jObj.get("numSyllables").toString().equals("" + 1)) {
                    oneSyl.put(jObj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return oneSyl;
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            youTubePlayer.cuePlaylist("PL6eBorfLzavmm5QkhSC4yF6HXNufPGKBC");


            youTubePlayer.setShowFullscreenButton(false);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("There was an error initializing the YouTubePlayer (%1$s)", youTubeInitializationResult.toString());
            //   Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    public void loadLyrics(int length) {

        int loadSize = length;
        int index = loaded_JSON.size();
        // Log.i("Static", "loadLyrics: " + index);
        for (int i = index; i < index+loadSize; i++) {
     //       Log.i("Static", "Root: " + i);
            try {
                cAPI(rootPool.get(i).root, rootPool.get(i).url);
                loaded_JSON.add(api_Lyrics);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //    Log.i("compare", "Root: " + rootPool.get(0).root);
        //    Log.i("compare", "loaded: " + loaded_JSON.get(0).get(0));
    }


    public void launchGallery() {
        Intent intent = new Intent(this, VideoGalleryActivity.class);
        startActivity(intent);
    }


}