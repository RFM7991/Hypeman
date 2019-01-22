package com.example.rob.Hypeman;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.CamcorderProfile;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import com.example.rob.Hypeman.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlayerEvent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback, YouTubePlayer.OnInitializedListener {

    private static final String CLIENT_ID = "e20e5b14c264467281e102378cf45708";
    private static final String REDIRECT_URI = "https://msuweb.montclair.edu/~mcphersonr1/callback";
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private Player mPlayer;
    private static final int REQUEST_CODE = 1337;

    public JSONArray JSONArr = null;
    public ArrayList<ArrayList<String>> loaded_JSON = new ArrayList<ArrayList<String>>();
    JSONArray finalArray;
    public ArrayList<String> lyricPool = new ArrayList<String>();
    public ArrayList<String> api_Lyrics = new ArrayList<String>();
    public ArrayList<Root> rootPool = new ArrayList<Root>();
    public int lyricIndex = 0;
    public int rootsLoaded = 0;
    private final String ROOTS_URL = "https://api.jsonbin.io/b/5c40ab4a7b31f426f85b4cb1";
    // roots must be longer than 4 letters
    // later make object with param for root and url
    //   private String[] roots =/* {"socrates", "start", "flip", "stop", "free", "style", "guillotine", "sword", "hell",
    //      "mars", "face", "brew", "boots", "bike", "floor",  "function", "pretend",
    //    "yes", "brother", "rumble",  "shadow", "socks", "quick"};
    //                   */
   // public ArrayList<Root> rootArr;
    public ArrayList<Root> rootArr = new ArrayList<>();

    {


        // https://api.jsonbin.io/b/5c40ab4a7b31f426f85b4cb1
/*
        // one syllable
        rootArr.add(new Root("sword", "http://api.jsonbin.io/b/5b087e027a973f4ce5784768"));
        rootArr.add(new Root("start", "http://api.jsonbin.io/b/5b087ee77a973f4ce578476a"));
        rootArr.add(new Root("flip", "http://api.jsonbin.io/b/5b0811c1c2e3344ccd96c126"));
        rootArr.add(new Root("stop", "http://api.jsonbin.io/b/5b08122ac83f6d4cc7349e1b"));
        rootArr.add(new Root("free", "http://api.jsonbin.io/b/5b088008c2e3344ccd96c16a"));
        rootArr.add(new Root("style", "http://api.jsonbin.io/b/5b088059c83f6d4cc7349e69"));
        rootArr.add(new Root("hell", "http://api.jsonbin.io/b/5b08808cc2e3344ccd96c171"));
        rootArr.add(new Root("mars", "http://api.jsonbin.io/b/5b0880b97a973f4ce5784771"));
        rootArr.add(new Root("face", "http://api.jsonbin.io/b/5b0886d37a973f4ce5784777"));
        rootArr.add(new Root("boots", "http://api.jsonbin.io/b/5b088809c83f6d4cc7349e72"));

        // 2.0
        rootArr.add(new Root("Peace", "http://api.jsonbin.io/b/5b259cf6287f6a187c06f890"));
        rootArr.add(new Root("Nice", "http://api.jsonbin.io/b/5b258c887a7d19187640226b"));
        rootArr.add(new Root("Smooth", "http://api.jsonbin.io/b/5b258dcc287f6a187c06f87b"));
        rootArr.add(new Root("axe", "http://api.jsonbin.io/b/5b25a48a7a7d19187640228a"));
        rootArr.add(new Root("course", "http://api.jsonbin.io/b/5b25a7ba7a7d19187640228e"));

        // two syllables
        rootArr.add(new Root("program", "http://api.jsonbin.io/b/5b088af7c83f6d4cc7349e74"));
        rootArr.add(new Root("precise", "http://api.jsonbin.io/b/5b089a89c83f6d4cc7349e86"));
        rootArr.add(new Root("tonight", "http://api.jsonbin.io/b/5b08a874c2e3344ccd96c189"));
        rootArr.add(new Root("speaker", "http://api.jsonbin.io/b/5b08aa350fb4d74cdf23e66b"));
        rootArr.add(new Root("flavor", "http://api.jsonbin.io/b/5b08ad2c0fb4d74cdf23e66d"));
        rootArr.add(new Root("nation", "http://api.jsonbin.io/b/5b08add70fb4d74cdf23e66f"));
        rootArr.add(new Root("label", "http://api.jsonbin.io/b/5b08b03e0fb4d74cdf23e675"));
        rootArr.add(new Root("Design", "http://api.jsonbin.io/b/5b08b0c30fb4d74cdf23e677"));
        rootArr.add(new Root("backpack", "http://api.jsonbin.io/b/5b08b195c2e3344ccd96c193"));

        //three syllables
        rootArr.add(new Root("guillotine", "http://api.jsonbin.io/b/5b0882777a973f4ce5784775"));
        rootArr.add(new Root("socrates", "http://api.jsonbin.io/b/5b08818cc83f6d4cc7349e6c/2"));

        // near rhymes
        rootArr.add(new Root("pocket", "http://api.jsonbin.io/b/5b19a9430fb4d74cdf23f457"));
        rootArr.add(new Root("power", "http://api.jsonbin.io/b/5b19d5d4c2e3344ccd96cf95"));
        rootArr.add(new Root("confirm", "http://api.jsonbin.io/b/5b19d6f40fb4d74cdf23f475"));
        rootArr.add(new Root("online", "http://api.jsonbin.io/b/5b19d810c2e3344ccd96cf97/1"));
        rootArr.add(new Root("master", "http://api.jsonbin.io/b/5b19d8c7c83f6d4cc734acb3"));
        rootArr.add(new Root("pretend", "http://api.jsonbin.io/b/5b0895c27a973f4ce5784780/1"));
        rootArr.add(new Root("rumble", "http://api.jsonbin.io/b/5b089964c83f6d4cc7349e84/1"));
        rootArr.add(new Root("provide", "http://api.jsonbin.io/b/5b22bad58bdb2a3588e207e1"));
        rootArr.add(new Root("fashion", "http://api.jsonbin.io/b/5b22b0728bdb2a3588e207d7"));
        rootArr.add(new Root("logic", "http://api.jsonbin.io/b/5b22b3b14e1aee358ec705a2"));
        rootArr.add(new Root("poet", "http://api.jsonbin.io/b/5b22b224bd4c0f35a0b468f7"));
        rootArr.add(new Root("ladies", "http://api.jsonbin.io/b/5b22b50abd4c0f35a0b468fb"));

        //    rootArr.add(new Root("", ""))

        //    rootArr.add(new Root("", ""));
    */
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
    ///////////////////////

    // Recorder 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


/*
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        Log.i("Mark", "Test1");
       // Log.i("INFO", roots[0]);
*/
        // get Roots from JSON
        try {
            initCAPI("test", ROOTS_URL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    /*    JSONRootRetriever jsonRoots = new JSONRootRetriever(ROOTS_URL);
        try {
            rootArr = jsonRoots.getRoots();
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        // populate rootPool
        for (int i = 0; i < rootArr.size(); i++) {
            rootPool.add(rootArr.get(i));
        }
        rootPool = shuffleRoots(rootPool);
        loadLyrics(10);
        //    loadLyrics(rootPool.size());

        // Youtube
        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubeplayerfragment);
       youTubePlayerFragment.initialize("AIzaSyBVSgb0m8KxkiAwdFm0YWdsqi5VnC8fh20",this);

        // Admob
        //     AdView mAdView;

        //      MobileAds.initialize(this,"ca-app-pub-1118271101921860~8233524493");

        //      mAdView = findViewById(R.id.adView);
        //       AdRequest adRequest = new AdRequest.Builder().build();
        //       mAdView.loadAd(adRequest);


        // camera
        if (null == savedInstanceState) {
            //test camera
            getFragmentManager().beginTransaction()
                    .add(R.id.container2, Camera2VideoFragment.newInstance())
                    .commit();

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    //on resume
    protected void onResume() {
        for (int i = 0; i < rootPool.size(); i++) {
            Log.i("all-roots", i + ": " + rootPool.get(i).root);
        }
        super.onResume();
        Log.i("static",  "" + rootsLoaded);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //      Log.i("static", "loaded_JSON.size: " + loaded_JSON.size());
                    Log.i("static", "rootPool.size: " + rootPool.size());
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
                        Log.i("static", "subload");
                        loadLyrics(1);
                    }
                }
            }
        }).start();

        Log.e(TAG, "onResume");
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
            Log.i("remove1", lyricPool.get(0));
            loaded_JSON.remove(0);
            Log.i("remove2", lyricPool.get(0));

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
    }

    /** Called when the user taps the center button */
    public void pressLyric(View view) throws IOException, JSONException {
        Button centerButton = (Button)findViewById(R.id.centerButton);
        Button lowerButton = (Button) findViewById(R.id.lyricButton);
        Button lastLyric = (Button) findViewById(R.id.lastLyric);
        Button nextLyric = (Button) findViewById(R.id.nextLyric);
        Button rootButton = (Button) findViewById(R.id.rootButton);



        for (int i =  0; i < loaded_JSON.get(0).size(); i++) {
            Log.i("lyrics-loaded", i + ": " + loaded_JSON.get(0).get(i));
        }

        for (int i =  0; i < api_Lyrics.size(); i++) {
            Log.i("lyrics-api", i + ": " + api_Lyrics.get(i));
        }
        if (rootButton.getText().toString().equals("Roots")) {
            pressRoot(rootButton);
        } else {
            lyricIndex = 0;
            Log.i("pool-size", "" + lyricPool.size());
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
        Log.i("Length", "" + oneSyl.length());
        // append route to oneSyl array to be compared

        oneSyl.put(rootObject);
        Log.i("length", oneSyl.length() + "");

        for (int i = oneSyl.length(); i < sortedArr.length(); i++) {
            try {
                Log.i("More syllables",i + ": " + sortedArr.getJSONObject(i).get("word").toString());
                JSONObject jObj = sortedArr.getJSONObject(i);
                boolean contains = false;
                // compare to single syllable words
                int j = 0;
                while (!contains) {
                    String oneSyllWord = oneSyl.getJSONObject(j).get("word").toString();
                    Log.i("checker", oneSyllWord);
                    // compare to each 1 syllable word
                    if (jObj.get("word").toString().contains(oneSyllWord) && oneSyllWord.length() > 3) {
                        contains = true;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Log.i("Contains", jObj.get("word").toString() + " Contains " + oneSyllWord);
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


    // Spotify player
    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");

        mPlayer.playUri(null, "spotify:track:5J2n4gbJVuM9YAySuGXh8a", 0, 0);

    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error var1) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
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
            Log.i("Static", "Root: " + i);
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

}