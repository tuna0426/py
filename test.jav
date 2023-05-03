package com.example.opencv_piano;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "MainActivity";

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private static final int MAX_STREAMS = 5;

    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba;
    private Mat mGray;
    private Mat mSize0;
    private SoundPool mSoundPool;
    private List<Key> mKeys;
    private NoteView mNoteView;
    private boolean mIsNoteVisible;

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    mOpenCvCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            System.loadLibrary("opencv_java4");
        } else {
            System.loadLibrary("opencv_java3");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Keep screen on while app is running
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Request camera permission if not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE);
        }

        // Set up camera view
        mOpenCvCameraView = findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        // Load sound samples
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(MAX_STREAMS)
                    .setAudioAttributes(attributes)
                    .build();
        } else {
            mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }
        loadSoundSamples();

        // Set up note view
        mNoteView = findViewById(R.id.note_view);
        mIsNoteVisible = false;
    }

    private void loadSoundSamples() {
        mKeys = new ArrayList<>();

        // Load note samples
        int[] whiteNotes = {R.raw.piano_a, R.raw.piano_b, R.raw.piano_c, R.raw.piano_d, R.raw.piano_e, R.raw
@Override
public void onCameraViewStarted(int width, int height) {
    mRgba = new Mat(height, width, CvType.CV_8UC4);
    mGray = new Mat(height, width, CvType.CV_8UC1);
    mSize0 = new Size();
    mChannels = new ArrayList<>();
// Initialize note sound players
for (int i = 0; i < mKeys.size(); i++) {
    int soundId = mSoundPool.load(this, mKeys.get(i).getNoteResourceId(), 1);
    mChannels.add(new NoteSoundPlayer(soundId));
}

// Initialize sound volume scaler
mVolumeScaler = new SoundVolumeScaler(mOpenCvCameraView.getWidth(), mOpenCvCameraView.getHeight());

// Start metronome
    mMetronome = new Metronome();
    mMetronome.setBpm(DEFAULT_BPM);
    mMetronome.start();
}

@Override
public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
        mChannels.clear();
        mSoundPool.release();
        mMetronome.stop();
    }

@Override
public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
    mRgba = inputFrame.rgba();
    mGray = inputFrame.gray();
    // Find contours in thresholded image
    Imgproc.threshold(mGray, mGray, THRESHOLD_MIN, THRESHOLD_MAX, Imgproc.THRESH_BINARY);
    List<MatOfPoint> contours = new ArrayList<>();
    Mat hierarchy = new Mat();
    Imgproc.findContours(mGray, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

    // Draw contours on color image and process notes
    Mat colorMat = new Mat();
    Imgproc.cvtColor(mGray, colorMat, Imgproc.COLOR_GRAY2RGBA);
for (int i = 0; i < contours.size(); i++) {
    Rect boundingRect = Imgproc.boundingRect(contours.get(i));
    if (isValidKey(boundingRect)) {
        Key key = getKeyByRect(boundingRect);
        if (key != null && key.isBlack() == isBlackKey(boundingRect)) {
            processNoteOn(key);
            Imgproc.rectangle(colorMat, boundingRect.tl(), boundingRect.br(), new Scalar(0, 255, 0, 255), 3);
        }
    }
}

// Draw key regions on color image
for (Key key : mKeys) {
    Imgproc.rectangle(colorMat, key.getRect().tl(), key.getRect().br(), key.isBlack() ? new Scalar(0, 0, 0, 255) : new Scalar(255, 255, 255, 255), 2);
}

// Scale sound volume based on distance from camera
double distance = getDistance();
double volume = mVolumeScaler.getVolume(distance);

// Play metronome click
boolean isClick = mMetronome.update();
if (isClick) {
    playMetronomeClick(volume);
}

// Play note sounds and draw note view
if (mIsNoteVisible) {
    Mat noteMat = new Mat(mNoteView.getHeight(), mNoteView.getWidth(), CvType.CV_8UC4, new Scalar(255, 255, 255, 255));
    for (Note note : mNotes) {
        int noteIndex = mKeys.indexOf(note.getKey());
        double noteVolume = note.getVolume(distance, volume);
        playNoteSound(noteIndex, noteVolume);
        drawNoteView(noteMat, note, noteVolume);
    }
