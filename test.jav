import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CvCameraViewListener2 {

    private static final String TAG = "MainActivity";

    private CameraBridgeViewBase mOpenCvCameraView;
    private Mat mRgba;
    private Mat mGray;
    private Size mSize0;

    // Define a SoundPool object
    private SoundPool mSoundPool;

    // Define an array to hold the sound IDs
    private int[] mSoundIds;

    // Initialize the SoundPool and load the sound files
    private void initSound() {
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundIds = new int[7];
        mSoundIds[0] = mSoundPool.load(this, R.raw.c4, 1);
        mSoundIds[1] = mSoundPool.load(this, R.raw.d4, 1);
        mSoundIds[2] = mSoundPool.load(this, R.raw.e4, 1);
        mSoundIds[3] = mSoundPool.load(this, R.raw.f4, 1);
        mSoundIds[4] = mSoundPool.load(this, R.raw.g4, 1);
        mSoundIds[5] = mSoundPool.load(this, R.raw.a4, 1);
        mSoundIds[6] = mSoundPool.load(this, R.raw.b4, 1);
    }

    // Release the SoundPool and free up resources
    private void releaseSound() {
        mSoundPool.release();
        mSoundPool = null;
        mSoundIds = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            mOpenCvCameraView = findViewById(R.id.main_surface);
            mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
            mOpenCvCameraView.setCvCameraViewListener(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        releaseSound();
    }

    @Override
    public void onResume() {
    super.onResume();
    initSound();
    if (!OpenCVLoader.initDebug()) {
        Log.d(TAG, "Internal OpenCV library not found.");
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    } else {
        Log.d(TAG, "OpenCV library found inside package. Using it!");
        mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
    }
}

private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
    @Override
    public void onManagerConnected(int status) {
        switch (status) {
            case LoaderCallbackInterface.SUCCESS: {
                Log.i(TAG, "OpenCV loaded successfully");
                // Load native library after(!) OpenCV initialization
                System.loadLibrary("detection_based_tracker");
                mOpenCvCameraView.enableView();
                break;
            }
            default: {
                super.onManagerConnected(status);
                break;
            }
        }
    }
};

private void initSound() {
    mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
    mSoundPoolMap = new HashMap<Integer, Integer>();
    mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
}


