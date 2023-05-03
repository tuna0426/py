import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends Activity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivity";
    private JavaCameraView mOpenCvCameraView;
    private SoundPool mSoundPool;
    private int[] mSoundIds;
    private int mNumSounds = 8;
    private Rect[] mKeys;
    private int mKeyWidth = 80;
    private int mKeyHeight = 400;
    private int mKeySpacing = 20;
    private int mNumKeys = 8;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Failed to load OpenCV!");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.camera_view);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mSoundPool = new SoundPool(mNumSounds, AudioManager.STREAM_MUSIC, 0);
        mSoundIds = new int[mNumSounds];
        mSoundIds[0] = mSoundPool.load(this, R.raw.c4, 1);
        mSoundIds[1] = mSoundPool.load(this, R.raw.d4, 1);
        mSoundIds[2] = mSoundPool.load(this, R.raw.e4, 1);
        mSoundIds[3] = mSoundPool.load(this, R.raw.f4, 1);
        mSoundIds[4] = mSoundPool.load(this, R.raw.g4, 1);
        mSoundIds[5] = mSoundPool.load(this, R.raw.a4, 1);
        mSoundIds[6] = mSoundPool.load(this, R.raw.c5, 1);
        mSoundIds[7] = mSoundPool.load(this, R.raw.b4, 1);

        mKeys = new Rect[mNumKeys];
        for (int i = 0; i < mNumKeys; i++) {
            int x = (i + 1) * mKeySpacing + i * mKeyWidth;
            int y = 0;
            mKeys[i] = new Rect(x, y, x + mKeyWidth, y + mKeyHeight);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Failed to load OpenCV!");
        } else {
            mOpenCvCameraView.enableView();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

   @Override
public void onCameraViewStarted(int width, int height) {
    Log.i(TAG, "Camera view started");
}

@Override
public void onCameraViewStopped() {
    Log.i(TAG, "Camera view stopped");
}


    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgba = inputFrame.rgba();
        Imgproc.cvtColor(rgba, rgba, Imgproc.COLOR_RGBA2GRAY);
        Core.flip(rgba, rgba, 1);
        for (int i = 0; i < mNumKeys; i++) {
            Rect key = mKeys[i];
            Mat roi = rgba.submat(key);
            Scalar mean = Core.mean(roi);
            if (mean.val[0] < 50) {
                mSoundPool.play(mSoundIds[i], 1, 1, 1, 0, 1);
            }
        }
        return rgba;
    }
}
