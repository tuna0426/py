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
        } else            Log.d(TAG, "OpenCV library found inside package.");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        releaseSound();
    }

    // Load OpenCV library and set up camera view
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mSize0 = new Size();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    // Process each frame from the camera and play piano sounds based on the position of the red object
    @Override
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
    }
        // Convert the image to grayscale
        Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGB2GRAY);

        // Apply a Gaussian blur to the image
        Imgproc.GaussianBlur(mGray, mGray, new Size(9, 9), 2, 2);

        // Detect edges in the image using Canny edge detection
        Imgproc.Canny(mGray, mGray, 50, 100);

        // Find contours in the image
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mGray, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));

        // Draw contours on the image
        Scalar color = new Scalar(255, 0, 0);
        Imgproc.drawContours(mRgba, contours, -1, color, 2);

        // Find the largest contour and get its bounding rectangle
        double maxArea = 0;
        int maxAreaIdx = -1;
        MatOfPoint temp_contour;
        MatOfPoint largest_contour = contours.get(0);
        List<MatOfPoint> largest_contours = new ArrayList<>();
        for (int idx = 0; idx < contours.size(); idx++) {
            temp_contour = contours.get(idx);
            double contourArea = Imgproc.contourArea(temp_contour);
            if (contourArea > maxArea) {
                maxArea = contourArea;
                maxAreaIdx = idx;
                largest_contour = temp_contour;
            }
        }

        // If the largest contour is found, get its position and play the corresponding piano sound
        if (maxAreaIdx != -1) {
    // Get the bounding rectangle of the largest contour
    int x = (int) Imgproc.boundingRect(largest_contour).tl().x;
    int y = (int) Imgproc.boundingRect(largest_contour).tl().y;
    int w = (int) Imgproc.boundingRect(largest_contour).size().width;
    int h = (int) Imgproc.boundingRect(largest_contour).size().height;
        }
    // Draw the bounding rectangle around the largest contour
    Imgproc.rectangle(mRgba, new Point(x, y), new Point(x + w, y + h), new Scalar(0, 255, 0), 2);

    // Check if the height of the bounding rectangle is greater than 50 pixels
    if (h > 50) {
        // Play the corresponding sound based on the x position of the bounding rectangle
        int key = (int) (x / (mRgba.cols() / 7));
        mSoundPool.play(mSoundIds[key], 1, 1, 1, 0, 1f);
    }
// SoundPool
public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private CameraBridgeViewBase mOpenCvCameraView;
    private SoundPool mSoundPool;
    private int[] mSoundIds = new int[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SoundPool
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setMaxStreams(7)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            mSoundPool = new SoundPool(7, AudioManager.STREAM_MUSIC, 0);
        }

        // Load sound files
        mSoundIds[0] = mSoundPool.load(this, R.raw.c4, 1);
        mSoundIds[1] = mSoundPool.load(this, R.raw.d4, 1);
        mSoundIds[2] = mSoundPool.load(this, R.raw.e4, 1);
        mSoundIds[3] = mSoundPool.load(this, R.raw.f4, 1);
        mSoundIds[4] = mSoundPool.load(this, R.raw.g4, 1);
        mSoundIds[5] = mSoundPool.load(this, R.raw.a4, 1);
        mSoundIds[6] = mSoundPool.load(this, R.raw.b4, 1);

        // Start camera preview
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.enableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(TAG, "OpenCV loaded successfully");
        } else {
            Log.d(TAG, "OpenCV not loaded");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        // Perform image processing
        Mat mRgba = inputFrame.rgba();
        Mat mGray = inputFrame.gray();
        Imgproc.GaussianBlur(mGray, mGray, new Size(9, 9), 2, 2);
        Imgproc.threshold(mGray, mGray, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mRgba, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
    }
// Find the index of the largest contour
    int maxAreaIdx = -1;
        double maxArea = 0;
        for (int i = 0; i < contours.size(); i++) {
        double area = Imgproc.contourArea(contours.get(i));
        if (area > maxArea) {
            maxArea = area;
            maxAreaIdx = i;
        }
    }

    if (maxAreaIdx != -1) {
        // Get the bounding rectangle of the largest contour
        int x = (int) Imgproc.boundingRect(contours.get(maxAreaIdx)).tl().x;
        int y = (int) Imgproc.boundingRect(contours.get(maxAreaIdx)).tl().y;
        int w = (int) Imgproc.boundingRect(contours.get(maxAreaIdx)).size().width;
        int h = (int) Imgproc.boundingRect(contours.get(maxAreaIdx)).size().height;

        if (h > 50) {
            // Play the sound corresponding to the key
            mSoundPool.play(mSoundIds[5], 1.0f, 1.0f, 0, 0, 1.0f);
        }
    }


