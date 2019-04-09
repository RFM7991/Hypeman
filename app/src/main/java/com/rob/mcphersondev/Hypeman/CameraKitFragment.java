package com.rob.mcphersondev.Hypeman;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.rob.mcphersondev.Hypeman.R;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CameraKitFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CameraKitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CameraKitFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private CameraView cameraView;
    private boolean isRecording = false;

    private OnFragmentInteractionListener mListener;
    private String videoPath;

    private  AudioManager mAudioManager;
    private int currentVolume;

    private Button recordButton;

    private final String REC = "REC";
    private final String STOP = "STOP";

    public CameraKitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CameraKitFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CameraKitFragment newInstance() {
       return new CameraKitFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // audio manager for volume control
        mAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        // run time permissions
        if (!checkPermission()) {
            openActivity();
        } else {
            if (checkPermission()) {
                requestPermissionAndContinue();
            } else {
                openActivity();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  =  inflater.inflate(R.layout.fragment_camera_kit, container, false);

        recordButton = view.findViewById(R.id.recordButton);
        setUpCameraView(view);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    @Override
    public void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }

    private void setUpCameraView(View v) {
        cameraView = (CameraView) v.findViewById(R.id.camera);
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {
            }

            @Override
            public void onError(CameraKitError cameraKitError) {
            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
            }

            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {
                Activity activity = getActivity();
                if (null != activity) {
                    Toast.makeText(activity, "Video saved: " + videoPath,
                            Toast.LENGTH_SHORT).show();
                    Log.d("RFM", "Video saved: " + videoPath);
                    launchShare(videoPath);


                }
                // The File parameter is an MP4 file.
                Log.d("RFM", cameraKitVideo.getVideoFile().getAbsolutePath());


            }
        });


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // stop recording
                if (isRecording) {
                    cameraView.stopVideo();
                    isRecording = false;

                    // set button
                    recordButton.setText(REC);

                    // increase volume to previous
                    if (!mAudioManager.isWiredHeadsetOn() && ! mAudioManager.isBluetoothA2dpOn() && ! mAudioManager.isBluetoothScoOn())
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, AudioManager.FLAG_SHOW_UI);


                }

                // start Recording
                else if (!isRecording) {
                    videoPath = getVideoFilePath();
                    cameraView.captureVideo(new File(videoPath));
                    isRecording = true;

                    // set button
                    recordButton.setText(STOP);


                    //To decrease media player volume
                    if (!mAudioManager.isWiredHeadsetOn() && ! mAudioManager.isBluetoothA2dpOn() && ! mAudioManager.isBluetoothScoOn()) {
                        currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                        float percent = 0.3f;
                        int recordingVolume = (int) (maxVolume * percent);
                        Log.d("vol", "rec" + recordingVolume + ", cur" + currentVolume );
                        if (mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) > recordingVolume)
                            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, recordingVolume, AudioManager.FLAG_SHOW_UI);
                    }


                }
                return;
            }
        });
    }

    public void launchShare(String p) {

        // launch share video intent
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("video/mp4");
        File vid = new File(p);
        Uri uri = Uri.fromFile(vid);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        // this.onPause();
        startActivity(Intent.createChooser(sendIntent, "Share video"));
    }



    private String getVideoFilePath() {
        File mydir = new File(Environment.getExternalStorageDirectory(), "Hypeman"); //Creating an internal dir;
        if (!mydir.exists())
        {
            mydir.mkdirs();
        }

        //   File dir = Environment.getExternalStorageDirectory();
        //  String fileName = new SimpleDateFormat("yyyyMMddHHmmss'.txt'").format(new Date());
        String fileName = System.currentTimeMillis() + ".mp4";
        return (mydir == null ? "" : (mydir.getAbsolutePath() + "/"))
                + fileName;
    }

    private static final int PERMISSION_REQUEST_CODE = 200;
    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(getContext(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("We need permisson");
                alertBuilder.setMessage("permission message");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    openActivity();
                } else {
                 //   finish();
                }

            } else {
              //  finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openActivity() {
        //add your further process after giving permission or to download images from remote server.
    }
}
